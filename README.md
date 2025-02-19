## 📚 pull-image 브랜치
로컬에서 빌드한 도커 이미지를 ec2 서버에서 pull 받아 컨테이너 실행

</br></br>

## ✏️ 배포 방법 및 명령어
- 도커파일 생성
```
// 프로젝트 경로 아래의 docker 디렉토리에 도커파일 (Dockerfile-spring, Dockerfile-mysql, Dockerfile-redis) 생성
// 파일 내용은 github 참고
```
- 프로젝트 빌드, 이미지 빌드 및 도커허브에 이미지 푸시
```
// 프로젝트경로로 이동
cd C:/project/deploy

// 프로젝트 clean 및 build (test 제외)
./gradlew clean build -x test

// spring 이미지 빌드 (태그는 커밋 SHA 값 -  git rev-parse HEAD)
docker build -t docker.io/songker/deploy-spring:4428750d33e73a12f058a3b72d7011dbf2b5cc1c -f docker/Dockerfile-spring ./build/libs

// 프로젝트경로 아래의 도커 디렉토리로 이동
cd docker

// mysql, redis 이미지 빌드 (태그는 V0.0.0 형식)
docker build -t docker.io/songker/deploy-mysql:V1.0.0 -f Dockerfile-mysql . 
docker build -t docker.io/songker/deploy-redis:V1.0.0 -f Dockerfile-redis .

// latest 태그 이미지 생성
docker build -t docker.io/songker/deploy-spring:latest -f docker/Dockerfile-spring ./build/libs
docker tag docker.io/songker/deploy-mysql:V1.0.0 docker.io/songker/deploy-mysql:latest
docker tag docker.io/songker/deploy-redis:V1.0.0 docker.io/songker/deploy-redis:latest

// 이미지 확인
docker images

// 도커허브 로그인
docker login

// 이미지 푸시
docker push docker.io/songker/deploy-spring:4428750d33e73a12f058a3b72d7011dbf2b5cc1c
docker push docker.io/songker/deploy-mysql:V1.0.0
docker push docker.io/songker/deploy-redis:V1.0.0
docker push docker.io/songker/deploy-spring:latest
docker push docker.io/songker/deploy-mysql:latest
docker push docker.io/songker/deploy-redis:latest
```

- ec2 서버 접속 및 초기설정
```
// ec2 서버 접속
ssh -i ~/.ssh/deploy-key.pem ec2-user@43.201.23.247


- docker desktop 설치
// 패키지 업데이트
sudo yum update -y
// 도커 설치
sudo yum install docker -y
// 설치 확인
docker --version
// 도커 실행
sudo systemctl start docker
// 자동실행 설정 
sudo systemctl enable docker
// 도커 실행 상태 확인
sudo systemctl status docker

// docker 그룹 생성 후 사용자 추가 -> sudo 없이 docker 실행 가능
sudo usermod -aG docker ec2-user
// 그룹 변경을 적용
newgrp docker
// 확인
groups


- .env 파일 생성
// /home/ec2-user 아래에 .env 파일 생성
vim .env
// 생성확인
cat .env

// 파일내용
MYSQL_ROOT_PASSWORD=abcd1234
MYSQL_DATABASE=test


-  네트워크 생성 및 Mysql, Redis, Spring Boot 이미지 풀 + 컨테이너 실행
// 네트워크 생성
docker network create deploy-network

// 스프링 이미지 풀
docker pull songker/deploy-spring:latest
// mysql 이미지 풀
docker pull docker.io/songker/deploy-mysql:latest
// redis 이미지 풀
docker pull docker.io/songker/deploy-redis:latest


// mysql 컨테이너 실행
docker run -d --name deploy-mysql --network deploy-network --env-file .env docker.io/songker/deploy-mysql:latest
// redis 컨테이너 실행
docker run -d --name deploy-redis --network deploy-network docker.io/songker/deploy-redis:latest
// Spring Boot 컨테이너 실행 - (mysql, redis 컨테이너가 완전히 실행되면)
docker run -d --name deploy-spring --network deploy-network -p 8080:8080 docker.io/songker/deploy-spring:latest
```
