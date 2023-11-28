plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = Config.namespace
    compileSdk = Config.compileSdkVersion

    defaultConfig {
        applicationId = Config.namespace
        minSdk = Config.minSdkVersion
        targetSdk = Config.targetSdkVersion
        versionCode = Config.versionCode
        versionName = Config.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildFeatures {
        compose = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = Config.javaSourceVersion
        targetCompatibility = Config.javaTargetVersion
    }
    kotlinOptions {
        jvmTarget = Config.javaTargetVersion.toString()
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Config.composeCompilerExtensionVersion
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

//val dataStoreVersion = "1.0.0"
//val coroutinesVersion = "1.6.4"
//val hiltVersion = "2.48.1"
//val hiltNavigationComposeVersion = "1.1.0"
//val lifecycleVersion = "2.6.2"
//val roomVersion = "2.6.0"
//val vicoVersion = "1.12.0"
//val junit5Version = "5.9.2"
//val splashScreenVersion = "1.0.1"
//val composeBomVersion = "2023.10.01"
//val espressoVersion = "3.5.1"
//val androidJUnitVersion = "1.1.5"
//val junitVersion = "4.13.2"
//val material3Version = "1.2.0-alpha10"
//val activityComposeVersion = "1.8.1"

dependencies {

    implementation(Dependencies.androidCoreKtx)
    // lifecycle
    implementation(Dependencies.lifecycleRuntimeKtx)
    implementation(Dependencies.lifecycleRuntimeCompose)
    implementation(Dependencies.lifecycleViewModelKtx)
    implementation(Dependencies.lifecycleViewModelCompose)
    implementation(Dependencies.lifecycleService)

    implementation(Dependencies.activityCompose)
    implementation(platform(Dependencies.composeBom))
    implementation(Dependencies.composeUI)
    implementation(Dependencies.composeUIGraphics)
    implementation(Dependencies.composeUIPreview)
    implementation(Dependencies.composeMaterial3)

    // datastore (shared preferences)
    implementation(Dependencies.datastore)

    // coroutines
    implementation(Dependencies.coroutinesCore)
    implementation(Dependencies.coroutinesAndroid)

    // splash screen
    implementation(Dependencies.splashScreenCore)

    // hilt
    implementation(Dependencies.hilt)
    implementation(Dependencies.hiltNavigationCompose)
    kapt(Dependencies.hiltCompiler)

    // room
    implementation(Dependencies.roomRuntime)
    annotationProcessor(Dependencies.roomCompiler)
    ksp(Dependencies.roomCompiler)
    implementation(Dependencies.roomKtx)
    testImplementation(Dependencies.roomTest)

    //  compose charts
    implementation(Dependencies.vicoCompose)
    implementation(Dependencies.vicoCore)

    testImplementation(Dependencies.junitApi)
    testImplementation(Dependencies.junitParams)
    testRuntimeOnly(Dependencies.junitEngine)
    testImplementation(Dependencies.junit)
    androidTestImplementation(Dependencies.androidJUnit)
    androidTestImplementation(Dependencies.espresso)
    androidTestImplementation(platform(Dependencies.composeBomTest))
    androidTestImplementation(Dependencies.uiJUnit4)
    debugImplementation(Dependencies.uiTooling)
    debugImplementation(Dependencies.uiTestManifest)
}

kapt {
    correctErrorTypes = true
}

tasks.withType<Test> {
    useJUnitPlatform() // Make all tests use JUnit 5
}