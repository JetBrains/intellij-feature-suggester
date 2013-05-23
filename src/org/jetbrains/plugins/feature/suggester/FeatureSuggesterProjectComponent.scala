package org.jetbrains.plugins.feature.suggester

import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project

/**
 * @author Alefas
 * @since 23.05.13
 */
class FeatureSuggesterProjectComponent(project: Project) extends ProjectComponent {
  def getComponentName: String = "Feature suggester project component"

  def projectOpened() {
  }

  def projectClosed() {}

  def initComponent() {}

  def disposeComponent() {}
}
