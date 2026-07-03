plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(libs.ktor.client.core)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.coroutines.core)

    implementation(projects.bffDto)
    implementation(projects.domain)
    implementation(projects.polynomDtoKmp)
    implementation(projects.mapping)
    implementation(projects.polynomClient)

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
}