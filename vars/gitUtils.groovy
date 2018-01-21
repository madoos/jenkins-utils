/**
Example

CSM([
    credentialId: "token",
    provider: 'github'
    url: 'github.com/madoos/generator-redpanda-node-module'
])

*/
def CSM(conf) {
    deleteDir()
    withCredentials([string(credentialsId: 'token', variable: 'GITHUB_TOKEN')]){
        sh("git clone https://${GITHUB_TOKEN}@${conf.url}.git .")
        sh("git checkout ${env.BRANCH_NAME}")
    }
}