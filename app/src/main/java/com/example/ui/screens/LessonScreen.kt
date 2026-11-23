package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.material.icons.rounded.HighlightOff
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Tune
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.Dialogue
import com.example.model.GrammarTable
import com.example.model.Lesson
import com.example.model.Phrase
import com.example.model.QuizQuestion
import com.example.model.Quote
import com.example.model.VocabItem
import com.example.ui.LessonSection
import com.example.ui.components.AcademySectionHeader
import com.example.ui.components.HebrewAudioButton
import com.example.ui.components.HebrewRtlText
import com.example.ui.theme.HebrewTextStyles
import com.example.ui.theme.IvritTheme

private data class DialogueBlock(
    val headerTitle: String?,
    val dialogue: Dialogue,
    val index: Int,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    lesson: Lesson,
    allLessons: List<Lesson>,
    selectedSection: LessonSection,
    quizAnswersForLesson: Map<Int, Int>,
    activeSpeakingText: String?,
    showTransliteration: Boolean,
    onSelectSection: (LessonSection) -> Unit,
    onSelectLesson: (Int) -> Unit,
    onSelectQuizOption: (Int, Int) -> Unit,
    onResetQuiz: () -> Unit,
    onSpeakHebrew: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ext = IvritTheme.colors
    val listState = rememberLazyListState()
    val switcherState = rememberLazyListState()

    LaunchedEffect(lesson.id) {
        listState.scrollToItem(0)
        val idx = allLessons.indexOfFirst { it.id == lesson.id }
        if (idx >= 0) {
            switcherState.animateScrollToItem(idx)
        }
    }

    val filteredTheory = remember(lesson.theory) {
        lesson.theory.filterNot { it.trimStart().startsWith("📜") }
    }

    val dialogueBlocks = remember(lesson.dialogues) {
        var lastTitle = ""
        lesson.dialogues.mapIndexed { index, d ->
            val title = d.title?.takeIf { it.isNotBlank() && it != lastTitle }
            if (d.title != null && d.title.isNotBlank()) {
                lastTitle = d.title
            }
            DialogueBlock(headerTitle = title, dialogue = d, index = index)
        }
    }

    val answeredCount = quizAnswersForLesson.size
    val correctCount = remember(lesson.quiz, quizAnswersForLesson) {
        quizAnswersForLesson.count { (qIdx, optIdx) ->
            lesson.quiz.getOrNull(qIdx)?.correct == optIdx
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = "Урок ${lesson.id}. ${lesson.title}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (lesson.hebrewTitle.isNotBlank()) {
                        HebrewRtlText(
                            text = lesson.hebrewTitle,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = HebrewTextStyles.VocabWord.fontFamily,
                                fontWeight = FontWeight.Bold,
                            ),
                            color = ext.luminousGold,
                        )
                    }
                }
            },
            navigationIcon = {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("lesson_back_button"),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(R.string.back_home),
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("lesson_settings_button"),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Tune,
                        contentDescription = stringResource(R.string.tts_speed_title),
                        tint = ext.luminousGold,
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = ext.headerBlueMid,
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White,
                actionIconContentColor = ext.luminousGold,
            ),
        )

        // Quick 1..15 Lesson Switcher Strip
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column {
                LazyRow(
                    state = switcherState,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(allLessons, key = { it.id }) { item ->
                        val isCurrent = item.id == lesson.id
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isCurrent) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surface
                            },
                            border = BorderStroke(
                                width = 1.5.dp,
                                color = ext.goldAccent,
                            ),
                            modifier = Modifier
                                .testTag("switcher_lesson_${item.id}")
                                .clip(RoundedCornerShape(10.dp))
                                .clickable(role = Role.Tab) { onSelectLesson(item.id) },
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isCurrent) ext.luminousGold else MaterialTheme.colorScheme.primary,
                                        ),
                                ) {
                                    Text(
                                        text = item.id.toString(),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (isCurrent) ext.headerNavyDark else Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                    )
                                }
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                }
                HorizontalDivider(color = ext.tanBorder)

                // Section Filter Chips
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(LessonSection.entries) { section ->
                        val selected = selectedSection == section
                        FilterChip(
                            selected = selected,
                            onClick = { onSelectSection(section) },
                            label = {
                                Text(
                                    text = section.label,
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selected,
                                borderColor = ext.goldAccent,
                            ),
                            modifier = Modifier.testTag("section_chip_${section.name.lowercase()}"),
                        )
                    }
                }
                HorizontalDivider(thickness = 1.5.dp, color = ext.goldAccent)
            }
        }

        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .testTag("lesson_content_list"),
        ) {
            // Lesson Hero Summary Card
            item(key = "lesson_header_${lesson.id}") {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(2.dp, ext.goldAccent),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier
                        .widthIn(max = 820.dp)
                        .fillMaxWidth(),
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(18.dp),
                    ) {
                        Text(
                            text = "Урок ${lesson.id}. ${lesson.title}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold,
                        )
                        if (lesson.hebrewTitle.isNotBlank()) {
                            HebrewRtlText(
                                text = lesson.hebrewTitle,
                                style = HebrewTextStyles.LessonTitle,
                                color = ext.goldAccent,
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Schedule,
                                    contentDescription = null,
                                    tint = ext.mutedBronze,
                                    modifier = Modifier.size(16.dp),
                                )
                                Text(
                                    text = lesson.duration,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = ext.mutedBronze,
                                )
                            }
                            lesson.level?.takeIf { it.isNotBlank() }?.let { lvl ->
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = ext.theorySurface,
                                    border = BorderStroke(1.dp, ext.goldAccent),
                                ) {
                                    Text(
                                        text = lvl,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    )
                                }
                            }
                        }
                        lesson.quote?.let { quote ->
                            if (selectedSection == LessonSection.ALL || selectedSection == LessonSection.THEORY) {
                                Spacer(modifier = Modifier.height(4.dp))
                                QuoteCard(
                                    quote = quote,
                                    isSpeaking = activeSpeakingText == quote.he.trim(),
                                    showTransliteration = showTransliteration,
                                    onSpeak = { onSpeakHebrew(quote.he) },
                                )
                            }
                        }
                    }
                }
            }

            // Theory Section
            if ((selectedSection == LessonSection.ALL || selectedSection == LessonSection.THEORY) &&
                filteredTheory.isNotEmpty()
            ) {
                item(key = "header_theory") {
                    AcademySectionHeader(
                        title = stringResource(R.string.section_theory),
                        icon = Icons.Rounded.AutoAwesome,
                        modifier = Modifier
                            .widthIn(max = 820.dp)
                            .padding(top = 4.dp),
                    )
                }
                itemsIndexed(
                    items = filteredTheory,
                    key = { idx, _ -> "theory_${lesson.id}_$idx" },
                ) { _, paragraph ->
                    TheoryCard(
                        text = paragraph,
                        modifier = Modifier.widthIn(max = 820.dp),
                    )
                }
                itemsIndexed(
                    items = lesson.grammarTables,
                    key = { idx, _ -> "gtable_${lesson.id}_$idx" },
                ) { _, table ->
                    GrammarTableCard(
                        table = table,
                        modifier = Modifier.widthIn(max = 820.dp),
                    )
                }
                itemsIndexed(
                    items = lesson.extraNotes,
                    key = { idx, _ -> "note_${lesson.id}_$idx" },
                ) { _, note ->
                    TheoryCard(
                        text = note,
                        modifier = Modifier.widthIn(max = 820.dp),
                    )
                }
            }

            // Dialogues Section
            if ((selectedSection == LessonSection.ALL || selectedSection == LessonSection.DIALOGUES) &&
                dialogueBlocks.isNotEmpty()
            ) {
                item(key = "header_dialogues") {
                    AcademySectionHeader(
                        title = stringResource(R.string.section_dialogues),
                        icon = Icons.Rounded.Forum,
                        modifier = Modifier
                            .widthIn(max = 820.dp)
                            .padding(top = 4.dp),
                    )
                }
                items(
                    items = dialogueBlocks,
                    key = { "dlg_${lesson.id}_${it.index}" },
                ) { block ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .widthIn(max = 820.dp)
                            .fillMaxWidth(),
                    ) {
                        block.headerTitle?.let { topic ->
                            Text(
                                text = topic,
                                style = MaterialTheme.typography.titleMedium,
                                color = ext.goldAccent,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 6.dp),
                            )
                        }
                        DialogueCard(
                            dialogue = block.dialogue,
                            activeSpeakingText = activeSpeakingText,
                            showTransliteration = showTransliteration,
                            onSpeak = onSpeakHebrew,
                            lessonId = lesson.id,
                            index = block.index,
                        )
                    }
                }
            }

            // Phrases Section
            if ((selectedSection == LessonSection.ALL || selectedSection == LessonSection.PHRASES) &&
                lesson.phrases.isNotEmpty()
            ) {
                item(key = "header_phrases") {
                    AcademySectionHeader(
                        title = stringResource(R.string.section_phrases),
                        icon = Icons.Rounded.ChatBubbleOutline,
                        modifier = Modifier
                            .widthIn(max = 820.dp)
                            .padding(top = 4.dp),
                    )
                }
                itemsIndexed(
                    items = lesson.phrases,
                    key = { idx, _ -> "phrase_${lesson.id}_$idx" },
                ) { idx, phrase ->
                    PhraseCard(
                        phrase = phrase,
                        isSpeaking = activeSpeakingText == phrase.he.trim(),
                        showTransliteration = showTransliteration,
                        onSpeak = { onSpeakHebrew(phrase.he) },
                        testTag = "phrase_audio_${lesson.id}_$idx",
                        modifier = Modifier.widthIn(max = 820.dp),
                    )
                }
            }

            // Vocabulary Section
            if ((selectedSection == LessonSection.ALL || selectedSection == LessonSection.VOCABULARY) &&
                lesson.vocabulary.isNotEmpty()
            ) {
                item(key = "header_vocab") {
                    AcademySectionHeader(
                        title = "${stringResource(R.string.section_vocabulary)} (${lesson.vocabulary.size})",
                        icon = Icons.AutoMirrored.Rounded.MenuBook,
                        modifier = Modifier
                            .widthIn(max = 820.dp)
                            .padding(top = 4.dp),
                    )
                }
                itemsIndexed(
                    items = lesson.vocabulary,
                    key = { idx, _ -> "vocab_${lesson.id}_$idx" },
                ) { idx, vocab ->
                    VocabCardRow(
                        item = vocab,
                        isSpeaking = activeSpeakingText == vocab.he.trim(),
                        showTransliteration = showTransliteration,
                        onSpeak = { onSpeakHebrew(vocab.he) },
                        testTag = "vocab_audio_${lesson.id}_$idx",
                        modifier = Modifier.widthIn(max = 820.dp),
                    )
                }
            }

            // Control Quiz Section
            if ((selectedSection == LessonSection.ALL || selectedSection == LessonSection.QUIZ) &&
                lesson.quiz.isNotEmpty()
            ) {
                item(key = "header_quiz") {
                    AcademySectionHeader(
                        title = stringResource(R.string.section_quiz),
                        icon = Icons.Rounded.Quiz,
                        modifier = Modifier
                            .widthIn(max = 820.dp)
                            .padding(top = 4.dp),
                    )
                }

                if (answeredCount > 0) {
                    item(key = "quiz_score_${lesson.id}") {
                        QuizScoreBanner(
                            correctCount = correctCount,
                            answeredCount = answeredCount,
                            totalCount = lesson.quiz.size,
                            onResetQuiz = onResetQuiz,
                            modifier = Modifier.widthIn(max = 820.dp),
                        )
                    }
                }

                itemsIndexed(
                    items = lesson.quiz,
                    key = { idx, _ -> "quiz_${lesson.id}_$idx" },
                ) { qIdx, question ->
                    QuizQuestionCard(
                        questionIndex = qIdx,
                        question = question,
                        selectedOption = quizAnswersForLesson[qIdx],
                        activeSpeakingText = activeSpeakingText,
                        onSpeakHebrew = onSpeakHebrew,
                        onSelectOption = { optIdx -> onSelectQuizOption(qIdx, optIdx) },
                        modifier = Modifier.widthIn(max = 820.dp),
                    )
                }
            }

            // Bottom Navigation Buttons
            item(key = "bottom_nav_${lesson.id}") {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .widthIn(max = 820.dp)
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp),
                ) {
                    OutlinedButton(
                        onClick = onNavigateBack,
                        border = BorderStroke(1.5.dp, ext.goldAccent),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("bottom_back_home_button"),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.back_home),
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    val nextLesson = allLessons.firstOrNull { it.id == lesson.id + 1 }
                    if (nextLesson != null) {
                        Button(
                            onClick = { onSelectLesson(nextLesson.id) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                            ),
                            border = BorderStroke(1.dp, ext.goldAccent),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("next_lesson_button"),
                        ) {
                            Text(
                                text = "Урок ${nextLesson.id}",
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                contentDescription = stringResource(R.string.next_lesson),
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuoteCard(
    quote: Quote,
    isSpeaking: Boolean,
    showTransliteration: Boolean,
    onSpeak: () -> Unit,
) {
    val ext = IvritTheme.colors
    Card(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.5.dp, ext.goldAccent),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(ext.quoteGradientStart, ext.quoteGradientEnd),
                    ),
                )
                .padding(14.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
            ) {
                HebrewAudioButton(
                    textToSpeak = quote.he,
                    isSpeaking = isSpeaking,
                    onClick = onSpeak,
                    testTag = "quote_audio_button",
                )
                Spacer(modifier = Modifier.width(8.dp))
                HebrewRtlText(
                    text = quote.he,
                    style = HebrewTextStyles.QuoteDisplay,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f, fill = false),
                )
            }
            if (showTransliteration) {
                Text(
                    text = quote.tr,
                    style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                    color = ext.mutedBronze,
                    textAlign = TextAlign.Center,
                )
            }
            Text(
                text = "«${quote.uk}»",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "— ${quote.who}",
                style = MaterialTheme.typography.labelMedium,
                color = ext.mutedBronze,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun TheoryCard(
    text: String,
    modifier: Modifier = Modifier,
) {
    val ext = IvritTheme.colors
    Surface(
        shape = RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp),
        color = ext.theorySurface,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary),
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
            )
        }
    }
}

