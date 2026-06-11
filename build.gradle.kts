plugins {
    id("org.jetbrains.intellij.platform") version "2.16.0"
    id("org.jetbrains.kotlin.jvm") version "2.4.0"
    id("idea")
}

group = "tech.stonks"
version = "0.11.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:adapter-rxjava3:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    implementation("com.google.code.gson:gson:2.14.0")
    implementation("io.reactivex.rxjava3:rxjava:3.1.12")
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")
    intellijPlatform {
        intellijIdea("2026.1.3")
        bundledPlugin("com.intellij.java")
        bundledPlugin("com.intellij.gradle")
        bundledPlugin("org.jetbrains.kotlin")
        pluginVerifier()
    }
}

kotlin {
    jvmToolchain(25)
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellijPlatform {
    buildSearchableOptions = false
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "251"
            untilBuild = provider { null }
        }
    }
}
