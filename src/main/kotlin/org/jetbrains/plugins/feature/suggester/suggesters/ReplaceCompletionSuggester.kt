package org.jetbrains.plugins.feature.suggester.suggesters

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiComment
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import org.jetbrains.plugins.feature.suggester.NoSuggestion
import org.jetbrains.plugins.feature.suggester.Suggestion
import org.jetbrains.plugins.feature.suggester.actions.BeforeCompletionChooseItemAction
import org.jetbrains.plugins.feature.suggester.actions.BeforeEditorTextInsertedAction
import org.jetbrains.plugins.feature.suggester.actions.BeforeEditorTextRemovedAction
import org.jetbrains.plugins.feature.suggester.actions.CompletionChooseItemAction
import org.jetbrains.plugins.feature.suggester.actions.EditorAction
import org.jetbrains.plugins.feature.suggester.actions.EditorCodeCompletionAction
import org.jetbrains.plugins.feature.suggester.actions.EditorEscapeAction
import org.jetbrains.plugins.feature.suggester.actions.EditorTextInsertedAction
import org.jetbrains.plugins.feature.suggester.actions.EditorTextRemovedAction
import org.jetbrains.plugins.feature.suggester.actionsLocalSummary
import org.jetbrains.plugins.feature.suggester.createDocumentationSuggestion
import org.jetbrains.plugins.feature.suggester.getParentByPredicate
import org.jetbrains.plugins.feature.suggester.getParentOfType
import org.jetbrains.plugins.feature.suggester.history.UserActionsHistory
import org.jetbrains.plugins.feature.suggester.suggesters.FeatureSuggester.Companion.createMessageWithShortcut
import org.jetbrains.plugins.feature.suggester.suggesters.lang.LanguageSupport
import java.util.concurrent.TimeUnit

class ReplaceCompletionSuggester : FeatureSuggester {
    companion object {
        const val POPUP_MESSAGE = "In completion popup you may use shortcut to replace current expression."
        const val SUGGESTING_ACTION_ID = "EditorChooseLookupItemReplace"
        const val SUGGESTING_DOC_URL = "https://www.jetbrains.com/help/idea/auto-completing-code.html#accept"
    }

    private data class EditedStatementData(val dotOffset: Int) {
        var isCompletionStarted: Boolean = false
        var textToDelete: String = ""
        var deletedText: String = ""
        var addedExprEndOffset: Int = -1

        val isCompletionFinished: Boolean
            get() = textToDelete != ""

        fun isAroundDot(offset: Int): Boolean {
            return offset in dotOffset..(dotOffset + 7)
        }

        fun isIdentifierNameDeleted(caretOffset: Int, newDeletedText: String): Boolean {
            return isCaretInRangeOfExprToDelete(caretOffset, newDeletedText) &&
                (deletedText.contains(textToDelete) || deletedText.contains(textToDelete.reversed()))
        }

        private fun isCaretInRangeOfExprToDelete(caretOffset: Int, newDeletedText: String): Boolean {
            return caretOffset in (addedExprEndOffset - 2)..(addedExprEndOffset + newDeletedText.length)
        }

        fun isDeletedTooMuch(): Boolean {
            return deletedText.length >= textToDelete.length * 2
        }
    }

    private val actionsSummary = actionsLocalSummary()
    override lateinit var langSupport: LanguageSupport

    private var editedStatementData: EditedStatementData? = null

    override fun getSuggestion(actions: UserActionsHistory): Suggestion {
        when (val action = actions.lastOrNull()) {
            is BeforeEditorTextRemovedAction -> {
                if (action.textFragment.text == ".") {
                    editedStatementData = createEditedStatementData(action, action.caretOffset)
                }
            }
            is BeforeEditorTextInsertedAction -> {
                if (editedStatementData != null &&
                    action.text == "." &&
                    action.caretOffset == editedStatementData!!.dotOffset
                ) {
                    editedStatementData!!.isCompletionStarted = true
                }
            }
            is EditorCodeCompletionAction -> {
                val caretOffset = action.caretOffset
                val document = action.document ?: return NoSuggestion
                if (document.getText(TextRange(caretOffset - 1, caretOffset)) == ".") {
                    editedStatementData = createEditedStatementData(action, action.caretOffset)?.apply {
                        isCompletionStarted = true
                    }
                }
            }
            is BeforeCompletionChooseItemAction -> {
                if (editedStatementData == null || editedStatementData?.isAroundDot(action.caretOffset) != true) {
                    return NoSuggestion
                }
                val curElement = action.psiFile?.findElementAt(action.caretOffset) ?: return NoSuggestion
                if (langSupport.isIdentifier(curElement)) {
                    // remove characters that user typed before completion
                    editedStatementData!!.textToDelete =
                        curElement.text.substring(action.caretOffset - curElement.startOffset)
                }
            }
            is CompletionChooseItemAction -> {
                editedStatementData?.addedExprEndOffset = action.caretOffset
            }
            is EditorTextInsertedAction -> {
                if (editedStatementData?.isCompletionFinished != true) return NoSuggestion
                if (action.caretOffset < editedStatementData!!.addedExprEndOffset) {
                    editedStatementData!!.addedExprEndOffset += action.text.length
                }
            }
            is EditorTextRemovedAction -> {
                if (editedStatementData?.isCompletionFinished != true) return NoSuggestion
                editedStatementData!!.deletedText += action.textFragment.text
                if (editedStatementData!!.isDeletedTooMuch()) {
                    editedStatementData = null
                } else if (editedStatementData!!.isIdentifierNameDeleted(
                        action.caretOffset,
                        action.textFragment.text
                    )
                ) {
                    editedStatementData = null
                    return createDocumentationSuggestion(
                        createMessageWithShortcut(SUGGESTING_ACTION_ID, POPUP_MESSAGE),
                        suggestingActionDisplayName,
                        SUGGESTING_DOC_URL
                    )
                }
            }
            is EditorEscapeAction -> {
                editedStatementData = null
            }
        }

        return NoSuggestion
    }

    override fun isSuggestionNeeded(minNotificationIntervalDays: Int): Boolean {
        return super.isSuggestionNeeded(
            actionsSummary,
            SUGGESTING_ACTION_ID,
            TimeUnit.DAYS.toMillis(minNotificationIntervalDays.toLong())
        )
    }

    @Suppress("DuplicatedCode")
    private fun createEditedStatementData(action: EditorAction, offset: Int): EditedStatementData? {
        val curElement = action.psiFile?.findElementAt(offset) ?: return null
        return if (curElement.getParentByPredicate(langSupport::isLiteralExpression) == null &&
            curElement.getParentOfType<PsiComment>() == null
        ) {
            EditedStatementData(offset)
        } else {
            null
        }
    }

    override val id: String = "Completion with replace"

    override val suggestingActionDisplayName: String = "Choose lookup item and replace"
}
