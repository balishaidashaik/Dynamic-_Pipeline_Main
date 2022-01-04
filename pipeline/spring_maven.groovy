pipeline {
    agent any 

    stages {

        stage("Code Check Out") {
            steps {
                git branch: 'master', credentialsId: env.Credential_ID, url: '${APP_URL}'
                echo("${APP_URL} Repository was successfully cloned.")
            }
        }

        stage("Maven Compile") {
            steps {
                echo ("Maven compilation")
                sh 'mvn compile'
                echo ("Compiling the maven project is completed")
            }
        }

        stage ("Maven Package") {    
            steps {
                echo ("Maven Package")
                sh 'mvn clean package'
                echo ("Packaging the maven project is completed")
            }
        }

        stage ("Maven Test") {     
            steps {
                echo 'Starting unit test execution'
                sh 'mvn test'
                echo 'Completed unit test execution'
            }       
        }
        
        /*stage ('code analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn verify sonar:sonar'
                }
            }
        }*/

        stage ('Packaging Artifacts') {
            steps {
                echo "Started Packaging artifacts"
                sh 'zip -r "${Application_Name}.jar.zip" target/*.jar'
                echo "Completed Packaging artifacts"
            }
        }     

        /*stage ('Storing artifcats in nexus') {
            steps {
                echo "Started uploading artifacts to Nexus repository"
                nexusArtifactUploader artifacts: [[artifactId: 'sampleapp', classifier: '', file: '${Application_Name}.jar.zip', type: 'zip']], credentialsId: '27756e6f-60a1-4a35-bbc8-157d1ea67b68', groupId: 'com.test', nexusUrl: '3.142.21.161:8081', nexusVersion: 'nexus3', protocol: 'http', repository: 'spring-maven', version: '1.0.0-SNAPSHOT'
                echo "Completed uploading artifacts to Nexus repository"
            }
        }*/

        stage ("publish artifacts to s3") {
            steps {
                step ([
                    $class: 'S3BucketPublisher',
                    entries: [[
                    sourceFile: '${Application_Name}.jar.zip',
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
                echo ("Artifacts are stored in the S3 bucket")
            }
        }
    }
}
