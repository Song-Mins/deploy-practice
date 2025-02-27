## 📚 Spring Boot 애플리케이션을 EC2 서버에 배포하는 다양한 방법 실습
이때까지 다양한 방법으로 Spring Boot 애플리케이션을 EC2 서버에 배포해보았는데 이러한 배포방법들을 정리하고자 한다.   
대략적인 내용은 다음과 같다.
1. 아래에서 설명할 배포방법들을 수동으로 배포해보고 수동으로 진행했던 배포 프로세스를 CI/CD 도구인 github-actions 를 활용해 자동화
2. 다음 버전의 애플리케이션을 배포할때 현재 실행중인 애플리케이션이 중단되지 않도록 하는 무중단 배포 방법

</br></br>

## ✏️ 배포 방법
이 프로젝트에서 실습할 배포 방법은 아래와 같다.
1. 로컬에서 빌드한 .jar 파일을 ec2 서버에 전송하여 실행
2. ec2 서버에서 프로젝트 클론하여 빌드 후 실행
3. 로컬에서 빌드한 도커 이미지를 ec2 서버에서 pull 받아 컨테이너 실행

</br>

## ✏️ 브랜치 종류 및 설명
여기서는 각 브랜치별 배포 방법의 특징을 간략히 설명한다.   
각 브랜치별 배포 방법에 대한 구체적인 구현 내용 및 명령어는 해당 브랜치의 README 파일을 참고하자.
### transfer-jar
- 로컬에서 빌드한 .jar 파일을 ec2 서버에 전송하여 실행

- 특징
  - 단순히 빌드된 .jar 파일을 서버로 전송하고 실행하면 되기 때문에 간단하고 빠르게 애플리케이션 실행 가능.
  - 서버에 직접 Java 를 설치해야하고 로컬 서버의 Java 버전과 일치해야됨.

### clone-project
- ec2 서버에서 프로젝트 클론하여 빌드 후 실행

- 특징
  - 서버에 프로젝트 코드가 존재하며 Git 을 활용해 원하는 시점의 코드로 바로 롤백할 수 있음.
  - 서버 자원이 애플리케이션 빌드 작업에 소모되므로 본래의 요청 처리나 운영에 영향을 줄 수 있음.
  - 서버에 직접 Java 를 설치해야하고 로컬 서버의 Java 버전과 일치해야됨.

### pull-image
- 로컬에서 빌드한 도커 이미지를 ec2 서버에서 pull 받아 컨테이너 실행

- 특징
  - d
  - d
  - d

### transfer-jar-cicd
- transfer-jar 브랜치의 배포방법을 자동화

### clone-project-cicd
- clone-project 브랜치의 배포방법을 자동화

### pull-image-cicd
- pull-image 브랜치의 배포방법을 자동화

</br>

## ✏️ mysql, redis 서버 실행 방법
현재 배포할려는 Spring Boot 애플리케이션을 실행하려면 mysql 서버와 redis 서버가 실행중 이여야 한다.    
이를 실행하는 방법에는 아래와 같이 대표적으로 3가지 방법이 있다.

1. AWS 서비스 (RDS, ElastiCache) 이용
- 자동 백업, 확장성, 고가용성 등의 기능을 통해 사용자는 애플리케이션 개발 및 운영에만 집중할 수 있다.
- 사용량에 따라 비용이 발생한다.

2. EC2 서버에 직접 설치 및 실행
- 추가적인 기술 없이도 설치 및 실행 가능
- 백업, 모니터링, 확장 등을 직접 관리 해야됨

3. 도커 이미지를 사용하는 방법
- 컨테이너화된 환경을 제공하므로 개발 환경과 운영 환경의 차이로 인한 문제를 최소화할 수 있다.
- 2번 방법과 동일하게 백업, 모니터링, 확장 등을 직접 관리해야됨

각 브랜치 별로 다른 방법을 사용해서 mysql, redis 서버를 실행할 것이다.

</br>

## ✏️ 도커 네트워크 및 EC2 보안그룹 설정 - pull-image, pull-image-cicd 브랜치
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

## ✏️ 도커 이미지 저장소 - pull-image, pull-image-cicd 브랜치
로컬에서 도커 이미지를 푸시하고 EC2 서버에서 풀하기 위해서 도커 이미지 저장소가 필요하다.  
대표적인 도커 이미지 저장소는 아래와 같이 3가지가 있다.

1. Docker Hub
- Docker의 기본 이미지 저장소로, Docker CLI 와 완벽하게 통합
- 커뮤니티에 공유된 수많은 공용 이미지에 접근 가능

2. Amazon Elastic Container Registry (ECR)
- AWS 서비스로, EC2, ECS, EKS와 같은 AWS 서비스와 긴밀히 통합
- IAM 을 통해 세밀한 권한 관리 가능
- 보안 및 인증 관리가 뛰어남.

3. GitHub Container Registry (GHCR)
- GitHub와 통합된 컨테이너 이미지 저장소로, GitHub Actions 와 원활하게 동작
- GitHub Actions에서 바로 사용 가능하여 CI/CD 파이프라인 설정이 간단
- 소스 코드와 이미지를 한 곳에서 관리 가능

pull-image 브랜치에서는 1, 2번 방법, pull-image-cicd 브랜치에서는 3번 방법을 이용할 것이다.

</br>
