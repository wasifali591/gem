pipeline {
    agent any
    
    triggers {
        githubPush()
    }
    environment {
        APP_NAME = "gem-app"
        IMAGE_NAME = "gem-app"
        VERSION = "1.0.${BUILD_NUMBER}"
    }

    stages {
        stage('Clean Workspace') {
            steps {
                deleteDir()
            }
        }
        
        stage('Clone Code') {
            steps {
                git branch: 'development',
                    credentialsId: 'github-creds',
                    url: 'https://github.com/wasifali591/gem.git'
            }
        }
        
        stage('Clean Maven Cache') {
            steps {
                sh 'rm -rf ~/.m2/repository || true'
            }
        }
        
        stage('Build JAR') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t $IMAGE_NAME:$VERSION .'
                sh 'docker tag $IMAGE_NAME:$VERSION $IMAGE_NAME:latest'
            }
        }

        stage('Stop Old Container') {
            steps {
                sh 'docker stop $APP_NAME || true'
                sh 'docker rm $APP_NAME || true'
            }
        }

        stage('Run Container') {
            steps {
                sh '''
                docker run -d \
                -p 8080:8080 \
                --name $APP_NAME \
                $IMAGE_NAME:$VERSION
                '''
            }
        }
    }

    post {
        success {
            echo "✅ Deployment Successful! Version: ${VERSION}"
        }
        failure {
            echo "❌ Deployment Failed!"
        }
    }
}