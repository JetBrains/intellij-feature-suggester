package org.jetbrains.plugins.featuresSuggester

import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.util.registry.Registry
import com.intellij.openapi.vfs.VirtualFile
import training.featuresSuggester.settings.FeatureSuggesterSettings
import training.featuresSuggester.suggesters.FeatureSuggester

class PluginActivator : FileEditorManagerListener {
    private val enabledKey = "feature.suggester.enable.suggesters"

    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        runInEdt {
            val project = source.project
            if (project.isDisposed) return@runInEdt
            if (!Registry.`is`(enabledKey, true)) {
                val dumbService = DumbService.getInstance(project)
                if (dumbService.isDumb) {
                    dumbService.runWhenSmart(this::enableSuggesters)
                } else {
                    enableSuggesters()
                }
            }
        }
    }

    private fun enableSuggesters() {
        val settings = FeatureSuggesterSettings.instance()
        Registry.get(enabledKey).setValue(true)
        FeatureSuggester.suggesters.forEach {
            settings.setEnabled(it.id, true)
        }
    }
}