import java.text.SimpleDateFormat
version = "2.0.0-dev18"

plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow").version("7.1.2")
}

repositories {
    mavenLocal()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("http://repo.crypticlib.com:8081/repository/maven-public/") {
        isAllowInsecureProtocol = true
    }
    maven("https://jitpack.io")
    maven("https://repo.rosewooddev.io/repository/public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.maven.apache.org/maven2/")
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://r.irepo.space/maven/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://nexus.phoenixdevt.fr/repository/maven-public/")
    mavenCentral()
}

dependencies {
    compileOnly("org.jetbrains:annotations:24.0.1")
    compileOnly("io.papermc.paper:paper-api:1.20.4")
    compileOnly("pers.neige.neigeitems:NeigeItems:1.15.113")
    compileOnly("net.kyori:adventure-api:4.14.0")
    compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("org.black_ixx:playerpoints:3.2.5")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("me.clip:placeholderapi:2.11.1")
    compileOnly("com.github.LoneDev6:API-ItemsAdder:3.5.0b")
    compileOnly("com.github.oraxen:oraxen:1.160.0")
    compileOnly("io.lumine:Mythic-Dist:5.3.5")
    compileOnly("io.lumine:MythicLib-dist:1.6.2-SNAPSHOT")
    compileOnly("net.Indyuce:MMOItems-API:6.9.5-SNAPSHOT")
    implementation("com.crypticlib:CrypticLib:0.15.0")
}

group = "com.github.yufiriamazenta"
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