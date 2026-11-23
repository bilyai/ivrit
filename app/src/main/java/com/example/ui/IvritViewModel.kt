package com.example.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.HebrewTtsManager
import com.example.audio.TtsState
import com.example.data.LessonRepository
import com.example.data.PreferencesRepository
import com.example.data.UserPreferences
import com.example.model.CoursePack
import com.example.model.DictionaryEntry
import com.example.model.Lesson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface CourseUiState {
    data object Loading : CourseUiState
    data class Success(
        val packs: List<CoursePack>,
        val lessons: List<Lesson>,
    ) : CourseUiState

    data class Error(val message: String) : CourseUiState
}

enum class LessonSection(val label: String) {
    ALL("Усе"),
    THEORY("Теорія"),
    DIALOGUES("Діалоги"),
    PHRASES("Фрази"),
    VOCABULARY("Словник"),
    QUIZ("Тест"),
}

data class UserNotice(
    val id: Long,
    val message: String,
)

class IvritViewModel(application: Application) : AndroidViewModel(application) {

    private val lessonRepository = LessonRepository(application)
    private val preferencesRepository = PreferencesRepository(application)
    private val ttsManager = HebrewTtsManager(application)

    private val _courseState = MutableStateFlow<CourseUiState>(CourseUiState.Loading)
    val courseState: StateFlow<CourseUiState> = _courseState.asStateFlow()

    val ttsState: StateFlow<TtsState> = ttsManager.state

    private val _userNotice = MutableStateFlow<UserNotice?>(null)
    val userNotice: StateFlow<UserNotice?> = _userNotice.asStateFlow()

    val preferences: StateFlow<UserPreferences> = preferencesRepository.preferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserPreferences(),
        )

    private val _selectedSection = MutableStateFlow(LessonSection.ALL)
    val selectedSection: StateFlow<LessonSection> = _selectedSection.asStateFlow()

    // Map<lessonId, Map<questionIndex, selectedOptionIndex>>
    private val _quizAnswers = MutableStateFlow<Map<Int, Map<Int, Int>>>(emptyMap())
    val quizAnswers: StateFlow<Map<Int, Map<Int, Int>>> = _quizAnswers.asStateFlow()

    private val _dictionaryQuery = MutableStateFlow("")
    val dictionaryQuery: StateFlow<String> = _dictionaryQuery.asStateFlow()

    private val _dictionaryLessonFilter = MutableStateFlow<Int?>(null)
    val dictionaryLessonFilter: StateFlow<Int?> = _dictionaryLessonFilter.asStateFlow()

    val filteredDictionary: StateFlow<List<DictionaryEntry>> = combine(
        _courseState,
        _dictionaryQuery,
        _dictionaryLessonFilter,
    ) { state, query, lessonFilter ->
        val lessons = (state as? CourseUiState.Success)?.lessons.orEmpty()
        val normalizedQuery = query.trim().lowercase()
        lessons.asSequence()
            .filter { lessonFilter == null || it.id == lessonFilter }
            .flatMap { lesson ->
                lesson.vocabulary.asSequence().map { vocab ->
                    DictionaryEntry(
                        lessonId = lesson.id,
                        lessonTitle = lesson.title,
                        item = vocab,
                    )
                }
            }
            .filter { entry ->
                if (normalizedQuery.isEmpty()) {
                    true
                } else {
                    entry.item.uk.lowercase().contains(normalizedQuery) ||
                        entry.item.tr.lowercase().contains(normalizedQuery) ||
                        entry.item.he.contains(normalizedQuery)
                }
            }
            .toList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    init {
        loadLessons()
    }

    fun loadLessons(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _courseState.value = CourseUiState.Loading
            runCatching {
                val packs = lessonRepository.getCoursePacks(forceRefresh = forceRefresh)
                val lessons = packs.flatMap { it.lessons }
                    .distinctBy { it.id }
                    .sortedBy { it.id }
                packs to lessons
            }.onSuccess { (packs, lessons) ->
                _courseState.value = CourseUiState.Success(packs = packs, lessons = lessons)
            }.onFailure { throwable ->
                _courseState.value = CourseUiState.Error(
                    throwable.localizedMessage ?: "Не вдалося завантажити курс івриту"
                )
            }
        }
    }

    fun importCoursePack(uri: Uri) {
        viewModelScope.launch {
            lessonRepository.importPackFromUri(uri)
                .onSuccess { pack ->
                    loadLessons(forceRefresh = true)
                    emitNotice("Додано модуль «${pack.packTitle}» (${pack.lessons.size} ур.)")
                }
                .onFailure { error ->
                    emitNotice("Помилка імпорту: ${error.localizedMessage ?: "некоректний JSON"}")
                }
        }
    }

    fun removeImportedPack(packId: String) {
        viewModelScope.launch {
            if (lessonRepository.removeImportedPack(packId)) {
                loadLessons(forceRefresh = true)
                emitNotice("Додатковий модуль видалено")
            }
        }
    }

    private fun emitNotice(message: String) {
        _userNotice.value = UserNotice(id = System.currentTimeMillis(), message = message)
    }

    fun selectSection(section: LessonSection) {
        _selectedSection.value = section
    }

    fun resetSectionFilter() {
        _selectedSection.value = LessonSection.ALL
    }

    fun selectQuizOption(lessonId: Int, questionIndex: Int, optionIndex: Int) {
        _quizAnswers.update { current ->
            val lessonMap = current[lessonId].orEmpty().toMutableMap()
            if (lessonMap.containsKey(questionIndex)) return@update current
            lessonMap[questionIndex] = optionIndex
            current + (lessonId to lessonMap)
        }
    }

    fun resetQuiz(lessonId: Int) {
        _quizAnswers.update { current ->
            current - lessonId
        }
    }

    fun updateDictionaryQuery(query: String) {
        _dictionaryQuery.value = query
    }

    fun selectDictionaryLessonFilter(lessonId: Int?) {
        _dictionaryLessonFilter.value = lessonId
    }

    fun speakHebrew(text: String) {
        ttsManager.speak(text, preferences.value.speechRate)
    }

    fun stopSpeaking() {
        ttsManager.stop()
    }

    fun setSpeechRate(rate: Float) {
        viewModelScope.launch {
            preferencesRepository.setSpeechRate(rate)
        }
    }

    fun setShowTransliteration(show: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setShowTransliteration(show)
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}
