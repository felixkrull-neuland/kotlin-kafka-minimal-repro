plugins {
    kotlin("jvm") version "1.9.23"
}



repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.kafka:kafka-clients:3.7.0")
    //implementation("io.github.nomisrev:kotlin-kafka:0.3.1")
    implementation("io.github.nomisrev:kotlin-kafka:0.4.0")
    testImplementation("org.testcontainers:kafka:1.19.7")
    testImplementation("ch.qos.logback:logback-classic:1.5.3")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
