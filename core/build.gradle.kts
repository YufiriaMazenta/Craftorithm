repositories {
    //CrypticLib
    maven("https://repo.crypticlib.incrafttime.top/repository/maven-public/")
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
    compileOnly("io.papermc.paper:paper-api:1.21.5-R0.1-SNAPSHOT")
    compileOnly("com.crypticlib:bukkit:${rootProject.findProperty("crypticlibVer")}")
    implementation("com.crypticlib:bukkit-ui:${rootProject.findProperty("crypticlibVer")}")
    implementation("com.crypticlib:bukkit-conversation:${rootProject.findProperty("crypticlibVer")}")
    implementation("com.crypticlib:bukkit-i18n:${rootProject.findProperty("crypticlibVer")}")
    implementation("com.crypticlib:common-compat:${rootProject.findProperty("crypticlibVer")}")
    implementation("com.crypticlib:common-script:${rootProject.findProperty("crypticlibVer")}")
}