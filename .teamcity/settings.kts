import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.projectFeatures.githubConnection
import jetbrains.buildServer.configs.kotlin.triggers.vcs

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

version = "2022.04"

project {

    buildType(Build)

    features {
        githubConnection {
            id = "PROJECT_EXT_7"
            displayName = "GitHub.com"
            clientId = "dmkazanskii"
            clientSecret = "credentialsJSON:4bd6a53b-863d-4737-b231-d9eda3cf771e"
        }
    }
}

object Build : BuildType({
    name = "Build"

    artifactRules = "target => target"
    buildNumberPattern = "netology-tc.%build.counter%"
    publishArtifacts = PublishMode.SUCCESSFUL

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        maven {
            name = "mvn clean test"

            conditions {
                doesNotContain("teamcity.build.branch", "master")
            }
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
        maven {
            name = "mvn clean package"

            conditions {
                contains("teamcity.build.branch", "master")
            }
            goals = "clean package"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
    }

    triggers {
        vcs {
            perCheckinTriggering = true
            groupCheckinsByCommitter = true
            enableQueueOptimization = false
        }
    }
})
