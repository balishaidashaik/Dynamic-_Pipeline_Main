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
	  [Yesterday 16:37] Sarma Evani, Raghavendra

    stage ("publish to s3") {
     step ([
     $class: 'S3BucketPublisher',
     entries: [[
     sourceFile: 'build/libs/*.jar',
     bucket: 'buildartifacts-dynamic-pipeline-jenkins',
     selectedRegion: 'us-east-2',
     noUploadOnFailure: true,
     managedArtifacts: true,
    //flatten: true,
     showDirectlyInBrowser: true,
     keepForever: true,
    ]],
    profileName: 'Dynamic-DevOps-Pipeline-Jenkins-S3',
    dontWaitForConcurrentBuildCompletion: false,
   ])



	  

  }
}
