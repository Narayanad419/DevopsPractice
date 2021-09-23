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
        stage('Artifactory upload'){
            steps {
                script{
                    def mavenPom = readMavenPom file: 'pom.xml'
                nexusArtifactUploader artifacts: [[artifactId: 'jetty-maven-plugin', classifier: '', 
                                                   file: "/home/ec2-user/workspace/Pipeline3/target/hello-world-war-${mavenPom.version}.war", 
                    type: 'war']
                    ],
                    credentialsId: 'Narayana-cred', 
                    groupId: 'com.efsavage', 
                    nexusUrl: 'http://15.207.79.185:8081/', 
                    nexusVersion: 'nexus3', 
                    protocol: 'http', 
                    repository: 'LearnDevops', 
                    version: "${mavenPom.version}"
                }    
              }
        }            
        stage('deploy'){
            steps {
                deploy adapters: [tomcat8(credentialsId: 'Narayana-tomcat', path: '', 
                url: 'http://3.109.219.27:8080/')], contextPath: 'hello', war: '**/target/*.war'
            }
        }
    }
}