@Composable
private fun DialogueCard(
    dialogue: Dialogue,
    activeSpeakingText: String?,
    showTransliteration: Boolean,
    onSpeak: (String) -> Unit,
    lessonId: Int,
    index: Int,
) {
    val ext = IvritTheme.colors
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ext.dialogueSurface),
        border = BorderStroke(1.dp, ext.tanBorder),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp),
            ) {
                DialogueTurnRow(
                    hebrew = dialogue.speaker1,
                    transliteration = dialogue.trans1,
                    translation = dialogue.translat1,
                    isSpeaking = activeSpeakingText == dialogue.speaker1.trim(),
                    showTransliteration = showTransliteration,
                    onSpeak = { onSpeak(dialogue.speaker1) },
                    testTag = "dlg_${lessonId}_${index}_s1",
                )

                val speaker2 = dialogue.speaker2
                if (!speaker2.isNullOrBlank()) {
                    DashedTanDivider(color = ext.tanBorder)
                    DialogueTurnRow(
                        hebrew = speaker2,
                        transliteration = dialogue.trans2.orEmpty(),
                        translation = dialogue.translat2.orEmpty(),
                        isSpeaking = activeSpeakingText == speaker2.trim(),
                        showTransliteration = showTransliteration,
                        onSpeak = { onSpeak(speaker2) },
                        testTag = "dlg_${lessonId}_${index}_s2",
                    )
                }
            }
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(ext.goldAccent),
            )
        }
    }
}

