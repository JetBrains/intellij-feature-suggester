package org.jetbrains.plugins.feature.suggester.suggesters.surroundWith

import junit.framework.TestCase
import org.jetbrains.plugins.feature.suggester.NoSuggestion
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class SurroundWithSuggesterKotlinTest : SurroundWithSuggesterTest() {

    override val testingCodeFileName: String = "KotlinCodeExample.kt"

    @Test
    override fun `testSurround one statement with IF and get suggestion`() {
        insertNewLineAt(6)
        type("if () {")
        insertNewLineAt(8)
        type("}")

        testInvokeLater {
            assertSuggestedCorrectly()
        }
    }

    @Test
    override fun `testSurround 2 statements with IF and add '}' at the line with second statement and get suggestion`() {
        insertNewLineAt(5)
        type("if (true){")
        moveCaretToLogicalPosition(7, 19)
        type("}")

        testInvokeLater {
            assertSuggestedCorrectly()
        }
    }

    @Test
    override fun `testSurround all statements in block with IF and get suggestion`() {
        insertNewLineAt(5)
        type("if(){")
        insertNewLineAt(14)
        type("}")

        testInvokeLater {
            assertSuggestedCorrectly()
        }
    }

    // todo: investigate why this test is falling
    @Ignore
    @Test
    override fun `testSurround one statement with IF in one line and get suggestion`() {
        moveCaretToLogicalPosition(6, 7)
        type("if(1 > 2 ){")
        moveCaretRelatively(12, 0)
        type("}")

        testInvokeLater {
            assertSuggestedCorrectly()
        }
    }

    @Test
    override fun `testSurround statements with FOR and get suggestion`() {
        insertNewLineAt(6)
        type("for (i in 0..10) {")
        insertNewLineAt(13)
        type("}")

        testInvokeLater {
            assertSuggestedCorrectly()
        }
    }

    @Test
    override fun `testSurround statements with WHILE and get suggestion`() {
        insertNewLineAt(7)
        type("while(false && true){")
        insertNewLineAt(10)
        type("}")

        testInvokeLater {
            assertSuggestedCorrectly()
        }
    }

    /**
     * This case must throw suggestion but not working now
     */
    @Test
    fun `testSurround IfStatement with IF and don't get suggestion`() {
        insertNewLineAt(9)
        type("if (false && true){")
        insertNewLineAt(13)
        type("}")

        testInvokeLater {
            TestCase.assertTrue(expectedSuggestion is NoSuggestion)
        }
    }

    @Test
    override fun `testSurround 0 statements with IF and don't get suggestion`() {
        insertNewLineAt(6)
        type("if (true) {    }")

        testInvokeLater {
            TestCase.assertTrue(expectedSuggestion is NoSuggestion)
        }
    }

    @Test
    override fun `testWrite if() but add braces in another place and don't get suggestion`() {
        insertNewLineAt(6)
        type("if() ")
        moveCaretToLogicalPosition(7, 20)
        type("{}")

        testInvokeLater {
            TestCase.assertTrue(expectedSuggestion is NoSuggestion)
        }
    }
}
