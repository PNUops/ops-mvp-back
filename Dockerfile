# 빌드 스테이지
FROM eclipse-temurin:17-jdk-jammy AS build

# 작업 디렉토리 설정
WORKDIR /workspace/app

# Gradle 관련 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# Gradle Wrapper에 실행 권한 부여
RUN chmod +x ./gradlew

RUN mkdir -p /workspace/app/build/generated-snippets

# 의존성 다운로드 및 애플리케이션 빌드
RUN ./gradlew build -x test

# 실행 스테이지
FROM eclipse-temurin:17-jre-jammy

# 작업 디렉토리 설정
WORKDIR /app

# 타임존 설정
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 빌드 스테이지에서 생성된 JAR 파일 복사
COPY --from=build /workspace/app/build/libs/*.jar app.jar

# 8080 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
