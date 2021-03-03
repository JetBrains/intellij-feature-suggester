import java.io.ByteArrayOutputStream
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = project.findProperty(key).toString()
fun getGitHash() = ByteArrayOutputStream().let {
    exec {
        commandLine("git", "rev-list", "--count", "HEAD")
        standardOutput = it
    }
}.toString().trim()

plugins {
    id("java")
    id("org.jetbrains.intellij") version "0.7.2"
    id("org.jetbrains.kotlin.jvm") version "1.3.72"
    id("org.jetbrains.changelog") version "1.1.2"
}

group = properties("pluginGroup")
version = properties("projectMajorVersion") + ".${getGitHash()}"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk11")
    testCompile("junit:junit:4.12")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    pluginName = properties("pluginName")
    version = properties("platformVersion")
    type = properties("platformType")
    downloadSources = properties("platformDownloadSources").toBoolean()
    updateSinceUntilBuild = true
    setPlugins(*properties("platformPlugins").split(',').map(String::trim).filter(String::isNotEmpty).toTypedArray())
}

changelog {
    version = project.version.toString()
    groups = emptyList()
    headerParserRegex = """\d+\.\d+""".toRegex()
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    patchPluginXml {
        version(project.version)
        sinceBuild(properties("pluginSinceBuild"))
        untilBuild(properties("pluginUntilBuild"))
        changeNotes(changelog.getLatest().toHTML())
    }

    runPluginVerifier {
        ideVersions(properties("pluginVerifierIdeVersions"))
    }
}
