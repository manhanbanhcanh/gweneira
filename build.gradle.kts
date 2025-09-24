plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.dv8tion:JDA:5.0.0-beta.24") //beta JDA version
}

application {
    mainClass.set("com.gweneira.Bot")
}