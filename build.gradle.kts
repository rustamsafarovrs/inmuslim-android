import io.gitlab.arturbosch.detekt.extensions.DetektExtension

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.jetbrains.kotlin.compose) apply false
    alias(libs.plugins.jetbrains.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.dagger.hilt.android) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
}

allprojects.onEach { project ->
    project.afterEvaluate {
        if (project.plugins.hasPlugin("org.jetbrains.kotlin.android")) {
            project.plugins.apply("io.gitlab.arturbosch.detekt")
            project.extensions.configure<DetektExtension> {
                config.setFrom(rootProject.files("config/detekt/detekt.yml"))
                baseline = rootProject.file("config/detekt/baseline.xml")
            }
            project.dependencies.add("detektPlugins", "io.gitlab.arturbosch.detekt:detekt-formatting:1.23.6")
        }
    }
}
