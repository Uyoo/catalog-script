FROM codercom/code-server:latest
COPY config.yaml /home/coder/.config/code-server/config.yaml

# PV 생성으로 인해 초기화된 원본 경로(/home/coder)에 code-server 환경설정 복사본(/data/coder)을 복사해주는 script 파일 생성
USER root
RUN mkdir /code-server-scripts
RUN chmod 777 -R /code-server-scripts
COPY copy-codeserver-config.sh /code-server-scripts/copy-codeserver-config.sh
RUN chmod +x /code-server-scripts/copy-codeserver-config.sh

# User 계정으로 전환
USER coder

ENV TZ=Asia/Seoul
ENV PORT=8443

EXPOSE 8443
