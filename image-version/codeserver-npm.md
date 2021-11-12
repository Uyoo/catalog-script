## KB One Cloud Code-Server NPM 이미지
---

## Description
[catalog-codeserver-common](https://hub.docker.com/repository/docker/hs2795/catalog-codeserver-common) 이미지를 Base로 NPM 관련 설정(apt / extension 등)이 진행된 Code-Server 이미지 

## Version
* ### 0.1.1
  * Configuration
    ```
      NVM을 통한 설치 (https://github.com/nvm-sh/nvm)
      $ sudo apt-get install -y curl
      $ sudo apt update
      $ curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.38.0/install.sh | bash
      $ source ~/.bashrc
      $ nvm install node

      $ which node
      $ sudo cp -r ${which node 경로} /usr/local/bin/node
    ```

  * Installed Extensions
    * Vetur
    * Vue 2 Snippets
    * Vue Peek
    * DotENV
    * ES7 React/Redux/GraphQL/React-Native snippets
    * React Snippets
