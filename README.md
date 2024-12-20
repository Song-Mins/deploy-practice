## 📚 clone-build 브랜치
ec2 서버에서 프로젝트 클론하여 빌드 후 실행
</br>

## ✏️ ec2 인스턴스 설정
- AMI 는 Amazon Linux 2023, 인스턴스 타입은 t2.medium 이다.   
- 프리티어인 t2.micro 를 사용하지 않은 이유는 ~~~~~~
- 보안그룹의 인바운드 규칙에 ssh 로 서버에 접속하기 위한 SSH 트래픽과   
스프링 애플리케이션이 잘 실행되는지 확인하기 위한 TCO 트래픽의 8080 포트를 열어준다

## ✏️ mysql, redis 서버 실행
현재 배포할려는 스프링 애플리케이션을 실행하기 위해선 mysql 서버와 redis 서버가 실행중이여야 한다.    
mysql 과 redis 서버를 실행하는 방법에는 대표적으로 아래의 3가지 방법이 있다.
1. 직접 ec2 서버에 설치하여 실행하는 방법
2. AWS 의 서비스 이용하는 방법
3. 도커 이미지를 사용하는 방법이 있다.

현재 배포 방법에서는 2번 방법인 AWS 의 서비스 이용할 것이다.
1번 방법은 ec2 서버로 로컬에서 빌드한 .jar 파일 전송하여 실행하는 배포하는 transfer-jar 브랜치에서 사용하였고 
3번 방법은 스프링 애플리케이션 실행 환경을 이미지로 만들어서 이미지를 ec2 서버로 전송하여 배포하는 trnasfer-image 브랜치에서 할 예정이다.

## ✏️ 배포 방법 및 명령어
- ec2 서버 접속 및 설정
```
// ec2 서버 접속
ssh -i ~/.ssh/deploy-key.pem ec2-user@3.39.251.119

// ec2 서버 타임존 변경 (스케줄러 정상 작동을 위해)
sudo timedatectl set-timezone Asia/Seoul
// 변경 확인
date
```
- jdk17 설치 및 실행
```
// 패키지 업데이트
sudo yum update -y

// jdk17 설치
sudo yum install java-17-amazon-corretto-devel
// 설치 확인
java --version
```
