# Coconut

### 모든 기능에 대한 시연은 [`발표 영상`](https://youtu.be/2taqqWY0Bdc)에서 확인해 보실 수 있습니다.
[`유저 관련 시연 영상`](https://www.youtube.com/watch?v=ngwVsvab2PI&list=PLRGgNhwOtJjx99T9o_LDysjJ6lMGxnAtE) 
[`실시간 채팅 관련 시연 영상`](https://www.youtube.com/playlist?list=PLRGgNhwOtJjyYf3EPLU7s5ufFJwA6coUI) 
[`크롤링 관련 시연 영상`](https://www.youtube.com/playlist?list=PLRGgNhwOtJjxCcFCmkz3JM7p_KKjjytCQ) 

<br/>

## 시스템 아키텍쳐 

![image](https://user-images.githubusercontent.com/44316546/159029390-202ea8bf-21f2-4a0d-8fff-276c33564105.png)

클라이언트(Front-End) : [`Android`](https://github.com/guswns3371/Coconut)  

서버(Back-End) : [`Spring Boot Application`](https://github.com/guswns3371/coconut-spring-server)
<br/>

![image](https://user-images.githubusercontent.com/44316546/159029466-644304f8-47c9-42f7-83bf-37c7bb25f24a.png)

클라이언트는 MVVM 디자인 패턴과 데이터 바인딩을 통해 ui를 구축하였고 RxJava와 Retrofit 라이브러리를 통해 비동기 처리를 하였습니다.  
koin 프레임워크로 의존성주입을 적용하였습니다.  

사용자 인증을 위해 OAuth2.0을 사용하는 AppAuth SDK를 이용하였고 StompProtocolAndroid 라이브러리로 실시간 소켓 통신을 구현하였습니다. 

마지막으로 푸쉬 알림을 위해 Firebase 클라우드 메시지를 이용했습니다.  
<br/>

![image](https://user-images.githubusercontent.com/44316546/159029553-7858a15e-6281-4090-8f93-07115b35f805.png)

서버는 스프링 프레임워크의 스프링 부트를 이용하였습니다.  

Lombok과 Spring Data Jpa ORM을 통해 객체지향적으로 데이터를 다뤘습니다. 

Jsoup 라이브러리를 통해 웹 크롤링을 구현하였습니다.  
<br/>

![image](https://user-images.githubusercontent.com/44316546/159029743-c900a5aa-216c-45f7-b74d-afed006a0a2e.png)

본 프로젝트의 주요기능은 크게 4가지로 나눌 수 있습니다.

유저인증, 유저, 실시간 채팅 그리고 크롤링 기능입니다.

<br/>

## 유저 관련 기능 [`시연 영상`](https://www.youtube.com/watch?v=ngwVsvab2PI&list=PLRGgNhwOtJjx99T9o_LDysjJ6lMGxnAtE)

![image](https://user-images.githubusercontent.com/44316546/159029883-811c0d8c-9354-4df3-bada-1ef843a73d6d.png)

먼저 유저인증 기능에는
회원가입, OAuth2.0을 통한 구글 연동 로그인
그리고 이메일 인증이 있습니다. 

회원가입 기능은
이메일 중복 확인을 완료한뒤
아이디, 비밀번호, 이름을 기입하여 회원가입을 할 수 있습니다.

최초 로그인시 이메일 인증화면으로 넘어가 인증을 해야합니다.
Login wih Google 버튼을 눌러
구글은 통한 연동 로그인을 할 수 있습니다


![image](https://user-images.githubusercontent.com/44316546/159029901-bdc9729c-7a7e-41ac-ab77-c047b0211ceb.png)

일반 회원가입을 한 유저는
회원가입에 사용된 이메일로 인증 토큰을 받게됩니다
이 토큰으로 이메일 인증을 할 수 있습니다.

![image](https://user-images.githubusercontent.com/44316546/159029916-a12282b2-2d8b-4904-b380-125b6aa03d79.png)

유저는 자신을 나타낼 프로필을 편집할 수 있습니다.

프로필 이미지, 배경 이미지, 상태 메시지, 이름 
그리고 아이디를 동시에 편집할 수 있습니다.

![image](https://user-images.githubusercontent.com/44316546/159029929-17ea7751-50a6-4206-a2bb-eef321ef9a0d.png)

또한 
유저 프로필 오른쪽 하단에 있는 초록 불빛으로
접속상태를 볼 수 있습니다.

안드로이드 컴포넌트인 service와 브로드캐스트 리시버를 활용했으며
서버의 웹소켓을 이용한 STOMP 프로토콜을 이용해 
실시간으로 유저 접속상태를 나타냈습니다.

<br/>

## 실시간 채팅 기능 [`시연 영상`](https://www.youtube.com/playlist?list=PLRGgNhwOtJjyYf3EPLU7s5ufFJwA6coUI)

![image](https://user-images.githubusercontent.com/44316546/159029942-0abcc540-3eaa-47c5-9eaa-a7feecb3aa71.png)


1. 다수의 대화상대를 초대하여 실시간 메시지를 전송하게 되면 
채팅방에 없는 유저들에게 푸쉬알림이 울립니다.

2. 유저마다 서로 다른 읽지 않은 메시지수를
채팅방 목록 오른쪽에 나타냈습니다.

3. 채팅방을 나가거나, 앱을 종료한 상태에서도
FCM 푸쉬 알림을 클릭하여 채팅방에 참가할 수 있습니다.

4. 유저는 여러 개의 이미지를 한번에 전송할 수 있습니다.
이미지의 개수에 따라 뷰 아이템의 그리드를 다르게 구현했습니다.

5. 유저는 채팅방을 나가거나, 다른 유저를 초대할 수 있습니다.
채팅방 인원이 변할 때 마다, 
채팅방속에선 공지 메시지가 보이고
채팅방 썸네일과 제목이 변경됩니다.

6. 채팅방 타이틀을 변경할 수 있습니다.
변경된 타이틀에 맞게 알림메시지속 채팅방 타이틀도 변하게 됩니다

7. 그룹채팅 뿐 아니라 1대1 채팅도 가능합니다.

8. 변경된 사용자 이름에 맞게 채팅방 타이틀도 변경됩니다

9. 채팅방 타이틀을 초기화할 수 있습니다

10. 나와의 채팅을 할 수 있습니다.

11. 채팅방속 인원수에 따라 채팅방 목록에 보이는 
썸네일이 변경됩니다.

<br/>

## 크롤링 기능 [`시연 영상`](https://www.youtube.com/playlist?list=PLRGgNhwOtJjxCcFCmkz3JM7p_KKjjytCQ)

![image](https://user-images.githubusercontent.com/44316546/159029948-4a0dd9d5-9a42-4f47-a766-85d2851378a6.png)

웹 크롤링을 통해
뉴스, 코로나 현황, 음원차트, 과기대 학사공지 및 개발자 채용공고 정보를 
유저에게 제공합니다.

뉴스 페이지에서
다음과 네이버의 실시간 인기 뉴스를 확인할 수 있고
코로나19 페이지는
대한민국 코로나 현황을 볼수 있습니다

음원차트 페이지는
멜론의 실시간 음원차트 목록을 보여줍니다

과기대 학사공지를 확인할 수 있고
프로그래머스 사이트의 개발자 채용 공고 정보를 볼수 있습니다.

<br/>

## 배포 과정

![image](https://user-images.githubusercontent.com/44316546/159030186-11c9b1b7-96a4-4a2e-93eb-9a04ea1a6974.png)

마지막으로 지속적 배포(CD)를 위해 젠킨스를 활용하였습니다. 

Github Repo에 소스 코드를 commit하면 젠킨스를 통해 프로젝트가 자동으로 빌드됩니다.  

빌드 결과 JAR 파일이 AWS EC2 서버에 업로드 되고, 무중단 자동 실행 스크립트 파일이 실행됩니다.
