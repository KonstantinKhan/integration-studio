rootProject.name = "integration-studio"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")


include(":shared")
include(":shared:integration-studio-transport-kmp")
include(":shared:integration-studio-common-models")
include(":shared:etl-mapper")
include(":ktor-server-app")
include(":polynom-client")
include(":excel-service")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
