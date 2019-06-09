pipeline {
    agent any
    stages {
        /*stage ('Checkout') {
            steps {
                checkout SCM
            }
        }*/

        stage ('Initialize') {
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                    mvn clean
                '''
            }
        }

        stage ('Tests') {
            steps {
                sh 'mvn test'
            }
        }

        stage ('Build') {
            steps {
                // A clean package is required for everyone to enjoy
                sh 'mvn clean package'
            }
        }
    }
    post {
        always {
            archiveArtifacts artifacts: 'target/xrp2-mcpc-ht-rc2-*.jar', fingerprint: true
        }
    }
}