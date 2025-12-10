dependencies {
    implementation(rootProject.libs.bundles.configurate)
    implementation(rootProject.libs.cloud.command.core)
    implementation(rootProject.libs.interfaces)
    compileOnly(rootProject.libs.cloud.api)
    compileOnly(rootProject.libs.paper.api)
}