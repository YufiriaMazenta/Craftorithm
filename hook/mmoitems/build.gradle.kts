repositories {
    maven("https://nexus.phoenixdevt.fr/repository/maven-public/")
}
dependencies {
    compileOnly(project(":core"))
    compileOnly("net.Indyuce:MMOItems-API:6.9.5-SNAPSHOT")
    compileOnly("io.lumine:MythicLib-dist:1.6.2-SNAPSHOT")
}