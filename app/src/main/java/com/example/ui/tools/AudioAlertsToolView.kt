package com.example.ui.tools

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioGenerator
import com.example.data.ToolItem
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

@Composable
fun AudioAlertsToolView(
    tool: ToolItem,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isAudioActive by remember { mutableStateOf(false) }
    var pitchFactor by remember { mutableStateOf(1.0f) }
    var selectedSirenType by remember { mutableStateOf("POLICE") }
    var ttsText by remember { mutableStateOf("EMERGENCY HELP NEEDED AT CURRENT LOCATION") }
    var ttsPitch by remember { mutableStateOf(1.0f) }

    var repeatingAlarmSeconds by remember { mutableStateOf(10) }
    var isAlarmTimerRunning by remember { mutableStateOf(false) }
    var alarmCountdown by remember { mutableStateOf(10) }

    var morseAudioText by remember { mutableStateOf("SOS") }

    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    DisposableEffect(context) {
        var ttsInstance: TextToSpeech? = null
        ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsInstance?.language = Locale.US
            }
        }
        tts = ttsInstance
        onDispose {
            ttsInstance.stop()
            ttsInstance.shutdown()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "#${tool.id}. ${tool.name}",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = SurvivalOrange
        )
        Text(
            text = tool.description,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        when (tool.id) {
            13 -> { // Loud Siren
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("POLICE", "AIR-RAID", "FIRE").forEach { type ->
                        FilterChip(
                            selected = selectedSirenType == type,
                            onClick = { selectedSirenType = type },
                            label = { Text(type) }
                        )
                    }
                }
                Button(
                    onClick = {
                        if (isAudioActive) {
                            viewModel.audioGenerator.stop()
                            isAudioActive = false
                        } else {
                            viewModel.audioGenerator.startContinuousTone(
                                AudioGenerator.SoundType.SIREN,
                                scope,
                                if (selectedSirenType == "AIR-RAID") 0.6f else 1.2f
                            )
                            isAudioActive = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAudioActive) EmergencyRed else SurvivalOrange
                    ),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text(if (isAudioActive) "STOP $selectedSirenType SIREN" else "START $selectedSirenType SIREN")
                }
            }
            14 -> { // Siren Pitch & Volume
                Text("Siren Pitch Multiplier: ${String.format("%.1fx", pitchFactor)}", color = TextPrimary, fontWeight = FontWeight.Bold)
                Slider(
                    value = pitchFactor,
                    onValueChange = { pitchFactor = it },
                    valueRange = 0.5f..2.5f,
                    colors = SliderDefaults.colors(thumbColor = SurvivalOrange, activeTrackColor = SurvivalOrange)
                )
                Button(
                    onClick = {
                        if (isAudioActive) {
                            viewModel.audioGenerator.stop()
                            isAudioActive = false
                        } else {
                            viewModel.audioGenerator.startContinuousTone(AudioGenerator.SoundType.SIREN, scope, pitchFactor)
                            isAudioActive = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isAudioActive) EmergencyRed else SurvivalOrange),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isAudioActive) "STOP ADJUSTABLE SIREN" else "PLAY ADJUSTABLE SIREN")
                }
            }
            15 -> { // Whistle Sound Generator
                Button(
                    onClick = {
                        if (isAudioActive) {
                            viewModel.audioGenerator.stop()
                            isAudioActive = false
                        } else {
                            viewModel.audioGenerator.startContinuousTone(AudioGenerator.SoundType.WHISTLE, scope)
                            isAudioActive = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isAudioActive) EmergencyRed else CyberGreen),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text(if (isAudioActive) "STOP 2800Hz WHISTLE" else "SOUND 2800Hz SURVIVAL WHISTLE")
                }
            }
            16 -> { // Dog Deterrent
                Button(
                    onClick = {
                        if (isAudioActive) {
                            viewModel.audioGenerator.stop()
                            isAudioActive = false
                        } else {
                            viewModel.audioGenerator.startContinuousTone(AudioGenerator.SoundType.DOG_DETERRENT, scope)
                            isAudioActive = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TacticalAmber),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isAudioActive) "STOP 18.5kHz TONE" else "EMIT 18.5kHz DOG DETERRENT TONE")
                }
            }
            17 -> { // Air Horn Sound
                Button(
                    onClick = {
                        if (isAudioActive) {
                            viewModel.audioGenerator.stop()
                            isAudioActive = false
                        } else {
                            viewModel.audioGenerator.startContinuousTone(AudioGenerator.SoundType.AIR_HORN, scope)
                            isAudioActive = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text(if (isAudioActive) "STOP AIR HORN" else "BLAST HIGH-DECIBEL AIR HORN")
                }
            }
            18 -> { // Pre-recorded Voice Messages
                Text("Select Emergency Speech Clip:", color = TextPrimary, fontWeight = FontWeight.Bold)
                val phrases = listOf(
                    "HELP NEEDED! WE ARE AT CURRENT LOCATION!",
                    "I NEED CLEAN WATER AND MEDICAL SUPPLIES!",
                    "CALL EMERGENCY RESCUE IMMEDIATELY!",
                    "SURVIVOR HERE! MAYDAY MAYDAY!"
                )
                phrases.forEach { phrase ->
                    Button(
                        onClick = { tts?.speak(phrase, TextToSpeech.QUEUE_FLUSH, null, phrase.hashCode().toString()) },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                    ) {
                        Text(phrase, fontSize = 12.sp, color = TextPrimary)
                    }
                }
            }
            19 -> { // Text-to-Speech Playback
                OutlinedTextField(
                    value = ttsText,
                    onValueChange = { ttsText = it },
                    label = { Text("Phrase To Synthesize") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { tts?.speak(ttsText, TextToSpeech.QUEUE_FLUSH, null, "1") },
                    colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("SYNTHESIZE SPEECH AUDIO")
                }
            }
            20 -> { // Loud Repeating Alarm Timer
                Text("Interval Delay: $repeatingAlarmSeconds Seconds", color = TextPrimary, fontWeight = FontWeight.Bold)
                Slider(
                    value = repeatingAlarmSeconds.toFloat(),
                    onValueChange = { repeatingAlarmSeconds = it.toInt() },
                    valueRange = 5f..60f,
                    steps = 11
                )
                if (isAlarmTimerRunning) {
                    Text("NEXT ALARM IN: $alarmCountdown SECONDS", color = EmergencyRed, fontWeight = FontWeight.Black, fontSize = 18.sp)
                }
                Button(
                    onClick = {
                        if (isAlarmTimerRunning) {
                            isAlarmTimerRunning = false
                        } else {
                            isAlarmTimerRunning = true
                            scope.launch {
                                while (isAlarmTimerRunning) {
                                    for (i in repeatingAlarmSeconds downTo 1) {
                                        alarmCountdown = i
                                        delay(1000)
                                        if (!isAlarmTimerRunning) break
                                    }
                                    if (isAlarmTimerRunning) {
                                        viewModel.audioGenerator.startContinuousTone(AudioGenerator.SoundType.SIREN, scope)
                                        delay(2000)
                                        viewModel.audioGenerator.stop()
                                    }
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isAlarmTimerRunning) EmergencyRed else CyberGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isAlarmTimerRunning) "CANCEL REPEATING ALARM TIMER" else "START REPEATING ALARM TIMER")
                }
            }
            21 -> { // Morse Code Audio Beeper
                OutlinedTextField(
                    value = morseAudioText,
                    onValueChange = { morseAudioText = it },
                    label = { Text("Morse Text to Play") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        scope.launch {
                            viewModel.blinkMorseText(morseAudioText)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("PLAY AUDIO MORSE CODE BEEPS")
                }
            }
            22 -> { // White Noise / Rain
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            if (isAudioActive) viewModel.audioGenerator.stop()
                            viewModel.audioGenerator.startContinuousTone(AudioGenerator.SoundType.WHITE_NOISE, scope)
                            isAudioActive = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SignalCyan),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("WHITE NOISE")
                    }
                    Button(
                        onClick = {
                            if (isAudioActive) viewModel.audioGenerator.stop()
                            viewModel.audioGenerator.startContinuousTone(AudioGenerator.SoundType.RAIN, scope)
                            isAudioActive = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("RAIN SOUND")
                    }
                }
                if (isAudioActive) {
                    Button(
                        onClick = {
                            viewModel.audioGenerator.stop()
                            isAudioActive = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
                    ) {
                        Text("STOP AUDIO MASKING")
                    }
                }
            }
            23 -> { // Audio Recorder
                var isRecording by remember { mutableStateOf(false) }
                Button(
                    onClick = {
                        if (isRecording) {
                            viewModel.voiceRecorder.stopRecording()
                            isRecording = false
                        } else {
                            viewModel.voiceRecorder.startRecording()
                            isRecording = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isRecording) EmergencyRed else SurvivalOrange),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text(if (isRecording) "STOP RECORDING VOICE MEMO" else "START RECORDING VOICE MEMO")
                }
            }
            24 -> { // Offline Playback of Recorded Memos
                val file = viewModel.voiceRecorder.currentOutputFile
                if (file != null && file.exists()) {
                    Text("Saved Voice Memo: ${file.name}", color = TextPrimary, fontSize = 12.sp)
                    Button(
                        onClick = { viewModel.voiceRecorder.startPlayback(file) },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("PLAY RECENT VOICE MEMO")
                    }
                } else {
                    Text("No voice memos recorded yet.", color = TextSecondary)
                }
            }
            25 -> { // Ultrasonic Tone
                Button(
                    onClick = {
                        if (isAudioActive) {
                            viewModel.audioGenerator.stop()
                            isAudioActive = false
                        } else {
                            viewModel.audioGenerator.startContinuousTone(AudioGenerator.SoundType.ULTRASONIC, scope)
                            isAudioActive = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TacticalAmber),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isAudioActive) "STOP 20kHz TONE" else "EMIT 20kHz ULTRASONIC TONE")
                }
            }
        }
    }
}
