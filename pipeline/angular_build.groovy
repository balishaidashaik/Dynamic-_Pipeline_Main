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
    stage('Publish files to Nexus') {
            steps {
                echo "Started uploading artifacts to Nexus repository"
                nexusArtifactUploader artifacts: [[artifactId: 'angular-helloworld', classifier: '', file: 'dist.zip', type: 'zip']], credentialsId: '01457bec-bf49-42bf-9b5f-a15944b135c4', groupId: 'com.angular', nexusUrl: '54.197.69.58:8081', nexusVersion: 'nexus3', protocol: 'http', repository: 'angularapp-artifacts', version: '5.0.0'
                echo "Completed uploading artifacts to Nexus repository"
            }
        } 
	
   
	    
    stage ("publish to s3") {
     steps {
       step ([
        $class: 'S3BucketPublisher',
        entries: [[
          sourceFile: 'dist.zip',
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
}
