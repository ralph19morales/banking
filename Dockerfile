# STEP 2 : BUILD THE PROJECT USING OPENJDK

FROM openjdk:8-jdk

ENV APP_HOME=/usr/app/

WORKDIR $APP_HOME

COPY build.gradle settings.gradle gradlew /usr/app/
COPY gradle $APP_HOME/gradle

RUN chmod +x ./gradlew && ./gradlew build -x test || return 0
COPY src src
RUN ./gradlew build -x test

# STEP 2 : DEPLOY JAR USING ONLY OPENJDK JRE ALPINE FOR SMALLER DOCKER IMAGE SIZE

FROM openjdk:8-jre-alpine

LABEL maintainer="ralph19.morales@gmail.com"

COPY --from=0 /usr/app/build/libs/banking.jar banking.jar

EXPOSE 9090

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/banking.jar"]