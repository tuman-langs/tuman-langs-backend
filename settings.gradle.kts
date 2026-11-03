rootProject.name = "tuman-langs-backend"

// Откуда Gradle скачивает плагины и библиотеки.
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

include(":server")
