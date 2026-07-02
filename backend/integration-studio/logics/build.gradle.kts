plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(projects.integrationStudioDomain)

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
}