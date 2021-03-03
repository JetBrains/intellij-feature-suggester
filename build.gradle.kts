import java.io.ByteArrayOutputStream
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("org.jetbrains.intellij") version "0.7.2"
    id("org.jetbrains.kotlin.jvm") version "1.3.72"
    id("org.jetbrains.changelog") version "1.1.2"
}

fun getGitHash(): String {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine("git", "rev-list", "--count", "HEAD")
        standardOutput = stdout
    }
    return stdout.toString().trim()
}

group = "org.jetbrains"
version = "211.${getGitHash()}"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testCompile("junit:junit:4.12")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "IU-211-EAP-SNAPSHOT"
    setPlugins("java", "Kotlin", "JavaScript", "Pythonid:211.6222.4")
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
        sinceBuild("211")
        changeNotes(changelog.getLatest().toHTML())
    }
}
