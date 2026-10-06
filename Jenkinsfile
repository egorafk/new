pipeline {
    agent any
    options { timestamps() }
    stages {
        stage('Build') {
            steps { sh 'mvn -B -ntp clean package'}
        }
    }  
}