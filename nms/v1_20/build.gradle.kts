repositories {
    maven("https://repo.tabooproject.org/repository/releases/")
}

dependencies {
    compileOnly(project(":core"))
    compileOnly("ink.ptms.core:v12000:12000:universal")
    compileOnly("com.google.guava:guava:33.2.0-jre")
    compileOnly("com.crypticlib:bukkit:${rootProject.findProperty("crypticlibVer")}")
    compileOnly("com.crypticlib:common-compat:${rootProject.findProperty("crypticlibVer")}")
}