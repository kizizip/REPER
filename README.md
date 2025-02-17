# 🏪 음성인식 기반 카페 레시피 도우미

## 👥 팀 구성
| 역할 | 이름 |
|------|------|
| 팀장 | 심근원 |
| 팀원 | 김정언, 박재영, 안주현, 이서현, 임지혜 |

## 🚀 기술 스택

| 분야        | 사용 기술 |
|------------|--------------------------------|
| **Backend** | ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat&logo=springboot&logoColor=white), ![JPA](https://img.shields.io/badge/JPA-6DB33F?style=flat) ,  ![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=mysql&logoColor=white), ![OpenAI](https://img.shields.io/badge/OpenAI-412991?style=flat&logo=openai&logoColor=white), ![Flask](https://img.shields.io/badge/Flask-000000?style=flat&logo=flask&logoColor=white), ![Elasticsearch](https://img.shields.io/badge/Elasticsearch-005571?style=flat&logo=elasticsearch&logoColor=white), ![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=flat&logo=firebase&logoColor=white) |
| **Frontend** | ![Android Studio](https://img.shields.io/badge/Android%20Studio-3DDC84?style=flat&logo=android-studio&logoColor=white), ![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=flat&logo=kotlin&logoColor=white), ![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=flat&logo=firebase&logoColor=white), ![MediaPipe](https://img.shields.io/badge/MediaPipe-FF6600?style=flat), ![Retrofit2](https://img.shields.io/badge/Retrofit2-007ACC?style=flat)    |
| **Infra** | ![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white), ![Jenkins](https://img.shields.io/badge/Jenkins-D24939?style=flat&logo=jenkins&logoColor=white) |
| **CI/CD** | ![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?style=flat&logo=github-actions&logoColor=white), ![Jenkins](https://img.shields.io/badge/Jenkins-D24939?style=flat&logo=jenkins&logoColor=white) |
| **Collaboration Tools** | ![Git](https://img.shields.io/badge/Git-F05032?style=flat&logo=git&logoColor=white), ![GitLab](https://img.shields.io/badge/GitLab-FC6D26?style=flat&logo=gitlab&logoColor=white), ![Jira](https://img.shields.io/badge/Jira-0052CC?style=flat&logo=jira&logoColor=white), ![Figma](https://img.shields.io/badge/Figma-F24E1E?style=flat&logo=figma&logoColor=white), ![Notion](https://img.shields.io/badge/Notion-000000?style=flat&logo=notion&logoColor=white) |

## 👨‍💻 팀원별 기술 기여

### 🔹 **Backend**
#### 🏷 박재영
- 
#### 🏷 이서현
- 

#### 🏷 임지혜 
- 
---

### 🔹 **Frontend**
#### 🏷 김정언 
- 

#### 🏷 심근원
- 

#### 🏷 안주현
- 
---
### 🔹 **Infra**
#### 🏷 김정언
- 



## 📊 프로젝트 기획

### 💡 기획 의도
카페 현장의 실질적인 문제를 해결하고자 다음과 같은 목표로 기획되었습니다:
- 다양한 카페 레시피의 빠른 학습 및 숙지 지원
- 핸즈프리 음성인식으로 위생적인 레시피 확인 가능
- 체계적인 레시피 관리 시스템 구축
- 신규 직원의 효율적인 교육 지원

<img width="6001" alt="개발 기획" src="https://github.com/user-attachments/assets/a6402e95-260e-4464-b7c0-116674385415" />

### 페르소나 분석
서비스의 주요 사용자를 이해하기 위해 세 가지 페르소나를 설정했습니다:

**1. 알바생 페르소나**
- 특징: 새로운 카페 알바생
- 니즈: 빠른 레시피 학습, 실수 없는 업무 수행

![페르소나-알바생ver](https://github.com/user-attachments/assets/21819574-7b24-4530-a05b-f4e3822629fc)

**2. 카페 운영자 페르소나**
- 특징: 카페 점주
- 니즈: 효율적인 직원 교육, 레시피 관리

![페르소나-사장님ver](https://github.com/user-attachments/assets/7425e78e-9ac7-4fe9-a64e-e315fedbaf40)

**3. 카페 관리자 페르소나**
- 특징: 매장 관리자
- 니즈: 체계적인 근태 관리, 업무 효율화


![페르소나-사장님(근태)ver](https://github.com/user-attachments/assets/f76b1783-b5c1-4c19-a017-c620fa5ec251)

### 고객여정 지도 (Customer Journey Map)
사용자 경험을 깊이 이해하기 위해 두 관점에서 여정 지도를 작성했습니다:

**1. 알바생 관점의 여정 지도**
![고객여정지도-알바생ver](https://github.com/user-attachments/assets/02046000-6228-45fe-9b34-1571cdd0ccfb)

**2. 운영자 관점의 여정 지도**
![고객여정지도-사장님ver](https://github.com/user-attachments/assets/ae4003c4-51fd-4516-9d3e-5ea2838daa25)

### 🎯 기대 효과
1. 알바생 측면:
   - 음성으로 간편한 레시피 확인
   - 단계별 상세 레시피로 실수 방지
   - 빠른 업무 적응 가능
   
2. 사장님 측면:
   - 체계적인 레시피 관리
   - 교육 시간 및 비용 절감
   - 매장 레시피 보안 강화
   - 일관된 맛과 품질 유지

<img width="5246" alt="기획의도 및 기대효과 및 어플 이름" src="https://github.com/user-attachments/assets/2bc4272a-1fea-4d7c-9ecc-aaf10a4daf79" />

## 🔍 시장 분석
### 경쟁사 분석
시장 조사를 통해 다음과 같은 차별화 전략을 수립했습니다:
- 음성인식 기반의 핸즈프리 솔루션
- 체계적인 레시피 관리 시스템
- 보안이 강화된 레시피 보호
- 직관적인 UI/UX로 사용자 편의성 극대화

<img width="7264" alt="시장조사 및 자료조사" src="https://github.com/user-attachments/assets/29de6882-fba1-4cb5-85d0-bbd33338876d" />
![D109 - Google Docs-이미지-0](https://github.com/user-attachments/assets/ba311011-a7f5-4209-82ec-59e520d7e892)
![D109 - Google Docs-이미지-1](https://github.com/user-attachments/assets/af0d313b-8035-403b-abae-314cafb84579)
![D109 - Google Docs-이미지-2](https://github.com/user-attachments/assets/09555e4c-4913-4d91-ab67-1ec9988ae4e1)
![D109 - Google Docs-이미지-3](https://github.com/user-attachments/assets/f89b3871-ca89-40fc-906f-d58c62bbc237)
![D109 - Google Docs-이미지-4](https://github.com/user-attachments/assets/bb4b0790-39d8-4291-a394-865b3ee9892e)


## 🛠 개발 방향
1. **기술적 요구사항**
   - 정확한 음성 인식 시스템 구현
   - 안정적인 데이터베이스 설계
   - 문서 파일 인식 및 변환 시스템
   - 보안 시스템 구축

2. **시스템 확장성**
   - 추가 기능 구현을 위한 확장 가능한 설계
   - API 연동을 위한 시스템 설계
   - 다양한 디바이스 지원


    |이름|1/13|1/14|1/15|1/16|1/17|
   |---|-----|----|----|----|----|
   |심근원|프로젝트 아이디어 도출, 조합|최종 아이디어 확정, 기능 명세서 작성|페르소나, 고객 여정 지도 작성|개발 기획 피드백 및 수정, 피그마 작업 준비| 피그마 작업 |
   |김정언|아이디어 도출 (종일 팀 회의)|아이디어 확정 및 아이디어 확장 (종일 팀 회의)| 기획안 작성 - 시장 조사(기획의도 자료 조사, 시장 규모 조사, 경쟁사 분석), 카페 메뉴별 레시피 더미 데이터 생성|프론트 기능 명세서 작성 (종일 프론트 회의)|피그마를 활용하여 앱 UI 및 프로토타입 디자인 |
   |박재영|아이디어 회의 - 아이디어 도출|아이디어 회의 - 아이디어 확정 및 아이디어 확장|페르소나, 고객여정지도 작성성|- api 명세서 초안 작성<br/>- DB 구체화<br/>- ERD 초안 제작|- api 명세서 구체화<br/>- ERD 완성 및 프론트에 전달|
   |안주현|아이디어 회의 - 아이디어 도출|아이디어 회의 - 아이디어 확정 및  아이디어확장|기획안 작성 - 주요고객층, 시장분석 및 자료조사, 더미 데이터 작성(이용할 카페 레시피 파일)|프론트엔드 기능명세서 작성,  Actvity, Fragment별 기능 작성|프로토타입, 목업 ,UI 디자인 |
   |이서현|아이디어 회의 - 아이디어 도출|아이디어 회의 - 아이디어 확정 및 아이디어 확장|기획의도 및 기대효과 도출|- api 명세서 초안 작성<br/>- DB 구체화<br/>- ERD 초안 제작|- api 명세서 구체화<br/>- ERD 완성 및 프론트에 전달|
   |임지혜|아이디어 회의 - 아이디어 도출|아이디어 회의 - 아이디어 확정 및 아이디어 확장|기획의도 및 기대효과 도출|- api 명세서 초안 작성<br/>- DB 구체화<br/>- ERD 초안 제작|- api 명세서 구체화<br/>- ERD 완성 및 프론트에 전달|
