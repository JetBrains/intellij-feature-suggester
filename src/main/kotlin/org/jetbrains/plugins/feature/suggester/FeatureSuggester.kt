package org.jetbrains.plugins.feature.suggester

import com.intellij.internal.statistic.local.ActionsLocalSummary
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.keymap.KeymapUtil
import org.jetbrains.plugins.feature.suggester.history.UserActionsHistory

@Suppress("UnstableApiUsage")
interface FeatureSuggester {

    companion object {
        val EP_NAME: ExtensionPointName<FeatureSuggester> =
            ExtensionPointName.create("org.intellij.featureSuggester.featureSuggester")

        val suggesters: List<FeatureSuggester> = EP_NAME.extensionList

        fun getSuggestingActionNames(): List<String> {
            return suggesters.map(FeatureSuggester::suggestingActionDisplayName)
        }

        fun createMessageWithShortcut(actionId: String, suggestionMessage: String): String {
            val shortcut = KeymapUtil.getShortcutText(actionId)
            return if (shortcut == "<no shortcut>") {
                "$suggestionMessage You can bind this action to convenient shortcut."
            } else {
                "$suggestionMessage $shortcut"
            }
        }
    }

    val needToClearLookup: Boolean
        get() = false

    fun getSuggestion(actions: UserActionsHistory): Suggestion

    fun isSuggestionNeeded(): Boolean

    fun isSuggestionNeeded(
        actionsSummary: ActionsLocalSummary,
        suggestingActionId: String,
        minNotificationIntervalMillis: Long
    ): Boolean {
        val actionStats = actionsSummary.getActionsStats()
        val summary = actionStats[suggestingActionId]
        return if (summary == null) {
            true
        } else {
            val lastTimeUsed = summary.last
            val delta = System.currentTimeMillis() - lastTimeUsed
            delta > minNotificationIntervalMillis
        }
    }

    val suggestingActionDisplayName: String
}