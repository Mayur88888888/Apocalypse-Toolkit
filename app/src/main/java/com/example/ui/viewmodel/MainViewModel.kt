package com.example.ui.viewmodel

import android.app.Application
import android.hardware.camera2.CameraManager
import android.os.Vibrator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioGenerator
import com.example.audio.VoiceRecorderHelper
import com.example.comms.BluetoothManagerHelper
import com.example.comms.CryptoHelper
import com.example.comms.WifiDirectManagerHelper
import com.example.data.SurvivalRepository
import com.example.data.db.*
import com.example.sensors.SensorHelper
import com.example.utils.MorseCodeHelper
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val repository: SurvivalRepository
    val sensorHelper: SensorHelper
    val audioGenerator = AudioGenerator()
    val voiceRecorder = VoiceRecorderHelper(application)
    val bluetoothHelper = BluetoothManagerHelper(application)
    val wifiDirectHelper = WifiDirectManagerHelper(application)

    private val cameraManager = application.getSystemService(Application.CAMERA_SERVICE) as CameraManager
    private val vibrator = application.getSystemService(Application.VIBRATOR_SERVICE) as Vibrator

    init {
        val db = AppDatabase.getDatabase(application)
        repository = SurvivalRepository(db, application)
        sensorHelper = SensorHelper(application)
        sensorHelper.startSensors()
    }

    // UI Navigation State
    private val _selectedCategoryId = MutableStateFlow<Int?>(null)
    val selectedCategoryId: StateFlow<Int?> = _selectedCategoryId

    private val _activeToolId = MutableStateFlow<Int?>(null)
    val activeToolId: StateFlow<Int?> = _activeToolId

    fun selectCategory(catId: Int?) {
        _selectedCategoryId.value = catId
        _activeToolId.value = null
    }

    fun selectTool(toolId: Int?) {
        _activeToolId.value = toolId
    }

    // Settings
    val highContrast = repository.highContrastFlow.stateIn(viewModelScope, SharingStarted.Lazily, false)
    val largeText = repository.largeTextFlow.stateIn(viewModelScope, SharingStarted.Lazily, false)
    val pinLock = repository.pinLockFlow.stateIn(viewModelScope, SharingStarted.Lazily, "")
    val deadmanHours = repository.deadmanHoursFlow.stateIn(viewModelScope, SharingStarted.Lazily, 24)

    fun toggleHighContrast(enabled: Boolean) = viewModelScope.launch { repository.setHighContrast(enabled) }
    fun toggleLargeText(enabled: Boolean) = viewModelScope.launch { repository.setLargeText(enabled) }
    fun setPinLock(pin: String) = viewModelScope.launch { repository.setPinLock(pin) }

    // Category 1: Flashlight & Signaling
    private val _isFlashlightOn = MutableStateFlow(false)
    val isFlashlightOn: StateFlow<Boolean> = _isFlashlightOn

    private val _strobeSpeedHz = MutableStateFlow(5f)
    val strobeSpeedHz: StateFlow<Float> = _strobeSpeedHz

    private var flashJob: Job? = null

    fun toggleFlashlight(on: Boolean) {
        flashJob?.cancel()
        _isFlashlightOn.value = on
        try {
            val cameraId = cameraManager.cameraIdList.firstOrNull { id ->
                cameraManager.getCameraCharacteristics(id).get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            }
            if (cameraId != null) {
                cameraManager.setTorchMode(cameraId, on)
            }
        } catch (_: Exception) {}
    }

    fun startStrobe(hz: Float) {
        _strobeSpeedHz.value = hz
        flashJob?.cancel()
        flashJob = viewModelScope.launch(Dispatchers.Default) {
            var state = false
            while (isActive) {
                state = !state
                toggleFlashlight(state)
                delay((1000f / (hz * 2)).toLong())
            }
        }
    }

    fun stopStrobe() {
        flashJob?.cancel()
        toggleFlashlight(false)
    }

    fun blinkMorseText(text: String) {
        flashJob?.cancel()
        flashJob = viewModelScope.launch(Dispatchers.Default) {
            val morse = MorseCodeHelper.textToMorse(text)
            for (char in morse) {
                when (char) {
                    '.' -> {
                        toggleFlashlight(true)
                        audioGenerator.playMorseBeep(150)
                        toggleFlashlight(false)
                        delay(150)
                    }
                    '-' -> {
                        toggleFlashlight(true)
                        audioGenerator.playMorseBeep(450)
                        toggleFlashlight(false)
                        delay(150)
                    }
                    ' ' -> delay(300)
                    '/' -> delay(700)
                }
            }
        }
    }

    // Category 10 / SOS
    fun triggerOneTapSos(contacts: List<EmergencyContactEntity>) {
        // Flash SOS + Siren sound + vibrate
        startStrobe(8f)
        audioGenerator.startContinuousTone(AudioGenerator.SoundType.SIREN, viewModelScope)
        try {
            vibrator.vibrate(longArrayOf(0, 300, 200, 300, 200, 300, 500, 800, 200, 800), -1)
        } catch (_: Exception) {}
    }

    fun stopSos() {
        stopStrobe()
        audioGenerator.stop()
    }

    // Category 12 Comms
    val chatMessages = repository.allChatMessages.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val discoveredBtPeers = bluetoothHelper.discoveredDevices
    val btConnectionState = bluetoothHelper.connectionState

    fun startBtDiscovery() = bluetoothHelper.startPeerDiscovery(viewModelScope)
    fun startBtServer() = bluetoothHelper.startServer(viewModelScope) { sender, msg ->
        viewModelScope.launch {
            repository.addChatMessage(ChatMessageEntity(senderName = sender, messageText = msg, isOutgoing = false))
        }
    }

    fun sendChatMessage(text: String, channelSecret: String = "APOCALYPSE_SECURE") {
        viewModelScope.launch {
            val key = CryptoHelper.deriveKey(channelSecret)
            val encryptedText = CryptoHelper.encrypt(text, key)
            bluetoothHelper.sendMessage(encryptedText)
            wifiDirectHelper.sendHotspotMessage(encryptedText)
            repository.addChatMessage(
                ChatMessageEntity(senderName = "Me", messageText = text, isOutgoing = true)
            )
        }
    }

    fun panicWipe() {
        viewModelScope.launch {
            repository.deleteAllNotes()
            repository.clearChatHistory()
        }
    }

    override fun onCleared() {
        super.onCleared()
        sensorHelper.stopSensors()
        audioGenerator.stop()
        bluetoothHelper.disconnect()
        wifiDirectHelper.stop()
    }
}
