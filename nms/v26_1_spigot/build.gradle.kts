repositories {
    maven("https://repo.tabooproject.org/repository/releases/")
}

dependencies {
    compileOnly(project(":core"))
    compileOnly(project(":nms:common"))
    compileOnly("ink.ptms.core:v260100:260100-minimize-java21")
    compileOnly("com.google.guava:guava:33.2.0-jre")
    compileOnly("com.crypticlib:bukkit:${rootProject.findProperty("crypticlibVer")}")
    compileOnly("com.crypticlib:bukkit-util:${rootProject.findProperty("crypticlibVer")}")
    compileOnly("com.crypticlib:common-compat:${rootProject.findProperty("crypticlibVer")}")
    compileOnly("org.jspecify:jspecify:1.0.0")
    compileOnly("it.unimi.dsi:fastutil:8.5.18")
}