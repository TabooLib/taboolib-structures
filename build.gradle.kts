import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `maven-publish`
    id("org.jetbrains.kotlin.jvm") version "1.5.10" apply false
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        maven("https://repo.tabooproject.org/repository/releases/")
        mavenCentral()
    }

    dependencies {
        "compileOnly"(kotlin("stdlib"))
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.addAll(listOf("-XDenableSunApiLintControl"))
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.freeCompilerArgs += listOf("-module-name", "${project.group}.${project.name}")
    }

    publishing {
        repositories {
            maven("http://ptms.ink:8081/repository/releases") {
                isAllowInsecureProtocol = true
                credentials {
                    username = project.findProperty("taboolibUsername").toString()
                    password = project.findProperty("taboolibPassword").toString()
                }
                authentication {
                    create<BasicAuthentication>("basic")
                }
            }
        }
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
            }
        }
    }
}