/*
    pipeline.checkoutSCM([
        credentialId: "token",
        url: "github.com/madoos/generator-redpanda-node-module"
    ])
*/
def checkoutSCM(config) {
    deleteDir()
    withCredentials([string(credentialsId: config.credentialId, variable: 'GITHUB_TOKEN')]){
        sh("git clone https://${GITHUB_TOKEN}@${config.url} .")
        sh("git checkout ${env.BRANCH_NAME}")
    }
}

def test(conf){
    println "${conf.test}"
}
