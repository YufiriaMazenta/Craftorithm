import java.text.SimpleDateFormat

plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow").version("7.1.2")
    id("io.papermc.paperweight.userdev").version("1.5.5")
}

repositories {
    mavenLocal()
    maven("https://jitpack.io")
    mavenCentral()
}

dependencies {
    implementation("com.github.YufiriaMazenta:CrypticLib:1.0.9")
    implementation(project(":common"))
    implementation(project(":v1_20_R2"))
    paperweight.paperDevBundle("1.20.2-R0.1-SNAPSHOT")
}

group = "com.github.yufiriamazenta"
version = "2.0.0-dev2"
var pluginVersion: String = version.toString() + "-" + SimpleDateFormat("yyyyMMdd").format(System.currentTimeMillis())
java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17

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
    compileJava {
        options.encoding = "UTF-8"
    }
    assemble {
        dependsOn(shadowJar)
    }
    shadowJar {
        archiveFileName.set("Craftorithm-$version.jar")
        relocate("crypticlib", "com.github.yufiriamazenta.craftorithm.crypticlib")
    }
    assemble {
        dependsOn(shadowJar)
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    tasks {
        compileJava {
            options.encoding = "UTF-8"
        }
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
        maven("https://nexus.phoenixdevt.fr/repository/maven-public/")
        mavenCentral()
    }
    dependencies {
        compileOnly("org.jetbrains:annotations:24.0.1")
        compileOnly("org.spigotmc:spigot-api:1.20.1-R0.1-SNAPSHOT")
        compileOnly("com.github.YufiriaMazenta:CrypticLib:1.0.9")
    }
    publishing {
        publications.create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}