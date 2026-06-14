import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.3.10"
    id("org.jetbrains.intellij.platform") version "2.16.0"
}

group = "dev.gelo"
version = providers.gradleProperty("pluginVersion").get()

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        // По умолчанию — версионный дистрибутив: воспроизводимая сборка в CI и на любой машине.
        // Локально для скорости можно указать localIdePath в ~/.gradle/gradle.properties —
        // тогда берём уже установленную IDEA и её JBR, ничего не качая.
        val localIde = providers.gradleProperty("localIdePath")
        if (localIde.isPresent) {
            local(localIde.get())
            jetbrainsRuntimeLocal("${localIde.get()}/jbr")
        } else {
            intellijIdeaCommunity("2024.3")
        }
        bundledPlugin("Git4Idea")
    }
}

intellijPlatform {
    // Инструментация байткода нам не нужна и тянет java-compiler-ant-tasks с зависающего CDN.
    instrumentCode = false

    pluginConfiguration {
        version = project.version.toString()
        ideaVersion {
            sinceBuild = "243"
            untilBuild = provider { null } // без верхней границы — не привязываемся к мажорной версии
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}
