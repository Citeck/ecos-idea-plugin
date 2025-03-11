import com.github.jengelman.gradle.plugins.shadow.transformers.AppendingTransformer

plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.6"
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://nexus.citeck.ru/repository/maven-public")
}

dependencies {
    implementation("ru.citeck.ecos.commons:ecos-commons:2.17.14")
    implementation("ru.citeck.ecos:ecos-snakeyaml:2.2.0")
    implementation("ru.citeck.ecos:ecos-jackson:2.10.4.3")
    implementation("io.github.microutils:kotlin-logging:1.12.5")
    implementation("ru.citeck.ecos.webapp:ecos-webapp-api:1.10.1")
    implementation("ru.citeck.ecos.context:ecos-context-lib:1.3.8")
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        relocate("mu", "citeck.mu") // KotlinLogging
        transform(AppendingTransformer::class.java) {
            exclude("tables/**")
        }
        configurations = listOf(project.configurations.runtimeClasspath.get())
    }

    test {
        useJUnitPlatform()
    }

    jar {
        enabled = false
    }
}

artifacts {
    add("archives", tasks.shadowJar)
}
