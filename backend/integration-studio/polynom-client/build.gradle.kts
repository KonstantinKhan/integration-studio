plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    implementation(libs.logback.classic)

    implementation(libs.logging)

    implementation(projects.polynomDtoKmp)
    implementation(projects.domain)
    implementation(projects.etlMapper)
    implementation(projects.mapping)
    implementation(projects.bffDto)
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}