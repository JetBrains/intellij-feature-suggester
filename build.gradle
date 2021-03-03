plugins {
    id 'java'
    id 'org.jetbrains.intellij' version '0.7.2'
    id 'org.jetbrains.kotlin.jvm' version '1.3.72'
}

def getGitHash = { ->
    def stdout = new ByteArrayOutputStream()
    exec {
        commandLine 'git', 'rev-list', '--count', 'HEAD'
        standardOutput = stdout
    }
    return stdout.toString().trim()
}

group 'org.jetbrains'
version "211.${getGitHash()}"

sourceCompatibility = 11
targetCompatibility = 11

repositories {
    mavenCentral()
}

dependencies {
    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
    testCompile group: 'junit', name: 'junit', version: '4.12'
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version 'IU-211-EAP-SNAPSHOT'
    plugins 'java', 'Kotlin', 'JavaScript', 'Pythonid:211.6222.4'
}
compileKotlin {
    kotlinOptions.jvmTarget = "11"
}
compileTestKotlin {
    kotlinOptions.jvmTarget = "11"
}
patchPluginXml {
    sinceBuild = "211"
    changeNotes """
      Add change notes here.<br>
      <em>most HTML tags may be used</em>"""
}
