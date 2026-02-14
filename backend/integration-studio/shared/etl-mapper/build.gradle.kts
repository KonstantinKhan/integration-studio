plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(projects.shared.integrationStudioTransportKmp)
    implementation(projects.shared.integrationStudioCommonModels)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}