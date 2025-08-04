plugins {
    id("org.jetbrains.intellij") version "1.17.2"
    java
}

group = "ru.citeck.ecos"
version = "2.0.21"

repositories {
    mavenCentral()
    maven("https://nexus.citeck.ru/repository/maven-public")
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.28")
    annotationProcessor("org.projectlombok:lombok:1.18.28")

    implementation("ru.citeck.ecos.commons:ecos-commons:2.17.18")
    implementation("ru.citeck.ecos:ecos-snakeyaml:2.2.0")
    implementation("ru.citeck.ecos:ecos-jackson:2.10.4.3")
    implementation("io.github.microutils:kotlin-logging:1.12.5")
    implementation("ru.citeck.ecos.webapp:ecos-webapp-api:1.10.1")
    implementation("ru.citeck.ecos.context:ecos-context-lib:1.3.8")

}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
//    version.set("2024.1")
    version.set("2023.3.8")
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