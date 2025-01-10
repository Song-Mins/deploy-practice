## 📚 pull-image 브랜치
로컬에서 빌드한 도커 이미지를 ec2 서버에서 pull 받아 컨테이너 실행

</br></br>

## ✏️ 배포 방법 및 명령어
- 도커파일 작성, 프로젝트 빌드, 이미지 빌드
```
// 프로젝트 경로하위의 docke 디렉토리 안에 도커파일 Dockerfile-backend 생성
// 파일 내용
FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY deploy-practice.jar /app

ENTRYPOINT ["java", "-jar", "deploy-practice.jar"]

EXPOSE 8080

// 프로젝트 빌드
인텔리제이 우측 Gradle 아이콘 클릭후 Tasks -> build -> clean, build 클릭

// 프로젝트경로로 이동
cd C:/project/deploy
// 이미지 빌드
docker build -f docker/Dockerfile-backend -t docker.io/songker/deploy-backend:1.0.0 build/libs
// 확인
docker images


```
- ec2 서버 접속 및 설정
```
// ec2 서버 접속
ssh -i ~/.ssh/deploy-key.pem ec2-user@3.39.251.119

// ec2 서버 타임존 변경 (스케줄러 정상 작동을 위해)
sudo timedatectl set-timezone Asia/Seoul
// 변경 확인
date
```
- docker desktop 설치 및 docker hub 로그인
```
// 패키지 업데이트
sudo yum update -y
// 도커 설치
sudo yum install docker -y
// 설치 확인
docker --version
// 도커 실행
sudo service docker start
// 자동실행 설정 
sudo systemctl enable docker
// 도커 실행 상태 확인
sudo systemctl status docker
// docker 그룹 생성 후 사용자(ec2-user) 추가 -> sudo 없이 docker 실행 가능(재부팅 해야됨)
sudo usermod -aG docker ec2-user

// 도커 허브 토큰 생성
// 생성한 토큰으로 도커 허브 로그인
docker login -u songker
```
-  Mysql, Redis, Spring Boot 이미지 풀 및 컨테이너 실행
```
// mysql 이미지 풀
docker pull
// redis 이미지 풀
docker pull
// 스프링 부트 이미지 풀
docker pull songker/deploy-backend:1.0.0

// mysql 컨테이너 실행
docker run -d --name $CONTAINER_NAME -p 3306:3306 -e MYSQL_ROOT_PASSWORD={PASSWORD} $IMAGE_NAME
// redis 컨테이너 실행
docker run -d --name $CONTAINER_NAME -p 6379:6379 -e REDIS_PASSWORD={PASSWORD} $IMAGE_NAME
// Spring Boot 컨테이너 실행
sudo docker run --name mysql-container -e MYSQL_ROOT_PASSWORD=`your-password` -d -p 3306:3306 mysql

```









