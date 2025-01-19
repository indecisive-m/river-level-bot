plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.serialization") version "2.1.0"

}

group = "org.example"
version = "1.0-SNAPSHOT"

val ktor_version: String by project

val logback_version: String by project

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("io.ktor:ktor-client-logging:$ktor_version")
    testImplementation(kotlin("test"))

}

tasks.test {
    useJUnitPlatform()
}