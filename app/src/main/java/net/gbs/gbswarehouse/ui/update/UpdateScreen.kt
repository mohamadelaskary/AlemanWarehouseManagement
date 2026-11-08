package net.gbs.gbswarehouse.ui.update

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.core.update.UpdateState

@Composable
fun UpdateScreen(
    navController: NavController,
    viewModel: UpdateViewModel = hiltViewModel()
) {
    val state by viewModel.updateState.collectAsState()
    val appVersion = viewModel.appUpdateManager.getLatestAppVersion()
    val context = LocalContext.current
    val activity = context as? Activity

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.application_update_available),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (appVersion != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Latest Version: ${appVersion.latestVersionName ?: "N/A"} (Code: ${appVersion.latestVersionCode})", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Release Notes:", style = MaterialTheme.typography.titleMedium)
                        Text(text = appVersion.releaseNotes ?: "No release notes provided.", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (state) {
                is UpdateState.UpdateAvailable, is UpdateState.Idle, is UpdateState.NoUpdate -> {
                    Button(
                        onClick = { viewModel.startDownload() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(R.string.download_update))
                    }
                }
                is UpdateState.Downloading -> {
                    val downloadState = state as UpdateState.Downloading
                    val percent = (downloadState.progress * 100).toInt()

                    Text(
                        text = "$percent%",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { downloadState.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Downloaded: ${downloadState.downloadedBytes / 1024 / 1024} MB / ${downloadState.totalBytes / 1024 / 1024} MB",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                is UpdateState.Validating -> {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Validating APK checksum (SHA-256)...", style = MaterialTheme.typography.bodyMedium)
                }
                is UpdateState.DownloadCompleted -> {
                    Button(
                        onClick = { if (activity != null) viewModel.installApk(activity) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Install Update")
                    }
                }
                is UpdateState.Installing -> {
                    Text(text = "Launching installer...", style = MaterialTheme.typography.bodyLarge)
                }
                is UpdateState.Error -> {
                    val errorMsg = (state as UpdateState.Error).message
                    Text(text = "Error: $errorMsg", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.startDownload() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Retry Download")
                    }
                }
                is UpdateState.Checking -> {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Checking for updates...", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
