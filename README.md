## 📚 transfer-jar 브랜치
로컬에서 빌드한 .jar 파일을 ec2 서버에 전송하여 실행

</br></br>

## ✏️ 배포 방법 및 명령어
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
// app 디렉토리에 deploy-practice.log 파일 생성
cd /home/ec2-user/app
vim deploy-practice.log

// scripts 디렉토리 생성
mkdir -p /home/ec2-user/scripts
// scripts 디렉토리에 init.sql 파일 생성
// 파일내용 - mysql 초기 테이블 생성 sql문
cd /home/ec2-user/scripts
vim init.sql


- jdk17, rdeis, mysql, nginx 설치 및 실행
// 패키지 업데이트
sudo yum update -y

// jdk17 설치
sudo yum install java-17-amazon-corretto-devel -y
// 설치 확인
java --version

// redis 설치
sudo yum install redis6 -y
// redis 실행
sudo systemctl start redis6
// 시스템 부팅시 redis 자동 실행 설정
sudo systemctl enable redis6
// redis 실행 상태 확인
sudo systemctl status redis6

// MySQL 8.0 저장소 추가하는 패키지 설치
sudo yum install https://dev.mysql.com/get/mysql80-community-release-el9-1.noarch.rpm -y
// GPG Key를 2023 버전으로 갱신 (갱신해야지 아래의 설치 명령어에서 에러 안남)
sudo rpm --import https://repo.mysql.com/RPM-GPG-KEY-mysql-2023
// mysql 설치
sudo yum install mysql-community-server -y
// mysql 실행
sudo systemctl start mysqld
// 시스템 부팅시 mysql 자동 실행 설정
sudo systemctl enable mysqld
// mysql 실행 상태 확인
sudo systemctl status mysqld

// nginx 설치
sudo yum install nginx -y
// nginx 실행
sudo systemctl start nginx
// 시스템 부팅시 nginx 자동 실행 설정
sudo systemctl enable nginx
// nginx 실행 상태 확인
sudo systemctl status nginx


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

// 데이터베이스 생성
CREATE DATABASE deploy;
// 데이터베이스 확인
SHOW DATABASES;
// 데이터베이스 선택
USE deploy;
// init.sql 실행
SOURCE /home/ec2-user/scripts/init.sql;
// 테이블 확인
SHOW TABLES;


- nginx 설정
// default.conf 파일 생성
// 파일 내용 - github 참고
cd /etc/nginx/conf.d/
sudo vim default.conf

// Certbot 설치
sudo yum install certbot python3-certbot-nginx -y
// https 발급 - (EC2 보안그룹에 Http, Https 허용 + 도메인, IP 연결)
sudo certbot --nginx -d deploy-practice.p-e.kr
// 정상 적용 확인 - (설정 파일 변경 됐는지 확인 )
cd /etc/nginx/conf.d/
vim default.conf

// 설정 파일 문법 에러 체크
sudo nginx -t 
// 설정 파일 반영
sudo nginx -s reload
```
- .jar 파일 ec2 서버로 전송 및 실행
```
// 로컬에서 jar 파일 빌드
cd 프로젝트 경로
./gradlew clean build -x test

// scp 명령어로 jar 파일 ec2 서버로 전송
cd build/libs
scp -i ~/.ssh/deploy-key.pem deploy-practice.jar ec2-user@52.79.43.243:/home/ec2-user/app

// ec2 서버에서 파일 전송 확인
cd /home/ec2-user/app
ls
// 전송된 jar 파일 백그라운드에서 종료 없이 실행
nohup java -jar deploy-practice.jar > deploy-practice.log 2>&1 &

// 로그 파일 확인 
cat deploy-practice.log
// 실시간 로그 파일 확인 
tail -f deploy-practice.log
```
