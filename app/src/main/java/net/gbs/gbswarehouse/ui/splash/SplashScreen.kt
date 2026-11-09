package net.gbs.gbswarehouse.ui.splash
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap

import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer

import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.Screens
import androidx.compose.animation.core.*
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration

import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.gbs.gbswarehouse.ui.theme.AppTheme

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val checkFinished by viewModel.checkFinished.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest { event ->
            Log.d("SplashScreen", "Navigation event received: $event.")
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

    val context = LocalContext.current
    val symbolResId = remember(context) {
        val id = context.resources.getIdentifier("gbs_symbol", "drawable", context.packageName)
        if (id != 0) id else R.drawable.gbs_symbol
    }
    val textResId = remember(context) {
        val id = context.resources.getIdentifier("gbs_text", "drawable", context.packageName)
        if (id != 0) id else R.drawable.gbs_text
    }

    GbsSplashScreen(
        isApiFinished = checkFinished,
        symbolPainter = painterResource(id = symbolResId),
        textPainter = painterResource(id = textResId),
        onEntranceAnimationCompleted = {
            viewModel.onEntranceAnimationCompleted()
        }
    )
}



//@Composable
//fun GbsSplashScreen(
//    modifier: Modifier = Modifier,
//    isApiFinished: Boolean,
//    symbolPainter: Painter,
//    textPainter: Painter,
//    onEntranceAnimationCompleted: () -> Unit
//) {
//    var entranceFinished by remember {
//        mutableStateOf(false)
//    }
//
//    var minimumTimeFinished by remember {
//        mutableStateOf(false)
//    }
//
//    var callbackTriggered by remember {
//        mutableStateOf(false)
//    }
//
//    // Minimum Splash display time
//    LaunchedEffect(Unit) {
//        delay(4500)
//        minimumTimeFinished = true
//    }
//
//    BoxWithConstraints(
//        modifier = modifier
//            .fillMaxSize()
//            .background(AppTheme.colors.bg),
//        contentAlignment = Alignment.Center
//    ) {
//        val density = LocalDensity.current
//
//        val screenWidthPx = with(density) {
//            maxWidth.toPx()
//        }
//
//        // Symbol starts outside the screen from the LEFT
//        val symbolStart = -(screenWidthPx / 2f + with(density) {
//            150.dp.toPx()
//        })
//
//        // Text starts outside the screen from the RIGHT
//        val textStart = screenWidthPx / 2f + with(density) {
//            150.dp.toPx()
//        }
//
//        val symbolOffset = remember(screenWidthPx) {
//            Animatable(symbolStart)
//        }
//
//        val textOffset = remember(screenWidthPx) {
//            Animatable(textStart)
//        }
//
//        // Logo entrance animation
//        LaunchedEffect(Unit) {
//            coroutineScope {
//                launch {
//                    symbolOffset.animateTo(
//                        targetValue = 0f,
//                        animationSpec = tween(
//                            durationMillis = 1300,
//                            easing = FastOutSlowInEasing
//                        )
//                    )
//                }
//
//                launch {
//                    textOffset.animateTo(
//                        targetValue = 0f,
//                        animationSpec = tween(
//                            durationMillis = 1300,
//                            easing = FastOutSlowInEasing
//                        )
//                    )
//                }
//            }
//
//            entranceFinished = true
//        }
//
//        // Loading state
//        val shouldShowLoading =
//            entranceFinished &&
//                    !(isApiFinished && minimumTimeFinished)
//
//        // Shimmer animation
//        val infiniteTransition = rememberInfiniteTransition(
//            label = "gbs_loading"
//        )
//
//        val shimmerPosition by infiniteTransition.animateFloat(
//            initialValue = -1.5f,
//            targetValue = 1.5f,
//            animationSpec = infiniteRepeatable(
//                animation = tween(
//                    durationMillis = 1400,
//                    easing = LinearEasing
//                ),
//                repeatMode = RepeatMode.Restart
//            ),
//            label = "shimmer_position"
//        )
//
//        // Subtle breathing animation
//        val breathingScale by infiniteTransition.animateFloat(
//            initialValue = 1f,
//            targetValue = 1.025f,
//            animationSpec = infiniteRepeatable(
//                animation = tween(
//                    durationMillis = 1100,
//                    easing = FastOutSlowInEasing
//                ),
//                repeatMode = RepeatMode.Reverse
//            ),
//            label = "breathing"
//        )
//
//        // Completion callback - exactly once
//        LaunchedEffect(
//            isApiFinished,
//            minimumTimeFinished,
//            entranceFinished
//        ) {
//            if (
//                isApiFinished &&
//                minimumTimeFinished &&
//                entranceFinished &&
//                !callbackTriggered
//            ) {
//                callbackTriggered = true
//
//                delay(700)
//
//                onEntranceAnimationCompleted()
//            }
//        }
//
//        // Logo
//        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 24.dp)
//                    .graphicsLayer {
//                        val scale = if (shouldShowLoading) {
//                            breathingScale
//                        } else {
//                            1f
//                        }
//
//                        scaleX = scale
//                        scaleY = scale
//                    },
//                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.Bottom
//            ) {
//
//                // Symbol enters from LEFT
//                Image(
//                    painter = symbolPainter,
//                    contentDescription = "GBS Symbol",
//                    contentScale = ContentScale.Fit,
//                    modifier = Modifier
//                        .wrapContentSize()
//                        .graphicsLayer {
//                            translationX = symbolOffset.value
//                        }
//                )
//
//                // No space between the two images
//                Spacer(
//                    modifier = Modifier.width(0.dp)
//                )
//
//                // Text enters from RIGHT
//                Image(
//                    painter = textPainter,
//                    contentDescription = "GBS Text",
//                    contentScale = ContentScale.Fit,
//                    modifier = Modifier
//                        .wrapContentSize()
//                        .graphicsLayer {
//                            translationX = textOffset.value
//                        }
//                )
//            }
//
//            // Shimmer overlay over the logo
//            if (shouldShowLoading) {
//                Box(
//                    modifier = Modifier
//                        .align(Alignment.Center)
//                        .width(220.dp)
//                        .height(75.dp)
//                        .graphicsLayer {
//                            translationX = shimmerPosition * 220.dp.toPx()
//                        }
//                        .background(
//                            brush = Brush.horizontalGradient(
//                                colors = listOf(
//                                    Color.Transparent,
//                                    Color.White.copy(alpha = 0.05f),
//                                    Color.White.copy(alpha = 0.65f),
//                                    Color.White.copy(alpha = 0.05f),
//                                    Color.Transparent
//                                )
//                            )
//                        )
//                )
//            }
//        }
//    }
//}




