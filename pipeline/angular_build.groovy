timeout(5) {
  node("master") {
   stage("Code Check Out") {
	   git branch: 'main', credentialsId: env.Credential_ID, url: '${APP_URL}'
      echo("${APP_URL} Repository was successfully cloned.")
    }	
	
   stage("Build Node Modules") {
      nodejs('Node') {
      sh 'npm install'
    }
      echo("Node Modules installed successully")
    }
	
    stage("Build/Package the Angular Application") {
      nodejs('Node') {
      sh 'npm run'
    }
      echo("AngularJs Application is Built Successfully")
    }

  }
}
