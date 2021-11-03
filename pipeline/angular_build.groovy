timeout(5) {
  node("master") {
    stage("Code Check Out") {
      git branch: 'main', credentialsId: env.Credential_ID, url: 'https://github.com/balishaidashaik/Dynamic-pipeline.git'
      echo("${GIT_URL} Repository was successfully cloned.")
    }
	
    stage("Build Node Modules") {
      nodejs('Node') {
      sh 'npm install'
    }
      echo("Node Modules installed successully")
    }
	
    stage("Build/Package the React Application") {
      nodejs('Node') {
      sh 'npm run build'
    }
      echo("React Application is Built Successfully")
    }

  }
}