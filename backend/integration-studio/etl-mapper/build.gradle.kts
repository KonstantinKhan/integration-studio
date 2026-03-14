plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(projects.integrationStudioTransportKmp)
    implementation(projects.integrationStudioCommonModels)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}