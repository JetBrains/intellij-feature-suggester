package org.jetbrains.plugins.feature.suggester;

import com.intellij.openapi.extensions.ExtensionPointName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.feature.suggester.changes.UserAction;
import scala.collection.immutable.List;

/**
 * @author Alefas
 * @since 23.05.13
 */
public abstract class FeatureSuggester {
  public static ExtensionPointName<FeatureSuggester> EP_NAME = ExtensionPointName.create("org.intellij.featureSuggester.featureSuggester");

  @NotNull
  public static FeatureSuggester[] getAllSuggesters() {
    return EP_NAME.getExtensions();
  }

  public abstract Suggestion getSuggestion(List<UserAction> actions);

  public abstract String getId();

  public boolean needToClearLookup() {
    return false;
  }
}
