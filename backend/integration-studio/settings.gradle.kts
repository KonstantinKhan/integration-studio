rootProject.name = "integration-studio"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")


include(":integration-studio-transport-kmp")
include(":integration-studio-common-models")
include(":etl-mapper")
include(":ktor-server-app")
include(":polynom-client")
include(":excel-service")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

include("integration-studio-polynom")
include("integration-studio-polynom:reference-service")
include("integration-studio-polynom:reference-service")