@Composable
private fun DialogueTurnRow(
    hebrew: String,
    transliteration: String,
    translation: String,
    isSpeaking: Boolean,
    showTransliteration: Boolean,
    onSpeak: () -> Unit,
    testTag: String,
) {
    val ext = IvritTheme.colors
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            HebrewAudioButton(
                textToSpeak = hebrew,
                isSpeaking = isSpeaking,
                onClick = onSpeak,
                testTag = testTag,
            )
            HebrewRtlText(
                text = hebrew,
                style = HebrewTextStyles.DialogueLine,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f),
            )
        }
        val subtitle = buildString {
            if (showTransliteration && transliteration.isNotBlank()) {
                append(transliteration)
                if (translation.isNotBlank()) append(" — ")
            }
            append(translation)
        }
        if (subtitle.isNotBlank()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = ext.mutedBronze,
            )
        }
    }
}

@Composable
private fun DashedTanDivider(color: Color) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(2.dp),
    ) {
        drawLine(
            color = color,
            start = Offset(0f, size.height / 2f),
            end = Offset(size.width, size.height / 2f),
            strokeWidth = 1.5.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f),
        )
    }
}

@Composable
private fun PhraseCard(
    phrase: Phrase,
    isSpeaking: Boolean,
    showTransliteration: Boolean,
    onSpeak: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier,
) {
    val ext = IvritTheme.colors
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ext.dialogueSurface),
        border = BorderStroke(1.dp, ext.tanBorder),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    HebrewAudioButton(
                        textToSpeak = phrase.he,
                        isSpeaking = isSpeaking,
                        onClick = onSpeak,
                        testTag = testTag,
                    )
                    HebrewRtlText(
                        text = phrase.he,
                        style = HebrewTextStyles.DialogueLine,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f),
                    )
                }
                if (showTransliteration) {
                    Text(
                        text = phrase.tr,
                        style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                        color = ext.mutedBronze,
                    )
                }
                Text(
                    text = phrase.uk,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                )
            }
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(ext.goldAccent),
            )
        }
    }
}

