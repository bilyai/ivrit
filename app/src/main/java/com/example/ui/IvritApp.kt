package com.example.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.R
import com.example.ui.components.TtsSettingsSheet
import com.example.ui.screens.DictionaryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LessonScreen

private object Routes {
    const val COURSE = "course"
    const val DICTIONARY = "dictionary"
    const val LESSON = "lesson/{lessonId}"

    fun lesson(id: Int) = "lesson/$id"
}

@Composable
fun IvritApp(
    viewModel: IvritViewModel = viewModel(),
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val courseState by viewModel.courseState.collectAsStateWithLifecycle()
    val ttsState by viewModel.ttsState.collectAsStateWithLifecycle()
    val userNotice by viewModel.userNotice.collectAsStateWithLifecycle()
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    val selectedSection by viewModel.selectedSection.collectAsStateWithLifecycle()
    val quizAnswers by viewModel.quizAnswers.collectAsStateWithLifecycle()
    val dictionaryQuery by viewModel.dictionaryQuery.collectAsStateWithLifecycle()
    val dictionaryLessonFilter by viewModel.dictionaryLessonFilter.collectAsStateWithLifecycle()
    val filteredDictionary by viewModel.filteredDictionary.collectAsStateWithLifecycle()

    var showSettingsSheet by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val importPackLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.importCoursePack(uri)
        }
    }

    LaunchedEffect(ttsState.noticeEventId) {
        val message = ttsState.noticeText
        if (ttsState.noticeEventId > 0L && !message.isNullOrBlank()) {
            snackbarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(userNotice?.id) {
        val message = userNotice?.message
        if (!message.isNullOrBlank()) {
            snackbarHostState.showSnackbar(message)
        }
    }

    val isTopLevelRoute = currentRoute == Routes.COURSE || currentRoute == Routes.DICTIONARY

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavigationRail = maxWidth >= 600.dp

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                if (isTopLevelRoute && !useNavigationRail) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 4.dp,
                    ) {
                        NavigationBarItem(
                            selected = currentRoute == Routes.COURSE,
                            onClick = {
                                if (currentRoute != Routes.COURSE) {
                                    navController.navigate(Routes.COURSE) {
                                        popUpTo(Routes.COURSE) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (currentRoute == Routes.COURSE) {
                                        Icons.Rounded.School
                                    } else {
                                        Icons.Outlined.School
                                    },
                                    contentDescription = stringResource(R.string.nav_course),
                                )
                            },
                            label = { Text(stringResource(R.string.nav_course)) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            ),
                            modifier = Modifier.testTag("nav_course_tab"),
                        )
                        NavigationBarItem(
                            selected = currentRoute == Routes.DICTIONARY,
                            onClick = {
                                if (currentRoute != Routes.DICTIONARY) {
                                    navController.navigate(Routes.DICTIONARY) {
                                        popUpTo(Routes.COURSE) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (currentRoute == Routes.DICTIONARY) {
                                        Icons.AutoMirrored.Rounded.MenuBook
                                    } else {
                                        Icons.AutoMirrored.Outlined.MenuBook
                                    },
                                    contentDescription = stringResource(R.string.nav_dictionary),
                                )
                            },
                            label = { Text(stringResource(R.string.nav_dictionary)) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            ),
                            modifier = Modifier.testTag("nav_dictionary_tab"),
                        )
                    }
                }
            },
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                if (isTopLevelRoute && useNavigationRail) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ) {
                        NavigationRailItem(
                            selected = currentRoute == Routes.COURSE,
                            onClick = {
                                if (currentRoute != Routes.COURSE) {
                                    navController.navigate(Routes.COURSE) {
                                        popUpTo(Routes.COURSE) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (currentRoute == Routes.COURSE) {
                                        Icons.Rounded.School
                                    } else {
                                        Icons.Outlined.School
                                    },
                                    contentDescription = stringResource(R.string.nav_course),
                                )
                            },
                            label = { Text(stringResource(R.string.nav_course)) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            ),
                            modifier = Modifier.testTag("rail_course_tab"),
                        )
                        NavigationRailItem(
                            selected = currentRoute == Routes.DICTIONARY,
                            onClick = {
                                if (currentRoute != Routes.DICTIONARY) {
                                    navController.navigate(Routes.DICTIONARY) {
                                        popUpTo(Routes.COURSE) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (currentRoute == Routes.DICTIONARY) {
                                        Icons.AutoMirrored.Rounded.MenuBook
                                    } else {
                                        Icons.AutoMirrored.Outlined.MenuBook
                                    },
                                    contentDescription = stringResource(R.string.nav_dictionary),
                                )
                            },
                            label = { Text(stringResource(R.string.nav_dictionary)) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            ),
                            modifier = Modifier.testTag("rail_dictionary_tab"),
                        )
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = Routes.COURSE,
                    modifier = Modifier.weight(1f),
                ) {
                    composable(Routes.COURSE) {
                        HomeScreen(
                            courseState = courseState,
                            quizAnswers = quizAnswers,
                            onOpenLesson = { lessonId ->
                                viewModel.resetSectionFilter()
                                navController.navigate(Routes.lesson(lessonId))
                            },
                            onOpenSettings = { showSettingsSheet = true },
                            onRetryLoad = { viewModel.loadLessons() },
                        )
                    }

                    composable(Routes.DICTIONARY) {
                        val allLessons = (courseState as? CourseUiState.Success)?.lessons.orEmpty()
                        DictionaryScreen(
                            lessons = allLessons,
                            entries = filteredDictionary,
                            query = dictionaryQuery,
                            selectedLessonFilter = dictionaryLessonFilter,
                            activeSpeakingText = ttsState.activeText,
                            showTransliteration = preferences.showTransliteration,
                            onQueryChange = viewModel::updateDictionaryQuery,
                            onSelectLessonFilter = viewModel::selectDictionaryLessonFilter,
                            onSpeakHebrew = viewModel::speakHebrew,
                            onOpenLesson = { lessonId ->
                                viewModel.resetSectionFilter()
                                navController.navigate(Routes.lesson(lessonId))
                            },
                            onOpenSettings = { showSettingsSheet = true },
                        )
                    }

                    composable(
                        route = Routes.LESSON,
                        arguments = listOf(navArgument("lessonId") { type = NavType.IntType }),
                    ) { entry ->
                        val lessonId = entry.arguments?.getInt("lessonId") ?: 1
                        val allLessons = (courseState as? CourseUiState.Success)?.lessons.orEmpty()
                        val lesson = allLessons.firstOrNull { it.id == lessonId } ?: allLessons.firstOrNull()
                        if (lesson != null) {
                            LessonScreen(
                                lesson = lesson,
                                allLessons = allLessons,
                                selectedSection = selectedSection,
                                quizAnswersForLesson = quizAnswers[lesson.id].orEmpty(),
                                activeSpeakingText = ttsState.activeText,
                                showTransliteration = preferences.showTransliteration,
                                onSelectSection = viewModel::selectSection,
                                onSelectLesson = { targetId ->
                                    if (targetId != lesson.id) {
                                        viewModel.stopSpeaking()
                                        navController.navigate(Routes.lesson(targetId)) {
                                            popUpTo(Routes.COURSE)
                                            launchSingleTop = true
                                        }
                                    }
                                },
                                onSelectQuizOption = { qIdx, optIdx ->
                                    viewModel.selectQuizOption(lesson.id, qIdx, optIdx)
                                },
                                onResetQuiz = { viewModel.resetQuiz(lesson.id) },
                                onSpeakHebrew = viewModel::speakHebrew,
                                onNavigateBack = {
                                    viewModel.stopSpeaking()
                                    navController.popBackStack(Routes.COURSE, inclusive = false)
                                },
                                onOpenSettings = { showSettingsSheet = true },
                            )
                        }
                    }
                }
            }
        }
    }

    if (showSettingsSheet) {
        val loadedPacks = (courseState as? CourseUiState.Success)?.packs.orEmpty()
        TtsSettingsSheet(
            speechRate = preferences.speechRate,
            showTransliteration = preferences.showTransliteration,
            coursePacks = loadedPacks,
            onSelectSpeechRate = viewModel::setSpeechRate,
            onToggleTransliteration = viewModel::setShowTransliteration,
            onImportJsonClick = {
                importPackLauncher.launch(arrayOf("application/json", "text/plain", "*/*"))
            },
            onRemovePack = viewModel::removeImportedPack,
            onDismiss = { showSettingsSheet = false },
        )
    }
}
