plugins {
    kotlin("jvm") version "1.3.72"
}

repositories {
    mavenLocal()
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("script-runtime"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.6")

}

sourceSets {
    main {
        java.srcDir("scripts")
    }
}