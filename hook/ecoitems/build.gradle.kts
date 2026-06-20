repositories {
    //Eco
    maven("https://repo.auxilor.io/repository/maven-public/")
    //CrypticLib
    maven("http://110.42.10.241:8082/repository/maven-public/") {
        isAllowInsecureProtocol = true
    }
}

dependencies {
    compileOnly(project(":core"))
    compileOnly("com.willfp:EcoEnchants:12.5.1")
    compileOnly("com.willfp:EcoItems:5.59.0")
    compileOnly("com.willfp:eco:6.74.2")
    compileOnly("com.willfp:libreforge:4.71.6:all@jar")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("com.crypticlib:bukkit:${rootProject.findProperty("crypticlibVer")}")
}