pipeline {
    agent any
     
    stages {
      stage('Code Check Out') {
         steps {
               //git branch: '${Branch_Commit}', credentialsId: env.Credential_ID, url: '${APP_URL}'
               echo("${APP_URL} Repository was successfully cloned.")
               checkout([$class: 'GitSCM', branches: [[name: '*/${Branch_Commit}']], extensions: [], userRemoteConfigs: [[credentialsId: env.Credential_ID, url: '${APP_URL}']]])
               }
         }
      stage('Restore packages') {
         steps{
            echo ("Restore package")
            sh 'dotnet restore ${FILE_NAME}.sln'
         }
      }        
      stage('Clean') {
         steps{
            echo ("Clean package")
            sh 'dotnet clean ${FILE_NAME}.sln --configuration Release'
         }
      }
      /*stage('Build and SonarQube Code Analysis') {
         steps{
            withSonarQubeEnv("SonarQube") {
               echo 'Starting Build and SonarQube analysis'
               sh '${Sonar_MSBuild} begin /k:${FILE_NAME} /d:sonar.host.url=http://3.21.88.185:9000 /v:sonar.projectVersion=1.0 /d:sonar.sourceEncoding=UTF-8'
               sh 'dotnet restore ${FILE_NAME}.sln'
		         sh 'dotnet build ${FILE_NAME}.sln /p:Configuration=Release'
               sh '${Sonar_MSBuild} end'
               echo 'Completed Build & SonarQube analysis'
            }
         }
      }*/
      stage('Build') {
         steps{
            sh 'dotnet build ${FILE_NAME}.sln --configuration Release --no-restore'
         }
      }
      stage('Test: Unit Test') {
         steps {
               sh 'dotnet test XUnitTestProject/XUnitTestProject.csproj --configuration Release --no-restore'
            }
         }
      stage('Publish') {
            steps{
            sh 'dotnet publish ${FILE_NAME}/${FILE_NAME}.csproj --configuration Release --no-restore'
            }
      }
      stage('Publish to s3'){
        parallel {
            stage('publishing webapp'){
                steps {
                    step ([
                        $class: 'S3BucketPublisher',
                        entries: [[
                        sourceFile: 'WebApplication/bin/Release/netcoreapp3.1/*.dll',
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
            stage('publishing Test'){    
                steps {
                    step ([
                        $class: 'S3BucketPublisher',
                        entries: [[
                        sourceFile: 'XUnitTestProject/bin/Release/netcoreapp3.1/*.dll',
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
        }
      }
   } 
}
