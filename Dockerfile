FROM maven:3.9.6-amazoncorretto-17-al2023 AS MAVEN_BUILD
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:22-ea-17-oraclelinux8
WORKDIR /app
RUN mkdir -p /opt/ngquangdat/demo/config
COPY --from=MAVEN_BUILD /app/target/*.jar demo.jar
EXPOSE 8081
CMD ["java", "-Dspring.config.additional-location=/opt/ngquangdat/demo/config/", "-jar", "demo.jar"]

#docker build . -t ngquangdat/demo --build-arg="JAVA_OPTS=-Dspring.config.additional-location=/opt/ngquangdat/demo/config/"
#docker run -p 8081:8081 ngquangdat/demo