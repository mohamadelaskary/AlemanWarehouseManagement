package net.gbs.gbswarehouse.ui.splash

import android.graphics.Color as AndroidColor
import android.net.Uri
import android.util.Log
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.Screens
import net.gbs.gbswarehouse.core.update.UpdateState
import net.gbs.gbswarehouse.ui.theme.AppTheme
import net.gbs.gbswarehouse.ui.theme.LocalDarkTheme

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isDark = LocalDarkTheme.current
    val videoResId = remember(isDark) {
        val blackId = context.resources.getIdentifier("splash_video_black", "raw", context.packageName)
        val whiteId = context.resources.getIdentifier("splash_video", "raw", context.packageName)
        val defaultId = context.resources.getIdentifier("splash_video", "raw", context.packageName)
        when {
            isDark && blackId != 0 -> blackId
            !isDark && whiteId != 0 -> whiteId
            else -> defaultId
        }
    }

    var isPlayingVideo by remember { mutableStateOf(videoResId != 0) }
    val updateState by viewModel.appUpdateManager.updateState.collectAsState()
    val colors = AppTheme.colors

    val backgroundColor = if (isDark) Color.Black else Color.White
    val textColor = if (isDark) Color.White else Color.Black

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest { event ->
            Log.d("SplashScreen", "Navigation event received: $event. Waiting 2 seconds before navigating...")
            kotlinx.coroutines.delay(2000L)
            when (event) {
                is SplashNavigationEvent.NavigateToLogin -> {
                    navController.navigate(Screens.LoginScreen.route) {
                        popUpTo(Screens.SplashScreen.route) { inclusive = true }
                    }
                }
                is SplashNavigationEvent.NavigateToUpdate -> {
                    navController.navigate(Screens.UpdateScreen.route) {
                        popUpTo(Screens.SplashScreen.route) { inclusive = true }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isPlayingVideo && videoResId != 0) {
            AndroidView(
                factory = { ctx ->
                    VideoView(ctx).apply {
                        if (!isDark) {
                            setBackgroundColor(AndroidColor.WHITE)
                        } else {
                            setBackgroundColor(AndroidColor.BLACK)
                        }
                        setVideoURI(Uri.parse("android.resource://${ctx.packageName}/$videoResId"))
                        setOnPreparedListener { mp ->
                            Log.d("SplashScreen", "Video prepared, duration=${mp.duration}, starting playback...")
                            mp.start()
                            // Stop 1 second before the end (min 500ms) to prevent logo disappearance
                            val stopDelay = (mp.duration - 1000).coerceAtLeast(500)
                            postDelayed({
                                if (isPlayingVideo) {
                                    Log.d("SplashScreen", "Stopping video 1s before end.")
                                    try {
                                        if (mp.isPlaying) mp.pause()
                                    } catch (_: Exception) {}
                                    isPlayingVideo = false
                                    viewModel.onVideoCompleted()
                                }
                            }, stopDelay.toLong())
                        }
                        setOnCompletionListener {
                            Log.d("SplashScreen", "Video playback completed.")
                            if (isPlayingVideo) {
                                isPlayingVideo = false
                                viewModel.onVideoCompleted()
                            }
                        }
                        setOnErrorListener { _, what, extra ->
                            Log.e("SplashScreen", "VideoView error: what=$what, extra=$extra.")
                            if (isPlayingVideo) {
                                isPlayingVideo = false
                                viewModel.onVideoCompleted()
                            }
                            true
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            )
        } else {
            if (!isPlayingVideo && videoResId == 0) {
                LaunchedEffect(Unit) {
                    Log.d("SplashScreen", "No video resource found.")
                    viewModel.onVideoCompleted()
                }
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Progress bar below video
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = colors.green
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Checking label below progress bar
        Text(
            text = when (updateState) {
                is UpdateState.Checking -> stringResource(R.string.checking_for_application_updates)
                else -> stringResource(R.string.loading_gbs_warehouse)
            },
            style = MaterialTheme.typography.bodyLarge,
            color = textColor
        )
    }
}
