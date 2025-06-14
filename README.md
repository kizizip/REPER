# <img src="docs/recipehelper_icon.png" width="20" alt="로고"> 당신 곁의 레시피 매니저, REPER

<img src="docs/recipehelper_thumbnail.png">


<br/>

## ✨ 개요

### 🥈 SSAFY 12기 공통 PJT 2등 🥈

**서비스명** : REPER

**한줄 설명** : 음성•모션 인식으로 레시피를 손 쉽게 볼 수 있는 카페 레시피 안내 서비스

**도메인** : 모바일

**팀원** : FE 3명 / BE 3명 (6명)

**프로젝트 기간** : 2025.01.06 ~ 2025.02.21 (7주)

[▶️영상 포트폴리오 보러가기](유튜브링크첨부)

<br/>

<div id="2"></div>

## 목차

1. [**기획 배경**](#-기획-배경)
1. [**주요 기능**](#-주요-기능)
1. [**주요 기능별 화면**](#-주요-기능별-화면)
1. [**기술적 특징**](#-기술적-특징)
1. [**기술 스택**](#-기술-스택)
1. [**프로젝트 진행 및 산출물**](#-프로젝트-진행-및-산출물)
1. [**개발 멤버 및 회고**](#-개발-멤버-및-역할분담)
1. [**메뉴얼 및 상세문서**](#-메뉴얼-및-상세-문서)

<br/>

<div id="2"></div>


## 🎯 기획 배경

- **카페 산업 내 레시피 교육의 비효율성 해소**  
  메뉴 다양화와 인력 교체가 잦아짐에 따라, 종이 매뉴얼이나 메신저를 통한 레시피 공유 방식은 교육 비용과 시간 낭비를 초래함


- **매장 간 정보 불균형에 따른 품질 편차 감소**  
  정보가 흩어져 발생하는 레시피 누락과 전달 오류를 방지하고, 표준화된 품질 유지에 기여

- **신속한 레시피 업데이트로 신입 교육 지원**  
  중앙 시스템에서 레시피를 실시간으로 업데이트하고, 시각·청각적 단계 안내로 초보자도 빠르게 습득 가능

- **작업 중 터치 최소화를 통한 위생 문제 해결**  
  음성 및 모션 기반 시스템으로 손을 씻지 못한 채 기기를 조작해야 하는 위생 문제를 근본적으로 차단

<br/>

<div id="3"></div>

## ✨ 주요 기능

### 서비스 설명

-   REPER는 신입 직원 교육 지연 문제를 해결하기 위해 **음성과 모션 기반의 레시피 안내 서비스**입니다.
-   주문이 들어오면 즉시 해당 레시피를 단계별로 볼 수 있고, 손을 대지 않아도 제어 가능한 **위생적·직관적 서비스**입니다.

### 주요 기능

**1. 레시피 자동 분류 및 시각화**

- 레시피 PDF를 업로드하면 **이름, 재료, 조리 방법**을 자동 추출
- OpenAI를 통해 레시피에 적절한 **대표 이미지 자동 생성**
- 추출된 단계별 조리 과정에 맞춰 **Lottie 애니메이션 자동 매칭**

**2. POS 연동 기반 실시간 레시피 안내**

- POS에서 주문이 접수되면, 앱으로 **새 주문 알림 자동 발송**
- 주문 내역에 맞는 **레시피 자동 검색**
- 직원은 직접 레시피를 찾지 않고 자동 검색된 레시피로 빠르게 조리 시작 가능

**3. 몰입형 시각적 안내 UI**

- 모든 조리 과정을 **Lottie 애니메이션**으로 시각화해 학습 효과 극대화
- **세로 모드**: 현재 단계만 표시해 집중도를 높이고 UI 최소화
- **가로 모드**: 레시피 단계 뿐만 아니라, 현재 진행 중인 주문 내역을 볼 수 있고, 다른 레시피로 건너뛰기 가능

**4. 음성 및 모션 기반 인터랙션**

- **"다음", "이전" 등의 음성 명령어**로 레시피 단계 제어
- **시끄러운 환경에서도 동작 가능한 모션 인식 제스처**로 터치 없이 조작

<br/>


<div id="4"></div>
// 여기까지 작성됨. 나머지는 내일로...

## ✨ 주요 기능별 화면

[↗️전체 서비스 화면 보러가기](위키링크)

### 1. 레시피 자동 분류 및 시각화

- 레시피 PDF 업로드 시 자동으로 
- 라벨링 정확도(Accuracy)가 90% 이상일 경우에만 다음 단계로 진행 
- 툴팁과 가이드라인 제공

<img src="/docs/gif/라벨링툴팁.gif" width="800">

### 2. POS 연동 기반 실시간 레시피 안내

- 튜토리얼 / 실습 모드에서 블록 조립으로 모델 학습 파이프라인 구성
- 실시간으로 Python 코드 변환, 전체 코드 복사 가능
- 각 블록에 대한 사용 가이드 및 툴팁 제공

<img src="/docs/gif/블록 기능.gif" width="800">

### 3. 몰입형 시각적 안내 UI

- 블록 조립 중 오류 발생 시, 원인과 해결 방법을 시각적으로 안내
- 실행 결과 및 오류 상황을 토대로 실시간 피드백 제공

<img src="/docs/gif/튜토리얼 복사&오류에러.gif" width="800">

### 4. 음성 및 모션 기반 인터랙션

- 학습 결과를 바탕으로 OpenAI 기반 성능 분석 및 개선 피드백 제공
- 필요 시 Notion으로 Export (관리자 코드 입력 필요)

<img src="/docs/gif/노션 export.gif" width="800">
<br/>
<div id="5"></div>

## ✨ 기술적 특징

### 1. JS <-> C# <-> Python 데이터 흐름 자동화

-   사용자가 블럭을 조립하면 학습 및 추론이 가능한 직관적인 구조

    <img src="/docs/자율프로젝트_최종발표.jpg" width="800">

### 2. 간단한 파라미터 조정으로 모델 구조와 성능을 체감할 수 있는 데이터셋 제공

-   튜토리얼 모드는 `바나나` 데이터셋, 실습 모드는 `가위바위보` 데이터셋을 제공

    <img src="/docs/자율프로젝트_기술설명.jpg" width="800">

### 3. 서비스 내에서 로컬 GPU를 사용한 학습 및 추론 가능

- 로컬 GPU를 사용한 학습 및 추론이 가능
- 로컬 GPU 뿐만 아니라 서버 GPU도 선택 가능

### 4. 가상환경 기반으로 별도의 환경 설정 없이 즉시 실행 가능

- 프로젝트 내 Python 가상환경에 모든 라이브러리 및 버전이 사전 설정되어 있음
- 추가 설치 과정 없이 코드 실행 및 모델 학습 가능

<br/>

## 📚 기술 스택

### Backend

<div align=left> 
  <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white">
  <img src="https://img.shields.io/badge/postgresql-4169E1?style=for-the-badge&logo=postgresql&logoColor=white"> 
  <img src="https://img.shields.io/badge/swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=white">
  <img src="https://img.shields.io/badge/postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white">
  <img src="https://img.shields.io/badge/intellijidea-000000?style=for-the-badge&logo=intellijidea&logoColor=white">
  <img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
  <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
  <img src="https://img.shields.io/badge/springsecurity-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white">
  <img src="https://img.shields.io/badge/openjdk-000000?style=for-the-badge&logo=openjdk&logoColor=white">
  <img src="https://img.shields.io/badge/fastapi-009688?style=for-the-badge&logo=python&logoColor=white">


</div>

### Frontend

<div align=left> 
  <img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">
  <img src="https://img.shields.io/badge/openjdk-000000?style=for-the-badge&logo=openjdk&logoColor=white">
  <img src="https://img.shields.io/badge/lottiefiles-00DDB3?style=for-the-badge&logo=lottiefiles&logoColor=white">
  <img src="https://img.shields.io/badge/react-61DAFB?style=for-the-badge&logo=react&logoColor=white">
</div>

### Desktop Application

<div align=left> 
  <img src="https://img.shields.io/badge/Csharp-00599C?style=for-the-badge&logo=&logoColor=white">
  <img src="https://img.shields.io/badge/VisualStudio-663399?style=for-the-badge&logo=&logoColor=white">
  <img src="https://img.shields.io/badge/winforms-00DDB3?style=for-the-badge&logo=&logoColor=white">
  <img src="https://img.shields.io/badge/blockly-61DAFB?style=for-the-badge&logo=&logoColor=white">
  <img src="https://img.shields.io/badge/Guna-663399?style=for-the-badge&logo=&logoColor=white">
  <img src="https://img.shields.io/badge/javascript-F7DF1E?style=for-the-badge&logo=&logoColor=white">


</div>

### AI

<div align=left> 
  <img src="https://img.shields.io/badge/python-3776AB?style=for-the-badge&logo=python&logoColor=white">
  <img src="https://img.shields.io/badge/yolo-111F68?style=for-the-badge&logo=yolo&logoColor=white">
  <img src="https://img.shields.io/badge/opencv-5C3EE8?style=for-the-badge&logo=opencv&logoColor=white">
  <img src="https://img.shields.io/badge/pytorch-EE4C2C?style=for-the-badge&logo=pytorch&logoColor=white">
  <img src="https://img.shields.io/badge/jupyter-F37626?style=for-the-badge&logo=jupyter&logoColor=white">
  <img src="https://img.shields.io/badge/googlecolab-F9AB00?style=for-the-badge&logo=googlecolab&logoColor=white">

</div>

### Infra

<div align=left> 
  <img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white">
  <img src="https://img.shields.io/badge/jenkins-D24939?style=for-the-badge&logo=jenkins&logoColor=white">
  <img src="https://img.shields.io/badge/nginx-009639?style=for-the-badge&logo=nginx&logoColor=white">
  <img src="https://img.shields.io/badge/letsencrypt-003A70?style=for-the-badge&logo=letsencrypt&logoColor=white">
  <img src="https://img.shields.io/badge/amazonec2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white">
  <img src="https://img.shields.io/badge/amazons3-569A31?style=for-the-badge&logo=amazons3&logoColor=white">

</div>

### Project Management & DevOps

<div align=left> 
  <img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white">
  <img src="https://img.shields.io/badge/gitlab-FC6D26?style=for-the-badge&logo=gitlab&logoColor=white">
  <img src="https://img.shields.io/badge/mattermost-0058CC?style=for-the-badge&logo=mattermost&logoColor=white">
  <img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=white">
  <img src="https://img.shields.io/badge/jira-0052CC?style=for-the-badge&logo=jira&logoColor=white">
  <img src="https://img.shields.io/badge/discord-5865F2?style=for-the-badge&logo=discord&logoColor=white">
  <img src="https://img.shields.io/badge/googledrive-4285F4?style=for-the-badge&logo=googledrive&logoColor=white">
 
</div>

<br/>

<div id="6"></div>

## ✨ 프로젝트 진행 및 산출물

- [API 명세서](https://github.com/breadbirds/SAI/wiki/%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EC%82%B0%EC%B6%9C%EB%AC%BC#1-api-%EB%AA%85%EC%84%B8%EC%84%9C)
- [ERD](https://github.com/breadbirds/SAI/wiki/%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EC%82%B0%EC%B6%9C%EB%AC%BC#2-erd)
- [시스템 아키텍쳐](https://github.com/breadbirds/SAI/wiki/%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EC%82%B0%EC%B6%9C%EB%AC%BC#3-%EC%8B%9C%EC%8A%A4%ED%85%9C-%EC%95%84%ED%82%A4%ED%85%8D%EC%B3%90)
- [화면 설계서](https://github.com/breadbirds/SAI/wiki/%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EC%82%B0%EC%B6%9C%EB%AC%BC#4-%ED%99%94%EB%A9%B4-%EC%84%A4%EA%B3%84%EC%84%9C)
<!-- - [GIT]()-->

<br/>

<div id="7"></div>

## 👥 Team Members

<table>
  <tr>
    <td align="center" width="300">
      <a href="https://github.com/breadbirds">
        <img src="https://github.com/breadbirds.png" width="300" style="border-radius: 50%;">
        <br>
        <strong>정유진</strong>
      </a>
    </td>
    <td align="center" width="300">
      <a href="https://github.com/DonghyeonKwon">
        <img src="https://github.com/DonghyeonKwon.png" width="300" style="border-radius: 50%;">
        <br>
        <strong>권동현</strong>
      </a>
    </td>
    <td align="center" width="300">
      <a href="https://github.com/JeongEon8">
        <img src="https://github.com/JeongEon8.png" width="300" style="border-radius: 50%;">
        <br>
        <strong>김정언</strong>
      </a>
    </td>
  </tr>
  <tr>
    <td align="center">
      <sub>AI 학습 블럭 설계<br>학습, 추론 로직 구현<br>로컬 GPU 사용 및 가상환경 세팅</sub>
    </td>
    <td align="center">
      <sub>백엔드 서버 개발<br>AI 모델 성능 보고서<br>노션 export</sub>
    </td>
    <td align="center">
      <sub>블록 UI/UX 구현<br>실시간 Python코드 변환<br>MVP패턴 설계</sub>
    </td>
  </tr>
  <tr>
    <td align="center" width="300">
      <a href="https://github.com/DoSeungGuk">
        <img src="https://github.com/DoSeungGuk.png" width="300" style="border-radius: 50%;">
        <br>
        <strong>도승국</strong>
      </a>
    </td>
    <td align="center" width="300">
      <a href="https://github.com/estel2005">
        <img src="https://github.com/estel2005.png" width="300" style="border-radius: 50%;">
        <br>
        <strong>박재영</strong>
      </a>
    </td>
    <td align="center" width="300">
      <a href="https://github.com/joehyejeong">
        <img src="https://github.com/joehyejeong.png" width="300" style="border-radius: 50%;">
        <br>
        <strong>조혜정</strong>
      </a>
    </td>
  </tr>
  <tr>
    <td align="center">
      <sub>라벨링 기능 및 UI/UX 구현<br>GPU 서버 연동, api 설계</sub>
    </td>
    <td align="center">
      <sub>WinForms UI 구현<br>재사용 가능한 컴포넌트 개발<br>UI/UX 디자인</sub>
    </td>
    <td align="center">
      <sub>메모장, 코드 하이라이팅 구현<br>랜딩페이지 UI 디자인</sub>
    </td>
  </tr>
</table>

<img src="/docs/역할분담.jpg">


<div id="8"></div>

## 📒 메뉴얼 및 상세 문서

-   [포팅메뉴얼](https://github.com/breadbirds/SAI/wiki/%ED%8F%AC%ED%8C%85-%EB%A9%94%EB%89%B4%EC%96%BC)
-   [전체 서비스 화면](https://github.com/breadbirds/SAI/wiki/%EC%84%9C%EB%B9%84%EC%8A%A4-%ED%99%94%EB%A9%B4)
- [프로젝트 산출물](https://github.com/breadbirds/SAI/wiki/%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EC%82%B0%EC%B6%9C%EB%AC%BC)

# 🏪 음성인식 기반 카페 레시피 도우미

## 👥 팀 구성
| 역할 | 이름 |
|------|------|
| 팀장 | 심근원 |
| 팀원 | 김정언, 박재영, 안주현, 이서현, 임지혜 |

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
