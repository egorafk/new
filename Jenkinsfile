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

        stage("SonarQube testing") {
            steps {
                withSonarQubeEnv('sonar') {
                    sh 'mvn -B -ntp org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.projectKey=advance'
                }
             }
          }

        stage("Quality Gate") {
             steps {
                 timeout(time: 5, unit: 'MINUTES') {
                     waitForQualityGate abortPipeline: true
                         }
                    }
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
