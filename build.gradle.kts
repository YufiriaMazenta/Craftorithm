version = "2.0.0-dev1"
group = "com.github.yufiria.craftorithm"
java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8
var repositoryUrl = "http://repo.crypticlib.com:8081/repository/"
repositoryUrl = if (rootProject.version.toString().endsWith("SNAPSHOT")) {
    repositoryUrl.plus("maven-snapshots/")
} else {
    repositoryUrl.plus("maven-releases/")
}

plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow").version("7.1.2")
}

repositories {
    mavenLocal()
    maven("http://repo.crypticlib.com:8081/repository/maven-public/") {
        isAllowInsecureProtocol = true
    }
    mavenCentral()
}

dependencies {
    implementation("com.crypticlib:CrypticLib:0.16.0")
    implementation(project(":api"))
    implementation(project(":plugin"))
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    compileJava {
        options.encoding = "UTF-8"
    }
    shadowJar {
        archiveFileName.set("Craftorithm-$version.jar")
        relocate("crypticlib", "com.github.yufiria.craftorithm.crypticlib")
    }
}

publishing {
    publishing {
        publications.create<MavenPublication>("maven") {
            from(components["java"])
            groupId = rootProject.group as String?
        }
        repositories {
            maven {
                url = uri(repositoryUrl)
                isAllowInsecureProtocol = true
                credentials {
                    username = project.findProperty("maven_username").toString()
                    password = project.findProperty("maven_password").toString()
                }
            }
        }
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    repositories {
        mavenLocal()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        mavenCentral()
    }

    dependencies {
        compileOnly("org.jetbrains:annotations:24.0.1")
        compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")

    }

    java.sourceCompatibility = JavaVersion.VERSION_1_8
    java.targetCompatibility = JavaVersion.VERSION_1_8
    version = rootProject.version

    tasks {
        compileJava {
            options.encoding = "UTF-8"
        }
    }

    publishing {
        publishing {
            publications.create<MavenPublication>("maven") {
                from(components["java"])
                groupId = rootProject.group as String?
            }
            repositories {
                maven {
                    url = uri(repositoryUrl)
                    isAllowInsecureProtocol = true
                    credentials {
                        username = project.findProperty("maven_username").toString()
                        password = project.findProperty("maven_password").toString()
                    }
                }
            }
        }
    }
}