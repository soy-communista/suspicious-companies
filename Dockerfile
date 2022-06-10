FROM  registry.yt.uz:5443/ubuntu-jdk

RUN mkdir -p /opt/app/storage

COPY /target/suspicious-company-0.0.1-SNAPSHOT.jar /opt/app/suspicious-company.jar

WORKDIR /opt/app

EXPOSE 8080

ENV LANGUAGE=zh_CN:zh:en_US:en \
    LANG=zh_CN.UTF-8 \
#    TZ=Asia/Tashkent \
    LC_NUMBERIC=zh_CN.UTF-8

CMD java -Dfile.encoding=UTF-8 -jar suspicious-company.jar --server.port=8080 --server.address=0.0.0.0
