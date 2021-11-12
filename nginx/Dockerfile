FROM nginx:latest

WORKDIR /etc/nginx

# nginxReloader: /etc/nginx/conf.d 파일의 변화를 감지하는 script
COPY nginx.conf /etc/nginx/nginx.conf
COPY code-server.conf /etc/nginx/conf.d/code-server.conf
COPY nginxReloader.sh /etc/nginx/nginxReloader.sh

# inotify-tools: nginxReloader.sh 동작에 사용
RUN chmod +x ./nginxReloader.sh
RUN apt-get update && apt install -y inotify-tools

# window에서 작성된 script의 호환 이슈 해결 (문제 없다면 주석처리)
RUN sed -i -e 's/\r$//' ./*.sh

EXPOSE 80

# ENTRYPOINT [ "/bin/bash", "-c", "sh ./nginxReloader.sh" ]