@Composable
fun GbsSplashScreen(
    modifier: Modifier = Modifier,
    isApiFinished: Boolean,
    symbolPainter: Painter,
    textPainter: Painter,
    onEntranceAnimationCompleted: () -> Unit
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val screenWidthPx = with(density) {
        configuration.screenWidthDp.dp.toPx()
    }

    var entranceFinished by remember { mutableStateOf(false) }
    var minimumTimeFinished by remember { mutableStateOf(false) }
    var callbackTriggered by remember { mutableStateOf(false) }

    val symbolOffset = remember(screenWidthPx) {
        Animatable(-(screenWidthPx / 2 + with(density) { 150.dp.toPx() }))
    }

    val textOffset = remember(screenWidthPx) {
        Animatable(screenWidthPx / 2 + with(density) { 150.dp.toPx() })
    }

    // Logo entrance animation
    LaunchedEffect(Unit) {
        coroutineScope {
            launch {
                symbolOffset.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = 1300,
                        easing = FastOutSlowInEasing
                    )
                )
            }

            launch {
                textOffset.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = 1300,
                        easing = FastOutSlowInEasing
                    )
                )
            }
        }

        entranceFinished = true
    }

    // Minimum splash duration
    LaunchedEffect(Unit) {
        delay(4500)
        minimumTimeFinished = true
    }

    // Finish splash after API and entrance animation
    LaunchedEffect(isApiFinished, minimumTimeFinished, entranceFinished) {
        if (
            isApiFinished &&
            minimumTimeFinished &&
            entranceFinished &&
            !callbackTriggered
        ) {
            delay(700)
            callbackTriggered = true
            onEntranceAnimationCompleted()
        }
    }

    // Border progress animation
    val infiniteTransition = rememberInfiniteTransition(label = "borderAnimations")

    val progressPosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1800,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "progressPosition"
    )

    // Diagonal shimmer animation
    val shimmerPosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1600,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerPosition"
    )

    val shouldShowLoading =
        entranceFinished && !(isApiFinished && minimumTimeFinished)

    Box(
        modifier = modifier.fillMaxSize().background(AppTheme.colors.bg),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .wrapContentSize()
                .padding(35.dp),
            contentAlignment = Alignment.Center
        ) {

            // Animated border
            if (shouldShowLoading) {
                Canvas(
                    modifier = Modifier.matchParentSize()
                ) {
                    val strokeWidth = 2.dp.toPx()
                    val cornerRadius = 18.dp.toPx()

                    val borderRect = Rect(
                        left = strokeWidth / 2,
                        top = strokeWidth / 2,
                        right = size.width - strokeWidth / 2,
                        bottom = size.height - strokeWidth / 2
                    )

                    val roundedPath = Path().apply {
                        addRoundRect(
                            RoundRect(
                                rect = borderRect,
                                cornerRadius = CornerRadius(
                                    cornerRadius,
                                    cornerRadius
                                )
                            )
                        )
                    }

                    // Base border
                    drawPath(
                        path = roundedPath,
                        color = Color(0xFF3A86FF).copy(alpha = 0.16f),
                        style = Stroke(width = strokeWidth)
                    )

                    // Moving progress segment
                    val pathMeasure = PathMeasure()
                    pathMeasure.setPath(roundedPath, false)

                    val pathLength = pathMeasure.length
                    val segmentLength = pathLength * 0.25f

                    val startDistance = progressPosition * pathLength
                    val endDistance = startDistance + segmentLength

                    val progressPath = Path()

                    if (endDistance <= pathLength) {
                        pathMeasure.getSegment(
                            startDistance,
                            endDistance,
                            progressPath,
                            true
                        )
                    } else {
                        pathMeasure.getSegment(
                            startDistance,
                            pathLength,
                            progressPath,
                            true
                        )

                        val remaining = endDistance - pathLength
                        val secondPath = Path()

                        pathMeasure.getSegment(
                            0f,
                            remaining,
                            secondPath,
                            true
                        )

                        progressPath.addPath(secondPath)
                    }

                    // Progress glow
                    drawPath(
                        path = progressPath,
                        color = Color(0xFF3A86FF).copy(alpha = 0.25f),
                        style = Stroke(
                            width = strokeWidth * 3.5f,
                            cap = StrokeCap.Round
                        )
                    )

                    // Progress line
                    drawPath(
                        path = progressPath,
                        color = Color(0xFF55A5FF),
                        style = Stroke(
                            width = strokeWidth,
                            cap = StrokeCap.Round
                        )
                    )

                    // Diagonal shimmer - border only
                    val diagonal = size.height

                    val shimmerStartX =
                        -diagonal + shimmerPosition * (size.width + diagonal)

                    val shimmerBrush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.9f),
                            Color.Transparent
                        ),
                        start = Offset(
                            x = shimmerStartX,
                            y = size.height
                        ),
                        end = Offset(
                            x = shimmerStartX + diagonal,
                            y = 0f
                        )
                    )

                    drawPath(
                        path = roundedPath,
                        brush = shimmerBrush,
                        style = Stroke(
                            width = strokeWidth * 2.5f,
                            cap = StrokeCap.Round
                        )
                    )
                }
            }

            // Logo images
            CompositionLocalProvider(
                LocalLayoutDirection provides LayoutDirection.Ltr
            ) {
                Row(
                    modifier = Modifier
                        .wrapContentSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Image(
                        painter = symbolPainter,
                        contentDescription = "GBS Symbol",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .wrapContentSize()
                            .graphicsLayer {
                                translationX = symbolOffset.value
                            }
                    )
                    Spacer(modifier = Modifier.width(0.dp))
                    Image(
                        painter = textPainter,
                        contentDescription = "GBS Text",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .wrapContentSize()
                            .graphicsLayer {
                                translationX = textOffset.value
                            }
                    )
                }
            }
        }
    }
}
