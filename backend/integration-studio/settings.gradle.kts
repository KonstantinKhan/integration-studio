rootProject.name = "integration-studio"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

include(":domain")
include(":polynom-dto-kmp")
include(":etl-mapper")
include(":ktor-server-app")
include(":polynom-client")
include(":excel-service")
include(":integration-studio-polynom")
include(":integration-studio-polynom:reference-service")
include(":mapping")
include(":bff-dto")
include(":logics")
include(":mdm-migration-console-app")