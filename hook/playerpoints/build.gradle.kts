repositories {
    //CrypticLib
    maven("http://110.42.10.241:8082/repository/maven-public/") {
        isAllowInsecureProtocol = true
    }
    //PlayerPoints
    maven("https://repo.rosewooddev.io/repository/public/")
}

dependencies {
    compileOnly(project(":core"))
    compileOnly("com.crypticlib:bukkit:${rootProject.findProperty("crypticlibVer")}")
    compileOnly("com.crypticlib:bukkit-i18n:${rootProject.findProperty("crypticlibVer")}")
    compileOnly("org.black_ixx:playerpoints:3.2.5")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
}