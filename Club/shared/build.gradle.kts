plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.sqldelight)
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
        it.binaries.framework {
            baseName = "Club"
            isStatic = true
        }

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
            implementation(libs.sqldelight.androidDriver)
            implementation(libs.androidx.viewmodel)
            implementation(libs.koin.android)
        }
        iosMain.dependencies {
            implementation(libs.sqldelight.nativeDriver)
            implementation(libs.koin.core)
        }
        commonMain.dependencies {
            implementation(libs.sqldelight.coroutinesExtensions)
            implementation(libs.koin.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.uuid)
            api(projects.singer)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

sqldelight {
    databases {
        create("GeneralDatabase") {
            packageName.set("dev.forcetower.unes.club.data.storage.database")
        }
    }
}

android {
    namespace = "dev.forcetower.unes.club"
    compileSdk = 34
    defaultConfig {
        minSdk = 28
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
