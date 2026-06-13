repositories {
//    maven("https://r.irepo.space/maven/")
    //MythicDist
    maven("https://mvn.lumine.io/repository/maven-public/")
}
dependencies {
    compileOnly(project(":core"))
    compileOnly("io.lumine:Mythic-Dist:5.3.5")
}