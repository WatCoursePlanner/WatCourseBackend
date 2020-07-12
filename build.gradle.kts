import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.google.protobuf.gradle.*

plugins {
    java
    id("com.google.protobuf") version "0.8.8"
    id("org.springframework.boot") version "2.3.1.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    id("com.squareup.wire") version "3.2.2"
    kotlin("jvm") version "1.3.72"
    kotlin("plugin.spring") version "1.3.72"
    kotlin("plugin.jpa") version "1.3.72"
}

group = "com.watcourses"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    maven { setUrl("https://dl.bintray.com/hotkeytlt/maven") }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.session:spring-session-core")
    implementation("com.squareup.wire:wire-runtime:3.2.2")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("org.jsoup:jsoup:1.13.1")
    implementation("com.vladmihalcea:hibernate-types-52:2.9.12")
    implementation("com.github.h0tk3y.betterParse:better-parse:0.4.0")
    implementation("io.github.lognet:grpc-spring-boot-starter:3.5.5")
    implementation("org.tomlj:tomlj:1.0.0")
    implementation("com.google.protobuf:protobuf-java:3.6.1")
    implementation("io.grpc:grpc-stub:1.15.1")
    implementation("io.grpc:grpc-protobuf:1.15.1")
    implementation("javax.annotation:javax.annotation-api:1.3.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("mysql:mysql-connector-java")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.assertj:assertj-core:3.16.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

wire {
    kotlin {
        rpcRole = "server"
        out = "src/main/kotlin/"
    }
}

sourceSets{
    main {
        proto {
            srcDir("src/main/kotlin")
        }
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.6.1"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.15.1"
        }
        id("grpckotlin") {
            artifact = "io.rouz:grpc-kotlin-gen:0.1.4"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc") {
                    outputSubDir = "kotlin"
                }
                id("grpckotlin") {
                    outputSubDir = "kotlin"
                }
            }
        }
    }
    generatedFilesBaseDir = "$projectDir/src/"
}
