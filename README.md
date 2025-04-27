## 📚 pull-image 브랜치
로컬에서 빌드한 도커 이미지를 ec2 서버에서 pull 받아 컨테이너 실행

</br></br>

## ✏️ 배포 방법 및 명령어
- 도커파일 생성
```
// 프로젝트 경로 아래의 docker 디렉토리에 도커파일 (Dockerfile-spring, Dockerfile-mysql, Dockerfile-redis, Dockerfile-nginx) 생성
// 파일 내용 - github 참고
```
- 이미지 빌드 + 도커허브에 이미지 푸시
```
// 프로젝트 경로로 이동
cd 프로젝트 경로

// 프로젝트 clean 및 build (test 제외)
./gradlew clean build -x test

// spring, nginx 이미지 빌드
// 태그는 커밋 SHA 값 (git rev-parse HEAD), V0.0 형식
docker build -t docker.io/songker/deploy-spring:4428750d33e73a12f058a3b72d7011dbf2b5cc1c -f docker/Dockerfile-spring ./build/libs
docker build -t docker.io/songker/deploy-nginx:V1.0 -f docker/Dockerfile-nginx ./nginx

// 프로젝트경로 아래의 도커 디렉토리로 이동
cd docker

// mysql, redis 이미지 빌드 (태그는 V0.0 형식)
docker build -t docker.io/songker/deploy-mysql:V1.0 -f Dockerfile-mysql .
docker build -t docker.io/songker/deploy-redis:V1.0 -f Dockerfile-redis .

// latest 태그 spring, mysql, redis, nginx 이미지 생성
docker tag docker.io/songker/deploy-spring:4428750d33e73a12f058a3b72d7011dbf2b5cc1c docker.io/songker/deploy-spring:latest
docker tag docker.io/songker/deploy-mysql:V1.0 docker.io/songker/deploy-mysql:latest
docker tag docker.io/songker/deploy-redis:V1.0 docker.io/songker/deploy-redis:latest
docker tag docker.io/songker/deploy-nginx:V1.0 docker.io/songker/deploy-nginx:latest

// 이미지 확인
docker images


// 도커허브 로그인
docker login

// 이미지 푸시
docker push docker.io/songker/deploy-spring:4428750d33e73a12f058a3b72d7011dbf2b5cc1c
docker push docker.io/songker/deploy-mysql:V1.0
docker push docker.io/songker/deploy-redis:V1.0
docker push docker.io/songker/deploy-nginx:V1.0
docker push docker.io/songker/deploy-spring:latest
docker push docker.io/songker/deploy-mysql:latest
docker push docker.io/songker/deploy-redis:latest
docker push docker.io/songker/deploy-nginx:latest
```
- ec2 서버 초기설정
```
// ec2 서버 접속
ssh -i ~/.ssh/deploy-key.pem ec2-user@3.34.97.141


// env 디렉토리 생성
mkdir -p /home/ec2-user/env
// env 디렉토리에 .env 파일 생성
cd /home/ec2-user/env
vim .env
// 파일내용
MYSQL_ROOT_PASSWORD=abcd1234
MYSQL_DATABASE=test

// scripts 디렉토리 생성
mkdir -p /home/ec2-user/scripts
// scripts 디렉토리에 init.sql 파일 생성
// 파일내용 - mysql 초기 테이블 생성 sql문
cd /home/ec2-user/scripts
vim init.sql


// 패키지 업데이트
sudo yum update -y

// cronie 설치
sudo yum install -y cronie

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


// 네트워크 생성
docker network create deploy-network


// mysql, redis, spring, nginx 이미지 풀
docker pull docker.io/songker/deploy-mysql:latest
docker pull docker.io/songker/deploy-redis:latest
docker pull docker.io/songker/deploy-spring:latest
docker pull docker.io/songker/deploy-nginx:latest


// mysql 컨테이너 실행
docker run -d \
  --name deploy-mysql \
  --network deploy-network \
  --env-file /home/ec2-user/env/.env \
  -v mysql-data:/var/lib/mysql \
  -v /home/ec2-user/scripts/init.sql:/docker-entrypoint-initdb.d/init.sql \
  docker.io/songker/deploy-mysql:latest

// redis 컨테이너 실행
docker run -d --name deploy-redis --network deploy-network docker.io/songker/deploy-redis:latest

// spring 컨테이너 실행
// mysql, redis 컨테이너가 완전히 실행되면 실행
// deploy-spring-blue 이름의 컨테이너가 실행중이어야지 nginx 컨테이너 초기 실행 가능
docker run -d --name deploy-spring-blue --network deploy-network docker.io/songker/deploy-spring:latest


// 인증서 저장 디렉토리 생성
mkdir -p /home/ec2-user/certs/live/deploy-practice.p-e.kr

// 챌린지 파일 저장 디렉토리 생성
mkdir -p /home/ec2-user/webroot

// 더미 인증서 생성
openssl req -x509 -nodes -newkey rsa:2048 -days 1 \
  -keyout /home/ec2-user/certs/live/deploy-practice.p-e.kr/privkey.pem \
  -out   /home/ec2-user/certs/live/deploy-practice.p-e.kr/fullchain.pem \
  -subj "/CN=deploy-practice.p-e.kr"

// options-ssl-nginx.conf 다운
curl -sSfLo /home/ec2-user/certs/options-ssl-nginx.conf \
  https://raw.githubusercontent.com/certbot/certbot/master/certbot-nginx/certbot_nginx/_internal/tls_configs/options-ssl-nginx.conf

// ssl-dhparams.pem 생성
openssl dhparam -out /home/ec2-user/certs/ssl-dhparams.pem 2048


// nginx 컨테이너 실행
docker run -d \
  --name deploy-nginx \
  --network deploy-network \
  -p 80:80 -p 443:443 \
  -v /home/ec2-user/webroot:/var/www/html:ro \
  -v /home/ec2-user/certs:/etc/letsencrypt \
  docker.io/songker/deploy-nginx:latest


// 더미 인증서 삭제 
sudo rm -rf /home/ec2-user/certs/live/deploy-practice.p-e.kr
sudo rm -rf /home/ec2-user/certs/archive/deploy-practice.p-e.kr
sudo rm -rf /home/ec2-user/certs/renewal/deploy-practice.p-e.kr.conf


// certbot 컨테이너로 인증서 발급
docker run --rm \
  -v /home/ec2-user/webroot:/var/www/html \
  -v /home/ec2-user/certs:/etc/letsencrypt \
  certbot/certbot certonly \
    --webroot \
    -w /var/www/html \
    -d deploy-practice.p-e.kr \
    --agree-tos \
    --email ssoogg5309@gmail.com \
    --non-interactive

// nginx 리로드
docker exec deploy-nginx nginx -s reload


// 갱신 작엉 시물레이션
docker run --rm \
  -v /home/ec2-user/webroot:/var/www/html \
  -v /home/ec2-user/certs:/etc/letsencrypt \
  certbot/certbot renew --dry-run


// 인증서 갱신 스크립트 생성
cd /home/ec2-user/scripts
vim renew_cert.sh
// 파일 내용
#!/bin/bash
set -e
# 갱신
/usr/bin/docker run --rm \
  -v /home/ec2-user/webroot:/var/www/html \
  -v /home/ec2-user/certs:/etc/letsencrypt \
  certbot/certbot renew --quiet
# nginx 리로드
docker exec deploy-nginx nginx -s reload

// 로그 파일 생성
sudo mkdir -p /var/log/letsencrypt
cd /var/log/letsencrypt
sudo vim renew.log


// 인증서 갱신 스케줄러 등록
crontab -e
// 명령어 내용
0 2 * * * /home/ec2-user/scripts/renew_cert.sh >> /var/log/letsencrypt/renew.log 2>&1
// 등록 스케줄러 확인
crontab -l
```
