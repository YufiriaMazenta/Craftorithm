repositories {
    //CrypticLib
    maven("https://repo.crypticlib.incrafttime.top/repository/maven-public/") {
        isAllowInsecureProtocol = true
    }
    //packetevents
    maven("https://repo.codemc.io/repository/maven-releases/")
}

dependencies {
    compileOnly(project(":core"))
    compileOnly("com.crypticlib:bukkit:${rootProject.findProperty("crypticlibVer")}")
    compileOnly("com.crypticlib:bukkit-i18n:${rootProject.findProperty("crypticlibVer")}")
    compileOnly("com.github.retrooper:packetevents-spigot:2.12.2")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
}