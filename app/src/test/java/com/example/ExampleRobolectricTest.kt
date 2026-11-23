package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.test.core.app.ApplicationProvider
import com.example.data.ImportedCourseDataSource
import com.example.data.LessonRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun `app name matches IVRIT PRO`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("IVRIT PRO", appName)
    }

    @Test
    fun `lesson repository parses modular asset pack and supports supplemental expansion`() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val importedSource = ImportedCourseDataSource(context)
        val repository = LessonRepository(context, importedSource = importedSource)

        val basePacks = repository.getCoursePacks(forceRefresh = true)
        assertEquals(1, basePacks.size)
        val lessons = repository.getLessons()
        assertEquals(15, lessons.size)
        assertEquals(300, lessons.sumOf { it.vocabulary.size })
        assertTrue(lessons.all { it.quiz.isNotEmpty() })

        // Verify supplemental JSON expansion works cleanly
        val supplementalJson = """
            {
              "packId": "test_expansion",
              "packTitle": "Розмовний модуль",
              "order": 2,
              "lessons": [
                {
                  "id": 16,
                  "title": "Аеропорт і подорожі",
                  "hebrewTitle": "נְמַל תְּעוּפָה",
                  "level": "Бет (A2)",
                  "vocabulary": [{"he": "מָטוֹס", "tr": "матос", "uk": "літак"}],
                  "quiz": [{"question": "Що означає מָטוֹס?", "options": ["Літак", "Потяг"], "correct": 0}]
                }
              ]
            }
        """.trimIndent()
        val imported = importedSource.saveImportedJson(supplementalJson, "test_expansion.json")
        assertTrue(imported.isSuccess)
        val expandedLessons = repository.getLessons(forceRefresh = true)
        assertEquals(16, expandedLessons.size)
        assertEquals("Аеропорт і подорожі", expandedLessons.last().title)

        // Clean up imported test pack
        assertTrue(repository.removeImportedPack("test_expansion"))
    }

    @Test
    fun `home screen renders and navigates to lesson and dictionary`() {
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithTag("home_lesson_list").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("home_lesson_list")
            .performScrollToNode(hasTestTag("lesson_card_1"))
        composeRule.onNodeWithTag("lesson_card_1").assertIsDisplayed().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("lesson_back_button").assertIsDisplayed()
        composeRule.onNodeWithTag("section_chip_quiz").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("lesson_content_list")
            .performScrollToNode(hasTestTag("quiz_0_opt_0"))
        composeRule.onNodeWithTag("quiz_0_opt_0").assertIsDisplayed().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("lesson_content_list")
            .performScrollToNode(hasTestTag("reset_quiz_button"))
        composeRule.onNodeWithTag("reset_quiz_button").assertIsDisplayed()
        composeRule.onNodeWithTag("lesson_back_button").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("nav_dictionary_tab").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("dictionary_search_input").assertIsDisplayed()
    }
}
