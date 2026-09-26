java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

dependencies {
    compileOnly(project(":core"))
    compileOnly(project(":nms:v26_1_spigot"))
    compileOnly("org.spigotmc:spigot:26.3")
    compileOnly("io.papermc.paper:paper-api:${rootProject.findProperty("paperApiVer")}")
    compileOnly("com.crypticlib:bukkit:${rootProject.findProperty("crypticlibVer")}")
    compileOnly("com.crypticlib:bukkit-util:${rootProject.findProperty("crypticlibVer")}")
    compileOnly("com.crypticlib:common-compat:${rootProject.findProperty("crypticlibVer")}")
}
