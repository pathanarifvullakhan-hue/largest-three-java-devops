# Largest of Three Numbers — Java + GitHub + Jenkins + Kubernetes

This project covers only:
- **6(a)** Find the largest of three numbers in Java and upload the source to GitHub using Git.
- **6(b)** Configure a Jenkins Pipeline build job and deploy a containerized sample Java application to Kubernetes.

No Spring Boot, Maven, Python, database, or Selenium is used.

## 1. Software required (Mac)
Install or confirm Java JDK 17+, Git, Docker Desktop, Jenkins LTS, and `kubectl`.

Check:
```bash
java -version
javac -version
git --version
docker --version
kubectl version --client
```

With Homebrew, install missing tools:
```bash
brew install openjdk@17 git jenkins-lts kubectl
```
Install Docker Desktop separately: https://www.docker.com/products/docker-desktop/
Start Docker Desktop. If Java is not found, follow Homebrew's printed instructions to link/configure it.

## 2. Extract and open the project
Extract the ZIP, then in Terminal:
```bash
cd ~/Downloads/largest-three-java-devops
```
Adjust the path if you extracted it elsewhere.

## 3. Run Java program — Problem 6(a)
```bash
mkdir -p out
javac -d out src/main/java/com/example/largest/LargestOfThree.java
java -cp out com.example.largest.LargestOfThree
```
Enter three numbers. Example:
```text
Enter first number: 12
Enter second number: 45
Enter third number: 23
Largest number = 45.0
```
Run automated tests:
```bash
./scripts/test.sh
```
Expected final line: `ALL TESTS PASSED`.

## 4. Upload to GitHub using Git — Problem 6(a)
1. Sign in at https://github.com and click **New repository**.
2. Repository name: `largest-three-java-devops`.
3. Choose Public or Private and create it. Do not initialize it with a README.
4. In Terminal, from the project folder:
```bash
git init
git add .
git commit -m "Add largest of three Java project"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/largest-three-java-devops.git
git push -u origin main
```
Replace `YOUR_USERNAME` with your GitHub username. If `origin` already exists:
```bash
git remote set-url origin https://github.com/YOUR_USERNAME/largest-three-java-devops.git
git push -u origin main
```
5. Refresh GitHub and confirm the project files appear.

## 5. Build and test the Docker container
Start Docker Desktop, then:
```bash
docker build -t largest-three-java:latest .
docker images
docker run --rm -p 8080:8080 largest-three-java:latest
```
Open http://localhost:8080. Stop with `Ctrl+C`.

## 6. Enable Kubernetes in Docker Desktop
1. Open Docker Desktop → **Settings** → **Kubernetes**.
2. Enable Kubernetes and apply/start it. Wait until it reports running.
3. In Terminal:
```bash
kubectl config get-contexts
kubectl config current-context
kubectl get nodes
```
For Docker Desktop, the context is commonly `docker-desktop`; if needed:
```bash
kubectl config use-context docker-desktop
```
Do not continue until `kubectl get nodes` shows a Ready node.

## 7. Configure Jenkins
Start Jenkins LTS:
```bash
brew services start jenkins-lts
```
Open http://localhost:8080 and complete the first-time setup:
1. Get the initial password from the path shown by Jenkins. A common Homebrew path is:
   ```bash
   cat ~/.jenkins/secrets/initialAdminPassword
   ```
   If that path does not exist, use the path shown on the Jenkins unlock page or in the service output.
2. Install Suggested Plugins and create your admin user.
3. Confirm **Pipeline** and **Git** plugins are installed (Manage Jenkins → Plugins).

### Create the Pipeline job (following the uploaded PDF's SCM approach)
1. Jenkins → **New Item**.
2. Job name: `Largest-Three-Kubernetes-Pipeline`.
3. Select **Pipeline** → **OK**.
4. In the Pipeline section, set Definition to **Pipeline script from SCM**.
5. SCM: **Git**.
6. Repository URL: `https://github.com/YOUR_USERNAME/largest-three-java-devops.git`.
7. Branch specifier: `*/main`.
8. Script Path: `Jenkinsfile`.
9. Click **Save**.

For a private repository, add a Jenkins credential with repository read access and select it in the Git SCM configuration. Never place a GitHub token in the Jenkinsfile.

## 8. Run Jenkins build and deployment
Before Build Now, ensure:
- Docker Desktop is running.
- Kubernetes is enabled and `kubectl get nodes` shows Ready.
- Docker and kubectl work in the same macOS user environment used by Jenkins.
- Jenkins can find `java`, `docker`, and `kubectl`.

Check command paths:
```bash
which java
which docker
which kubectl
```
If Jenkins Console Output reports a command not found, add the directory returned by `which` to the Jenkins service PATH and restart Jenkins. Common Homebrew directories are `/opt/homebrew/bin` (Apple Silicon) and `/usr/local/bin` (Intel); use your actual command paths.

In Jenkins:
1. Open `Largest-Three-Kubernetes-Pipeline`.
2. Click **Build Now**.
3. Open the latest build → **Console Output**.
4. Confirm `Checkout`, `Compile and Test`, `Build Docker Image`, and `Deploy to Kubernetes` succeed.

The Jenkinsfile builds the image locally and uses `imagePullPolicy: Never`, intended for Docker Desktop's local Kubernetes. If using a remote cluster, push the image to a container registry and change the image reference and pull policy in `k8s/deployment.yaml`.

## 9. Verify Kubernetes
```bash
kubectl get deployments
kubectl get pods
kubectl get services
kubectl rollout status deployment/largest-three-app
```
Wait for the pod to be Running/Ready. Open http://localhost:30080.

API test:
```bash
curl "http://localhost:30080/largest?a=12&b=45&c=23"
```
Expected:
```json
{"a":12.0,"b":45.0,"c":23.0,"largest":45.0}
```

## 10. Stop and clean up
```bash
kubectl delete -f k8s/service.yaml
kubectl delete -f k8s/deployment.yaml
brew services stop jenkins-lts
```
Quit Docker Desktop normally when finished.

## Practical record / exam write-up

### 6(a) Largest among three numbers
**Aim:** To write a Java program to find the largest among three numbers and upload the program to GitHub using Git.

**Tools required:** Java JDK, macOS Terminal, Git, GitHub.

**Procedure:** Create the Java source file; compile and execute it; test different inputs; initialize a Git repository; add and commit files; create a GitHub repository; connect the remote repository; push the `main` branch.

**Result:** The Java program finds the largest of three numbers, and the source code is uploaded to GitHub.

### 6(b) Jenkins build job and Kubernetes deployment
**Aim:** To configure a Jenkins Pipeline build job and deploy a sample containerized Java application using Kubernetes.

**Tools required:** Jenkins, GitHub, Docker Desktop, Kubernetes, `kubectl`, Java JDK.

**Procedure:** Upload the project to GitHub; configure a Jenkins Pipeline job using **Pipeline script from SCM**; enter the repository URL, `*/main`, and `Jenkinsfile`; run the build; build the Docker image; apply the Kubernetes Deployment and Service; verify the pod and access the application through NodePort.

**Result:** Jenkins builds and tests the Java application, creates its Docker image, and deploys the sample application to Kubernetes.

## Project structure
```text
largest-three-java-devops/
├── src/main/java/com/example/largest/
│   ├── LargestOfThree.java
│   └── CalculatorServer.java
├── src/test/java/com/example/largest/
│   └── LargestOfThreeTest.java
├── scripts/test.sh
├── k8s/deployment.yaml
├── k8s/service.yaml
├── Dockerfile
├── Jenkinsfile
└── README.md
```
