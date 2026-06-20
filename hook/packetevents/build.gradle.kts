repositories {
    //CrypticLib
    maven("http://110.42.10.241:8082/repository/maven-public/") {
        isAllowInsecureProtocol = true
    }
    //packetevents
    maven("https://repo.codemc.io/repository/maven-releases/")
}

dependencies {
    compileOnly(project(":core"))
    compileOnly("com.crypticlib:bukkit:${rootProject.findProperty("crypticlibVer")}")
    compileOnly("com.github.retrooper:packetevents-spigot:2.12.2")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
}