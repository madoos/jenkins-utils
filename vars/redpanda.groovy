def checkoutSCM(conf) {
    deleteDir()
    withCredentials([string(credentialsId: conf.credentialId, variable: 'GITHUB_TOKEN')]){
        sh("git clone https://${GITHUB_TOKEN}@${conf.url} .")
        sh("git checkout ${env.BRANCH_NAME}")
    }
}

def runWithNodeJS(conf){
    version = (conf.version == null) ? 8 : conf.version
    runner = (conf.runner == null) ? "1.3.0" : conf.runner
    sh("docker run --privileged --rm -e NPM_COMMAND=${conf.cmd} -e NODE=${version} -v `pwd`:/workspace redpandaci/npm-command-runner:${runner}")
}

def publishCoverage(){
    publishHTML([
        allowMissing: false,
        alwaysLinkToLastBuild: false,
        keepAll: false, reportDir: "coverage/lcov-report",
        reportFiles: "index.html",
        reportName: "Coverage Report"
    ])
}

def closeRelease(conf){
  branch = env.BRANCH_NAME
  version = getRelease()
  tag = "v${version}"
  commit = (conf.commit == null ) ? "New: Update to version ${tag}": "${conf.commit} ${tag}"

  sh('git config user.email "redpandaci@gmail.com" && git config user.name "redpandaci"')
  sh("git add package* CHANGELOG.md")
  sh("git commit --no-verify -m '${commit}'")
  sh("git tag -a ${tag} -m '${commit}'")
  sh("git checkout develop && git merge ${branch}")
  sh("git checkout master && git merge ${branch}")
  sh("git push origin develop && git push origin master && git push --tags")
}

def npmPublish(conf){
    withCredentials([string(credentialsId: conf.credentialId, variable: 'NPM_TOKEN')]){
        runner = (conf.runner == null) ? "1.3.0" : conf.runner
        release = getRelease()
        sh("docker run --privileged -e NPM_COMMAND=publish -e NPM_TOKEN=${NPM_TOKEN} -e VERSION=${release} -v `pwd`:/workspace redpandaci/npm-command-runner:${runner}")
    }
}

def getRelease(){
  return env.BRANCH_NAME.contains("/v") ? branch.split("/v")[1] : branch.split("/")[1]
}