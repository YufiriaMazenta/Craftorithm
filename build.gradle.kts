plugins {
    `java-library`
    `maven-publish`
    id("io.github.goooler.shadow").version("8.1.7")
}

rootProject.version = "2.0.0-alpha1"
rootProject.group = "pers.yufiria"

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "io.github.goooler.shadow")
    java.sourceCompatibility = JavaVersion.VERSION_17
    java.targetCompatibility = JavaVersion.VERSION_17
    tasks {
        build {
            dependsOn(shadowJar)
        }
        compileJava {
            dependsOn(clean)
            options.encoding = "UTF-8"
        }
        shadowJar {
            archiveFileName.set("${rootProject.name}-${project.name}-${rootProject.version}.jar")
            relocate("crypticlib", "com.github.yufiriamazenta.craftorithm.crypticlib")
            relocate("de.tr7zw.changeme.nbtapi", "com.github.yufiriamazenta.craftorithm.nbtapi")
        }
    }
    publishing {
        publications.create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}