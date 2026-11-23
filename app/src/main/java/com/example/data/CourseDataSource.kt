package com.example.data

import android.content.Context
import com.example.model.CoursePack
import com.example.model.Lesson
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Abstraction for course content providers so future remote, database,
 * or supplemental asset packs can be plugged in without modifying UI code.
 */
interface CourseDataSource {
    suspend fun loadCoursePacks(): List<CoursePack>
}

internal object CourseJsonParser {
    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val packAdapter = moshi.adapter(CoursePack::class.java)
    private val lessonListType = Types.newParameterizedType(List::class.java, Lesson::class.java)
    private val lessonListAdapter = moshi.adapter<List<Lesson>>(lessonListType)

    fun parsePackOrLessonArray(
        rawJson: String,
        fallbackId: String,
        fallbackTitle: String,
        isBuiltIn: Boolean,
    ): CoursePack {
        val trimmed = rawJson.trim()
        return if (trimmed.startsWith("[")) {
            val lessons = lessonListAdapter.fromJson(trimmed).orEmpty()
            CoursePack(
                packId = fallbackId,
                packTitle = fallbackTitle,
                lessons = lessons,
                isBuiltIn = isBuiltIn,
            )
        } else {
            val parsed = packAdapter.fromJson(trimmed)
                ?: error("Некоректний формат JSON модуля курсу")
            parsed.copy(isBuiltIn = isBuiltIn)
        }
    }

    fun serializePack(pack: CoursePack): String = packAdapter.toJson(pack)
}

class AssetCourseDataSource(private val context: Context) : CourseDataSource {

    override suspend fun loadCoursePacks(): List<CoursePack> = withContext(Dispatchers.IO) {
        val packs = mutableListOf<CoursePack>()
        val courseFiles = runCatching {
            context.assets.list("courses")?.filter { it.endsWith(".json") }?.sorted().orEmpty()
        }.getOrDefault(emptyList())

        for (fileName in courseFiles) {
            val json = context.assets.open("courses/$fileName")
                .bufferedReader(Charsets.UTF_8)
                .use { it.readText() }
            val fallbackId = fileName.removeSuffix(".json")
            packs += CourseJsonParser.parsePackOrLessonArray(
                rawJson = json,
                fallbackId = fallbackId,
                fallbackTitle = fallbackId,
                isBuiltIn = true,
            )
        }

        // Legacy single-file fallback for backward compatibility
        if (packs.isEmpty()) {
            val hasLegacy = runCatching {
                context.assets.list("")?.contains("ivrit_courses.json") == true
            }.getOrDefault(false)
            if (hasLegacy) {
                val json = context.assets.open("ivrit_courses.json")
                    .bufferedReader(Charsets.UTF_8)
                    .use { it.readText() }
                packs += CourseJsonParser.parsePackOrLessonArray(
                    rawJson = json,
                    fallbackId = "academic_hebrew_core",
                    fallbackTitle = "Базовий академічний курс",
                    isBuiltIn = true,
                )
            }
        }

        packs.sortedBy { it.order }
    }
}

class ImportedCourseDataSource(private val context: Context) : CourseDataSource {

    private val importedDir: File
        get() = File(context.filesDir, "imported_courses").apply { mkdirs() }

    override suspend fun loadCoursePacks(): List<CoursePack> = withContext(Dispatchers.IO) {
        importedDir.listFiles { file -> file.isFile && file.extension == "json" }
            ?.sortedBy { it.name }
            ?.mapNotNull { file ->
                runCatching {
                    val raw = file.readText(Charsets.UTF_8)
                    CourseJsonParser.parsePackOrLessonArray(
                        rawJson = raw,
                        fallbackId = file.nameWithoutExtension,
                        fallbackTitle = "Додатковий модуль (${file.nameWithoutExtension})",
                        isBuiltIn = false,
                    )
                }.getOrNull()
            }
            .orEmpty()
    }

    suspend fun saveImportedJson(rawJson: String, fileLabel: String): Result<CoursePack> =
        withContext(Dispatchers.IO) {
            runCatching {
                val safeId = fileLabel
                    .removeSuffix(".json")
                    .replace(Regex("[^a-zA-Z0-9_-]"), "_")
                    .ifBlank { "custom_${System.currentTimeMillis()}" }
                val parsed = CourseJsonParser.parsePackOrLessonArray(
                    rawJson = rawJson,
                    fallbackId = safeId,
                    fallbackTitle = fileLabel.removeSuffix(".json").ifBlank { "Додатковий модуль" },
                    isBuiltIn = false,
                )
                require(parsed.lessons.isNotEmpty()) {
                    "Файл не містить жодного уроку"
                }
                val normalizedPack = parsed.copy(
                    packId = parsed.packId.ifBlank { safeId },
                    isBuiltIn = false,
                )
                val targetFile = File(importedDir, "${normalizedPack.packId}.json")
                targetFile.writeText(CourseJsonParser.serializePack(normalizedPack), Charsets.UTF_8)
                normalizedPack
            }
        }

    suspend fun removeImportedPack(packId: String): Boolean = withContext(Dispatchers.IO) {
        val file = File(importedDir, "$packId.json")
        if (file.exists()) file.delete() else false
    }
}
