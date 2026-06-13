repositories {
    //CraftEngine
    maven("https://repo.momirealms.net/releases/")
}

dependencies {
    compileOnly(project(":core"))
    compileOnly("net.momirealms:craft-engine-core:26.5")
    compileOnly("net.momirealms:craft-engine-bukkit:26.5")
}