package org.jetbrains.plugins.feature.suggester.suggesters.unwrap

import junit.framework.TestCase
import org.jetbrains.plugins.feature.suggester.NoSuggestion
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class UnwrapSuggesterJavaTest : UnwrapSuggesterTest() {

    override val testingCodeFileName = "JavaCodeExample.java"

    @Test
    override fun `testUnwrap IF statement and get suggestion`() {
        moveCaretToLogicalPosition(11, 9)
        deleteSymbolAtCaret()
        selectBetweenLogicalPositions(lineStartIndex = 9, columnStartIndex = 41, lineEndIndex = 9, columnEndIndex = 3)
        deleteSymbolAtCaret()

        testInvokeLater {
            assertSuggestedCorrectly()
        }
    }

    @Test
    override fun `testUnwrap one-line IF and get suggestion`() {
        selectBetweenLogicalPositions(
            lineStartIndex = 41,
            columnStartIndex = 25,
            lineEndIndex = 41,
            columnEndIndex = 12
        )
        deleteSymbolAtCaret()
        moveCaretToLogicalPosition(41, 18)
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
        selectBetweenLogicalPositions(lineStartIndex = 32, columnStartIndex = 40, lineEndIndex = 32, columnEndIndex = 9)
        deleteSymbolAtCaret()
        moveCaretToLogicalPosition(35, 13)
        deleteSymbolAtCaret()

        testInvokeLater {
            assertSuggestedCorrectly()
        }
    }

    @Test
    override fun `testUnwrap WHILE and get suggestion`() {
        selectBetweenLogicalPositions(lineStartIndex = 37, columnStartIndex = 26, lineEndIndex = 37, columnEndIndex = 0)
        deleteSymbolAtCaret()
        moveCaretToLogicalPosition(40, 13)
        deleteSymbolAtCaret()

        testInvokeLater {
            assertSuggestedCorrectly()
        }
    }

    @Test
    override fun `testUnwrap commented IF and don't get suggestion`() {
        insertNewLineAt(31, 12)
        type(
            """//if(true) {
            |//i++; j--;
            |//}""".trimMargin()
        )

        selectBetweenLogicalPositions(
            lineStartIndex = 31,
            columnStartIndex = 14,
            lineEndIndex = 31,
            columnEndIndex = 24
        )
        deleteSymbolAtCaret()
        moveCaretToLogicalPosition(33, 15)
        deleteSymbolAtCaret()

        testInvokeLater {
            TestCase.assertTrue(expectedSuggestion is NoSuggestion)
        }
    }

    @Test
    override fun `testUnwrap IF written in string block and don't get suggestion`() {
        insertNewLineAt(31, 12)
        type(
            """String s = "if(true) {
            |i++; j--;
            |}"""".trimMargin()
        )

        selectBetweenLogicalPositions(
            lineStartIndex = 31,
            columnStartIndex = 24,
            lineEndIndex = 31,
            columnEndIndex = 34
        )
        deleteSymbolAtCaret()
        moveCaretToLogicalPosition(33, 22)
        deleteSymbolAtCaret()

        testInvokeLater {
            TestCase.assertTrue(expectedSuggestion is NoSuggestion)
        }
    }
}
