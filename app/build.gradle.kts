plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.example.happyplaces"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.happyplaces"
        minSdk = 30
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures{
        viewBinding = true
    }
}

dependencies {
    implementation("de.hdodenhof:circleimageview:3.1.0") //Dexter dependency
    implementation("com.karumi:dexter:6.2.3")

    implementation("androidx.room:room-runtime:2.6.1") // Replace with the latest version
    implementation("androidx.room:room-ktx:2.6.1") // For Kotlin extensions
    kapt("androidx.room:room-compiler:2.6.1") // Annotation processor
    implementation("androidx.activity:activity-ktx:1.9.3")// Support for co-routines with activities

    implementation ("com.github.bumptech.glide:glide:4.16.0") //Glide dependencies
    kapt ("com.github.bumptech.glide:compiler:4.12.0") // Support for glide annotations

    implementation("com.google.android.libraries.places:places:4.1.0") //Places dependency

    implementation("com.google.android.gms:play-services-location:21.3.0") //GMS Location dependency
    implementation(libs.play.services.maps) // Maps dependency

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}