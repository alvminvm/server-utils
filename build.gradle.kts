plugins {
    id("java-library")
    id("idea")
    kotlin("plugin.spring") version "1.9.24"
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.6"
    kotlin("jvm") version "1.9.24"
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
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    compileOnly("org.springframework.boot:spring-boot-starter-web")
    api(libs.grpc.stub)
    api(libs.google.gson)
}