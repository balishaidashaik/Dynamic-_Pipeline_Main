pipeline {
     agent any
     stages{
       stage("Code Check Out") {
        steps {
         git branch: 'main', credentialsId: env.Credential_ID, url: '${APP_URL}'
         echo("${APP_URL} Repository was successfully cloned.")
    }
   }
	
   stage("Build Node Modules") {
     steps{
        nodejs('Node') {
        sh 'npm install'
    }
        echo("Node Modules installed successully")
   }
 }
   stage("Build/Package the Angular Application") {
     steps{
        nodejs('Node') {
        sh 'ng build'
    }
   }
  }
   stage("Test the Angular Application") {
     steps{
        nodejs('Node') {
        sh 'ng --test'
    }
   }
  }
   stage ('Packaging Artifacts') {
            steps {
                nodejs('Node') {
                    echo "Started Packaging artifacts"
                    sh 'zip -r "dist.zip" dist/'
                    echo "Completed Packaging artifacts"
                }
            }
        }
	
    /*stage("Build Node Modules") {
      nodejs('Node') {
      sh 'npm install sonarqube-scanner'
    }
      echo("Node Modules installed successully")
    }
	
    
    
    stage ('Code Analysis') {
      nodejs('Node') {
      sh 'npm run sonar'
    }
   }*/

	    
    stage ("publish to s3") {
      step ([
        $class: 'S3BucketPublisher',
        entries: [[
          sourceFile: '',
          bucket: 'dynamicnodes3bucket',
          selectedRegion: 'us-east-1',
          noUploadOnFailure: true,
          managedArtifacts: true,
          //flatten: true,
          showDirectlyInBrowser: true,
          keepForever: true,
        ]],
        profileName: 'dynamicpipeline -artifacts',
        dontWaitForConcurrentBuildCompletion: false,
      ])
      echo("AngularJs Application is Built Successfully")
     }
	 
 }
}
