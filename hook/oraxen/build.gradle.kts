repositories {
    //Oraxen
    maven("https://repo.oraxen.com/releases")
}

dependencies {
    compileOnly(project(":core"))
    compileOnly("io.th0rgal:oraxen:1.191.0")
}