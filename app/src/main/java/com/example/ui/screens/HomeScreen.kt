package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.Lesson
import com.example.ui.CourseUiState
import com.example.ui.components.HebrewRtlText
import com.example.ui.components.HeroBannerCard
import com.example.ui.theme.HebrewTextStyles
import com.example.ui.theme.IvritTheme

@Composable
fun HomeScreen(
    courseState: CourseUiState,
    quizAnswers: Map<Int, Map<Int, Int>>,
    onOpenLesson: (Int) -> Unit,
    onOpenSettings: () -> Unit,
    onRetryLoad: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ext = IvritTheme.colors

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        // Academy Royal Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            ext.headerNavyDark,
                            ext.headerBlueMid,
                            ext.headerBlueBright,
                        ),
                    ),
                ),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 14.dp, start = 16.dp, end = 16.dp),
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "✡ IVRIT PRO ✡",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = MaterialTheme.typography.displaySmall.letterSpacing,
                        modifier = Modifier.align(Alignment.Center),
                    )
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .minimumInteractiveComponentSize()
                            .testTag("settings_button"),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Tune,
                            contentDescription = stringResource(R.string.tts_speed_title),
                            tint = ext.luminousGold,
                        )
                    }
                }
                Text(
                    text = stringResource(R.string.academy_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = ext.headerSubtitle,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    HebrewRtlText(
                        text = stringResource(R.string.academy_motto_he),
                        style = HebrewTextStyles.Motto,
                        color = ext.luminousGold,
                    )
                    Text(
                        text = " • ${stringResource(R.string.academy_motto_uk)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ext.luminousGold,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .align(Alignment.BottomCenter)
                    .background(ext.goldAccent),
            )
        }

        when (courseState) {
            is CourseUiState.Loading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            is CourseUiState.Error -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ),
                        border = BorderStroke(1.5.dp, ext.goldAccent),
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(20.dp),
                        ) {
                            Text(
                                text = courseState.message,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center,
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable(onClick = onRetryLoad)
                                    .minimumInteractiveComponentSize(),
                            ) {
                                Text(
                                    text = "Спробувати знову",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                                )
                            }
                        }
                    }
                }
            }

            is CourseUiState.Success -> {
                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    val columns = when {
                        maxWidth >= 900.dp -> 3
                        maxWidth >= 560.dp -> 2
                        else -> 1
                    }

                    val packRows = remember(courseState.packs, columns) {
                        courseState.packs.map { pack ->
                            pack to pack.lessons.chunked(columns)
                        }
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(
                            horizontal = 16.dp,
                            vertical = 18.dp,
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("home_lesson_list"),
                    ) {
                        item(key = "hero") {
                            Box(modifier = Modifier.widthIn(max = 920.dp)) {
                                HeroBannerCard()
                            }
                        }

                        item(key = "section_title") {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .widthIn(max = 920.dp)
                                    .fillMaxWidth()
                                    .padding(top = 4.dp, bottom = 2.dp),
                            ) {
                                Text(
                                    text = "Програма курсу (${courseState.lessons.size} уроків)",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.ExtraBold,
                                )
                                Text(
                                    text = "${courseState.lessons.sumOf { it.vocabulary.size }} слів",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = ext.goldAccent,
                                )
                            }
                        }

                        packRows.forEach { (pack, chunkedLessons) ->
                            if (courseState.packs.size > 1) {
                                item(key = "pack_header_${pack.packId}") {
                                    Column(
                                        modifier = Modifier
                                            .widthIn(max = 920.dp)
                                            .fillMaxWidth()
                                            .padding(top = 6.dp),
                                    ) {
                                        Text(
                                            text = pack.packTitle,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.ExtraBold,
                                        )
                                        if (pack.packSubtitle.isNotBlank()) {
                                            Text(
                                                text = pack.packSubtitle,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = ext.mutedBronze,
                                            )
                                        }
                                    }
                                }
                            }

                            itemsIndexed(
                                items = chunkedLessons,
                                key = { index, _ -> "pack_${pack.packId}_row_$index" },
                            ) { _, rowLessons ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier
                                        .widthIn(max = 920.dp)
                                        .fillMaxWidth()
                                        .animateItem(),
                                ) {
                                    rowLessons.forEach { lesson ->
                                        val answeredMap = quizAnswers[lesson.id].orEmpty()
                                        val correctCount = answeredMap.count { (qIdx, optIdx) ->
                                            lesson.quiz.getOrNull(qIdx)?.correct == optIdx
                                        }
                                        LessonGridCard(
                                            lesson = lesson,
                                            answeredCount = answeredMap.size,
                                            correctCount = correctCount,
                                            onClick = { onOpenLesson(lesson.id) },
                                            modifier = Modifier.weight(1f),
                                        )
                                    }
                                    repeat(columns - rowLessons.size) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }

                        item(key = "footer") {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .widthIn(max = 920.dp)
                                    .fillMaxWidth()
                                    .padding(top = 12.dp, bottom = 8.dp),
                            ) {
                                HorizontalDivider(color = ext.tanBorder)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "✡ 🕎 ✡",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = ext.goldAccent,
                                )
                                Text(
                                    text = "© 2026 IVRIT PRO",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = ext.mutedBronze,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonGridCard(
    lesson: Lesson,
    answeredCount: Int,
    correctCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ext = IvritTheme.colors
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.5.dp, ext.goldAccent),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("lesson_card_${lesson.id}")
            .clip(RoundedCornerShape(12.dp))
            .clickable(role = Role.Button, onClick = onClick),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .border(1.5.dp, ext.luminousGold, CircleShape),
            ) {
                Text(
                    text = lesson.id.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.ExtraBold,
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (lesson.hebrewTitle.isNotBlank()) {
                    HebrewRtlText(
                        text = lesson.hebrewTitle,
                        style = HebrewTextStyles.VocabWord.copy(
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight,
                        ),
                        color = ext.goldAccent,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Schedule,
                            contentDescription = null,
                            tint = ext.mutedBronze,
                            modifier = Modifier.size(13.dp),
                        )
                        Text(
                            text = lesson.duration,
                            style = MaterialTheme.typography.labelMedium,
                            color = ext.mutedBronze,
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoStories,
                            contentDescription = null,
                            tint = ext.mutedBronze,
                            modifier = Modifier.size(13.dp),
                        )
                        Text(
                            text = "${lesson.vocabulary.size} слів",
                            style = MaterialTheme.typography.labelMedium,
                            color = ext.mutedBronze,
                        )
                    }
                    if (lesson.quiz.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Quiz,
                                contentDescription = null,
                                tint = if (answeredCount == lesson.quiz.size) {
                                    ext.quizCorrectBorder
                                } else {
                                    ext.mutedBronze
                                },
                                modifier = Modifier.size(13.dp),
                            )
                            Text(
                                text = if (answeredCount > 0) {
                                    "$correctCount/${lesson.quiz.size}"
                                } else {
                                    "${lesson.quiz.size} тесту"
                                },
                                style = MaterialTheme.typography.labelMedium,
                                color = if (answeredCount == lesson.quiz.size) {
                                    ext.quizCorrectBorder
                                } else {
                                    ext.mutedBronze
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}
