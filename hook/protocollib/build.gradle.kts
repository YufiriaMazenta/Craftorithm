repositories {
    //CrypticLib
    maven("https://repo.crypticlib.incrafttime.top/repository/maven-public/") {
        isAllowInsecureProtocol = true
    }
}

dependencies {
    compileOnly(project(":core"))
    compileOnly("com.crypticlib:bukkit:${rootProject.findProperty("crypticlibVer")}")
    compileOnly("com.crypticlib:bukkit-i18n:${rootProject.findProperty("crypticlibVer")}")
    compileOnly("net.dmulloy2:ProtocolLib:5.4.0")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
}