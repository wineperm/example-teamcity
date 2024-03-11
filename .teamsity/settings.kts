import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.projectFeatures.buildReportTab
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2023.11"

project {
    description = "Contains all other projects"

    vcsRoot(HttpsGithubComWinepermExampleTeamcityGit)

    features {
        buildReportTab {
            id = "PROJECT_EXT_1"
            title = "Code Coverage"
            startPage = "coverage.zip!index.html"
        }
    }

    cleanup {
        baseRule {
            preventDependencyCleanup = false
        }
    }

    subProject(Netology)
}

object HttpsGithubComWinepermExampleTeamcityGit : GitVcsRoot({
    name = "https://github.com/wineperm/example-teamcity.git"
    url = "https://github.com/wineperm/example-teamcity.git"
    branch = "refs/heads/master"
    authMethod = password {
        userName = "wineperm"
        password = "credentialsJSON:8aee4a48-13a5-4549-9d77-f6277118b499"
    }
})


object Netology : Project({
    name = "netology"

    vcsRoot(Netology_HttpsGithubComWinepermExampleTeamcityGitRefsHeadsMaster)

    buildType(Netology_Build)
})

object Netology_Build : BuildType({
    name = "Build"

    artifactRules = "target/*.jar => target"
    publishArtifacts = PublishMode.SUCCESSFUL

    vcs {
        root(Netology_HttpsGithubComWinepermExampleTeamcityGitRefsHeadsMaster)
    }

    steps {
        maven {
            name = "clean deploy"
            id = "Maven2"

            conditions {
                contains("teamcity.build.branch", "master")
            }
            goals = "clean deploy"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
            userSettingsSelection = "settings.xml"
        }
        maven {
            name = "clean test"
            id = "Maven2_1"

            conditions {
                doesNotContain("teamcity.build.branch", "master")
            }
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
            userSettingsSelection = "settings.xml"
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        perfmon {
        }
    }
})

object Netology_HttpsGithubComWinepermExampleTeamcityGitRefsHeadsMaster : GitVcsRoot({
    name = "https://github.com/wineperm/example-teamcity.git#refs/heads/master"
    url = "https://github.com/wineperm/example-teamcity.git"
    branch = "refs/heads/master"
    branchSpec = "refs/heads/*"
    authMethod = password {
        userName = "wineperm"
        password = "credentialsJSON:8aee4a48-13a5-4549-9d77-f6277118b499"
    }
})
