package net.theluckycoder.qr.ui

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.CompoundBarcodeView
import kotlinx.coroutines.delay
import net.theluckycoder.qr.viewmodel.MainViewModel
import java.util.*

class ScannerScreen : Screen {

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = viewModel<MainViewModel>()
        val cameraPermissionState = rememberPermissionState(
            android.Manifest.permission.CAMERA
        )

        var roomId by remember { mutableStateOf<String?>(null) }

        if (cameraPermissionState.status.isGranted) {
            if (roomId == null) {
                Scanner {
                    roomId = it
                }
            } else {
                LaunchedEffect(Unit) {
                    viewModel.openDoor(roomId!!)
                    delay(1500)
                    navigator.pop()
                }

                Box(contentAlignment = Alignment.Center) {
                    Text("Se deschide camera...", style = MaterialTheme.typography.headlineLarge)
                }
            }

        } else {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Column(Modifier.align(Alignment.Center)) {
                    val textToShow = if (cameraPermissionState.status.shouldShowRationale) {
                        // If the user has denied the permission but the rationale can be shown,
                        // then gently explain why the app requires this permission
                        "The camera is important for this app. Please grant the permission."
                    } else {
                        // If it's the first time the user lands on this feature, or the user
                        // doesn't want to be asked again for this permission, explain that the
                        // permission is required
                        "Camera permission required for this feature to be available. " +
                                "Please grant the permission"
                    }
                    Text(textToShow)
                    Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                        Text("Request permission")
                    }
                }
            }
        }
    }

    @Composable
    fun Scanner(onDecode: (String) -> Unit) {
        val ctx = LocalContext.current

        val compoundBarcodeView = remember {
            CompoundBarcodeView(ctx).apply {
                val capture = CaptureManager(ctx as Activity, this)
                capture.initializeFromIntent(ctx.intent, null)
                this.setStatusText("")
                capture.decode()
                this.resume()
                this.decodeContinuous { result ->
//                    if (roomId != null) {
//                        return@decodeContinuous
//                    }
                    result.text?.let { barCodeOrQr ->
                        //Do something and when you finish this something
                        //put scanFlag = false to scan another item
                        onDecode(barCodeOrQr)
                    }
                    //If you don't put this scanFlag = false, it will never work again.
                    //you can put a delay over 2 seconds and then scanFlag = false to prevent multiple scanning

                }
            }
        }

        DisposableEffect(
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { compoundBarcodeView },
            )
        ) {
            onDispose {
                compoundBarcodeView.pause()
            }
        }
    }
}