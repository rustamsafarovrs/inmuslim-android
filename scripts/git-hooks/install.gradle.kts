tasks.register<Copy>("copyGitHooks") {
    description = "Copies the git hooks from scripts/git-hooks to the .git folder."
    from("${rootDir}/scripts/git-hooks") {
        include("**/*.sh")
        rename("(.*).sh", "$1")
    }
    into("${rootDir}/.git/hooks")
}

tasks.register<Exec>("installGitHooks") {
    group = "git hooks"
    workingDir(rootDir)
    commandLine("chmod")
    args("-R", "+x",  ".git/hooks/")
    dependsOn("copyGitHooks")
    doLast {
        println("Git hooks installed successfully.")
    }
}
