plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)

    implementation(projects.shared.integrationStudioTransportKmp)
    implementation(projects.shared.integrationStudioCommonModels)
    implementation(projects.shared.etlMapper)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    implementation(libs.logback.classic)

    implementation(libs.logging)

}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}