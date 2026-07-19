repositories {
    maven("https://repo.crypticlib.incrafttime.top/repository/maven-public/") {
        isAllowInsecureProtocol = true
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

dependencies {
    compileOnly(project(":core"))
    compileOnly("io.papermc.paper:paper-core:26.1.1")
    compileOnly("io.papermc.paper:paper-api:1.21.5-R0.1-SNAPSHOT")
    compileOnly("com.crypticlib:bukkit:${rootProject.findProperty("crypticlibVer")}")
    compileOnly("com.crypticlib:common-compat:${rootProject.findProperty("crypticlibVer")}")
}
