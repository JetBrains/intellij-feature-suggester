package org.jetbrains.plugins.featuresSuggester;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.util.registry.Registry;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import training.featuresSuggester.settings.FeatureSuggesterSettings;
import training.featuresSuggester.suggesters.FeatureSuggester;

import java.util.List;

public class PluginActivator implements FileEditorManagerListener {
    private static final String ENABLED_KEY = "feature.suggester.enable.suggesters";

    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        ApplicationManager.getApplication().invokeLater(() -> {
            var project = source.getProject();
            if (project.isDisposed()) return;
            if (!Registry.is(ENABLED_KEY, true)) {
                var dumbService = DumbService.getInstance(project);
                if (dumbService.isDumb()) {
                    dumbService.runWhenSmart(this::enableSuggesters);
                } else {
                    enableSuggesters();
                }
            }
        });
    }

    private void enableSuggesters() {
        Registry.get(ENABLED_KEY).setValue(true);

        var settings = FeatureSuggesterSettings.instance();
        List<FeatureSuggester> suggesters = FeatureSuggester.Companion.getSuggesters();
        for (var suggester : suggesters) {
            settings.setEnabled(suggester.getId(), true);
        }
    }
}