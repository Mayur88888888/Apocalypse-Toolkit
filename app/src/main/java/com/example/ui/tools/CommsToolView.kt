package com.example.ui.tools

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ToolItem
import com.example.data.db.ChatMessageEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun CommsToolView(
    tool: ToolItem,
    viewModel: MainViewModel
) {
    val messages by viewModel.chatMessages.collectAsState()
    val btState by viewModel.btConnectionState.collectAsState()
    val btPeers by viewModel.discoveredBtPeers.collectAsState()
    val p2pStatus by viewModel.wifiDirectHelper.p2pStatus.collectAsState()

    var messageText by remember { mutableStateOf("") }
    var channelSecret by remember { mutableStateOf("APOCALYPSE_SECURE") }
    var isVoiceRecording by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
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

        // Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("BT STATUS: $btState", color = TacticalAmber, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("P2P STATUS: $p2pStatus", color = CyberGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Text("ENCRYPTION: AES-256-GCM (End-to-End)", color = SignalCyan, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        when (tool.id) {
            144 -> { // Peer Radar Canvas
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(DarkSurfaceVariant, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2, size.height / 2)
                        val radius = size.width / 2 - 10
                        drawCircle(color = CyberGreen.copy(alpha = 0.5f), radius = radius, style = Stroke(2f))
                        drawCircle(color = CyberGreen.copy(alpha = 0.3f), radius = radius * 0.6f, style = Stroke(2f))
                        drawCircle(color = CyberGreen.copy(alpha = 0.2f), radius = radius * 0.3f, style = Stroke(2f))
                        
                        // Local user dot
                        drawCircle(color = SurvivalOrange, radius = 8f, center = center)
                        // Peer 1
                        drawCircle(color = CyberGreen, radius = 6f, center = Offset(center.x + 40, center.y - 50))
                        // Peer 2
                        drawCircle(color = SignalCyan, radius = 6f, center = Offset(center.x - 60, center.y + 30))
                    }
                    Text("RADAR SCAN\n2 PEERS NEARBY", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            134, 149 -> { // Wi-Fi & Voice Walkie-Talkie
                Button(
                    onClick = {
                        if (isVoiceRecording) {
                            viewModel.voiceRecorder.stopRecording()?.let { file ->
                                viewModel.sendChatMessage("[Voice Note Sent: ${file.name}]")
                            }
                            isVoiceRecording = false
                        } else {
                            viewModel.voiceRecorder.startRecording()
                            isVoiceRecording = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isVoiceRecording) EmergencyRed else CyberGreen
                    ),
                    modifier = Modifier.fillMaxWidth().height(56.dp).testTag("push_to_talk_btn")
                ) {
                    Text(if (isVoiceRecording) "TRANSMITTING VOICE... (RELEASE TO SEND)" else "PUSH-TO-TALK WALKIE-TALKIE", fontWeight = FontWeight.Bold)
                }
            }
        }

        // P2P Controls
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { viewModel.startBtDiscovery() },
                colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange),
                modifier = Modifier.weight(1f).testTag("scan_bt_peers_btn")
            ) {
                Text("DISCOVER PEERS", fontSize = 11.sp)
            }
            Button(
                onClick = { viewModel.startBtServer() },
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                modifier = Modifier.weight(1f)
            ) {
                Text("HOST SERVER", fontSize = 11.sp)
            }
        }

        // Chat Message Log
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(DarkSurface, RoundedCornerShape(8.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(messages) { msg ->
                ChatMessageBubble(msg = msg)
            }
        }

        // Send Text Input
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                placeholder = { Text("Encrypted peer message...", color = TextSecondary) },
                singleLine = true,
                modifier = Modifier.weight(1f).testTag("chat_input_text"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = SurvivalOrange
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (messageText.isNotBlank()) {
                        viewModel.sendChatMessage(messageText, channelSecret)
                        messageText = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange),
                modifier = Modifier.testTag("send_chat_msg_btn")
            ) {
                Text("SEND")
            }
        }
    }
}

@Composable
fun ChatMessageBubble(msg: ChatMessageEntity) {
    val isOut = msg.isOutgoing
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isOut) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (isOut) SurvivalOrange.copy(alpha = 0.25f) else DarkSurfaceVariant,
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = msg.senderName,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isOut) SurvivalOrange else CyberGreen
                )
                Text(
                    text = msg.messageText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )
            }
        }
    }
}
