FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY src ./src
RUN mkdir -p out && javac -d out src/main/java/com/example/largest/LargestOfThree.java src/main/java/com/example/largest/CalculatorServer.java

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/out ./out
EXPOSE 8080
ENV PORT=8080
USER 10001
CMD ["java", "-cp", "out", "com.example.largest.CalculatorServer"]
