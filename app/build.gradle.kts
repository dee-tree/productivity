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

    // lottie animation
    implementation(Dependencies.lottieAnimation)

    // hilt
    implementation(Dependencies.hilt)
    implementation(Dependencies.hiltNavigationCompose)
    kapt(Dependencies.hiltCompiler)

    // room
    implementation(Dependencies.roomRuntime)
    ksp(Dependencies.roomCompiler)
    implementation(Dependencies.roomKtx)
    testImplementation(Dependencies.roomTest)
    androidTestImplementation(Dependencies.roomTest)

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