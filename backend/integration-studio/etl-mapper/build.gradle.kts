plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(projects.integrationStudioTransportKmp)
    implementation(projects.integrationStudioDomain)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}