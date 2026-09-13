plugins {
    alias(libs.plugins.kotlin.jvm)
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}
