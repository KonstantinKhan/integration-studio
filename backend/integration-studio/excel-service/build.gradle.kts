plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotest)
    application
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.poi)

    implementation(libs.logback.classic)
    implementation(libs.log4j.to.slf4j)

    implementation(projects.shared.integrationStudioCommonModels)

    testImplementation(libs.kotest.framework.engine)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertion.core)
    testImplementation(libs.kotest.property)
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("com.khan366kos.etl.excel.service.MainKt")
}
