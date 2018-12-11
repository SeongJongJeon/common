node {
    checkout scm
    def testImage = docker.build("seongjongjeon/exmaple-api", "./dockerfiles/api")

    testImage.inside {
        sh './gradlew build -x test'
    }
}



pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh 'make'
                archiveArtifacts artifacts: '**/build/libs/*.jar', fingerprint: true
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying....'
            }
        }

        stage('API') {
            agent {
                docker { image: 'java:8' }
            }
        }
    }
}