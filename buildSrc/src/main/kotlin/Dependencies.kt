object Dependencies {
    val androidCoreKtx by lazy { "androidx.core:core-ktx:${Versions.androidCoreKtxVersion}" }

    val lifecycleRuntimeKtx by lazy { "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycleVersion}" }
    val lifecycleRuntimeCompose by lazy { "androidx.lifecycle:lifecycle-runtime-compose:${Versions.lifecycleVersion}" }
    val lifecycleViewModelKtx by lazy { "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycleVersion}" }
    val lifecycleViewModelCompose by lazy { "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.lifecycleVersion}" }
    val lifecycleService by lazy { "androidx.lifecycle:lifecycle-service:${Versions.lifecycleVersion}" }

    val activityCompose by lazy { "androidx.activity:activity-compose:${Versions.activityComposeVersion}" }

    val composeBom by lazy { "androidx.compose:compose-bom:${Versions.composeBomVersion}" }
    val composeUI by lazy { "androidx.compose.ui:ui" }
    val composeUIGraphics by lazy { "androidx.compose.ui:ui-graphics" }
    val composeUIPreview by lazy { "androidx.compose.ui:ui-tooling-preview" }
    val composeMaterial3 by lazy { "androidx.compose.material3:material3:${Versions.material3Version}" }

    val datastore by lazy { "androidx.datastore:datastore-preferences:${Versions.dataStoreVersion}" }

    val coroutinesCore by lazy { "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutinesVersion}" }
    val coroutinesAndroid by lazy { "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutinesVersion}" }

    val splashScreenCore by lazy { "androidx.core:core-splashscreen:${Versions.splashScreenVersion}" }

    val lottieAnimation by lazy { "com.airbnb.android:lottie-compose:${Versions.lottieVersion}" }

    val hilt by lazy { "com.google.dagger:hilt-android:${Versions.hiltVersion}" }
    val hiltNavigationCompose by lazy { "androidx.hilt:hilt-navigation-compose:${Versions.hiltNavigationComposeVersion}" }
    val hiltCompiler by lazy { "com.google.dagger:hilt-android-compiler:${Versions.hiltVersion}" }

    val roomRuntime by lazy { "androidx.room:room-runtime:${Versions.roomVersion}" }
    val roomCompiler by lazy { "androidx.room:room-compiler:${Versions.roomVersion}" }
    val roomKtx by lazy { "androidx.room:room-ktx:${Versions.roomVersion}" }
    val roomTest by lazy { "androidx.room:room-testing:${Versions.roomVersion}" }

    val gson by lazy { "com.google.code.gson:gson:${Versions.gsonVersion}" }

    val vicoCompose by lazy { "com.patrykandpatrick.vico:compose-m3:${Versions.vicoVersion}" }
    val vicoCore by lazy { "com.patrykandpatrick.vico:core:${Versions.vicoVersion}" }

    val junitApi by lazy { "org.junit.jupiter:junit-jupiter-api:${Versions.junit5Version}" }
    val junitParams by lazy { "org.junit.jupiter:junit-jupiter-params:${Versions.junit5Version}" }
    val junitEngine by lazy { "org.junit.jupiter:junit-jupiter-engine:${Versions.junit5Version}" }
    val junit by lazy { "junit:junit:${Versions.junitVersion}" }
    val androidJUnit by lazy { "androidx.test.ext:junit:${Versions.androidJUnitVersion}" }
    val espresso by lazy { "androidx.test.espresso:espresso-core:${Versions.espressoVersion}" }
    val composeBomTest by lazy { "androidx.compose:compose-bom:${Versions.composeBomVersion}" }
    val uiJUnit4 by lazy { "androidx.compose.ui:ui-test-junit4" }
    val uiTooling by lazy { "androidx.compose.ui:ui-tooling" }
    val uiTestManifest by lazy { "androidx.compose.ui:ui-test-manifest" }
}
