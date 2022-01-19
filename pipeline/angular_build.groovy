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
   
    	    
    stage ("publish to s3") {
     steps {
       step ([
        $class: 'S3BucketPublisher',
        entries: [[
          sourceFile: 'dist.zip',
          bucket: 'dynamic-s3bucket',
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
      echo("AngularJs Application is Built Successfully")
     }
    }
    
    stage('Publish files to Nexus') {
	    steps {
		    sh 'npm publish'
	    }
    }
    /*stage('Publish files to Nexus Artifacts') {
         steps {
        //echo "Started uploading artifacts to Nexus repository"
        nexusArtifactUploader credentialsId: '2869fc27-b947-42e9-8fc6-3f93fcc8dece', groupId: 'com.angular', nexusUrl: '18.223.156.120:8395/', nexusVersion: 'nexus3', protocol: 'http', repository: 'http://18.223.156.120:8395/repository/npm-private/', version: '3.37.3-02'
        //echo "Completed uploading artifacts to Nexus repository"
            }
        }*/
   }
}
