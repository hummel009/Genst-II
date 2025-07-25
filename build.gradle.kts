import java.time.LocalDate
import java.time.format.DateTimeFormatter

plugins {
	id("org.jetbrains.kotlin.jvm") version "latest.release"
	id("com.gtnewhorizons.retrofuturagradle") version "latest.release"
}

group = "com.github.hummel"
version = LocalDate.now().format(DateTimeFormatter.ofPattern("yy.MM.dd"))

val embed: Configuration by configurations.creating

dependencies {
	embed("org.jetbrains.kotlin:kotlin-stdlib:latest.release")
	implementation(rfg.deobf(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar")))))
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(8)
	}
}

minecraft {
	mcVersion = "1.7.10"
	username = "Hummel009"
}

tasks {
	register("copyForgeUnpacked") {
		doLast {
			val userHome = System.getProperty("user.home")
			val gradleUserHome = System.getenv("GRADLE_USER_HOME")

			if (gradleUserHome != null) {
				val path1 = "$gradleUserHome/caches/minecraft/net/minecraftforge/forge/1.7.10-10.13.4.1614-1.7.10"
				val path2 = "$userHome/.gradle/caches/minecraft/net/minecraftforge/forge/1.7.10-10.13.4.1614-1.7.10"
				if (path1 != path2) {
					val sourceDir = File(path1)
					val targetDir = File(path2)
					targetDir.mkdirs()
					sourceDir.copyRecursively(targetDir, overwrite = true)
				}
			}
		}
	}
	jar {
		manifest {
			attributes(
				mapOf(
					"Main-Class" to "genst.util.PatchKt"
				)
			)
		}
		from(embed.map {
			if (it.isDirectory) it else zipTree(it)
		})
		duplicatesStrategy = DuplicatesStrategy.EXCLUDE
	}
	runClient {
		dependsOn("copyForgeUnpacked")
	}
	runServer {
		dependsOn("copyForgeUnpacked")
	}
	withType<JavaCompile>().configureEach {
		options.encoding = "UTF-8"
	}
}
