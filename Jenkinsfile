@Library ('folio_jenkins_shared_libs') _

buildMvn {
  publishModDescriptor = 'no'
  mvnDeploy = 'yes'
  buildNode = 'jenkins-agent-java11'
  buildDeb = true

  doApiLint = true
  doApiDoc = true
  apiTypes = 'RAML'
  apiDirectories = 'okapi-core/src/main/raml'

  doDocker = {
    buildJavaDocker {
      dockerDir = 'okapi-core'
      overrideConfig  = 'no'
      publishMaster = 'yes'
      healthChk = 'yes'
      healthChkCmd = 'curl --fail http://localhost:9130/_/proxy/health || exit 1'
      runArgs = 'dev'
    }
  }
}

