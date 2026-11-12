// Android: solo el arranque (Activity, flavors dev/prod, google-services). La app vive en :shared.
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

// google-services.json no se sube a git (ver CONTRIBUTING.md). El plugin solo se aplica si ya
// descargaste alguno; así el proyecto compila antes de crear los proyectos de Firebase.
// Rutas: androidApp/src/dev/google-services.json y androidApp/src/prod/google-services.json
val tieneGoogleServices = listOf("dev", "prod").any { file("src/$it/google-services.json").exists() }
if (tieneGoogleServices) apply(plugin = libs.plugins.google.services.get().pluginId)

// ./gradlew ... -Pfulbo.emuladores=true  (o en ~/.gradle/gradle.properties) conecta la variante
// dev a los emuladores de Firebase. Ver firebase/README.md.
val usarEmuladores = (findProperty("fulbo.emuladores") as String?) == "true"

kotlin {
    compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) }
}

android {
    namespace = "pe.fulbo.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "pe.fulbo.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
        vectorDrawables { useSupportLibrary = true }
    }

    // dev = proyecto Firebase fulbo-dev, prod = fulbo-prod
    flavorDimensions += "env"
    productFlavors {
        create("dev") {
            dimension = "env"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            buildConfigField("boolean", "USAR_EMULADORES", usarEmuladores.toString())
            // 10.0.2.2 = localhost de tu PC visto desde el emulador de Android
            buildConfigField("String", "EMULADOR_HOST", "\"10.0.2.2\"")
            resValue("string", "app_name", "FULBO Dev")
        }
        create("prod") {
            dimension = "env"
            buildConfigField("boolean", "USAR_EMULADORES", "false")
            buildConfigField("String", "EMULADOR_HOST", "\"\"")
            resValue("string", "app_name", "FULBO")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" }
    }
    lint {
        // El lint de AGP 8.7 se cae con este detector de lifecycle y Kotlin 2.2. La app no usa
        // LiveData. Quitar al subir AGP.
        disable += "NullSafeMutableLiveData"
    }
}

dependencies {
    implementation(project(":shared"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.activity.compose)
    implementation(libs.koin.android)
}
