package org.jetbrains.plugins.feature.suggester.defaultSuggesters

import org.jetbrains.plugins.feature.suggester.{PopupSuggestion, NoSuggestion, Suggestion, FeatureSuggester}
import org.jetbrains.plugins.feature.suggester.changes.{ChildRemovedAction, ChildReplacedAction, ChildAddedAction, UserAction}
import com.intellij.psi.{PsiErrorElement, PsiFile, PsiComment}
import com.intellij.openapi.util.text.StringUtil

/**
 * @author Alefas
 * @since 23.05.13
 */
class LineCommentingSuggester extends FeatureSuggester {
  val POPUP_MESSAGE = "Why no to use line commenting feature (Ctrl + Slash)"
  val UNCOMMENTING_POPUP_MESSAGE = "Why no to use line uncommenting feature (Ctrl + Slash)"
  val DESCRIPTOR_ID = "codeassists.comment.line"

  private var uncommentingActionStart: Option[UserAction] = None

  def getSuggestion(actions: List[UserAction]): Suggestion = {
    actions.last match {
      case ChildAddedAction(_, child: PsiComment) if child.getText.startsWith("//") =>
        if (checkCommentAdded(child.getContainingFile, child.getTextRange.getStartOffset)) {
          return SuggestingUtil.createSuggestion(Some(DESCRIPTOR_ID), POPUP_MESSAGE)
        }
      case ChildReplacedAction(_, child: PsiComment, oldChild) if child.getText.startsWith("//") &&
        !oldChild.isInstanceOf[PsiComment] =>
        if (checkCommentAdded(child.getContainingFile, child.getTextRange.getStartOffset)) {
          return SuggestingUtil.createSuggestion(Some(DESCRIPTOR_ID), POPUP_MESSAGE)
        }
      case ChildReplacedAction(_, child, oldChild: PsiComment) if oldChild.getText.startsWith("//") && !child.isInstanceOf[PsiComment] =>
        val offset = child.getTextRange.getStartOffset
        if (checkCommentAdded(child.getContainingFile, offset)) {
          val suggestion = SuggestingUtil.createSuggestion(Some(DESCRIPTOR_ID), UNCOMMENTING_POPUP_MESSAGE)
          if (suggestion.isInstanceOf[PopupSuggestion]) {
            uncommentingActionStart = Some(actions.last)
          }
          return suggestion
        }
      case ChildRemovedAction(parent, child: PsiErrorElement) if child.getText == "/" && uncommentingActionStart.isDefined =>
        if (actions.contains(uncommentingActionStart.get)) {
          uncommentingActionStart = None
          return SuggestingUtil.createSuggestion(Some(DESCRIPTOR_ID), UNCOMMENTING_POPUP_MESSAGE)
        }
      case _ =>
    }
    NoSuggestion
  }

  def getId: String = "Commenting suggester"

  private def checkCommentAdded(file: PsiFile, offset: Int): Boolean = {
    val fileBeforeCommentText = file.getText.substring(0, offset)
    fileBeforeCommentText.reverseIterator.takeWhile(_ != '\n').forall(StringUtil.isWhiteSpace)
  }
}
