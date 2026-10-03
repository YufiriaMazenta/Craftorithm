import org.gradle.api.attributes.java.TargetJvmVersion
import org.gradle.jvm.toolchain.JavaLanguageVersion

plugins {
    `java-library`
    `maven-publish`
    id("io.github.goooler.shadow").version("8.1.7")
}

repositories {
    mavenLocal()
    //CrypticLib
    maven("https://repo2.crypticlib.com/releases/")
    maven("https://repo2.crypticlib.com/snapshots/")
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
    props["postgresql_version"] = rootProject.findProperty("postgresqlVer").toString()
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
    java.toolchain.languageVersion.set(JavaLanguageVersion.of(25))
    configurations.configureEach {
        if (name == "compileClasspath" || name == "testCompileClasspath" || name == "testRuntimeClasspath") {
            attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 25)
        }
    }
    repositories {
        maven("https://repo.papermc.io/repository/maven-public/")
        //CrypticLib
        maven("https://repo2.crypticlib.com/releases/")
        maven("https://repo2.crypticlib.com/snapshots/")
    }
    dependencies {
        compileOnly("org.jetbrains:annotations:${rootProject.findProperty("jetbrainsAnnotationsVer")}")
        compileOnly("com.crypticlib:bukkit:${rootProject.findProperty("crypticlibVer")}")
    }
    tasks {
        compileJava {
            options.encoding = "UTF-8"
        }
    }
}
