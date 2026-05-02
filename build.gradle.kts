import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.time.Instant

plugins {
    antlr
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)

    alias(libs.plugins.jetbrainsCompose)
}

group = "com.kube.log"
version = "1.6.6"

repositories {
    mavenCentral()
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation(libs.logback.classic)

    antlr(libs.antlr)

    testImplementation(platform(libs.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation(compose.desktop.currentOs)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.components.splitpane)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.core.jvm)
    implementation(libs.kotlinx.coroutines.swing)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
}

compose.desktop {
    application {
        mainClass = "com.kube.log.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg)

            macOS {
                bundleID = "com.KubeLog"
                iconFile.set(project.file("src/main/app/AppIcon.icns"))

                signing {
                    sign.set(true)
                    identity.set("Developer ID Application: Code Signing Certificate")
                }
            }
        }
    }
}

composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    metricsDestination = layout.buildDirectory.dir("compose_compiler")
}

tasks.processResources {
    inputs.property("group", project.group)
    inputs.property("name", project.name)
    inputs.property("version", project.version)
    inputs.property("time", Instant.now())

    filesMatching("**/*.properties") {
        expand(inputs.properties)
    }
}

tasks.generateGrammarSource {
    arguments = arguments + listOf("-visitor", "-long-messages", "-package", "com.kube.log.search.query")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    dependsOn(tasks.generateGrammarSource)
    dependsOn(tasks.generateTestGrammarSource)
}

tasks.register("version") {
    description = "Prints the project version"
    doLast {
        println(version)
    }
}