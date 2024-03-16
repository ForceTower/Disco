plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.androidLibrary)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
//        it.binaries.framework {
//            baseName = "Singer"
//            isStatic = true
//        }

        it.compilations.configureEach {
            compilerOptions.configure {
                // Try out preview custom allocator in K/N 1.9
                // https://kotlinlang.org/docs/whatsnew19.html#preview-of-custom-memory-allocator
                freeCompilerArgs.add("-Xallocator=custom")

                // https://kotlinlang.org/docs/whatsnew19.html#compiler-option-for-c-interop-implicit-integer-conversions
//                freeCompilerArgs.add("-XXLanguage:+ImplicitSignedToUnsignedIntegerConversion")

                // Enable debug symbols:
                // https://kotlinlang.org/docs/native-ios-symbolication.html
                freeCompilerArgs.add("-Xadd-light-debug=enable")

                // Various opt-ins
                freeCompilerArgs.addAll(
                    "-opt-in=kotlinx.cinterop.ExperimentalForeignApi",
                    "-opt-in=kotlinx.cinterop.BetaInteropApi",
                )
            }
        }
    }

    targets.configureEach {
        compilations.configureEach {
            compilerOptions.configure {
                freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonMain.dependencies {
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.contentnegotiation)
            implementation(libs.ktor.serialization.json)
            implementation(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "dev.forcetower.unes.singer"
    compileSdk = 34
    defaultConfig {
        minSdk = 28
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}