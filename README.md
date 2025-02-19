## 📚 pull-image-cicd 브랜치
pull-image 브랜치의 내용을 cicd 도구인 github actions 를 이용해서 자동화

</br></br>

## ✏️ 태그 관리
spring boot 애플리케이션을 실행하는 이미지는 워크플로우가 실행될때,
커밋 sha 값으로 이미지를 만들고 푸시한다음 latest 태그를 추가해서 푸시

mysql, redis 이미지는 V0.0.0 형식으로 이미지 빌드하고 latest 태그를 추가해서 푸시


ec2 서버에서 이미지를 풀받아 사용할때는 latest 태그를 풀받아 사용.


## ✏️ spring 컨테이너 종료시 응답중일때는 ?

</br>

## ✏️ 배포 방법 및 명령어
- 도커파일 생성
```
// 프로젝트 경로 아래의 docker 디렉토리에 도커파일 (Dockerfile-spring, Dockerfile-mysql, Dockerfile-redis) 생성
// 파일 내용은 github 참고

// 프로젝트 경로 아래에 .env 파일 생성
// 파일내용
MYSQL_ROOT_PASSWORD=abcd1234
MYSQL_DATABASE=test
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
```