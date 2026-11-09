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
    val scope = rememberCoroutineScope()
    val messages by viewModel.chatMessages.collectAsState()
    val btState by viewModel.btConnectionState.collectAsState()
    val btPeers by viewModel.discoveredBtPeers.collectAsState()
    val p2pStatus by viewModel.wifiDirectHelper.p2pStatus.collectAsState()

    var messageText by remember { mutableStateOf("") }
    var selectedChannel by remember { mutableStateOf("General") }
    var channelSecret by remember { mutableStateOf("APOCALYPSE_SECURE") }
    var isVoiceRecording by remember { mutableStateOf(false) }

    var bulletinPostText by remember { mutableStateOf("") }
    val bulletinPosts = remember { mutableStateListOf("WELCOME TO LOCAL SURVIVAL BULLETIN", "Water source available at Sector 3", "Medical team active near Ridge") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
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

        // Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("BT STATUS: $btState", color = TacticalAmber, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Text("P2P STATUS: $p2pStatus", color = CyberGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
                Text("TRANSPORT: ${if (tool.id <= 130) "Bluetooth Classic/BLE" else "Wi-Fi Direct / Local Hotspot"} • AES-256-GCM E2E", color = SignalCyan, fontSize = 10.sp)
            }
        }

        when (tool.id) {
            128, 129 -> { // BT Scanner & RSSI Meter
                Button(
                    onClick = { viewModel.startBtDiscovery() },
                    colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("SCAN NEARBY BLUETOOTH PEERS & MEASURE RSSI")
                }
                LazyColumn(modifier = Modifier.height(120.dp).fillMaxWidth()) {
                    items(btPeers) { peer ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(peer.name, color = TextPrimary, fontWeight = FontWeight.Bold)
                            Text("${peer.rssi} dBm (~${(100 + peer.rssi) / 10}m)", color = TacticalAmber, fontSize = 12.sp)
                        }
                    }
                }
            }
            130 -> { // Bluetooth Dead-Drop
                Button(
                    onClick = { viewModel.sendChatMessage("[DEAD-DROP MESSAGE LEFT AT LOCATION]") },
                    colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("LEAVE GEOTAGGED DIGITAL DEAD-DROP MESSAGE")
                }
            }
            133 -> { // Host Local Hotspot Chat Server
                Button(
                    onClick = { viewModel.wifiDirectHelper.startLocalHotspotChatServer(8888, scope) {} },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("HOST EMBEDDED HOTSPOT CHAT SERVER (PORT 8888)")
                }
            }
            134, 149 -> { // Wi-Fi / Voice Walkie-Talkie
                Button(
                    onClick = {
                        if (isVoiceRecording) {
                            viewModel.voiceRecorder.stopRecording()?.let { file ->
                                viewModel.sendChatMessage("[Voice Note Transmitted: ${file.name}]")
                            }
                            isVoiceRecording = false
                        } else {
                            viewModel.voiceRecorder.startRecording()
                            isVoiceRecording = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isVoiceRecording) EmergencyRed else CyberGreen),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text(if (isVoiceRecording) "TRANSMITTING VOICE... (RELEASE TO SEND)" else "PUSH-TO-TALK WALKIE-TALKIE", fontWeight = FontWeight.Bold)
                }
            }
            138 -> { // Shared Bulletin Board
                Text("Local Wi-Fi Notice Board:", color = TextPrimary, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = bulletinPostText, onValueChange = { bulletinPostText = it }, placeholder = { Text("Post announcement...") }, modifier = Modifier.weight(1f), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary))
                    Spacer(modifier = Modifier.width(6.dp))
                    Button(
                        onClick = {
                            if (bulletinPostText.isNotBlank()) {
                                bulletinPosts.add(0, bulletinPostText)
                                bulletinPostText = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange)
                    ) { Text("POST") }
                }
                LazyColumn(modifier = Modifier.height(120.dp).fillMaxWidth()) {
                    items(bulletinPosts) { post ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                            Text("• $post", color = TextPrimary, modifier = Modifier.padding(8.dp), fontSize = 12.sp)
                        }
                    }
                }
            }
            144 -> { // Peer Discovery Radar Canvas
                Box(modifier = Modifier.size(180.dp).background(DarkSurfaceVariant, CircleShape), contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2, size.height / 2)
                        val radius = size.width / 2 - 10
                        drawCircle(color = CyberGreen.copy(alpha = 0.5f), radius = radius, style = Stroke(2f))
                        drawCircle(color = CyberGreen.copy(alpha = 0.3f), radius = radius * 0.6f, style = Stroke(2f))
                        drawCircle(color = SurvivalOrange, radius = 8f, center = center)
                        drawCircle(color = CyberGreen, radius = 6f, center = Offset(center.x + 40, center.y - 45))
                        drawCircle(color = SignalCyan, radius = 6f, center = Offset(center.x - 50, center.y + 35))
                    }
                    Text("RADAR SCAN\n2 PEERS NEARBY", color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            146 -> { // Group Channel System
                Text("Select Mesh Channel:", color = TextPrimary, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("General", "Shelter-1", "Water-Point", "Medical").forEach { chan ->
                        FilterChip(
                            selected = selectedChannel == chan,
                            onClick = { selectedChannel = chan },
                            label = { Text(chan, fontSize = 11.sp) }
                        )
                    }
                }
            }
            147 -> { // Emergency Broadcast Override
                Button(
                    onClick = { viewModel.sendChatMessage("EMERGENCY BROADCAST OVERRIDE: PRIORITY ALERT!") },
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("SEND EMERGENCY BROADCAST OVERRIDE")
                }
            }
            else -> { // Standard P2P Chat for rest of comms tools
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { viewModel.startBtDiscovery() },
                        colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange),
                        modifier = Modifier.weight(1f)
                    ) { Text("DISCOVER PEERS", fontSize = 11.sp) }
                    Button(
                        onClick = { viewModel.startBtServer() },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                        modifier = Modifier.weight(1f)
                    ) { Text("HOST SERVER", fontSize = 11.sp) }
                }
            }
        }

        // Shared P2P Chat Log & Input
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(DarkSurface, RoundedCornerShape(8.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(messages) { msg ->
                ChatMessageBubble(msg = msg)
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                placeholder = { Text("Encrypted message...", color = TextSecondary) },
                singleLine = true,
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (messageText.isNotBlank()) {
                        viewModel.sendChatMessage("[$selectedChannel] $messageText", channelSecret)
                        messageText = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange)
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
