repositories {
    //MythicDist
    maven("https://mvn.lumine.io/repository/maven-public/")
    //CrypticLib
    maven("http://110.42.10.241:8082/repository/maven-public/") {
        isAllowInsecureProtocol = true
    }
}
dependencies {
    compileOnly(project(":core"))
    compileOnly("io.lumine:Mythic-Dist:5.3.5")
    compileOnly("com.crypticlib:bukkit:${rootProject.findProperty("crypticlibVer")}")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")

}