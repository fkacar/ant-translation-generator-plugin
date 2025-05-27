plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.20"
    id("org.jetbrains.intellij") version "1.16.1"
}

group = "com.ant"
version = "1.0.0"

repositories {
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2023.2.5")
    
    // IDE tipini buradan değiştirebilirsiniz:
    // - "IC" = IntelliJ IDEA Community Edition
    // - "IU" = IntelliJ IDEA Ultimate Edition
    // - "RD" = Rider
    // - "PS" = PhpStorm
    // - "WS" = WebStorm
    type.set("RD") // Rider için
    
    // Disable IDE auto-reloading
    downloadSources.set(false)
    instrumentCode.set(false)
}

dependencies {
    implementation("org.json:json:20231013")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.20")
    
    // Test dependencies
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("232")
        untilBuild.set("241.*")
    }

    runIde {
        // Rider'ı çalıştırırken daha fazla bellek ayır
        jvmArgs("-Xmx2048m")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
    
    test {
        useJUnitPlatform()
    }
}
