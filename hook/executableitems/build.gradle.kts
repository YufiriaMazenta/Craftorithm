repositories {
    //CrypticLib
    maven("http://110.42.10.241:8082/repository/maven-public/") {
        isAllowInsecureProtocol = true
    }
}

dependencies {
    compileOnly(project(":core"))
    compileOnly("com.ssomar:SCore:5.24.10.5")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("com.crypticlib:bukkit:${rootProject.findProperty("crypticlibVer")}")
}