plugins {
    id("java-library")
    id("idea")
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
    kotlin("jvm") version "1.9.25"
}

group = "com.krzhi"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

repositories {
    maven { setUrl("https://maven.aliyun.com/repository/google/") }
    maven { setUrl("https://maven.aliyun.com/repository/public/") }
    maven { setUrl("https://maven.aliyun.com/repository/spring/") }
    maven { setUrl("https://maven.aliyun.com/repository/gradle-plugin/") }
    maven { setUrl("https://maven.aliyun.com/repository/spring-plugin/") }
    maven { setUrl("https://oss.sonatype.org/service/local/staging/deploy/maven2/") }


    mavenCentral()
}

dependencies {
    api("org.jetbrains.kotlin:kotlin-reflect")

    api("org.springframework.boot:spring-boot-starter")
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-security")
    api("org.springframework.boot:spring-boot-configuration-processor")
    api("org.springframework.boot:spring-boot-starter-data-redis")
    api("org.springframework.boot:spring-boot-starter-cache")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("com.mysql:mysql-connector-j")

    api("com.squareup.okhttp3:okhttp:4.12.0")

    api("com.google.code.gson:gson:2.11.0")

    api("commons-codec:commons-codec:1.18.0")
    api("commons-io:commons-io:2.19.0")
    api("org.apache.commons:commons-lang3:3.17.0")

    api("io.jsonwebtoken:jjwt:0.13.0")
}