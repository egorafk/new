pipeline {

    agent any

    parameters {
        string(name: 'BASE_VERSION', defaultValue: '1.0', description: 'base vesrion')
    }

    environment {
          JAVA_HOME = "/usr/lib/jvm/java-17-openjdk-amd64"
          PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
          VERSION = "${params.BASE_VERSION}.${env.BUILD_NUMBER}"
    }

    stages {
        stage('Build') {
            steps { sh 'mvn -B -ntp -q -Drevision=$VERSION clean package'}
        }

        stage("SonarQube testing") {
            steps {
                withSonarQubeEnv('sonar') {
                    sh 'mvn -B -ntp -q -Drevision=$VERSION org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.projectKey=advance'
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

        stage("Push to nexus") {
             steps {
                   withCredentials(
                      [usernamePassword( 
                         credentialsId: 'nexus-credentials',
                         usernameVariable: 'NEXUS_USER',
                         passwordVariable: 'NEXUS_PASSWORD'
                      )
                   ])
                    {
                     withMaven(mavenSettingsConfig: 'nexus-settings') {
                         sh 'mvn -B -ntp -q -Drevision=$VERSION deploy -DskipTests'
                         }
                       }
            }
        }
    }  
}
