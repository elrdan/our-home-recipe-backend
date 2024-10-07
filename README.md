# 우리집 레시피

## 📌 프로젝트 목적
- 실제 서비스 중인 레시피 관련 사이트를 참고하여 기본적인 기능을 구현하면서 기본적인 기능을 학습하고, 논의를 통해 없는 기능을 추가하는 등 확장 및 유지보수를 서비스까지 하는걸 목표합니다.(1차 기본기능 기능구현 완료)

## ⌛️ 프로젝트 기간
- 2024년 8월 27일 ~ 2024년 10월 6일(약 6주)

## 👥 팀원
- **이성오:** 백엔드 & 프론트 - [깃 허브](https://github.com/elrdan)
- **이소연:** 프론트 - [깃 허브](https://github.com/isylsy166)

## 도메인 별 목표
- 회원
  - 회원 별 권한에 따라 접근할 수 있는 API 제어
  - JWT(accessToken, refreshToken)을 사용
  - 로그아웃 기능 구현(Redis를 통해 BlackList에 등록하여 만료시간까지 토큰을 사용하지 못하도록 처리)
  - aws s3를 이용해 이미지 저장 및 관리
  - 회원간 검색을 통해 팔로워, 팔로잉 구현
- 레시피
  - 레시피 등록, 조회, 상세조회 구현
  - 레시피 등록시에 필요한 메타데이터 조회 API를 통해 유효한 데이터를 사용
  - 레시피 댓글 구현
- 이벤트
  - 이메일 인증을 위해 SMTP를 활용하여 인증코드(Redis에 저장) 발송 구현
  - 이메일이 발송된 인증코드를 Redis를 통해 검증하고 인증하는 기능 구현

## 기술 스택
- **개발 환경** </br>
![Java](https://img.shields.io/badge/Java-007396?style=flat-square&logo=java&logoColor=white)
![Spring](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat-square&logo=spring-boot&logoColor=white)
![JPA](https://img.shields.io/badge/JPA-59666C?style=flat-square&logo=Hibernate&logoColor=white)
![Querydsl](https://img.shields.io/badge/Querydsl-0769AD?style=flat-square&logo=Querydsl&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=flat-square&logo=github-actions&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=flat-square&logo=swagger&logoColor=white)
![Postman](https://img.shields.io/badge/Postman-FF6C37?style=flat-square&logo=postman&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-232F3E?style=flat-square&logo=amazon-aws&logoColor=white)

- **협업 도구** </br>
![Discord](https://img.shields.io/badge/Discord-5865F2?style=flat-square&logo=discord&logoColor=white)
![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=github&logoColor=white)

## 소프트웨어 아키텍처
<img src="https://github.com/user-attachments/assets/f99ca1b2-bd61-4c55-80d7-d289525ec990" alt="소프트웨어 아키텍처" width="400"/>

## 프로젝트 문서(요구사항, API 문서, 스크럼 등)
https://github.com/OurHomeRecipe/our-home-recipe-wiki/wiki
