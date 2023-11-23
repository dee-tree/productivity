import org.gradle.api.JavaVersion

object Config {
    const val minSdkVersion = 26
    const val targetSdkVersion = 34
    const val compileSdkVersion = targetSdkVersion

    const val namespace = "edu.app.productivity"

    const val versionCode = 1
    const val versionName = "0.1"

    const val composeCompilerExtensionVersion = "1.5.3"

    val javaTargetVersion = JavaVersion.VERSION_1_8
    val javaCompileVersion = JavaVersion.VERSION_1_8
    val javaSourceVersion = JavaVersion.VERSION_1_8
}
