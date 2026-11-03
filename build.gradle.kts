// Общие настройки проекта. Плагины объявлены здесь с apply false,
// чтобы Gradle загружал их один раз, а подключал модуль server.
plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}
