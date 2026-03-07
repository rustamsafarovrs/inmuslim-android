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
    alias(libs.plugins.compose.stability.analyzer) apply false
}

allprojects.onEach { project ->
    project.afterEvaluate {
        if (project.plugins.hasPlugin(libs.plugins.jetbrains.kotlin.android.get().pluginId)) {
            project.plugins.apply(libs.plugins.detekt.get().pluginId)
            project.extensions.configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
                config.setFrom(rootProject.files("config/detekt/detekt.yml"))
                baseline = rootProject.file("config/detekt/baseline.xml")
            }
            project.dependencies.add("detektPlugins", libs.detekt.formatting.get().toString())
        }

        val lintBaseline = rootProject.file("config/lint/baseline.xml")
        val warnings = true
        val disabledList = listOf(
            "IconLocation",
            "AndroidGradlePluginVersion",
            "GradleDependency",
            "OldTargetApi",
            "Aligned16KB",
            "VectorPath",
        )
        if (project.plugins.hasPlugin(libs.plugins.android.application.get().pluginId)) {
            project.extensions.configure<com.android.build.api.dsl.ApplicationExtension> {
                lint {
                    baseline = lintBaseline
                    warningsAsErrors = warnings
                    disable.addAll(disabledList)
                }
            }
        }
        if (project.plugins.hasPlugin(libs.plugins.android.library.get().pluginId)) {
            project.extensions.configure<com.android.build.api.dsl.LibraryExtension> {
                lint {
                    baseline = lintBaseline
                    warningsAsErrors = warnings
                    disable.addAll(disabledList)
                }
            }
        }

        if (project.plugins.hasPlugin(libs.plugins.jetbrains.kotlin.compose.get().pluginId)) {
            project.plugins.apply(libs.plugins.compose.stability.analyzer.get().pluginId)
        }
    }
}

apply(from = "scripts/git-hooks/install.gradle.kts")
