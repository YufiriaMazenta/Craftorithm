import java.text.SimpleDateFormat

plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow").version("7.1.2")
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
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://nexus.phoenixdevt.fr/repository/maven-public/")
    maven("http://repo.crypticlib.com:8081/repository/maven-public/") {
        isAllowInsecureProtocol = true
    }
    mavenCentral()
}

dependencies {
    compileOnly("org.jetbrains:annotations:24.0.1")
    compileOnly("org.spigotmc:spigot:1.19.4-R0.1-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT") {
        targetConfiguration = "runtime"
    }
    compileOnly("net.kyori:adventure-api:4.14.0")
    compileOnly("org.spigotmc:spigot-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("org.black_ixx:playerpoints:3.2.5")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("me.clip:placeholderapi:2.11.1")
    compileOnly("com.github.LoneDev6:API-ItemsAdder:3.5.0b")
    compileOnly("com.github.oraxen:oraxen:1.160.0")
    compileOnly("io.lumine:Mythic-Dist:5.3.5")
    implementation("com.crypticlib:CrypticLib:0.3.3")
}

group = "com.github.yufiriamazenta"
version = "1.5.2-dev1"
var pluginVersion: String = version.toString() + "-" + SimpleDateFormat("yyyyMMdd").format(System.currentTimeMillis())
java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks {
    val props = HashMap<String, String>()
    props["version"] = pluginVersion
    processResources {
        filesMatching("plugin.yml") {
            expand(props)
        }
        filesMatching("config.yml") {
            expand(props)
        }
    }
    build {
        dependsOn(shadowJar)
    }
    compileJava {
        options.encoding = "UTF-8"
    }
    shadowJar {
        archiveFileName.set("Craftorithm-$version.jar")
        relocate("crypticlib", "com.github.yufiriamazenta.craftorithm.crypticlib")
    }
}