## KB One Cloud Code-Server Base 이미지
---

## Description
개발언어 종류에 상관없이 기본적으로 필요한 설정(apt / extension 등)이 진행된 Code-Server 이미지 

## Version
* ### 0.1.1
  * Configuration
    ```
      $ sudo apt-get update
      $ sudo apt install -y telnet wget curl vim
      
      // git은 공식 이미지 자체에 이미 설치가 되어있음
      // $ sudo apt-get install git
      // $ sudo apt install -y git

      // root password 설정
      $ sudo passwd

      // workspaces 폴더 생성
     ```

  * Installed Extensions
    * Prettier-Code formatter
    * TabOut
    * Korean Language Pack for visual Studio Code
    * gitLens
    * Git history
    * Git graph
    * Gitignore
    * Markdown all in one
    * Swagger viewer
    * Xml
    * Mysql
    * Code Runner
    * YAML
    * REST Client
    * Jira Plugin
    * Jira and Bitbucket (official)

    * Auto Close Tag
    * Auto Rename Tag
    * CSS Peek
    * Bracket Pair Colorsizer 2
    * Highlight Matching Tag
    * HTML CSS Support
    * Javascript (ES6) code snippets
    * Babel ES6/ES7
    * ESLint

* ### 0.1.2
  * Configuration
    ```
      // /home/coder의 환경설정 복사본 생성 (/data)
      $ sudo mkdir /data
      $ sudo cp -r /home/coder /data/
      // $ sudo touch /data/coder/check-copy

      // 사용자의 sudo 접근 제한
      // $ su -
      // $ chmod 4750 /bin/su
     ```

  * Installed Extensions
    * 0.1.0과 동일
