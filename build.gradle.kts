import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.ByteArrayOutputStream

fun properties(key: String) = project.findProperty(key).toString()
fun getGitHash() = ByteArrayOutputStream().apply {
    exec {
        commandLine("git", "rev-list", "--count", "HEAD")
        standardOutput = this@apply
    }
}.toString().trim()

plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.10.0"
    id("org.jetbrains.kotlin.jvm") version "1.7.20"
    id("org.jetbrains.changelog") version "1.3.1"
    id("io.gitlab.arturbosch.detekt") version "1.19.0"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
}

group = properties("pluginGroup")
version = properties("projectMajorVersion") + ".${getGitHash()}"

repositories {
    mavenCentral()
    maven {
        url = uri("https://cache-redirector.jetbrains.com/www.jetbrains.com/intellij-repository/releases")
    }
    maven {
        url = uri("https://cache-redirector.jetbrains.com/www.jetbrains.com/intellij-repository/snapshots")
    }
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.17.1")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    pluginName.set(properties("pluginName"))
    version.set(properties("platformVersion"))
    type.set(properties("platformType"))
    downloadSources.set(properties("platformDownloadSources").toBoolean())
    updateSinceUntilBuild.set(true)

    // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file.
    plugins.set(properties("platformPlugins").split(',').map(String::trim).filter(String::isNotEmpty))
}

changelog {
    version.set(project.version.toString())
    groups.set(emptyList())
    headerParserRegex.set("""\d+\.\d+""".toRegex())
}

detekt {
    config = files("./detekt-config.yml")
    buildUponDefaultConfig = true

    reports {
        html.enabled = false
        xml.enabled = false
        txt.enabled = false
    }
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    wrapper {
        gradleVersion = properties("gradleVersion")
    }

    patchPluginXml {
        version.set(project.version.toString())
        sinceBuild.set(properties("pluginSinceBuild"))
        untilBuild.set(properties("pluginUntilBuild"))
        changeNotes.set(provider { changelog.getLatest().toHTML() })
    }

    runPluginVerifier {
        ideVersions.set(properties("pluginVerifierIdeVersions").split(',').map(String::trim).filter(String::isNotEmpty))
    }
}
