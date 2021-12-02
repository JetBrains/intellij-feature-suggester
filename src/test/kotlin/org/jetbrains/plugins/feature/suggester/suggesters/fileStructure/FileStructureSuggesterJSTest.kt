package org.jetbrains.plugins.feature.suggester.suggesters.fileStructure

import junit.framework.TestCase
import org.jetbrains.plugins.feature.suggester.NoSuggestion
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class FileStructureSuggesterJSTest : FileStructureSuggesterTest() {

    override val testingCodeFileName: String = "JavaScriptCodeExample.js"

    @Test
    override fun `testFind field and get suggestion`() {
        val fromOffset = logicalPositionToOffset(8, 0)
        performFindInFileAction("field", fromOffset)
        focusEditor()

        testInvokeLater {
            assertSuggestedCorrectly()
        }
    }

    @Test
    override fun `testFind method and get suggestion`() {
        val fromOffset = logicalPositionToOffset(4, 0)
        performFindInFileAction("mai", fromOffset)
        focusEditor()

        testInvokeLater {
            assertSuggestedCorrectly()
        }
    }

    @Test
    override fun `testFind function parameter and don't get suggestion`() {
        val fromOffset = logicalPositionToOffset(14, 0)
        performFindInFileAction("args", fromOffset)
        focusEditor()

        testInvokeLater {
            TestCase.assertTrue(expectedSuggestion is NoSuggestion)
        }
    }

    @Test
    override fun `testFind local variable declaration and don't get suggestion`() {
        val fromOffset = logicalPositionToOffset(14, 0)
        performFindInFileAction("abc", fromOffset)
        focusEditor()

        testInvokeLater {
            TestCase.assertTrue(expectedSuggestion is NoSuggestion)
        }
    }

    @Test
    override fun `testFind variable usage and don't get suggestion`() {
        val fromOffset = logicalPositionToOffset(18, 0)
        performFindInFileAction("abc", fromOffset)
        focusEditor()

        testInvokeLater {
            TestCase.assertTrue(expectedSuggestion is NoSuggestion)
        }
    }

    @Test
    override fun `testFind method usage and don't get suggestion`() {
        val fromOffset = logicalPositionToOffset(18, 0)
        performFindInFileAction("main", fromOffset)
        focusEditor()

        testInvokeLater {
            TestCase.assertTrue(expectedSuggestion is NoSuggestion)
        }
    }

    @Test
    override fun `testFind type usage and don't get suggestion`() {
        val fromOffset = logicalPositionToOffset(16, 25)
        performFindInFileAction("anotherCl", fromOffset)
        focusEditor()

        testInvokeLater {
            TestCase.assertTrue(expectedSuggestion is NoSuggestion)
        }
    }
}
