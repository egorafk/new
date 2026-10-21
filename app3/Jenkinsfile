pipeline {
    agent any
    environment {
          JAVA_HOME = "/usr/lib/jvm/java-17-openjdk-amd64"
          PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
    }
    stages {
        stage('Build') {
            steps { sh 'mvn -B -ntp clean package'}
        }

        stage('Start') {
            steps { sh '''
                        cp target/advance.jar /opt/salary/advance.jar
                        sudo systemctl restart advance.service
                    '''
            }
        }
    }  
}
