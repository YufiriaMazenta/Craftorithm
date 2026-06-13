repositories {
    //Nexo
    maven("https://repo.nexomc.com/releases")
}

dependencies {
    compileOnly(project(":core"))
    compileOnly("com.nexomc:nexo:0.7.0")
}