FROM maven:3.9.6-amazoncorretto-17-al2023 AS MAVEN_BUILD
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=MAVEN_BUILD /app/target/*.jar demo.jar
EXPOSE 8081
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar demo.jar" ]

#docker build . -t ngquangdat/demo
#docker run -p 8081:8081 ngquangdat/demo