pipeline {
    agent any 

    stages {

        stage("Code Check Out") {
            steps {
                git branch: 'master', credentialsId: env.Credential_ID, url: '${APP_URL}'
                echo("${APP_URL} Repository was successfully cloned.")
            }
        }
	
        stage("Install Node Modules") {
            steps {
                nodejs('Node') {
                    sh 'npm install sonarqube-scanner'
                    echo("Node Modules are installed successully")
                }
            }
        }
	
        stage("Build/Package the React Application") {
            steps {
                nodejs('Node') {
                    sh 'npm run build'
                    echo("Build is completed")
                }
            }
        }

        /*stage ('Code Analysis') {
            steps {
                nodejs('Node') {
                    sh 'npm run sonar'
                    echo("Code Analysis is success")
                }
            }
        }*/

        stage ('Unit test application') {
            steps {
                nodejs('Node') {
                    echo 'Starting unit test execution'
                    sh 'npm test'
                    echo 'Completed unit test execution'
                }
            }
        }
    
        stage('Packaging Artifacts') {
            steps {
                nodejs('Node') {
                    echo "Started Packaging artifacts"
                    sh 'zip -r "build.zip" build/'
                    echo "Completed Packaging artifacts"
                }
            }
        }
    
        stage ("publish artifacts to s3") {
            steps {
                step ([
                    $class: 'S3BucketPublisher',
                    entries: [[
                        sourceFile: 'build.zip',
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
                echo ("Artifacts are stored in the S3 bucket")
            }
        }
      
        /*stage('Publish files to Nexus') {
            steps {
                echo "Started uploading artifacts to Nexus repository"
                nexusArtifactUploader artifacts: [[artifactId: 'react-helloworld', classifier: '', file: 'build.zip', type: 'zip']], credentialsId: '27756e6f-60a1-4a35-bbc8-157d1ea67b68', groupId: 'com.react', nexusUrl: '3.142.21.161:8081', nexusVersion: 'nexus3', protocol: 'http', repository: 'react-repo', version: '1.0.0'
                echo ('Artifacts are stored in nexus')
            }
        }*/
    }
}

