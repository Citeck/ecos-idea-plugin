import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import java.net.URI

plugins {
    id("org.jetbrains.intellij.platform") version "2.4.0"
    id("com.gradleup.shadow") version "8.3.6"
    kotlin("jvm") version "2.0.21"
    id("java")
}

group = "ru.citeck.idea"
version = "3.3.0"

kotlin {
    jvmToolchain(17)
}

subprojects {
    group = parent!!.group
    version = parent!!.version
}

repositories {
    mavenCentral()
    mavenLocal()

    intellijPlatform {
        localPlatformArtifacts()
        jetbrainsRuntime()

        repositories.ivy {
            this.name = "JetBrains IDE Installers"
            this.url = URI("https://download-cf.jetbrains.com")
            patternLayout {
                listOf(
                    "[organization]/[module]-[revision](-[classifier]).[ext]",
                    "[organization]/[module]-[revision](.[classifier]).[ext]",
                    "[organization]/[revision]/[module]-[revision](-[classifier]).[ext]",
                    "[organization]/[revision]/[module]-[revision](.[classifier]).[ext]",
                ).forEach { artifact(it) }
            }
            metadataSources { artifact() }
            content {
                IntelliJPlatformType.values()
                    .filter { it != IntelliJPlatformType.AndroidStudio }
                    .mapNotNull { it.installer }
                    .forEach {
                        includeModule(it.groupId, it.artifactId)
                    }
            }
        }
        releases()
        snapshots()
        intellijDependencies()
        marketplace()
    }
}

dependencies {
    implementation(project(":citeck-plugin-deps", configuration = "archives"))

    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")

    intellijPlatform {
        intellijIdeaUltimate("2025.1")
        // intellijIdeaUltimate("2024.3.5")

        bundledPlugins(
            "com.intellij.java",
            "JavaScript",
            "org.jetbrains.plugins.yaml",
            "org.jetbrains.idea.maven",
            "org.jetbrains.kotlin"
        )

        testFramework(TestFrameworkType.Platform)
    }

    testImplementation("junit:junit:4.13.2")
}

val generatedResources by lazy {
    val resourcesDir = layout.buildDirectory.file("generated-resources").get().asFile
    if (!resourcesDir.exists()) {
        resourcesDir.mkdir()
    }
    resourcesDir
}

val generateArtifactsIndex by tasks.registering {
    val dirToScan = file("src/main/resources/citeck/artifacts")
    val outputFile = generatedResources.resolve("citeck/artifacts/index.json")
    outputs.file(outputFile)
    doLast {
        val types = dirToScan.walkTopDown()
            .filter { it.isFile && it.name == "meta.json" }
            .map { it.relativeTo(dirToScan).invariantSeparatorsPath }
            .toList()
        outputFile.parentFile.mkdirs()
        outputFile.writeText(Json.encodeToString(types))
    }
}

sourceSets.main {
    resources.srcDir(generatedResources)
}

tasks {

    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    patchPluginXml {
        sinceBuild.set("231.9423.9")
        untilBuild.set("253.*")
    }

    processResources {
        dependsOn("generateArtifactsIndex")
    }

    runIde {
        jvmArgs(
            "-Xmx2G",
        )
    }
}
