#!groovy

pipeline {

    agent {
        node {
            label 'docker'
        }
    }
    options {
        timestamps()
    }

    environment {
        IMAGE = readMavenPom().getArtifactId()
        VERSION = readMavenPom().getVersion()
        registry = "diegocas0807/games-monolith"
        FOO = credentials("34115442-3e39-4449-9644-07589c9b836a")
    }

    stages {
        stage('Build') {
            agent {
                docker {
                    reuseNode true
                    image 'maven:3.5.0-jdk-8'
                }
            }
            steps {
                withMaven(options: [findbugsPublisher(), junitPublisher(ignoreAttachments: false)]) {
                    sh 'mvn clean findbugs:findbugs package'
                }
            }
            post {
                success {
                    archiveArtifacts(artifacts: '**/target/*.jar', allowEmptyArchive: true)
                }
            }
        }

        stage('Unit Tests') {
            agent any
            steps {
                echo 'Run unit tests...'
                sh 'mvn clean test'
            }
        }

        stage('Quality Analysis') {
            parallel {
              stage ('Integration Test') {
                    agent any
                    steps {
                        echo 'Run integration tests...'
                        sh 'mvn verify'
                    }
                }
            }
        }

        stage('Build Image') {
            when {
                branch 'master'
            }
            steps {
                sh """
            docker login $FOO_USR/$FOO_PSW
            sh 'echo "FOO is $FOO"'
            sh 'echo "FOO_USR is $FOO_USR"'
            sh 'echo "FOO_PSW is $FOO_PSW"'
            docker build -t ${IMAGE} .
            docker tag ${IMAGE} ${IMAGE}:${VERSION}
            docker kill \$(docker ps -a -q)
            
        """
            }
        }
        stage('Publish Image') {
            when {
                branch 'master'
            }
            steps {
                sh """
                    docker run -d --rm -t -p 8085:8080 ${IMAGE} 
        """
            }
        }
    }
    post {
        failure {
            mail to: 'jenkinsmdas@gmail.com',
                    subject: "Failed Pipeline: ${currentBuild.fullDisplayName}",
                    body: "Something was wrong!: ${env.BUILD_URL}"
        }
        success {
            mail to: 'alex.soto@salle.url.edu',
                    subject: "Pipeline Success!!!",
                    body: "THANK YOU.."
        }
    }
}