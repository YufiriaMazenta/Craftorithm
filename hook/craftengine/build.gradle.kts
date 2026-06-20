repositories {
    //CraftEngine
    maven("https://repo.momirealms.net/releases/")
    //CrypticLib
    maven("http://110.42.10.241:8082/repository/maven-public/") {
        isAllowInsecureProtocol = true
    }
}

dependencies {
    compileOnly(project(":core"))
    compileOnly("net.momirealms:craft-engine-core:26.5")
    compileOnly("net.momirealms:craft-engine-bukkit:26.5")
    compileOnly("com.crypticlib:bukkit:${rootProject.findProperty("crypticlibVer")}")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
}