plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "com.khan366kos"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(projects.integrationStudioCommonModels)
    implementation(projects.integrationStudioTransportKmp)

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
}