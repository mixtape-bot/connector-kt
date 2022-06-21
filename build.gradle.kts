import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform.getCurrentOperatingSystem
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension as KME
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithHostTests as KNTWHT

plugins {
    kotlin("multiplatform") version "1.6.21"
}

group = "lol.dimensional"
version = "1.0"

repositories {
    maven("https://maven.dimensional.fun/releases")
    maven("https://jitpack.io/")
    mavenCentral()
}

kotlin {
    jvm {
        compilations["main"].kotlinOptions {
            jvmTarget = "16"
        }

        compilations["main"].dependencies {
            implementation("gg.mixtape:native-loader:1.0")
            implementation("com.sedmelluq:lavaplayer:1.6.4")
            implementation("net.dv8tion:JDA:5.0.0-alpha.11")
            implementation("com.github.minndevelopment:jda-ktx:master-SNAPSHOT")
            implementation("ch.qos.logback:logback-classic:1.2.10")
            implementation(kotlin("stdlib"))
        }
    }

    setupNative("native") {
        val usrInclude = File("/usr/include")
        val mingwPath = File(System.getenv("MINGW64_DIR") ?: "C:/msys64/mingw64")

        binaries {
            sharedLib {
                baseName = "kt_native"
            }
        }

        compilations["main"].cinterops {
            // JDK is required here, JRE is not enough
            val jvm by creating {
                val javaHome = File(System.getenv("JAVA_HOME") ?: System.getProperty("java.home"))
                packageName = "lib.jni"

                includeDirs(
                    Callable { File(javaHome, "include") },
                    Callable { File(javaHome, "include/darwin") },
                    Callable { File(javaHome, "include/linux") },
                    Callable { File(javaHome, "include/win32") }
                )
            }

            val opus by creating {
                packageName = "lib.opus"
                includeDirs(Callable { File(usrInclude, "opus") })
            }

            val samplerate by creating {
                packageName = "lib.samplerate"
                includeDirs(Callable { usrInclude })
            }
        }
    }

    sourceSets {
        val nativeMain by getting
    }
}

fun KME.setupNative(name: String, configure: KNTWHT.() -> Unit): KNTWHT {
    val os = getCurrentOperatingSystem()
    return when {
        os.isLinux -> linuxX64(name, configure)
        os.isWindows -> mingwX64(name, configure)
        os.isMacOsX -> macosX64(name, configure)
        else -> error("OS $os is not supported")
    }
}
