package org.jetbrains.plugins.feature.suggester.suggesters.unwrap

import junit.framework.TestCase
import org.jetbrains.plugins.feature.suggester.NoSuggestion
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class UnwrapSuggesterKotlinTest : UnwrapSuggesterTest() {

    override val testingCodeFileName = "KotlinCodeExample.kt"

    @Test
    override fun `testUnwrap IF statement and get suggestion`() {
        moveCaretToLogicalPosition(11, 9)
        deleteSymbolAtCaret()
        selectBetweenLogicalPositions(lineStartIndex = 9, columnStartIndex = 42, lineEndIndex = 9, columnEndIndex = 3)
        deleteSymbolAtCaret()

        testInvokeLater {
            assertSuggestedCorrectly()
        }
    }

    @Test
    override fun `testUnwrap one-line IF and get suggestion`() {
        selectBetweenLogicalPositions(lineStartIndex = 31, columnStartIndex = 23, lineEndIndex = 31, columnEndIndex = 8)
        deleteSymbolAtCaret()
        moveCaretRelatively(6, 0)
        deleteSymbolAtCaret()

        testInvokeLater {
            assertSuggestedCorrectly()
        }
    }

    @Test
    override fun `testUnwrap IF with deleting multiline selection and get suggestion`() {
        selectBetweenLogicalPositions(lineStartIndex = 8, columnStartIndex = 23, lineEndIndex = 10, columnEndIndex = 5)
        deleteSymbolAtCaret()
        moveCaretToLogicalPosition(9, 9)
        deleteSymbolAtCaret()

        testInvokeLater {
            assertSuggestedCorrectly()
        }
    }

    @Test
    override fun `testUnwrap FOR and get suggestion`() {
        selectBetweenLogicalPositions(lineStartIndex = 22, columnStartIndex = 34, lineEndIndex = 22, columnEndIndex = 9)
        deleteSymbolAtCaret()
        moveCaretToLogicalPosition(25, 13)
        deleteSymbolAtCaret()

        testInvokeLater {
            assertSuggestedCorrectly()
        }
    }

    @Test
    override fun `testUnwrap WHILE and get suggestion`() {
        selectBetweenLogicalPositions(lineStartIndex = 27, columnStartIndex = 27, lineEndIndex = 27, columnEndIndex = 0)
        deleteSymbolAtCaret()
        moveCaretToLogicalPosition(30, 13)
        deleteSymbolAtCaret()

        testInvokeLater {
            assertSuggestedCorrectly()
        }
    }

    @Test
    override fun `testUnwrap commented IF and don't get suggestion`() {
        insertNewLineAt(21, 12)
        type(
            """//if(true) {
            |//i++; j--;
            |//}""".trimMargin()
        )

        selectBetweenLogicalPositions(
            lineStartIndex = 21,
            columnStartIndex = 14,
            lineEndIndex = 21,
            columnEndIndex = 24
        )
        deleteSymbolAtCaret()
        moveCaretToLogicalPosition(23, 15)
        deleteSymbolAtCaret()

        testInvokeLater {
            TestCase.assertTrue(expectedSuggestion is NoSuggestion)
        }
    }

    @Test
    override fun `testUnwrap IF written in string block and don't get suggestion`() {
        insertNewLineAt(21, 12)
        type("val s = \"\"\"if(true) {\ni++\nj--\n}")

        selectBetweenLogicalPositions(
            lineStartIndex = 21,
            columnStartIndex = 23,
            lineEndIndex = 21,
            columnEndIndex = 33
        )
        deleteSymbolAtCaret()
        moveCaretToLogicalPosition(24, 18)
        deleteSymbolAtCaret()

        testInvokeLater {
            TestCase.assertTrue(expectedSuggestion is NoSuggestion)
        }
    }
}
