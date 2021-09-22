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
                nexusArtifactUploader artifacts: [
                    [
                        artifactId: 'jetty-maven-plugin',
                        classifier: '', 
                        file: '/target/',
                        type: 'war']
                        ],
                        credentialsId: 'Narayana-cred', 
                        groupId: 'com.efsavage', 
                        nexusUrl: '15.207.79.185:8080', 
                        nexusVersion: 'nexus3', 
                        protocol: 'http', 
                        repository: 'LearnDevops', 
                        version: '2.0.4'
              }
        }            
        stage('deploy'){
            steps {
                deploy adapters: [tomcat8(credentialsId: 'Narayana-tomcat cred', path: '',
                url: 'http://3.109.219.27:8080/')], contextPath: 'hello', war: '**/*.war'
            }
        }
    }
}
