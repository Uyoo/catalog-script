FROM jenkins/jenkins:lts

USER root 

COPY install-script.sh /install-script.sh
RUN  chmod +x /install-script.sh

# window에서 작성된 script의 호환 이슈 해결 (문제 없다면 주석처리)
RUN sed -i -e 's/\r$//' ./*.sh
RUN  /install-script.sh