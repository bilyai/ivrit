package com.example.data

import android.content.Context
import android.net.Uri
import com.example.model.CoursePack
import com.example.model.Lesson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LessonRepository(
    private val context: Context,
    private val assetSource: AssetCourseDataSource = AssetCourseDataSource(context),
    private val importedSource: ImportedCourseDataSource = ImportedCourseDataSource(context),
    private val extraSources: List<CourseDataSource> = emptyList(),
) {

    @Volatile
    private var cachedPacks: List<CoursePack>? = null

    suspend fun getCoursePacks(forceRefresh: Boolean = false): List<CoursePack> {
        if (!forceRefresh) {
            cachedPacks?.let { return it }
        }
        return withContext(Dispatchers.IO) {
            val allSources = listOf(assetSource, importedSource) + extraSources
            val loadedPacks = allSources.flatMap { it.loadCoursePacks() }
                .sortedWith(compareBy<CoursePack> { !it.isBuiltIn }.thenBy { it.order })
            cachedPacks = loadedPacks
            loadedPacks
        }
    }

    suspend fun getLessons(forceRefresh: Boolean = false): List<Lesson> {
        return getCoursePacks(forceRefresh)
            .flatMap { it.lessons }
            .distinctBy { it.id }
            .sortedBy { it.id }
    }

    suspend fun importPackFromUri(uri: Uri): Result<CoursePack> = withContext(Dispatchers.IO) {
        runCatching {
            val rawJson = context.contentResolver.openInputStream(uri)
                ?.bufferedReader(Charsets.UTF_8)
                ?.use { it.readText() }
                ?: error("Не вдалося прочитати вибраний файл")
            val label = uri.lastPathSegment?.substringAfterLast('/') ?: "custom_module"
            val result = importedSource.saveImportedJson(rawJson, label).getOrThrow()
            cachedPacks = null
            result
        }
    }

    suspend fun removeImportedPack(packId: String): Boolean {
        val removed = importedSource.removeImportedPack(packId)
        if (removed) {
            cachedPacks = null
        }
        return removed
    }
}
