package org.jetbrains.plugins.feature.suggester.defaultSuggesters

import org.jetbrains.plugins.feature.suggester.{NoSuggestion, Suggestion, FeatureSuggester}
import org.jetbrains.plugins.feature.suggester.changes.UserAction

/**
 * @author Alefas
 * @since 24.05.13
 */
class IntroduceVariableSuggester extends FeatureSuggester {
  def getSuggestion(actions: List[UserAction]): Suggestion = {
    NoSuggestion
  }

  def getId: String = "Introduce variable suggester"
}
