repositories {
    //CrypticLib
    maven("http://110.42.10.241:8082/repository/maven-public/") {
        isAllowInsecureProtocol = true
    }
    //Vault
    maven("https://jitpack.io")
    //PlaceHolderAPI
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://mvn.lumine.io/repository/maven-public/")

}

dependencies {
    compileOnly("net.luckperms:api:5.4")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("net.kyori:adventure-api:4.14.0")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("com.crypticlib:bukkit:${rootProject.findProperty("crypticlibVer")}")
    implementation("com.crypticlib:bukkit-ui:${rootProject.findProperty("crypticlibVer")}")
    implementation("com.crypticlib:bukkit-conversation:${rootProject.findProperty("crypticlibVer")}")
    implementation("com.crypticlib:bukkit-i18n:${rootProject.findProperty("crypticlibVer")}")
//    implementation("com.crypticlib:bukkit-action:${rootProject.findProperty("crypticlibVer")}")
    implementation("com.crypticlib:common-compat:${rootProject.findProperty("crypticlibVer")}")
    implementation("com.crypticlib:common-database:${rootProject.findProperty("crypticlibVer")}")
    implementation("com.crypticlib:common-script:${rootProject.findProperty("crypticlibVer")}")
}