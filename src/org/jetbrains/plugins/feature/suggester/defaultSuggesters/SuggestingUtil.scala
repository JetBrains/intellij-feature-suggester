package org.jetbrains.plugins.feature.suggester.defaultSuggesters

import org.jetbrains.plugins.feature.suggester.{PopupSuggestion, FeatureUsageSuggestion, Suggestion}
import com.intellij.featureStatistics.ProductivityFeaturesRegistry

/**
 * @author Alefas
 * @since 23.05.13
 */
object SuggestingUtil {
  def createSuggestion(descriptorId: String, popupMessage: String, usageDelta: Long = 1000): Suggestion = {
    val descriptor = ProductivityFeaturesRegistry.getInstance().getFeatureDescriptor(descriptorId)
    val lastTimeUsed = descriptor.getLastTimeUsed
    val delta = System.currentTimeMillis() - lastTimeUsed
    if (delta < usageDelta) return FeatureUsageSuggestion
    PopupSuggestion(popupMessage)
  }
}
