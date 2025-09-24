plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.dv8tion:JDA:5.0.0-beta.24") //beta JDA version
    implementation("org.reflections:reflections:0.10.2")
}

application {
    mainClass.set("com.gweneira.Bot")
}