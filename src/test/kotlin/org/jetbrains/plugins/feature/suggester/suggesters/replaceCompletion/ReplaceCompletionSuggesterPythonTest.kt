package org.jetbrains.plugins.feature.suggester.suggesters.replaceCompletion

import org.jetbrains.plugins.feature.suggester.NoSuggestion
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ReplaceCompletionSuggesterPythonTest : ReplaceCompletionSuggesterTest() {

    override val testingCodeFileName: String = "PythonCodeExample.py"

    @Test
    override fun `testDelete and type dot, complete method call, remove previous identifier and get suggestion`() {
        moveCaretToLogicalPosition(27, 13)
        deleteAndTypeDot()
        val variants = completeBasic() ?: fail()
        chooseCompletionItem(variants[0])
        repeat(8) { typeDelete() }

        testInvokeLater {
            assertSuggestedCorrectly()
        }
    }

    @Test
    override fun `testCall completion, complete method call, remove previous identifier and get suggestion`() {
        moveCaretToLogicalPosition(52, 47)
        val variants = completeBasic() ?: fail()
        chooseCompletionItem(variants[1])
        deleteTextBetweenLogicalPositions(
            lineStartIndex = 52,
            columnStartIndex = 52,
            lineEndIndex = 52,
            columnEndIndex = 76
        )

        testInvokeLater {
            assertSuggestedCorrectly()
        }
    }

    @Test
    override fun `testCall completion, complete with method call, add parameter to method call, remove previous identifier and get suggestion`() {
        moveCaretToLogicalPosition(52, 47)
        val variants = completeBasic() ?: fail()
        chooseCompletionItem(variants[1])
        type("123")
        deleteTextBetweenLogicalPositions(
            lineStartIndex = 52,
            columnStartIndex = 56,
            lineEndIndex = 52,
            columnEndIndex = 79
        )

        testInvokeLater {
            assertSuggestedCorrectly()
        }
    }

    @Test
    override fun `testCall completion, complete with property, remove previous identifier and get suggestion`() {
        moveCaretToLogicalPosition(52, 18)
        val variants = completeBasic() ?: fail()
        chooseCompletionItem(variants[0])
        repeat(23) { typeDelete() }

        testInvokeLater {
            assertSuggestedCorrectly()
        }
    }

    @Test
    override fun `testCall completion inside arguments list, complete method call, remove previous identifier and get suggestion`() {
        moveCaretToLogicalPosition(52, 80)
        val variants = completeBasic() ?: fail()
        chooseCompletionItem(variants[1])
        repeat(5) { typeDelete() }

        testInvokeLater {
            assertSuggestedCorrectly()
        }
    }

    @Test
    override fun `testCall completion, type additional characters, complete, remove previous identifier and get suggestion`() {
        moveCaretToLogicalPosition(52, 18)
        completeBasic()
        type("fu")
        val variants = getLookupElements() ?: fail()
        chooseCompletionItem(variants[0])
        repeat(25) { typeDelete() }

        testInvokeLater {
            assertSuggestedCorrectly()
        }
    }

    @Test
    override fun `testCall completion, complete method call, remove another equal identifier and don't get suggestion`() {
        moveCaretToLogicalPosition(52, 47)
        val variants = completeBasic() ?: fail()
        chooseCompletionItem(variants[1])
        deleteTextBetweenLogicalPositions(
            lineStartIndex = 53,
            columnStartIndex = 8,
            lineEndIndex = 53,
            columnEndIndex = 35
        )

        testInvokeLater {
            assertTrue(expectedSuggestion is NoSuggestion)
        }
    }
}
