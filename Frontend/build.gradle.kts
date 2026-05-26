import com.github.gradle.node.npm.task.NpmTask

plugins {
    base
    id("com.github.node-gradle.node") version "7.1.0"
}

node {
    download.set(false)
}

val buildFrontend by tasks.registering(NpmTask::class) {
    group = "build"
    description = "Build the React frontend"
    dependsOn(tasks.npmInstall)
    args.set(listOf("run", "build"))
    inputs.dir("src")
    inputs.file("package.json")
    inputs.file("vite.config.ts")
    outputs.dir("dist")
}

val testFrontend by tasks.registering(NpmTask::class) {
    group = "verification"
    description = "Test the React frontend"
    dependsOn(tasks.npmInstall)
    args.set(listOf("run", "test"))
}

tasks.named("assemble") {
    dependsOn(buildFrontend)
    dependsOn(":Backend:assemble")
}

tasks.named("check") {
    dependsOn(testFrontend)
}