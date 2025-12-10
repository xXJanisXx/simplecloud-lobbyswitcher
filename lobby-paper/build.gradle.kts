dependencies {
    api(project(":lobby-shared"))
    implementation(rootProject.libs.cloud.command.paper)
    implementation(rootProject.libs.interfaces)
    compileOnly(rootProject.libs.cloud.api)
    compileOnly(rootProject.libs.paper.api)
}