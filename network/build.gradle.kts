plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("plugin.serialization") version "2.1.10"
    id("maven-publish")
    id("org.jetbrains.dokka") version "1.8.10"
    id("jacoco") // Add JaCoCo plugin
}
jacoco {
    toolVersion = "0.8.8" // Specify the JaCoCo version
}
android {
    namespace = "com.dubizzle.network"
    compileSdk = 34

    defaultConfig {
        minSdk = 23
        targetSdk = 34

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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //networking
    api(platform(libs.okhttp.bom))
    api(libs.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.converter.scalars)
    implementation (libs.retrofit2.rxjava2.adapter)
    implementation (libs.logging.interceptor)
    // Gson for json parsing
    implementation (libs.gson)
    implementation (libs.koin.android)
    implementation (libs.kotlinx.serialization.json)

    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit.ktx)

    testImplementation(libs.mockk)
    testImplementation (libs.test.core.ktx)

}

publishing {

    publications {
        create<MavenPublication>("release") {
            groupId = "com.dubizzle"  // Change to your GitHub username
            artifactId = "network"             // Change to your library name
            version = System.getenv("VERSION_NAME")?.plus("_beta") ?: "0.0.8"

            afterEvaluate {
                from(components["release"])
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/zainempg/network")
            credentials {
                val username = System.getenv("GPR_USERNAME") ?: project.findProperty("GPR_USERNAME") as String?
                val password = System.getenv("GPR_TOKEN") ?: project.findProperty("GPR_TOKEN") as String?

                if (username == null || password == null) {
                    throw GradleException("GitHub Packages credentials are not set. Please set GPR_USERNAME and GPR_TOKEN environment variables.")
                }

                this.username = username
                this.password = password
            }
        }
    }
}
tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)

    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R\$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*"
    )

    val debugTree = fileTree("${buildDir}/intermediates/javac/debug") {
        exclude(fileFilter)
    }

    val kotlinDebugTree = fileTree("${buildDir}/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }

    val execFile = fileTree(buildDir) {
        include("jacoco/testDebugUnitTest.exec")
    }

    sourceDirectories.setFrom(files("$projectDir/src/main/java"))
    classDirectories.setFrom(files(debugTree, kotlinDebugTree))
    executionData.setFrom(files(execFile))
}

tasks.dokkaHtml.configure {
    outputDirectory.set(buildDir.resolve("dokka"))
}
