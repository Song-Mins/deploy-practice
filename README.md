## 📚 transfer-jar-cicd 브랜치
transfer-jar 브랜치의 내용을 cicd 도구인 github actions 를 이용해서 자동화

</br></br>

## ✏️ 배포 방법 및 명령어
- 워크플로우 파일 생성
```
// 프로젝트 경로 아래의 .github/workflows 디렉토리에 워크플로우 파일 (deploy.yaml) 생성
// 파일 내용은 github 참고
```
- ec2 서버 접속 및 초기설정
```
// ec2 서버 접속
ssh -i ~/.ssh/deploy-key.pem ec2-user@3.39.251.119

// ec2 서버 타임존 변경 (스케줄러 정상 작동을 위해)
sudo timedatectl set-timezone Asia/Seoul
// 변경 확인
date

// app 디렉토리 생성
mkdir -p /home/ec2-user/app


- jdk17, rdeis, mysql 설치 및 실행
// 패키지 업데이트
sudo yum update -y

// jdk17 설치
sudo yum install java-17-amazon-corretto-devel
// 설치 확인
java --version

// redis 설치
sudo yum install redis6
// redis 실행
sudo systemctl start redis6
// 시스템 부팅시 redis 자동 실행 설정
sudo systemctl enable redis6
// redis 실행 상태 확인
sudo systemctl status redis6

// MySQL 8.0 저장소 추가하는 패키지 설치
sudo yum install https://dev.mysql.com/get/mysql80-community-release-el9-1.noarch.rpm
// GPG Key를 2023 버전으로 갱신 (갱신해야지 아래의 설치 명령어에서 에러 안남)
sudo rpm --import https://repo.mysql.com/RPM-GPG-KEY-mysql-2023
// mysql 설치
sudo yum install mysql-community-server
// 설치 확인
mysql --version
// mysql 실행
sudo systemctl start mysqld
// 시스템 부팅시 mysql 자동 실행 설정
sudo systemctl enable mysqld
// mysql 실행 상태 확인
sudo systemctl status mysqld

// 실행중인 전체 프로그램 확인
ps -ef


- mysql 설정
// 임시 비밀번호 받기
sudo grep 'temporary password' /var/log/mysqld.log

// 위의 임시 비밀번호로 MySQL 서버에 접속 
mysql -u root -p

// root@localhost 계정 비밀번호 변경
ALTER USER 'root'@'localhost' IDENTIFIED BY '변경할 비밀번호 입력';
// 비밀번호 변경 반영 
FLUSH PRIVILEGES;
// 현재 MySQL 서버에 존재하는 모든 사용자 계정을 확인
SELECT User, Host FROM mysql.user;

// 필요한 데이터베이스 생성
CREATE DATABASE deploy;
```
