plugins {
    id("java")
}

group = "me.rejomy"
version = "1.0"

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    flatDir {
        dirs("libs")
    }
}

dependencies {
    compileOnly("me.clip:placeholderapi:2.11.5")
    compileOnly(files("libs/citizensapi-2.0.40-SNAPSHOT.jar"))
    compileOnly(files("libs/server.jar"))
}