repositories {
    maven("https://r.irepo.space/maven/")
    //CrypticLib
    maven("http://110.42.10.241:8082/repository/maven-public/") {
        isAllowInsecureProtocol = true
    }
}
dependencies {
    compileOnly(project(":core"))
    compileOnly("com.crypticlib:bukkit:${rootProject.findProperty("crypticlibVer")}")
    compileOnly("pers.neige.neigeitems:NeigeItems:1.15.113")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
}