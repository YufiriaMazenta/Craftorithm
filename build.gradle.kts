import java.text.SimpleDateFormat
version = "1.10.24"

plugins {
    `java-library`
    `maven-publish`
    id("io.github.goooler.shadow").version("8.1.7")
}

repositories {
    mavenLocal()
    maven("https://repo.crypticlib.com:8081/repository/maven-public/")
    maven("https://repo.auxilor.io/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
    //Oraxen
    maven("https://repo.oraxen.com/releases")
    maven("https://jitpack.io")
    maven("https://repo.rosewooddev.io/repository/public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.maven.apache.org/maven2/")
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://r.irepo.space/maven/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://nexus.phoenixdevt.fr/repository/maven-public/")
    //EcoEnchants
    maven("https://repo.auxilor.io/repository/maven-public/")
    //NBT-API
    maven("https://repo.codemc.io/repository/maven-public/")
    //Nexo
    maven("https://repo.nexomc.com/releases")
    //CraftEngine
    maven("https://repo.momirealms.net/releases/")
    mavenCentral()
}

dependencies {
    compileOnly("org.jetbrains:annotations:24.0.1")
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    compileOnly("pers.neige.neigeitems:NeigeItems:1.15.113")
    compileOnly("net.kyori:adventure-api:4.14.0")
    compileOnly("org.spigotmc:spigot-api:1.21-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("org.black_ixx:playerpoints:3.2.5")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.github.LoneDev6:API-ItemsAdder:3.6.3-beta-14")
    compileOnly("io.th0rgal:oraxen:1.191.0")
    compileOnly("io.lumine:Mythic-Dist:5.9.1")
    compileOnly("io.lumine:MythicLib-dist:1.6.2-SNAPSHOT")
    compileOnly("net.Indyuce:MMOItems-API:6.9.5-SNAPSHOT")
    compileOnly("com.willfp:EcoEnchants:12.5.1")
    compileOnly("com.willfp:EcoItems:5.59.0")
    compileOnly("com.willfp:eco:6.74.2")
    compileOnly("com.willfp:libreforge:4.71.6:all@jar")
    compileOnly("com.ssomar:SCore:5.24.10.5")
    compileOnly("com.nexomc:nexo:0.7.0")
    compileOnly("net.momirealms:craft-engine-core:0.0.57")
    compileOnly("net.momirealms:craft-engine-bukkit:0.0.57")
    compileOnly(fileTree("libs"))
    implementation("com.crypticlib:bukkit:${rootProject.findProperty("crypticlibVer")}")
    implementation("com.crypticlib:bukkit-ui:${rootProject.findProperty("crypticlibVer")}")
    implementation("com.crypticlib:bukkit-conversation:${rootProject.findProperty("crypticlibVer")}")
    implementation("com.crypticlib:bukkit-i18n:${rootProject.findProperty("crypticlibVer")}")
//    implementation("de.tr7zw:item-nbt-api:2.12.4")
}

group = "com.github.yufiriamazenta"
var pluginVersion: String = version.toString() + "-" + SimpleDateFormat("yyyyMMdd").format(System.currentTimeMillis())
java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21

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
        dependsOn(clean)
        options.encoding = "UTF-8"
    }
    shadowJar {
        archiveFileName.set("Craftorithm-$version.jar")
        relocate("crypticlib", "com.github.yufiriamazenta.craftorithm.crypticlib")
        relocate("de.tr7zw.changeme.nbtapi", "com.github.yufiriamazenta.craftorithm.nbtapi")
    }
}