plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.ktor)

    application
}


application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.sessions)

    implementation(libs.ktor.client.cio)

    implementation(libs.ktor.serialization.kotlinx.json)

    implementation(libs.logback.classic)

    implementation(projects.shared.integrationStudioTransportKmp)
    implementation(projects.shared.integrationStudioCommonModels)
    implementation(projects.shared.etlMapper)
    implementation(projects.polynomClient)

    implementation(projects.excelService)
    implementation(project(":shared:etl-mapper"))

    testImplementation("io.ktor:ktor-server-test-host")
}

kotlin {
    jvmToolchain(21)
}