package org.jetbrains.plugins.feature.suggester.defaultSuggesters

import org.jetbrains.plugins.feature.suggester.{NoSuggestion, Suggestion, FeatureSuggester}
import org.jetbrains.plugins.feature.suggester.changes.{ChildReplacedAction, UserAction}
import com.intellij.openapi.command.CommandProcessor
import com.intellij.psi.PsiComment

/**
 * @author Alefas
 * @since 24.05.13
 */
class SuppressWarningSuggester extends FeatureSuggester {
  val POPUP_MESSAGE = "Why no to use quickfix for inspection to suppress it (Alt + Enter)"

  def getSuggestion(actions: List[UserAction]): Suggestion = {
    val name = CommandProcessor.getInstance().getCurrentCommandName
    if (name != null) return NoSuggestion //it's not user typing action, so let's do nothing
    actions.last match {
      case ChildReplacedAction(_, child: PsiComment, oldChild) if child.getText.startsWith("//noinspection") =>
        if (oldChild.isInstanceOf[PsiComment] && oldChild.getText.startsWith("//noinspection")) return NoSuggestion
        if (SuggestingUtil.checkCommentAddedToLineStart(child.getContainingFile, child.getTextRange.getStartOffset)) {
          return SuggestingUtil.createSuggestion(null, POPUP_MESSAGE)
        }
      case _ =>
    }
    NoSuggestion
  }

  def getId: String = "Suppress warning suggester"
}
