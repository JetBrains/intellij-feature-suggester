package org.jetbrains.plugins.feature.suggester.suggesters.replaceCompletion

import org.jetbrains.plugins.feature.suggester.NoSuggestion
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ReplaceCompletionSuggesterJSTest : ReplaceCompletionSuggesterTest() {

    override val testingCodeFileName: String = "JavaScriptCodeExample.js"

    @Test
    override fun `testDelete and type dot, complete method call, remove previous identifier and get suggestion`() {
        moveCaretToLogicalPosition(24, 21)
        deleteAndTypeDot()
        val variants = completeBasic() ?: fail()
        chooseCompletionItem(variants[1])
        repeat(5) { typeDelete() }

        testInvokeLater {
            assertSuggestedCorrectly()
        }
    }

    @Test
    override fun `testCall completion, complete method call, remove previous identifier and get suggestion`() {
        moveCaretToLogicalPosition(72, 53)
        val variants = completeBasic() ?: fail()
        chooseCompletionItem(variants[0])
        deleteTextBetweenLogicalPositions(
            lineStartIndex = 72,
            columnStartIndex = 68,
            lineEndIndex = 72,
            columnEndIndex = 90
        )

        testInvokeLater {
            assertSuggestedCorrectly()
        }
    }

    @Test
    override fun `testCall completion, complete with method call, add parameter to method call, remove previous identifier and get suggestion`() {
        moveCaretToLogicalPosition(72, 53)
        val variants = completeBasic() ?: fail()
        chooseCompletionItem(variants[0])
        type("123")
        deleteTextBetweenLogicalPositions(
            lineStartIndex = 72,
            columnStartIndex = 72,
            lineEndIndex = 72,
            columnEndIndex = 93
        )

        testInvokeLater {
            assertSuggestedCorrectly()
        }
    }

    @Test
    override fun `testCall completion, complete with property, remove previous identifier and get suggestion`() {
        moveCaretToLogicalPosition(72, 26)
        val variants = completeBasic() ?: fail()
        chooseCompletionItem(variants[1])
        repeat(21) { typeDelete() }

        testInvokeLater {
            assertSuggestedCorrectly()
        }
    }

    @Test
    override fun `testCall completion inside arguments list, complete method call, remove previous identifier and get suggestion`() {
        moveCaretToLogicalPosition(72, 84)
        val variants = completeBasic() ?: fail()
        chooseCompletionItem(variants[0])
        repeat(15) { typeDelete() }

        testInvokeLater {
            assertSuggestedCorrectly()
        }
    }

    @Test
    override fun `testCall completion, type additional characters, complete, remove previous identifier and get suggestion`() {
        moveCaretToLogicalPosition(72, 26)
        completeBasic()
        type("cycles")
        val variants = getLookupElements() ?: fail()
        chooseCompletionItem(variants[0])
        repeat(22) { typeDelete() }

        testInvokeLater {
            assertSuggestedCorrectly()
        }
    }

    @Test
    override fun `testCall completion, complete method call, remove another equal identifier and don't get suggestion`() {
        moveCaretToLogicalPosition(72, 53)
        val variants = completeBasic() ?: fail()
        chooseCompletionItem(variants[0])
        deleteTextBetweenLogicalPositions(
            lineStartIndex = 73,
            columnStartIndex = 12,
            lineEndIndex = 73,
            columnEndIndex = 37
        )

        testInvokeLater {
            assertTrue(expectedSuggestion is NoSuggestion)
        }
    }
}
