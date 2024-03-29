plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.jiaoay.multiimageview"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.jiaoay.multiimageview"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.9.22"))
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core")
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android")
    implementation(group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version = "2.7.0")
    implementation(group = "androidx.fragment", name = "fragment-ktx", version = "1.6.2")

    implementation(group = "com.github.bumptech.glide", name = "glide", version = "4.16.0")
}