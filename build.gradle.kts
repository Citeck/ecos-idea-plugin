plugins {
    id("org.jetbrains.intellij") version "1.14.2"
    java
}

group = "ru.citeck.ecos"
version = "2.0.4"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.28")
    annotationProcessor("org.projectlombok:lombok:1.18.28")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.17.0")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version.set("2020.2.4")
    type.set("IU") // Target IDE Platform
    plugins.set(listOf("com.intellij.java", "JavaScript", "org.jetbrains.plugins.yaml"))
}


tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    patchPluginXml {
        sinceBuild.set("202")
        untilBuild.set("250.*")
    }

    runIde {
        jvmArgs("-Xmx2G")
    }

}