import java.text.SimpleDateFormat

plugins {
    `java-library`
    `maven-publish`
    id("io.github.goooler.shadow").version("8.1.7")
}

repositories {
    mavenLocal()
    //CrypticLib
    maven("http://110.42.10.241:8082/repository/maven-public/") {
        isAllowInsecureProtocol = true
    }
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    implementation(project(":hook:azureflow"))
    implementation(project(":hook:craftengine"))
    implementation(project(":hook:ecoitems"))
    implementation(project(":hook:executableitems"))
    implementation(project(":hook:itemsadder"))
    implementation(project(":hook:mmoitems"))
    implementation(project(":hook:mythicmobs"))
    implementation(project(":hook:neigeitems"))
    implementation(project(":hook:nexo"))
    implementation(project(":hook:oraxen"))
    implementation(project(":hook:sx-item"))
    implementation(project(":hook:packetevents"))
    implementation(project(":hook:protocollib"))
    implementation("com.crypticlib:bukkit:${rootProject.findProperty("crypticlibVer")}")
}

version = "${rootProject.findProperty("pluginVer")}"
group = "com.github.yufiriamazenta"
var pluginVersion: String = version.toString() + "-" + SimpleDateFormat("yyyyMMdd").format(System.currentTimeMillis())
java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

val crypticlibRelocate = "pers.yufiria.craftorithm.crypticlib"

tasks {
    val props = HashMap<String, String>()
    props["version"] = pluginVersion
    processResources {
        filesMatching("plugin.yml") {
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
        relocate("crypticlib", crypticlibRelocate)
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "io.github.goooler.shadow")
    version = rootProject.version
    java.sourceCompatibility = JavaVersion.VERSION_21
    java.targetCompatibility = JavaVersion.VERSION_21
    repositories {
        mavenLocal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
    dependencies {
        compileOnly("org.jetbrains:annotations:24.0.1")
        compileOnly("com.crypticlib:bukkit:${rootProject.findProperty("crypticlibVer")}")
    }
    tasks {
        build {
            dependsOn(shadowJar)
        }
        compileJava {
            dependsOn(clean)
            options.encoding = "UTF-8"
        }
        shadowJar {
            relocate("crypticlib", crypticlibRelocate)
        }
    }
}
