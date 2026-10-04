dependencies {
    compileOnly(project(":core"))
    compileOnly(project(":nms:common"))
    compileOnly(project(":nms:v26_1_paper"))
    compileOnly("io.papermc.paper:paper-core:26.2")
    compileOnly("io.papermc.paper:paper-api:${rootProject.findProperty("paperApiVer")}")
    compileOnly("com.crypticlib:bukkit:${rootProject.findProperty("crypticlibVer")}")
    compileOnly("com.crypticlib:bukkit-util:${rootProject.findProperty("crypticlibVer")}")
    compileOnly("com.crypticlib:common-compat:${rootProject.findProperty("crypticlibVer")}")
}
