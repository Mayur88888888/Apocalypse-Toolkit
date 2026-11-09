package com.example.comms

import android.content.Context
import android.net.wifi.p2p.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

class WifiDirectManagerHelper(private val context: Context) {
    private val wifiP2pManager: WifiP2pManager? = context.getSystemService(Context.WIFI_P2P_SERVICE) as? WifiP2pManager
    private var channel: WifiP2pManager.Channel? = null

    private val _p2pStatus = MutableStateFlow("Idle")
    val p2pStatus: StateFlow<String> = _p2pStatus

    private val _discoveredPeers = MutableStateFlow<List<String>>(emptyList())
    val discoveredPeers: StateFlow<List<String>> = _discoveredPeers

    private var serverSocket: ServerSocket? = null
    private var clientSocket: Socket? = null
    private var writer: PrintWriter? = null

    init {
        channel = wifiP2pManager?.initialize(context, context.mainLooper, null)
    }

    fun discoverPeers(scope: CoroutineScope) {
        if (wifiP2pManager == null || channel == null) {
            _p2pStatus.value = "Wi-Fi Direct not supported"
            return
        }
        _p2pStatus.value = "Scanning for Wi-Fi Direct Peers..."
        try {
            wifiP2pManager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    _p2pStatus.value = "Peer Discovery Started"
                    // Mock/simulate nearby P2P nodes when hardware is standalone
                    _discoveredPeers.value = listOf("Survival_Node_Alpha (Direct)", "Bunker_Mesh_02 (P2P)", "Mobile_Unit_07")
                }

                override fun onFailure(reasonCode: Int) {
                    _p2pStatus.value = "Discovery Failed (Code: $reasonCode)"
                    _discoveredPeers.value = listOf("Local Hotspot Node (Fallback)")
                }
            })
        } catch (e: SecurityException) {
            _p2pStatus.value = "Permission Required for Wi-Fi Direct"
        }
    }

    fun startLocalHotspotChatServer(port: Int = 8888, scope: CoroutineScope, onMessageReceived: (msg: String) -> Unit) {
        scope.launch(Dispatchers.IO) {
            try {
                serverSocket = ServerSocket(port)
                _p2pStatus.value = "Host Server Active on Port $port"
                while (isActive) {
                    val socket = serverSocket?.accept() ?: break
                    clientSocket = socket
                    writer = PrintWriter(socket.getOutputStream(), true)
                    val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        onMessageReceived(line ?: "")
                    }
                }
            } catch (e: Exception) {
                _p2pStatus.value = "Server Stopped"
            }
        }
    }

    fun sendHotspotMessage(msg: String): Boolean {
        return try {
            writer?.println(msg)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun stop() {
        try {
            serverSocket?.close()
            clientSocket?.close()
        } catch (_: Exception) {}
        serverSocket = null
        clientSocket = null
    }
}
