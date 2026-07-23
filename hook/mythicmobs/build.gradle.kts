repositories {
    //MythicDist
    maven("https://mvn.lumine.io/repository/maven-public/")
    //CrypticLib
    maven("https://repo.crypticlib.incrafttime.top/repository/maven-public/")
}
dependencies {
    compileOnly(project(":core"))
    compileOnly("io.lumine:Mythic-Dist:5.3.5")
    compileOnly("com.crypticlib:bukkit:${rootProject.findProperty("crypticlibVer")}")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")

}