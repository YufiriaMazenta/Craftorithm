dependencies {
    compileOnly(project(":core"))
    compileOnly("org.spigot:spiogt:26.1")
    compileOnly("com.crypticlib:common-compat:${rootProject.findProperty("crypticlibVer")}")
}