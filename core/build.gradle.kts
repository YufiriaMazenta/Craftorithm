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
    implementation("com.crypticlib:common-database:${rootProject.findProperty("crypticlibVer")}")
    implementation("com.crypticlib:common-util:${rootProject.findProperty("crypticlibVer")}")
    //服务端不自带PostgreSQL驱动, 由Paper在运行时下载(见plugin.yml的libraries)
    compileOnly("org.postgresql:postgresql:${rootProject.findProperty("postgresqlVer")}")

    testImplementation("com.crypticlib:bukkit:${rootProject.findProperty("crypticlibVer")}")
    testImplementation("io.papermc.paper:paper-api:${rootProject.findProperty("paperApiVer")}")
    testImplementation("org.postgresql:postgresql:${rootProject.findProperty("postgresqlVer")}")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    //内嵌PostgreSQL, 本地无需安装数据库服务
    testImplementation("io.zonky.test:embedded-postgres:2.0.7")
    testImplementation("io.zonky.test.postgres:embedded-postgres-binaries-windows-amd64:16.4.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.2")
}

tasks.test {
    useJUnitPlatform()
}