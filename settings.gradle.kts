@file:Suppress("UnstableApiUsage")

dependencyResolutionManagement {
	repositories {
		mavenCentral()
		maven("https://repo.papermc.io/repository/maven-public/")
	}

	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}

rootProject.name = "MiniMessageCommand"
