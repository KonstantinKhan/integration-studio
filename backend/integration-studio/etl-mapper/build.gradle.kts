plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(projects.integrationStudioTransportKmp)
    implementation(projects.integrationStudioDomain)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}