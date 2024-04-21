plugins {
    id("org.jetbrains.intellij") version "1.17.2"
    java
}

group = "ru.citeck.ecos"
version = "2.0.15"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.28")
    annotationProcessor("org.projectlombok:lombok:1.18.28")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version.set("2024.1")
//    version.set("2023.3.6")
//    version.set("2022.2.5")
    type.set("IU") // Target IDE Platform
    plugins.set(listOf("com.intellij.java", "JavaScript", "org.jetbrains.plugins.yaml", "org.jetbrains.idea.maven"))
}


tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    patchPluginXml {
        sinceBuild.set("222")
        untilBuild.set("250.*")
    }

    runIde {
        jvmArgs("-Xmx2G")
    }

}