plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(projects.integrationStudioDomain)
    implementation(projects.excelService)
    implementation(projects.logics)

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
}