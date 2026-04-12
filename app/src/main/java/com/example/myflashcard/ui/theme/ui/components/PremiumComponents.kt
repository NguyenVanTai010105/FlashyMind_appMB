package com.example.flashcardapp.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.flashcardapp.ui.theme.*

@Composable
fun GlobalGlassBackground(
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainAppGradient)
    ) {
        // LIQUID SHAPES (Nâng cấp hệ thống loang màu dứt khoát)
        Box(
            modifier = Modifier
                .offset(x = (-80).dp, y = 150.dp)
                .size(300.dp)
                .blur(100.dp)
                .background(LiquidPink.copy(alpha = 0.15f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 100.dp, y = 50.dp)
                .size(250.dp)
                .blur(120.dp)
                .background(LiquidBlue.copy(alpha = 0.15f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 100.dp)
                .size(400.dp)
                .blur(150.dp)
                .background(LiquidPurple.copy(alpha = 0.1f), CircleShape)
        )
        
        content()
    }
}

@Composable
fun PastelGlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 30.dp,
    horizontalPadding: Dp = 12.dp,
    verticalPadding: Dp = 12.dp,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color = GlassWhite,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .padding(horizontal = horizontalPadding, vertical = verticalPadding)
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = Color.Black.copy(alpha = 0.4f),
                spotColor = Color.Black.copy(alpha = 0.4f)
            )
            .clip(RoundedCornerShape(cornerRadius))
            .let { 
                if (onClick != null) it.clickable { onClick() } else it
            }
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = GlassBorder,
                shape = RoundedCornerShape(cornerRadius)
            )
    ) {
        content()
    }
}

@Composable
fun StreakLineChart(
    points: List<Float>, // Values from 0f to 1f
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxWidth().height(80.dp)) {
        val width = size.width
        val height = size.height
        val spacing = width / (points.size - 1)
        
        val path = Path()
        points.forEachIndexed { index, value ->
            val x = index * spacing
            val y = height - (value * height)
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        
        drawPath(
            path = path,
            color = Color.White.copy(alpha = 0.8f),
            style = Stroke(width = 3.dp.toPx())
        )
        
        // Draw points
        points.forEachIndexed { index, value ->
            val x = index * spacing
            val y = height - (value * height)
            drawCircle(
                color = if (index % 2 == 0) PastelBlueLight else SoftPink,
                radius = 5.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}

@Composable
fun FloatingPillNav(
    tabs: List<TabItem>,
    currentTab: String,
    onTabSelected: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 24.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .height(64.dp)
                .fillMaxWidth()
                .shadow(25.dp, RoundedCornerShape(32.dp), spotColor = Color.Black.copy(alpha = 0.2f)),
            color = GlassWhiteHeavy,
            shape = RoundedCornerShape(32.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEach { tab ->
                    val isSelected = currentTab == tab.id
                    
                    IconButton(onClick = { onTabSelected(tab.id) }) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label,
                            tint = if (isSelected) LiquidBlue else SoftText.copy(alpha = 0.3f),
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StackedFlashcard(
    word: String,
    meaning: String,
    imageUri: String? = null,
    phonetic: String = "",
    isFlipped: Boolean = false,
    onFlip: () -> Unit = {},
    onSpeak: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val rotationState = animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(600),
        label = "cardFlip"
    )
    val rotation = rotationState.value

    Box(
        modifier = modifier
            .padding(16.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 16f * density
            }
            .clickable { onFlip() },
        contentAlignment = Alignment.Center
    ) {
        if (rotation.compareTo(90f) <= 0) {
            // FRONT Side
            PastelGlassCard(
                modifier = Modifier.fillMaxSize(),
                cornerRadius = 32.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // IMAGE (Above word)
                    if (!imageUri.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.05f))
                        ) {
                            AsyncImage(
                                model = imageUri,
                                contentDescription = "Flashcard Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                        Spacer(Modifier.height(24.dp))
                    }

                    Text(
                        text = word,
                        style = Typography.displayLarge,
                        color = SoftText,
                        textAlign = TextAlign.Center
                    )
                    
                    if (phonetic.isNotBlank()) {
                        Text(
                            text = phonetic,
                            style = Typography.bodyLarge,
                            color = PastelBlue,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    // Listen Button
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(PastelBlue.copy(alpha = 0.1f))
                            .border(1.dp, PastelBlue.copy(alpha = 0.2f), CircleShape)
                            .clickable { onSpeak() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Hear",
                            tint = PastelBlue,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        } else {
            // BACK Side
            PastelGlassCard(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f },
                cornerRadius = 32.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "DỊCH NGHĨA",
                        style = Typography.labelSmall,
                        color = PastelBlue,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = meaning,
                        style = Typography.displayMedium,
                        textAlign = TextAlign.Center,
                        color = SoftText,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun BackgroundTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 100.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Text(
            text = text,
            style = BackgroundTitleStyle,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun PastelIconButton(
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = GlassWhite
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(containerColor)
            .border(1.dp, GlassBorder, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        icon()
    }
}

@Composable
fun GlassyProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 10.dp,
    color: Color = PastelBlue
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.2f))
            .border(0.5.dp, Color.White.copy(alpha = 0.2f), CircleShape)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .fillMaxHeight()
                .background(
                    Brush.horizontalGradient(
                        listOf(color.copy(alpha = 0.7f), color)
                    )
                )
        )
    }
}

data class TabItem(val id: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable
fun PastelButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = PastelBlue,
    textColor: Color = Color.White
) {
    val alpha = if (enabled) 1f else 0.5f
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .shadow(
                if (enabled) 10.dp else 0.dp, 
                RoundedCornerShape(30.dp), 
                spotColor = containerColor.copy(alpha = 0.4f)
            )
            .clip(RoundedCornerShape(30.dp))
            .background(containerColor.copy(alpha = alpha))
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            style = Typography.displaySmall,
            fontWeight = FontWeight.Bold
        )
    }
}
