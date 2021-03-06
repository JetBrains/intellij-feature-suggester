package org.jetbrains.plugins.feature.suggester.suggesters

import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiComment
import org.jetbrains.plugins.feature.suggester.NoSuggestion
import org.jetbrains.plugins.feature.suggester.Suggestion
import org.jetbrains.plugins.feature.suggester.actions.EditorTextInsertedAction
import org.jetbrains.plugins.feature.suggester.actionsLocalSummary
import org.jetbrains.plugins.feature.suggester.createTipSuggestion
import org.jetbrains.plugins.feature.suggester.history.ChangesHistory
import org.jetbrains.plugins.feature.suggester.history.UserActionsHistory
import org.jetbrains.plugins.feature.suggester.suggesters.FeatureSuggester.Companion.createMessageWithShortcut
import org.jetbrains.plugins.feature.suggester.suggesters.lang.LanguageSupport
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class LineCommentingSuggester : FeatureSuggester {

    companion object {
        const val POPUP_MESSAGE = "Try the Comment Line feature to do it faster."
        const val SUGGESTING_ACTION_ID = "CommentByLineComment"
        const val SUGGESTING_TIP_FILENAME = "neue-CommentCode.html"
        const val NUMBER_OF_COMMENTS_TO_GET_SUGGESTION = 3
        const val MAX_TIME_MILLIS_INTERVAL_BETWEEN_COMMENTS = 5000
    }

    private data class DocumentLine(val startOffset: Int, val endOffset: Int, val text: String)
    private data class CommentData(val lineNumber: Int, val documentRef: WeakReference<Document>, val timeMillis: Long)

    private val actionsSummary = actionsLocalSummary()
    override lateinit var langSupport: LanguageSupport

    private val commentsHistory = ChangesHistory<CommentData>(NUMBER_OF_COMMENTS_TO_GET_SUGGESTION)
    private var firstSlashAddedAction: EditorTextInsertedAction? = null

    override fun getSuggestion(actions: UserActionsHistory): Suggestion {
        val curAction = actions.lastOrNull() ?: return NoSuggestion
        if (curAction is EditorTextInsertedAction) {
            if (isCommentSymbolAdded(curAction, '/')) {
                firstSlashAddedAction = curAction
            } else if (firstSlashAddedAction != null && isSecondSlashAdded(curAction, firstSlashAddedAction!!) ||
                isCommentSymbolAdded(curAction, '#')
            ) {
                val document = curAction.document ?: return NoSuggestion
                val commentData = CommentData(
                    lineNumber = document.getLineNumber(curAction.caretOffset),
                    documentRef = WeakReference(document),
                    timeMillis = curAction.timeMillis
                )
                commentsHistory.add(commentData)
                firstSlashAddedAction = null

                if (commentsHistory.size == NUMBER_OF_COMMENTS_TO_GET_SUGGESTION &&
                    commentsHistory.isLinesCommentedInARow()
                ) {
                    commentsHistory.clear()
                    return createTipSuggestion(
                        createMessageWithShortcut(SUGGESTING_ACTION_ID, POPUP_MESSAGE),
                        suggestingActionDisplayName,
                        SUGGESTING_TIP_FILENAME
                    )
                }
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

    private fun isCommentSymbolAdded(action: EditorTextInsertedAction, symbol: Char): Boolean {
        with(action) {
            val psiFile = this.psiFile ?: return false
            val document = this.document ?: return false
            if (text != symbol.toString()) return false
            val psiElement = psiFile.findElementAt(caretOffset) ?: return false
            if (psiElement is PsiComment || psiElement.nextSibling is PsiComment) return false
            val line = document.getLineByOffset(caretOffset)
            val lineBeforeSlash = line.text.substring(0, caretOffset - line.startOffset)
            return lineBeforeSlash.isBlank() && line.text.trim() != symbol.toString()
        }
    }

    private fun isSecondSlashAdded(curAction: EditorTextInsertedAction, prevAction: EditorTextInsertedAction): Boolean {
        val curPsiFile = curAction.psiFile ?: return false
        val curDocument = curAction.document ?: return false
        val prevPsiFile = prevAction.psiFile ?: return false
        val prevDocument = curAction.document ?: return false
        if (curPsiFile !== prevPsiFile || curDocument !== prevDocument) return false
        return curAction.text == "/" &&
            abs(curAction.caretOffset - prevAction.caretOffset) == 1 &&
            curDocument.getLineNumber(curAction.caretOffset) == prevDocument.getLineNumber(prevAction.caretOffset)
    }

    private fun ChangesHistory<CommentData>.isLinesCommentedInARow(): Boolean {
        val comments = asIterable()
        return !(
            comments.map(CommentData::lineNumber)
                .sorted()
                .zipWithNext { first, second -> second - first }
                .any { it != 1 } ||
                comments.map { it.documentRef.get() }
                    .zipWithNext { first, second -> first != null && first === second }
                    .any { !it } ||
                comments.map(CommentData::timeMillis)
                    .zipWithNext { first, second -> second - first }
                    .any { it > MAX_TIME_MILLIS_INTERVAL_BETWEEN_COMMENTS }
            )
    }

    private fun Document.getLineByOffset(offset: Int): DocumentLine {
        val lineNumber = getLineNumber(offset)
        val startOffset = getLineStartOffset(lineNumber)
        val endOffset = getLineEndOffset(lineNumber)
        return DocumentLine(
            startOffset = startOffset,
            endOffset = endOffset,
            text = getText(TextRange(startOffset, endOffset))
        )
    }

    override val id: String = "Comment with line comment"

    override val suggestingActionDisplayName: String = "Comment with line comment"
}
