repositories {
    maven("https://repo.tabooproject.org/repository/releases/")
    //CrypticLib
    maven("http://110.42.10.241:8082/repository/maven-public/") {
        isAllowInsecureProtocol = true
    }
}

dependencies {
    compileOnly(project(":core"))
    compileOnly("ink.ptms.core:v260100:260100-minimize-java21")
    compileOnly("com.crypticlib:bukkit:${rootProject.findProperty("crypticlibVer")}")
}