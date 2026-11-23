package com.example.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CoursePack(
    val packId: String = "custom_pack",
    val packTitle: String = "Додатковий модуль",
    val packSubtitle: String = "",
    val version: Int = 1,
    val order: Int = 100,
    val lessons: List<Lesson> = emptyList(),
    val isBuiltIn: Boolean = true,
)

@JsonClass(generateAdapter = true)
data class Lesson(
    val id: Int,
    val title: String,
    val hebrewTitle: String = "",
    val duration: String = "90 хвилин",
    val level: String? = null,
    val tags: List<String> = emptyList(),
    val quote: Quote? = null,
    val theory: List<String> = emptyList(),
    val grammarTables: List<GrammarTable> = emptyList(),
    val extraNotes: List<String> = emptyList(),
    val dialogues: List<Dialogue> = emptyList(),
    val phrases: List<Phrase> = emptyList(),
    val vocabulary: List<VocabItem> = emptyList(),
    val quiz: List<QuizQuestion> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class GrammarTable(
    val title: String = "",
    val headers: List<String> = emptyList(),
    val rows: List<List<String>> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class Quote(
    val he: String,
    val tr: String,
    val uk: String,
    val who: String,
)

@JsonClass(generateAdapter = true)
data class Dialogue(
    val title: String? = null,
    val speaker1: String = "",
    val trans1: String = "",
    val translat1: String = "",
    val speaker2: String? = null,
    val trans2: String? = null,
    val translat2: String? = null,
)

@JsonClass(generateAdapter = true)
data class Phrase(
    val he: String,
    val tr: String,
    val uk: String,
)

@JsonClass(generateAdapter = true)
data class VocabItem(
    val he: String,
    val tr: String,
    val uk: String,
)

@JsonClass(generateAdapter = true)
data class QuizQuestion(
    val question: String,
    val hebrew: String? = null,
    val options: List<String> = emptyList(),
    val correct: Int = 0,
)

data class DictionaryEntry(
    val lessonId: Int,
    val lessonTitle: String,
    val item: VocabItem,
)
