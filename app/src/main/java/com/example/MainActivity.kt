package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.ui.screens.CategoryToolsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.theme.ApocalypseToolkitTheme
import com.example.ui.tools.ToolDetailDialog
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        // Permissions handled gracefully in ViewModel & Sensor Helper
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        requestRequiredPermissions()

        setContent {
            val highContrast by viewModel.highContrast.collectAsState()
            val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()
            val activeToolId by viewModel.activeToolId.collectAsState()

            ApocalypseToolkitTheme(darkTheme = true, highContrast = highContrast) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val catId = selectedCategoryId
                    if (catId == null) {
                        DashboardScreen(
                            viewModel = viewModel,
                            onCategoryClick = { categoryId -> viewModel.selectCategory(categoryId) },
                            onSosClick = { viewModel.selectTool(99) } // 99 = One-Tap SOS
                        )
                    } else {
                        BackHandler {
                            viewModel.selectCategory(null)
                        }
                        CategoryToolsScreen(
                            categoryId = catId,
                            onBackClick = { viewModel.selectCategory(null) },
                            onToolClick = { toolId -> viewModel.selectTool(toolId) }
                        )
                    }

                    // Active Tool Dialog overlay
                    activeToolId?.let { toolId ->
                        ToolDetailDialog(
                            toolId = toolId,
                            viewModel = viewModel,
                            onDismiss = { viewModel.selectTool(null) }
                        )
                    }
                }
            }
        }
    }

    private fun requestRequiredPermissions() {
        val permissionsToRequest = mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.VIBRATE,
            Manifest.permission.SEND_SMS
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT)
            permissionsToRequest.add(Manifest.permission.BLUETOOTH_SCAN)
            permissionsToRequest.add(Manifest.permission.BLUETOOTH_ADVERTISE)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest.add(Manifest.permission.NEARBY_WIFI_DEVICES)
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        val missing = permissionsToRequest.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missing.isNotEmpty()) {
            requestPermissionLauncher.launch(missing.toTypedArray())
        }
    }
}