@Composable
private fun VocabCardRow(
    item: VocabItem,
    isSpeaking: Boolean,
    showTransliteration: Boolean,
    onSpeak: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier,
) {
    val ext = IvritTheme.colors
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, ext.tanBorder),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
        ) {
            HebrewAudioButton(
                textToSpeak = item.he,
                isSpeaking = isSpeaking,
                onClick = onSpeak,
                testTag = testTag,
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.uk,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                )
                if (showTransliteration) {
                    Text(
                        text = item.tr,
                        style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                        color = ext.mutedBronze,
                    )
                }
            }
            HebrewRtlText(
                text = item.he,
                style = HebrewTextStyles.VocabWord,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.End,
            )
        }
    }
}

@Composable
private fun QuizScoreBanner(
    correctCount: Int,
    answeredCount: Int,
    totalCount: Int,
    onResetQuiz: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ext = IvritTheme.colors
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ext.theorySurface),
        border = BorderStroke(1.5.dp, ext.goldAccent),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Column {
                Text(
                    text = "Результат: $correctCount / $totalCount",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = "Відповідей: $answeredCount з $totalCount",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ext.mutedBronze,
                )
            }
            OutlinedButton(
                onClick = onResetQuiz,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("reset_quiz_button"),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.reset_quiz),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

