## 📚 clone-build 브랜치
ec2 서버에서 프로젝트 클론하여 빌드 후 실행

</br></br>

## ✏️ ec2 인스턴스 설정
- AMI 는 Amazon Linux 2023, 인스턴스 타입은 t2.medium 이다.   
- 프리티어인 t2.micro 를 사용하지 않은 이유는 ~~~~~~
- 보안그룹의 인바운드 규칙에 ssh 로 서버에 접속하기 위한 SSH 트래픽과   
스프링 애플리케이션이 잘 실행되는지 확인하기 위한 TCO 트래픽의 8080 포트를 열어준다

</br>

## ✏️ mysql, redis 서버 실행
main 브랜치에서 설명했듯이 이 브랜치에서는 AWS 의 서비스를 이용한다

</br>

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
