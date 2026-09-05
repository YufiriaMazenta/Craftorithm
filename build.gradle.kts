plugins {
    `java-library`
    `maven-publish`
    id("io.github.goooler.shadow").version("8.1.7")
}

repositories {
    mavenLocal()
    //CrypticLib
    maven("https://repo.crypticlib.com/repository/maven-public/")
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    //hook与nms模块全部打入最终jar, 新nms模块只需include进settings.gradle.kts
    rootProject.subprojects
        .filter { it.path == ":hook" || it.path.startsWith(":nms:") }
        .forEach { implementation(project(it.path)) }
    implementation("com.crypticlib:bukkit:${rootProject.findProperty("crypticlibVer")}")
}

version = "${rootProject.findProperty("pluginVer")}"
group = "pers.yufiria.craftorithm"
val gitHash: String by lazy {
    runCatching {
        val process = ProcessBuilder("git", "rev-parse", "--short", "HEAD")
            .directory(project.rootDir)
            .start()
        val exitCode = process.waitFor()
        if (exitCode == 0) {
            process.inputStream.bufferedReader(Charsets.UTF_8).readText().trim()
        } else {
            null
        }
    }.getOrNull() ?: "unknown"
}

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
    props["version"] = "$version-$gitHash"
    processResources {
        outputs.upToDateWhen { false }
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
    build {
        dependsOn(shadowJar)
    }
    compileJava {
//        dependsOn(clean)
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
        //CrypticLib
        maven("https://repo.crypticlib.com/repository/maven-public/")
    }
    dependencies {
        compileOnly("org.jetbrains:annotations:${rootProject.findProperty("jetbrainsAnnotationsVer")}")
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
