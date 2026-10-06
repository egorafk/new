pipeline {
    agent any

    stages {
        stage('Build') {
            steps { sh 'mvn -B -ntp clean package'}
        }
    }  
}
