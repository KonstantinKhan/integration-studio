plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(projects.polynomDtoKmp)
    implementation(projects.bffDto)
    implementation(projects.domain)

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}