import java.text.SimpleDateFormat

plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenLocal()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://jitpack.io")
    maven("https://repo.rosewooddev.io/repository/public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.maven.apache.org/maven2/")
    maven("https://mvn.lumine.io/repository/maven-public/")
    mavenCentral()
}

dependencies {
    compileOnly("org.jetbrains:annotations:24.0.1")
    compileOnly("org.spigotmc:spigot:1.19.4-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("org.black_ixx:playerpoints:3.2.5")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("me.clip:placeholderapi:2.11.1")
}

group = "com.github.yufiriamazenta"
version = "1.4.0-dev1"
var pluginVersion: String = version.toString() + "-" + SimpleDateFormat("yyyyMMdd").format(System.currentTimeMillis())
java.sourceCompatibility = JavaVersion.VERSION_1_8

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType(JavaCompile::class.java) {
    options.encoding = "UTF-8"
}

tasks {
    val props = HashMap<String, String>()
    props["version"] = pluginVersion
    "processResources"(ProcessResources::class) {
        filesMatching("plugin.yml") {
            expand(props)
        }
        filesMatching("config.yml") {
            expand(props)
        }
    }
}