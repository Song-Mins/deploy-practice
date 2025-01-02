## 📚 pull-image 브랜치
로컬에서 빌드한 도커 이미지를 ec2 서버에서 pull 받아 컨테이너 실행

</br></br>

## ✏️ 도커 네트워크 및 EC2 보안그룹 설정
Mysql 컨테이너,  Redis 컨테이너는 보안상 Spring Boot 컨테이너에서만 접근 가능하도록 제한해야한다.

설정
- 따라서, Spring Boot 컨테이너는 8080:8080 으로 포트포워딩, Mysql 과 Redis 컨테이너는 포트포워딩을 설정하지 않을 것이고 이 3개의 컨테이너를 동일한 네트워크 환경에서 실행하여 해당 네트워크 외부에서는 접근하지 못하게 할것이다.  
- 또한, 기본 네트워크인 bridge 네트워크에서는 컨테이너 이름 기반의 DNS 를 지원하지 않아 IP 주소를 직접 사용해야 하므로 새로운 커스텀 네트워크를 만들어서 사용할 것이다.
- 그리고, 이렇게만 설정해도 되지만 ec2 인스턴스의 보안그룹의 인바운드 설정에서 Spring Boot 컨테이너에 접근하기 위한 8080 포트와 SSH 로 EC2 인스턴스에 접근하기 위한 SSH 트래픽만 열어두고 나머지는 제한함으로써 외부에서 Mysql 과 Redis 컨테이너에 접속할 가능성을 완전히 없앴다.  

추가 내용
- 만약, Mysql 컨테이너와 Redis 컨테이너에도 똑같이 포트포워딩 설정을 해주고 EC2 인스턴스의 보안 그룹의 인바운드 설정에 해당 포트포워딩된 포트에 대해 내부 트래픽만 허용한다면 도커 네트워크 설정 없이 EC2 인스턴스의 보안 그룹 설정만으로 외부 접근을 제어할 수 있다.   
- 하지만, 이방법은 외부로 부터의 접근을 열어두는 가능성을 남기고 내부의 백엔드 애플리케이션이 아닌 다른 서비스가 MySQL 컨테이너와 redis 컨테이너에 접근 가능하다는 단점이 있다.   
- 그리고 도커 컨테이너의 접근 제한은 도커 네트워크 설정을 통해 하는것이 적절하고 좀 더 세밀한 제한이 가능하다.

</br>

## ✏️ 도커 이미지 저장소
ㅇㅁㅇㅁㅁㅇㅁ

</br>

## ✏️ mysql, redis 서버 실행
main 브랜치에서 설명한대로   
mysql, redis 서버를 실행하는 여러가지 방법중에 이 브랜치에서는 ~~~  실행할 것이다.

</br>

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