@Composable
private fun QuizQuestionCard(
    questionIndex: Int,
    question: QuizQuestion,
    selectedOption: Int?,
    activeSpeakingText: String?,
    onSpeakHebrew: (String) -> Unit,
    onSelectOption: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val ext = IvritTheme.colors
    val hasAnswered = selectedOption != null

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, ext.tanBorder),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
        ) {
            Text(
                text = "${questionIndex + 1}. ${question.question}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
            )

            val hebrewPrompt = question.hebrew
            if (!hebrewPrompt.isNullOrBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    HebrewRtlText(
                        text = hebrewPrompt,
                        style = HebrewTextStyles.DialogueLine,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    HebrewAudioButton(
                        textToSpeak = hebrewPrompt,
                        isSpeaking = activeSpeakingText == hebrewPrompt.trim(),
                        onClick = { onSpeakHebrew(hebrewPrompt) },
                        testTag = "quiz_audio_$questionIndex",
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                question.options.forEachIndexed { optionIndex, optionText ->
                    val isSelected = selectedOption == optionIndex
                    val isCorrectOption = optionIndex == question.correct

                    val targetBg = when {
                        !hasAnswered -> MaterialTheme.colorScheme.surface
                        isCorrectOption -> ext.quizCorrectBg
                        isSelected && !isCorrectOption -> ext.quizWrongBg
                        else -> MaterialTheme.colorScheme.surface
                    }
                    val targetBorder = when {
                        !hasAnswered -> ext.tanBorder
                        isCorrectOption -> ext.quizCorrectBorder
                        isSelected && !isCorrectOption -> ext.quizWrongBorder
                        else -> ext.tanBorder
                    }
                    val targetTextColor = when {
                        !hasAnswered -> MaterialTheme.colorScheme.onSurface
                        isCorrectOption -> ext.quizCorrectText
                        isSelected && !isCorrectOption -> ext.quizWrongText
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }

                    val bgColor by animateColorAsState(targetBg, label = "opt_bg")
                    val borderColor by animateColorAsState(targetBorder, label = "opt_border")

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = bgColor,
                        border = BorderStroke(1.5.dp, borderColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .minimumInteractiveComponentSize()
                            .testTag("quiz_${questionIndex}_opt_$optionIndex")
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(
                                enabled = !hasAnswered,
                                role = Role.Button,
                                onClick = { onSelectOption(optionIndex) },
                            ),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 11.dp),
                        ) {
                            Text(
                                text = optionText,
                                style = MaterialTheme.typography.bodyLarge,
                                color = targetTextColor,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f),
                            )
                            if (hasAnswered && isCorrectOption) {
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = "Правильна відповідь",
                                    tint = ext.quizCorrectBorder,
                                    modifier = Modifier.size(20.dp),
                                )
                            } else if (hasAnswered && isSelected) {
                                Icon(
                                    imageVector = Icons.Rounded.HighlightOff,
                                    contentDescription = "Неправильна відповідь",
                                    tint = ext.quizWrongBorder,
                                    modifier = Modifier.size(20.dp),
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
private fun GrammarTableCard(
    table: GrammarTable,
    modifier: Modifier = Modifier,
) {
    val ext = IvritTheme.colors
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, ext.goldAccent),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (table.title.isNotBlank()) {
                Text(
                    text = table.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                )
            }
            if (table.headers.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    table.headers.forEach { header ->
                        Text(
                            text = header,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
            table.rows.forEachIndexed { rIdx, row ->
                if (rIdx > 0) {
                    HorizontalDivider(color = ext.tanBorder)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    row.forEach { cell ->
                        Text(
                            text = cell,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}
