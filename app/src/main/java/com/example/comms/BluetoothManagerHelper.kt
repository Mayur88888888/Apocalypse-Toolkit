package com.example.comms

import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.os.ParcelUuid
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class BluetoothManagerHelper(private val context: Context) {
    private val bluetoothAdapter: BluetoothAdapter? = (context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState

    private val _discoveredDevices = MutableStateFlow<List<DiscoveredPeer>>(emptyList())
    val discoveredDevices: StateFlow<List<DiscoveredPeer>> = _discoveredDevices

    private val _receivedMessages = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val receivedMessages: StateFlow<List<Pair<String, String>>> = _receivedMessages

    private var serverSocket: BluetoothServerSocket? = null
    private var clientSocket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null
    private var inputStream: InputStream? = null
    private var commJob: Job? = null

    private val APP_UUID = UUID.fromString("94f39c20-6d4b-11eb-a8b3-0242ac130003")

    fun isBluetoothAvailable(): Boolean = bluetoothAdapter != null && bluetoothAdapter.isEnabled

    fun startPeerDiscovery(scope: CoroutineScope) {
        if (!isBluetoothAvailable()) {
            _connectionState.value = ConnectionState.UNAVAILABLE
            return
        }
        _connectionState.value = ConnectionState.SEARCHING
        scope.launch(Dispatchers.IO) {
            try {
                val bonded = bluetoothAdapter?.bondedDevices ?: emptySet()
                val peers = bonded.map { device ->
                    DiscoveredPeer(
                        name = try { device.name ?: "Unknown Peer" } catch (_: SecurityException) { "Apocalypse Peer" },
                        address = device.address,
                        rssi = -65,
                        device = device
                    )
                }
                _discoveredDevices.value = peers
                if (peers.isNotEmpty()) {
                    _connectionState.value = ConnectionState.DISCOVERED
                }
            } catch (e: SecurityException) {
                _connectionState.value = ConnectionState.ERROR
            }
        }
    }

    fun startServer(scope: CoroutineScope, onMessageReceived: (sender: String, msg: String) -> Unit) {
        if (!isBluetoothAvailable()) return
        scope.launch(Dispatchers.IO) {
            try {
                _connectionState.value = ConnectionState.LISTENING
                serverSocket = bluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord("ApocalypseToolkit", APP_UUID)
                val socket = serverSocket?.accept()
                if (socket != null) {
                    clientSocket = socket
                    outputStream = socket.outputStream
                    inputStream = socket.inputStream
                    _connectionState.value = ConnectionState.CONNECTED
                    listenForData(onMessageReceived)
                }
            } catch (e: Exception) {
                _connectionState.value = ConnectionState.DISCONNECTED
            }
        }
    }

    fun connectToPeer(device: BluetoothDevice, scope: CoroutineScope, onMessageReceived: (sender: String, msg: String) -> Unit) {
        scope.launch(Dispatchers.IO) {
            try {
                _connectionState.value = ConnectionState.CONNECTING
                val socket = device.createInsecureRfcommSocketToServiceRecord(APP_UUID)
                socket.connect()
                clientSocket = socket
                outputStream = socket.outputStream
                inputStream = socket.inputStream
                _connectionState.value = ConnectionState.CONNECTED
                listenForData(onMessageReceived)
            } catch (e: Exception) {
                e.printStackTrace()
                _connectionState.value = ConnectionState.ERROR
            }
        }
    }

    private fun listenForData(onMessageReceived: (sender: String, msg: String) -> Unit) {
        val buffer = ByteArray(1024)
        while (clientSocket?.isConnected == true) {
            try {
                val bytes = inputStream?.read(buffer) ?: -1
                if (bytes > 0) {
                    val rawText = String(buffer, 0, bytes, Charsets.UTF_8)
                    val sender = clientSocket?.remoteDevice?.name ?: "Remote Peer"
                    onMessageReceived(sender, rawText)
                }
            } catch (e: Exception) {
                _connectionState.value = ConnectionState.DISCONNECTED
                break
            }
        }
    }

    fun sendMessage(message: String): Boolean {
        return try {
            outputStream?.write(message.toByteArray(Charsets.UTF_8))
            outputStream?.flush()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun disconnect() {
        try {
            inputStream?.close()
            outputStream?.close()
            clientSocket?.close()
            serverSocket?.close()
        } catch (_: Exception) {}
        clientSocket = null
        serverSocket = null
        _connectionState.value = ConnectionState.DISCONNECTED
    }

    data class DiscoveredPeer(
        val name: String,
        val address: String,
        val rssi: Int,
        val device: BluetoothDevice? = null
    )

    enum class ConnectionState {
        DISCONNECTED, UNAVAILABLE, SEARCHING, DISCOVERED, LISTENING, CONNECTING, CONNECTED, ERROR
    }
}
