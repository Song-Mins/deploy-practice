## 📚 pull-image-cicd 브랜치
pull-image 브랜치의 내용을 cicd 도구인 github actions 를 이용해서 자동화

</br></br>


## ✏️ 배포 방법 및 명령어
- 도커 파일 및 워크플로우 파일 생성
```
// 프로젝트 경로 아래의 docker 디렉토리에 도커파일 (Dockerfile-spring, Dockerfile-mysql, Dockerfile-redis) 생성
// 프로젝트 경로 아래의 .github/workflows 디렉토리에 워크플로우 파일 (deploy.yaml) 생성
// 파일 내용은 github 참고
```
- 이미지 빌드 및 도커허브에 이미지 푸시
```
// 프로젝트경로 아래의 도커 디렉토리로 이동
cd C:/project/deploy/docker

// 이미지 빌드 (태그는 V0.0.0 형식)
docker build -t docker.io/songker/deploy-mysql:V1.0.0 -f Dockerfile-mysql . 
docker build -t docker.io/songker/deploy-redis:V1.0.0 -f Dockerfile-redis .

// latest 태그 이미지 생성
docker tag docker.io/songker/deploy-mysql:V1.0.0 docker.io/songker/deploy-mysql:latest
docker tag docker.io/songker/deploy-redis:V1.0.0 docker.io/songker/deploy-redis:latest

// 도커허브 로그인
docker login

// 이미지 푸시
docker push docker.io/songker/deploy-mysql:V1.0.0
docker push docker.io/songker/deploy-redis:V1.0.0
docker push docker.io/songker/deploy-mysql:latest
docker push docker.io/songker/deploy-redis:latest

// 이미지 확인
docker images
```
- ec2 서버 접속 및 초기설정
```
// ec2 서버 접속
ssh -i ~/.ssh/deploy-key.pem ec2-user@43.201.23.247


- Docker Engine 설치
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

- init.sql 파일 생성
// /home/ec2-user 아래에 init.sql 파일 생성
vim init.sql
// 생성확인
cat init.sql
```
