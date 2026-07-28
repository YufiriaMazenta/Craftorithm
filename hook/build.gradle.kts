repositories {
    //CraftEngine
    maven("https://repo.momirealms.net/releases/")
    //Eco
    maven("https://repo.auxilor.io/repository/maven-public/")
    //ItemsAdder, SX-Item, Vault
    maven("https://jitpack.io")
    //MMOItems
    maven("https://nexus.phoenixdevt.fr/repository/maven-public/")
    //MythicDist
    maven("https://mvn.lumine.io/repository/maven-public/")
    //NeigeItems
    maven("https://r.irepo.space/maven/")
    //Nexo
    maven("https://repo.nexomc.com/releases")
    //Oraxen
    maven("https://repo.oraxen.com/releases")
    //packetevents
    maven("https://repo.codemc.io/repository/maven-releases/")
    //PlayerPoints
    maven("https://repo.rosewooddev.io/repository/public/")
    //VaultUnlocked
    maven("https://repo.codemc.io/repository/creatorfromhell/")
}

dependencies {
    compileOnly(project(":core"))
    compileOnly("emaki.jiuwu.craft:emaki-item-api:2.6.1")
    compileOnly("com.crypticlib:bukkit:${rootProject.findProperty("crypticlibVer")}")
    compileOnly("com.crypticlib:bukkit-i18n:${rootProject.findProperty("crypticlibVer")}")
    compileOnly("com.crypticlib:common-script:${rootProject.findProperty("crypticlibVer")}")
    compileOnly("io.papermc.paper:paper-api:${rootProject.findProperty("paperApiVer")}")
    //AzureFlow
    compileOnly(fileTree("libs"))
    //CraftEngine
    compileOnly("net.momirealms:craft-engine-core:26.5")
    compileOnly("net.momirealms:craft-engine-bukkit:26.5")
    //EcoItems
    compileOnly("com.willfp:EcoEnchants:12.5.1")
    compileOnly("com.willfp:EcoItems:5.59.0")
    compileOnly("com.willfp:eco:6.74.2")
    compileOnly("com.willfp:libreforge:4.71.6:all@jar")
    //ExecutableItems
    compileOnly("com.ssomar:SCore:5.24.10.5")
    //ItemsAdder
    compileOnly("com.github.LoneDev6:API-ItemsAdder:3.6.3-beta-14")
    //MMOItems
    compileOnly("net.Indyuce:MMOItems-API:6.9.5-SNAPSHOT")
    compileOnly("io.lumine:MythicLib-dist:1.6.2-SNAPSHOT")
    //MythicMobs
    compileOnly("io.lumine:Mythic-Dist:5.3.5")
    //NeigeItems
    compileOnly("pers.neige.neigeitems:NeigeItems:1.15.113")
    //Nexo
    compileOnly("com.nexomc:nexo:0.7.0")
    //Oraxen
    compileOnly("io.th0rgal:oraxen:1.191.0")
    //packetevents
    compileOnly("com.github.retrooper:packetevents-spigot:2.12.2")
    //PlayerPoints
    compileOnly("org.black_ixx:playerpoints:3.2.5")
    //ProtocolLib
    compileOnly("net.dmulloy2:ProtocolLib:5.4.0")
    //SX-Item
    compileOnly("com.github.Saukiya:SX-Item:4.4.0")
    //Vault
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude("org.bukkit", "bukkit")
    }
    compileOnly("net.milkbowl.vault:VaultUnlockedAPI:2.16")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}
