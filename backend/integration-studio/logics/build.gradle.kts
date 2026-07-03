plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(libs.ktor.client.core)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.coroutines.core)

    implementation(projects.integrationStudioDomain)
    implementation(projects.integrationStudioTransportKmp)
    implementation(projects.integrationStudioBffTransport)
    implementation(projects.integrationStudioMapping)
    implementation(projects.etlMapper)
    implementation(projects.polynomClient)

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
}