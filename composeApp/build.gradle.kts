import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting
        
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            // MQTT
            implementation("io.github.davidepianca98:kmqtt-common:0.4.8")
            implementation("io.github.davidepianca98:kmqtt-client:0.4.8")

            implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.5.1")

        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation("uk.co.caprica:vlcj:4.8.3")
        }
    }
}


compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.ndipatri.gaggiakmpsimulator"
            packageVersion = "1.0.0"
        }
    }
}
