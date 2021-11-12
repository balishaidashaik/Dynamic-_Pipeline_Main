timeout(5) {
  node("master") {
    stage('Checkout App Source Code') {
                    steps {
                        script {
                                scmCode.gitCheckOut(repo_host,app_branch,DCSE_GitTag_Credential)
                                echo "Completed App source checkout "
                            }
                    }
                }	
	
    stage("Build Node Modules") {
      nodejs('Node') {
      sh 'npm install'
    }
      echo("Node Modules installed successully")
    }
	
    stage("Build/Package the Angular Application") {
      nodejs('Node') {
      sh 'npm run build'
    }
      echo("AngularJs Application is Built Successfully")
    }

  }
}
