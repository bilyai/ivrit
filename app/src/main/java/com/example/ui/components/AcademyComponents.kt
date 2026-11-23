package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.FolderCopy
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.CoursePack
import com.example.ui.theme.HebrewTextStyles
import com.example.ui.theme.IvritTheme
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun StarOfDavidCanvas(
    modifier: Modifier = Modifier,
    strokeColor: Color = IvritTheme.colors.luminousGold,
    ringColor: Color = Color.White.copy(alpha = 0.35f),
) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val outerRadius = size.minDimension * 0.45f
        val starRadius = size.minDimension * 0.34f
        val strokeWidth = size.minDimension * 0.045f

        drawCircle(
            color = ringColor,
            radius = outerRadius,
            center = center,
            style = Stroke(width = 1.5.dp.toPx()),
        )

        fun buildTrianglePath(startAngleDeg: Double): Path {
            return Path().apply {
                for (i in 0 until 3) {
                    val angleRad = Math.toRadians(startAngleDeg + i * 120.0)
                    val x = center.x + (starRadius * cos(angleRad)).toFloat()
                    val y = center.y + (starRadius * sin(angleRad)).toFloat()
                    if (i == 0) moveTo(x, y) else lineTo(x, y)
                }
                close()
            }
        }

        val strokeStyle = Stroke(width = strokeWidth, join = StrokeJoin.Round)
        drawPath(buildTrianglePath(-90.0), color = strokeColor, style = strokeStyle)
        drawPath(buildTrianglePath(90.0), color = strokeColor, style = strokeStyle)
    }
}

@Composable
fun HebrewRtlText(
    text: String,
    style: TextStyle,
    color: Color = MaterialTheme.colorScheme.primary,
    textAlign: TextAlign = TextAlign.Start,
    modifier: Modifier = Modifier,
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Text(
            text = text,
            style = style,
            color = color,
            textAlign = textAlign,
            modifier = modifier,
        )
    }
}

@Composable
fun HebrewAudioButton(
    textToSpeak: String,
    isSpeaking: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonSize: Dp = 38.dp,
    testTag: String = "audio_button",
) {
    val listenDesc = stringResource(R.string.listen_hebrew)
    val bgColor by animateColorAsState(
        targetValue = if (isSpeaking) {
            MaterialTheme.colorScheme.primary
        } else {
            IvritTheme.colors.audioButtonBg
        },
        label = "audio_bg",
    )
    val iconColor by animateColorAsState(
        targetValue = if (isSpeaking) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            IvritTheme.colors.audioButtonContent
        },
        label = "audio_icon",
    )
    val scale by animateFloatAsState(
        targetValue = if (isSpeaking) 1.08f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "audio_scale",
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .minimumInteractiveComponentSize()
            .testTag(testTag)
            .semantics { contentDescription = "$listenDesc: $textToSpeak" }
            .clip(CircleShape)
            .clickable(role = Role.Button, onClick = onClick),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(buttonSize)
                .scale(scale)
                .clip(CircleShape)
                .background(bgColor)
                .border(
                    width = 1.5.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                ),
        ) {
            Icon(
                imageVector = if (isSpeaking) {
                    Icons.Rounded.GraphicEq
                } else {
                    Icons.AutoMirrored.Rounded.VolumeUp
                },
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
fun HeroBannerCard(
    modifier: Modifier = Modifier,
) {
    val ext = IvritTheme.colors
    Card(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, ext.goldAccent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp),
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_academy_hero),
                contentDescription = stringResource(R.string.hero_uk_invite),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                ext.headerNavyDark.copy(alpha = 0.72f),
                                ext.headerBlueMid.copy(alpha = 0.85f),
                                ext.headerNavyDark.copy(alpha = 0.94f),
                            ),
                        ),
                    ),
            )

            // Tallit-inspired top & bottom white stripes
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(Color.White.copy(alpha = 0.82f)),
                )
                Spacer(modifier = Modifier.height(5.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(Color.White.copy(alpha = 0.50f)),
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(Color.White.copy(alpha = 0.50f)),
                )
                Spacer(modifier = Modifier.height(5.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(Color.White.copy(alpha = 0.82f)),
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp, vertical = 24.dp),
            ) {
                StarOfDavidCanvas(
                    modifier = Modifier.size(96.dp),
                    strokeColor = ext.luminousGold,
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f),
                ) {
                    HebrewRtlText(
                        text = stringResource(R.string.hero_hebrew_greeting),
                        style = HebrewTextStyles.HeroDisplay,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                    )
                    HebrewRtlText(
                        text = stringResource(R.string.hero_hebrew_invite),
                        style = HebrewTextStyles.LessonTitle,
                        color = ext.luminousGold,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.hero_uk_invite),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = stringResource(R.string.hero_meta),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.86f),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
fun AcademySectionHeader(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    val ext = IvritTheme.colors
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp),
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.ExtraBold,
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        HorizontalDivider(
            thickness = 2.dp,
            color = ext.goldAccent,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TtsSettingsSheet(
    speechRate: Float,
    showTransliteration: Boolean,
    coursePacks: List<CoursePack>,
    onSelectSpeechRate: (Float) -> Unit,
    onToggleTransliteration: (Boolean) -> Unit,
    onImportJsonClick: () -> Unit,
    onRemovePack: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val ext = IvritTheme.colors
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Speed,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = stringResource(R.string.tts_speed_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            val speeds = listOf(
                0.70f to "0.70x Повільно",
                0.85f to "0.85x Стандарт",
                1.00f to "1.00x Швидко",
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                speeds.forEach { (rate, label) ->
                    val selected = kotlin.math.abs(speechRate - rate) < 0.05f
                    FilterChip(
                        selected = selected,
                        onClick = { onSelectSpeechRate(rate) },
                        label = { Text(label, style = MaterialTheme.typography.labelMedium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selected,
                            borderColor = ext.goldAccent,
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("speed_chip_${rate}"),
                    )
                }
            }

            HorizontalDivider(color = ext.tanBorder)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .minimumInteractiveComponentSize(),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Translate,
                        contentDescription = null,
                        tint = ext.goldAccent,
                    )
                    Column {
                        Text(
                            text = "Показувати транскрипцію",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = "Вимкніть, щоб тренувати читання івриту без підказок",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Switch(
                    checked = showTransliteration,
                    onCheckedChange = onToggleTransliteration,
                    modifier = Modifier.testTag("transliteration_switch"),
                )
            }

            HorizontalDivider(color = ext.tanBorder)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.FolderCopy,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "Модулі курсу та доповнення",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            coursePacks.forEach { pack ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = ext.theorySurface,
                    border = BorderStroke(1.dp, ext.tanBorder),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = pack.packTitle,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "${pack.lessons.size} уроків · ${if (pack.isBuiltIn) "вбудований" else "імпортований"}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = ext.mutedBronze,
                            )
                        }
                        if (!pack.isBuiltIn) {
                            IconButton(
                                onClick = { onRemovePack(pack.packId) },
                                modifier = Modifier.minimumInteractiveComponentSize(),
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.DeleteOutline,
                                    contentDescription = "Видалити модуль",
                                    tint = MaterialTheme.colorScheme.error,
                                )
                            }
                        }
                    }
                }
            }

            Button(
                onClick = onImportJsonClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
                border = BorderStroke(1.dp, ext.goldAccent),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("import_module_button"),
            ) {
                Icon(
                    imageVector = Icons.Rounded.AddCircleOutline,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Доповнити курс (.json файл)",
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
