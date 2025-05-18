## 📚 Spring Boot 애플리케이션을 EC2 서버에 배포하는 다양한 방법 실습
Spring Boot 애플리케이션을 EC2 서버에 배포하는 3가지 방법을 직접 수동으로 해보고, 이러한 수동 배포 프로세스를 CI/CD 도구인 GitHub Actions를 활용하여 자동화
- 리버스 프록시로 Nginx를 활용하여 Blue-Green 방식의 무중단 배포 구현 및 HTTPS 설정, IP별 초당 요청 제한을 적용
- 인프라 구성 시, 배포 프로세스에 더 집중하기 위해 고가용성이나 자동 복구와 같은 고급 인프라 기능은 고려하지 않고 단일 EC2 인스턴스를 사용
- Spring Boot, MySQL, Redis, Nginx 실행 시 직접 설치해서 실행하는 방법과 도커 컨테이너를 활용하는 방법 둘 다 실습하여 비교
  
위의 내용을 브랜치별로 상세히 정리

</br></br>

## ✏️ 배포 방법
이 프로젝트에서 실습할 배포 방법
1. 로컬에서 빌드한 .jar 파일을 ec2 서버에 전송하여 실행 - (transfer-jar, transfer-jar-cicd 브랜치)
2. ec2 서버에서 프로젝트 클론하여 빌드 후 실행 - (clone-project, clone-project-cicd 브랜치)
3. 로컬에서 빌드한 도커 이미지를 ec2 서버에서 pull 받아 컨테이너 실행 - (pull-image, pull-image-cicd 브랜치)
