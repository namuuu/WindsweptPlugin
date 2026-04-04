plugins {
    id("java")
}

group = "fr.namu"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    register<Copy>("uploadJarToServer") {
        from("build/libs/${project.name}-${project.version}.jar")
        into("D:/Workspace/Perso/minecraft/server-1.21.11-127/plugins")
    }

    named("build") {
        finalizedBy("uploadJarToServer")
    }
}