repositories {
    //Eco
    maven("https://repo.auxilor.io/repository/maven-public/")
}

dependencies {
    compileOnly(project(":core"))
    compileOnly("com.willfp:EcoEnchants:12.5.1")
    compileOnly("com.willfp:EcoItems:5.59.0")
    compileOnly("com.willfp:eco:6.74.2")
    compileOnly("com.willfp:libreforge:4.71.6:all@jar")
}