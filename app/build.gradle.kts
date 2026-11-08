import java.util.Properties
import java.io.FileInputStream
import java.io.FileOutputStream
import java.time.LocalDate

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

val versionPropertiesFile = file("version.properties")
val versionProperties = Properties()

if (versionPropertiesFile.exists()) {
    versionProperties.load(FileInputStream(versionPropertiesFile))
} else {
    versionProperties.setProperty("VERSION_CODE", "1")
    versionProperties.setProperty("VERSION_NAME", "1.0")
    versionProperties.store(FileOutputStream(versionPropertiesFile), "Version properties")
}

var versionCodeValue = versionProperties.getProperty("VERSION_CODE", "1").toInt()
val versionNameValue = versionProperties.getProperty("VERSION_NAME", "1.0")

val taskNames = gradle.startParameter.taskNames
val isReleaseFileCreatingBuild = taskNames.any { taskName ->
    taskName.contains("assembleRelease", ignoreCase = true) ||
    taskName.contains("bundleRelease", ignoreCase = true) ||
    taskName.contains("packageRelease", ignoreCase = true)
}

if (isReleaseFileCreatingBuild) {
    versionCodeValue++
    versionProperties.setProperty("VERSION_CODE", versionCodeValue.toString())
    versionProperties.store(FileOutputStream(versionPropertiesFile), "Updated on release build")
    println("Auto-incremented release versionCode to: $versionCodeValue")
}

android {
    namespace = "net.gbs.gbswarehouse"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "net.gbs.gbswarehouse"
        minSdk = 24
        targetSdk = 36
        versionCode = versionCodeValue
        versionName = versionNameValue

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

androidComponents {
    onVariants(selector().withBuildType("release")) { variant ->
        val currentDate = LocalDate.now().toString()
        variant.outputs.forEach { output ->
            output.outputFileName.set("Version Number $versionCodeValue Build Date $currentDate.apk")
        }
    }
}

dependencies {
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
//    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
  //  implementation(libs.androidx.ui.tooling.preview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
//    debugImplementation(libs.androidx.compose.ui.tooling)
//    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation (libs.hilt.android)
//    debugImplementation(libs.androidx.ui.tooling)
    ksp (libs.hilt.android.compiler)

    implementation (libs.androidx.hilt.navigation.compose)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation (libs.androidx.room.runtime)
    implementation (libs.androidx.room.ktx)

    ksp (libs.androidx.room.compiler)

    implementation(libs.androidx.datastore.preferences)

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.compose.material.icons.extended)
 //   implementation("com.github.mohamadelaskary.WarehouseFlow:honeywellscanner:v1.0.3")
    implementation(project(":honeywellscanner"))
//    implementation(files("../honeywellscanner/libs/DataCollection.aar"))
    implementation("com.composables:icons-lucide:1.1.0")
}
