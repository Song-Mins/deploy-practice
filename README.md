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

## ✏️ ec2 인스턴스 설정
- AMI 는 Amazon Linux 2023, 인스턴스 타입은 t2.medium 이다.   
- 프리티어인 t2.micro 를 사용하지 않은 이유는 서버에 직접 mysql, redis 서버를 설치하다 보니  
cpu 사용량이 계속 100을 초과하여서 t2.midium 으로 변경하였다.   
- 보안그룹의 인바운드 규칙에 ssh 로 서버에 접속하기 위한 SSH 트래픽과   
스프링 애플리케이션이 잘 실행되는지 확인하기 위한 TCO 트래픽의 8080 포트를 열어준다

</br>

## ✏️ mysql, redis 서버 실행
main 브랜치에서 설명한대로   
mysql, redis 서버를 실행하는 여러가지 방법중에 이 브랜치에서는 ~~~  실행할 것이다.

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
- jdk17, rdeis, mysql 설치 및 실행
```
// 패키지 업데이트
sudo yum update -y

// jdk17 설치
sudo yum install java-17-amazon-corretto-devel
// 설치 확인
java --version

// redis 설치
sudo yum install redis6
// redis 실행
sudo systemctl restart redis6
// 시스템 부팅시 redis 자동 실행 설정
sudo systemctl enable redis6
// redis 실행 상태 확인
sudo systemctl status redis6

// 주석
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

// 실행중인 전체 프로그램 확인
ps -ef
```
- mysql 설정
```
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
- .jar 파일 ec2 서버로 전송 및 실행
```
// scp 명령어로 jar 파일 ec2 서버로 전송
scp -i ~/.ssh/deploy-key.pem DainReview.jar ec2-user@3.39.251.119:/home/ec2-user/

// ec2 서버에서 파일 전송 확인
ls
// 전송된 jar 파일 실행  
sudo java -jar DainReview.jar
// 백그라운드에서 종료 없이 실행
nohup java -jar DainReview.jar &
// nohup.out 파일 확인 
cat nohup.out
// 실시간 nohup.out 파일 확인 
tail -f nohup.out
```








