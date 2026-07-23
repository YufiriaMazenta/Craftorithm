repositories {
    maven("https://jitpack.io")
    //CrypticLib
    maven("https://repo.crypticlib.incrafttime.top/repository/maven-public/")
}

dependencies {
    compileOnly(project(":core"))
    compileOnly("com.github.Saukiya:SX-Item:4.4.0")
    compileOnly("com.crypticlib:bukkit:${rootProject.findProperty("crypticlibVer")}")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
}