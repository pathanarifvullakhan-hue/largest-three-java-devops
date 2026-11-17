pipeline {
    agent any
    options { timestamps() }
    environment {
        IMAGE_NAME = 'largest-three-java:latest'
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Compile and Test') {
            steps {
                sh 'chmod +x scripts/test.sh'
                sh './scripts/test.sh'
            }
        }
        stage('Build Docker Image') {
            steps {
                sh 'docker build -t ${IMAGE_NAME} .'
            }
        }
        stage('Deploy to Kubernetes') {
            steps {
                sh 'kubectl apply -f k8s/deployment.yaml'
                sh 'kubectl apply -f k8s/service.yaml'
                sh 'kubectl rollout status deployment/largest-three-app --timeout=120s'
                sh 'kubectl get pods'
                sh 'kubectl get services'
            }
        }
    }
    post {
        success {
            echo 'SUCCESS: Java tests passed and the application was deployed to Kubernetes.'
            echo 'Open http://localhost:30080'
        }
        failure {
            echo 'Pipeline failed. Review the stage output in Console Output.'
        }
    }
}
