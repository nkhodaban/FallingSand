FROM openjdk:18-jdk-slim
RUN apt-get update && apt-get install -y \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libxt6 \
    libfreetype6 \
    fontconfig
ENV DISPLAY :0.0
WORKDIR /app
COPY . /app
COPY ./Sound /app/Sound
RUN javac *.java
CMD ["java", "Main"]