package org.jetbrains.plugins.feature.suggester.suggesters.fileStructure

import junit.framework.TestCase
import org.jetbrains.plugins.feature.suggester.NoSuggestion
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class FileStructureSuggesterPythonTest : FileStructureSuggesterTest() {

    override val testingCodeFileName: String = "PythonCodeExample.py"

    @Test
    override fun `testFind field and get suggestion`() {
        val fromOffset = logicalPositionToOffset(16, 0)
        performFindInFileAction("field", fromOffset)
        focusEditor()

        testInvokeLater {
            assertSuggestedCorrectly()
        }
    }

    @Test
    fun `testFind global variable and get suggestion`() {
        val fromOffset = logicalPositionToOffset(0, 0)
        performFindInFileAction("bcd", fromOffset)
        focusEditor()

        testInvokeLater {
            assertSuggestedCorrectly()
        }
    }

    @Test
    override fun `testFind method and get suggestion`() {
        val fromOffset = logicalPositionToOffset(0, 0)
        performFindInFileAction("functi", fromOffset)
        focusEditor()

        testInvokeLater {
            assertSuggestedCorrectly()
        }
    }

    @Test
    fun `testFind class and get suggestion`() {
        val fromOffset = logicalPositionToOffset(0, 0)
        performFindInFileAction("clazz", fromOffset)
        focusEditor()

        testInvokeLater {
            assertSuggestedCorrectly()
        }
    }

    @Test
    override fun `testFind function parameter and don't get suggestion`() {
        val fromOffset = logicalPositionToOffset(0, 0)
        performFindInFileAction("aaa", fromOffset)
        focusEditor()

        testInvokeLater {
            TestCase.assertTrue(expectedSuggestion is NoSuggestion)
        }
    }

    @Test
    override fun `testFind local variable declaration and don't get suggestion`() {
        val fromOffset = logicalPositionToOffset(35, 0)
        performFindInFileAction("strin", fromOffset)
        focusEditor()

        testInvokeLater {
            TestCase.assertTrue(expectedSuggestion is NoSuggestion)
        }
    }

    override fun `testFind variable usage and don't get suggestion`() {
        val fromOffset = logicalPositionToOffset(10, 0)
        performFindInFileAction("aaa", fromOffset)
        focusEditor()

        testInvokeLater {
            TestCase.assertTrue(expectedSuggestion is NoSuggestion)
        }
    }

    @Test
    override fun `testFind method usage and don't get suggestion`() {
        val fromOffset = logicalPositionToOffset(14, 0)
        performFindInFileAction("function", fromOffset)
        focusEditor()

        testInvokeLater {
            TestCase.assertTrue(expectedSuggestion is NoSuggestion)
        }
    }

    @Test
    override fun `testFind type usage and don't get suggestion`() {
        val fromOffset = logicalPositionToOffset(31, 9)
        performFindInFileAction("Claz", fromOffset)
        focusEditor()

        testInvokeLater {
            TestCase.assertTrue(expectedSuggestion is NoSuggestion)
        }
    }
}
