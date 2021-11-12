## KB One Cloud Catalog Reverse Nginx 이미지
---

## Description
Code-Server로 접속하기 위한 Reverse Proxy 이미지

## Version
* ### 0.1.0
  * Dockerfile
    * conf.d 파일의 변화를 감지하는 기능 (inotify-tools)

  * Configuration
    ```
    ## workflow    
    1. code-server에 / 로 접근하면, auth_request 를 통해서 API 서버로 인증 요청
    2. 최초에는 code-server token만 존재하고 접속자 token이 없음 =>  401 발생
      2-1. 401 발생시 front page 로 이동 -> 접속자의 token 을 cookie로 획득 -> code-server 리다이렉트 시키며 get parameter로 token 을 전송
    3. 다시 auth_request 를 통해서 접속자 token과 code-server 소유자 token을 전송 -> 서버에서 복호화 후 비교처리 및 인증 완료

    ## fallback 
    1. auth_request 가 연결이 안되거나 500 시리즈를 리턴하면, fallback 으로 전송하여 인증 없이 진입 시킨다.
    2. 이때 웹 소켓 통신은 200이 아닌 101로 재처리 한다.
    ```

* ### 0.1.1
  * Dockerfile
    * ..

  * Configuration
    * ..