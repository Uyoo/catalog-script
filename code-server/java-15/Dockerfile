FROM catalog-codeserver-common:0.1.0

# ===== Java =====
# Java 버전 다운로드 & 압축해제
RUN wget https://download.java.net/java/GA/jdk15/779bf45e88a44cbd9ea6621d33e33db1/36/GPL/openjdk-15_linux-x64_bin.tar.gz
RUN tar -xvf openjdk-15_linux-x64_bin.tar.gz

# jvm 폴더 생성 & 해당 경로로 jdk폴더 복사 & 소유자 변경(Coder)
USER root
RUN mkdir /usr/lib/jvm && cp -r jdk-15/ /usr/lib/jvm/java-15-openjdk && chown -R coder:coder /usr/lib/jvm/

# Java 환경변수 설정
ENV JAVA_HOME=/usr/lib/jvm/java-15-openjdk
ENV PATH=$JAVA_HOME/bin:$PATH


# ===== Gralde =====
# unzip 설치 & Gradle 버전 다운로드 & 압축해제
RUN apt install -y unzip
RUN wget https://services.gradle.org/distributions/gradle-7.1.1-bin.zip -P /tmp
RUN unzip -d /opt/gradle /tmp/gradle-*.zip

# Gradle 환경변수 설정
ENV GRADLE_HOME=/opt/gradle/gradle-7.1.1
ENV PATH=$GRADLE_HOME/bin:$PATH

# 불필요한 폴더 삭제
RUN rm -r jdk-15 && rm -r openjdk-15_linux-x64_bin.tar.gz && rm -r /tmp/gradle-*.zip


# User 계정으로 전환
USER coder