package com.example.ui.tools

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.audio.AudioGenerator
import com.example.data.ToolItem
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
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
    var ttsText by remember { mutableStateOf("EMERGENCY HELP NEEDED AT CURRENT LOCATION") }

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
            text = tool.name,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = SurvivalOrange
        )
        Text(
            text = tool.description,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        if (tool.isHardwareDependent) {
            Surface(
                color = TacticalAmber.copy(alpha = 0.2f),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = "NOTE: ${tool.fallbackInfo}",
                    color = TacticalAmber,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        when (tool.id) {
            13, 14 -> { // Siren
                Text("Pitch Adjustment: ${(pitchFactor * 100).toInt()}%", color = TextPrimary)
                Slider(
                    value = pitchFactor,
                    onValueChange = { pitchFactor = it },
                    valueRange = 0.5f..2.0f,
                    colors = SliderDefaults.colors(thumbColor = SurvivalOrange, activeTrackColor = SurvivalOrange)
                )
                Button(
                    onClick = {
                        if (isAudioActive) {
                            viewModel.audioGenerator.stop()
                            isAudioActive = false
                        } else {
                            viewModel.audioGenerator.startContinuousTone(
                                AudioGenerator.SoundType.SIREN,
                                scope,
                                pitchFactor
                            )
                            isAudioActive = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAudioActive) EmergencyRed else SurvivalOrange
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("siren_start_stop_btn")
                ) {
                    Text(if (isAudioActive) "STOP SIREN" else "PLAY EMERGENCY SIREN")
                }
            }
            15 -> { // Whistle
                Button(
                    onClick = {
                        if (isAudioActive) {
                            viewModel.audioGenerator.stop()
                            isAudioActive = false
                        } else {
                            viewModel.audioGenerator.startContinuousTone(
                                AudioGenerator.SoundType.WHISTLE,
                                scope
                            )
                            isAudioActive = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAudioActive) EmergencyRed else CyberGreen
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isAudioActive) "STOP WHISTLE" else "PLAY SURVIVAL WHISTLE")
                }
            }
            16, 25 -> { // Dog deterrent / Ultrasonic
                Button(
                    onClick = {
                        if (isAudioActive) {
                            viewModel.audioGenerator.stop()
                            isAudioActive = false
                        } else {
                            viewModel.audioGenerator.startContinuousTone(
                                AudioGenerator.SoundType.DOG_DETERRENT,
                                scope
                            )
                            isAudioActive = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TacticalAmber),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isAudioActive) "STOP ULTRASONIC TONE" else "EMIT HIGH-FREQUENCY DETERRENT")
                }
            }
            18, 19 -> { // Voice Messages / TTS
                OutlinedTextField(
                    value = ttsText,
                    onValueChange = { ttsText = it },
                    label = { Text("Phrase To Speak") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = SurvivalOrange
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { tts?.speak(ttsText, TextToSpeech.QUEUE_FLUSH, null, "1") },
                        colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange)
                    ) {
                        Text("SPEAK PHRASE")
                    }
                    Button(
                        onClick = { tts?.speak("MAYDAY MAYDAY NEED MEDICAL ASSISTANCE", TextToSpeech.QUEUE_FLUSH, null, "2") },
                        colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
                    ) {
                        Text("SAY MAYDAY")
                    }
                }
            }
            22 -> { // White noise
                Button(
                    onClick = {
                        if (isAudioActive) {
                            viewModel.audioGenerator.stop()
                            isAudioActive = false
                        } else {
                            viewModel.audioGenerator.startContinuousTone(
                                AudioGenerator.SoundType.WHITE_NOISE,
                                scope
                            )
                            isAudioActive = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SignalCyan),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isAudioActive) "STOP WHITE NOISE" else "PLAY NOISE MASKING FOR SLEEP")
                }
            }
            23, 24 -> { // Audio Memo Recorder / Playback
                var isRecording by remember { mutableStateOf(false) }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRecording) EmergencyRed else SurvivalOrange
                        )
                    ) {
                        Text(if (isRecording) "STOP RECORDING" else "RECORD VOICE MEMO")
                    }
                    Button(
                        onClick = {
                            viewModel.voiceRecorder.currentOutputFile?.let {
                                viewModel.voiceRecorder.startPlayback(it)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberGreen)
                    ) {
                        Text("PLAYBACK MEMO")
                    }
                }
            }
            else -> {
                Button(
                    onClick = {
                        if (isAudioActive) {
                            viewModel.audioGenerator.stop()
                            isAudioActive = false
                        } else {
                            viewModel.audioGenerator.startContinuousTone(
                                AudioGenerator.SoundType.AIR_HORN,
                                scope
                            )
                            isAudioActive = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isAudioActive) "STOP AIR HORN" else "SOUND AIR HORN BLAST")
                }
            }
        }
    }
}
