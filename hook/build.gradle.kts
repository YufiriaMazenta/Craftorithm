repositories {
    //CraftEngine, CustomFishing
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
    //PlaceHolderAPI
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    compileOnly(project(":core"))
    compileOnly("emaki.jiuwu.craft:emaki-corelib-api:${rootProject.findProperty("emakiCoreLibApiVer")}")
    compileOnly("emaki.jiuwu.craft:emaki-item-api:${rootProject.findProperty("emakiItemApiVer")}")
    compileOnly("com.crypticlib:bukkit:${rootProject.findProperty("crypticlibVer")}")
    compileOnly("com.crypticlib:bukkit-i18n:${rootProject.findProperty("crypticlibVer")}")
    compileOnly("com.crypticlib:common-script:${rootProject.findProperty("crypticlibVer")}")
    compileOnly("io.papermc.paper:paper-api:${rootProject.findProperty("paperApiVer")}")
    //AzureFlow
    compileOnly(fileTree("libs"))
    //CraftEngine
    compileOnly("net.momirealms:craft-engine-core:${rootProject.findProperty("craftEngineVer")}")
    compileOnly("net.momirealms:craft-engine-bukkit:${rootProject.findProperty("craftEngineVer")}")
    //CustomFishing
    compileOnly("net.momirealms:custom-fishing:${rootProject.findProperty("customFishingVer")}")
    //EcoItems
    compileOnly("com.willfp:EcoEnchants:${rootProject.findProperty("ecoEnchantsVer")}")
    compileOnly("com.willfp:EcoItems:${rootProject.findProperty("ecoItemsVer")}")
    compileOnly("com.willfp:eco:${rootProject.findProperty("ecoVer")}")
    compileOnly("com.willfp:libreforge:${rootProject.findProperty("libreforgeVer")}:all@jar")
    //ExecutableItems
    compileOnly("com.ssomar:SCore:${rootProject.findProperty("scoreVer")}")
    //ItemsAdder
    compileOnly("com.github.LoneDev6:API-ItemsAdder:${rootProject.findProperty("itemsAdderApiVer")}")
    //MMOItems
    compileOnly("net.Indyuce:MMOItems-API:${rootProject.findProperty("mmoItemsApiVer")}")
    compileOnly("io.lumine:MythicLib-dist:${rootProject.findProperty("mythicLibDistVer")}")
    //MythicMobs
    compileOnly("io.lumine:Mythic-Dist:${rootProject.findProperty("mythicDistVer")}")
    //NeigeItems
    compileOnly("pers.neige.neigeitems:NeigeItems:${rootProject.findProperty("neigeItemsVer")}")
    //Nexo
    compileOnly("com.nexomc:nexo:${rootProject.findProperty("nexoVer")}")
    //Oraxen
    compileOnly("io.th0rgal:oraxen:${rootProject.findProperty("oraxenVer")}")
    //packetevents
    compileOnly("com.github.retrooper:packetevents-spigot:${rootProject.findProperty("packeteventsSpigotVer")}")
    //PlayerPoints
    compileOnly("org.black_ixx:playerpoints:${rootProject.findProperty("playerpointsVer")}")
    //ProtocolLib
    compileOnly("net.dmulloy2:ProtocolLib:${rootProject.findProperty("protocolLibVer")}")
    //SX-Item
    compileOnly("com.github.Saukiya:SX-Item:${rootProject.findProperty("sxItemVer")}")
    //Vault
    compileOnly("com.github.MilkBowl:VaultAPI:${rootProject.findProperty("vaultApiVer")}") {
        exclude("org.bukkit", "bukkit")
    }
    compileOnly("net.milkbowl.vault:VaultUnlockedAPI:${rootProject.findProperty("vaultUnlockedApiVer")}")
    compileOnly("net.advancedplugins:AdvancedEnchantments-API:${rootProject.findProperty("advancedEnchantmentsApiVer")}")
    compileOnly("me.clip:placeholderapi:${rootProject.findProperty("placeholderApiVer")}")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}
