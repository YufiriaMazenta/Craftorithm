repositories {
    //Vault
    maven("https://jitpack.io")
    //PlaceHolderAPI
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://mvn.lumine.io/repository/maven-public/")

}

dependencies {
    compileOnly("net.luckperms:api:${rootProject.findProperty("luckPermsApiVer")}")
    compileOnly("me.clip:placeholderapi:${rootProject.findProperty("placeholderApiVer")}")
    compileOnly("net.kyori:adventure-api:${rootProject.findProperty("adventureApiVer")}")
    compileOnly("io.papermc.paper:paper-api:${rootProject.findProperty("paperApiVer")}")
    compileOnly("com.crypticlib:bukkit:${rootProject.findProperty("crypticlibVer")}")
    implementation("com.crypticlib:bukkit-ui:${rootProject.findProperty("crypticlibVer")}")
    implementation("com.crypticlib:bukkit-conversation:${rootProject.findProperty("crypticlibVer")}")
    implementation("com.crypticlib:bukkit-i18n:${rootProject.findProperty("crypticlibVer")}")
    implementation("com.crypticlib:common-compat:${rootProject.findProperty("crypticlibVer")}")
    implementation("com.crypticlib:common-script:${rootProject.findProperty("crypticlibVer")}")
}