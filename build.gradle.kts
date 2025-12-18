import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.21"
    id("org.jetbrains.intellij") version "1.16.1"
}

group = "com.mybatisplus.generator"
version = "1.0.1"

repositories {
    mavenCentral()
    maven("https://maven.aliyun.com/repository/public")
}

dependencies {
    // MyBatis-Plus Generator
    implementation("com.baomidou:mybatis-plus-generator:3.5.3.1")
    implementation("com.baomidou:mybatis-plus-core:3.5.3.1")
    implementation("com.baomidou:mybatis-plus-annotation:3.5.3.1")

    // Template engines
    implementation("org.freemarker:freemarker:2.3.32")
    implementation("org.apache.velocity:velocity-engine-core:2.3")

    // Database drivers
    implementation("mysql:mysql-connector-java:8.0.33")
    implementation("org.postgresql:postgresql:42.6.0")
    implementation("com.oracle.database.jdbc:ojdbc8:21.9.0.0")

    // Swagger annotations
    implementation("io.swagger:swagger-annotations:1.6.11")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
}

intellij {
    version.set("2023.3")
    type.set("IC") // IntelliJ IDEA Community Edition

    plugins.set(listOf(
        "com.intellij.java"
    ))
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
        options.encoding = "UTF-8"
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("233")
        untilBuild.set("243.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }

    buildSearchableOptions {
        enabled = false
    }

    instrumentCode {
        enabled = false
    }
}
