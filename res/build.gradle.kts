import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "tj.rsdevteam.inmuslim.res"
    compileSdk = libs.versions.androidSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.androidMinSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.java.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.java.get())
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget(libs.versions.java.get()))
        }
    }
}

// Android 13+ appends the device languages to the app language and picks the first one with its own folder,
// so without a values-tg copy of the Tajik default, choosing Tajik on an English phone shows values-en.
abstract class GenerateTajikStringsTask : DefaultTask() {

    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val defaultStrings: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(defaultStrings.get().asFile)
        val root = document.documentElement
        (0 until root.childNodes.length)
            .map { root.childNodes.item(it) }
            .filter { it is Element && it.getAttribute("translatable") == "false" }
            .forEach { root.removeChild(it) }
        val target = outputDir.file("values-tg/strings.xml").get().asFile
        target.parentFile.mkdirs()
        TransformerFactory.newInstance().newTransformer().transform(DOMSource(document), StreamResult(target))
    }
}

androidComponents {
    onVariants { variant ->
        val task = tasks.register<GenerateTajikStringsTask>(
            "generate${variant.name.replaceFirstChar(Char::uppercase)}TajikStrings"
        ) {
            defaultStrings.set(layout.projectDirectory.file("src/main/res/values/strings.xml"))
        }
        variant.sources.res?.addGeneratedSourceDirectory(task, GenerateTajikStringsTask::outputDir)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
}
