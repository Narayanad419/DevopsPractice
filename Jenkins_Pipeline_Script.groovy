currentBuild.displayName="Pipeline2-#"+BUILD_NUMBER
pipeline {
    agent {label 'Slave'}
    environment {PATH = "/opt/maven/bin:$PATH"}
    stages {
        stage('Hello') {
            steps {
                echo 'Hello World'
            }
        }
        stage('checkout'){
            steps {
                checkout([$class: 'GitSCM',
                branches: [[name: '*/master']],
                extensions: [],
                userRemoteConfigs: [[url: 'https://github.com/Narayanad419/hello-world-war.git']]])
            }
        }
        stage('build'){
            steps {
                sh '''mvn package'''
            }
        }
        stage('deploy'){
            steps {
                deploy adapters: [tomcat8(credentialsId: 'tomcat', path: '',
                url: 'http://3.109.219.27:8080/')],
                contextPath: 'Hello', war: '**target/*.war'
            }
        }
    }
}
