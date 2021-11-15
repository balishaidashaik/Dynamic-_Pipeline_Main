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
      sh 'npm run build'
    }
      echo("AngularJs Application is Built Successfully")
    }

   /* stage('NPM Install') {
       withEnv(["NPM_CONFIG_LOGLEVEL=warn"]) {
            sh 'npm install'
       }
    }

   // stage('Test') {
        //withEnv(["CHROME_BIN=/usr/bin/chromium-browser"]) {
         // sh 'ng test --progress=false --watch false'
       // }
       // junit '**/test-results.xml'
    //}

    stage('Lint') {
        sh 'ng lint'
    }

    stage('Build') {
        milestone()
        sh 'ng build --prod --aot --sm --progress=false'
    }

    stage('Archive') {
        sh 'tar -cvzf dist.tar.gz --strip-components=1 dist'
        archive 'dist.tar.gz'*/
    }

 }
}
