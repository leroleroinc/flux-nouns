FROM gradle:8.1.1-jdk17-alpine

WORKDIR /app

EXPOSE 8083

COPY . .

CMD ["gradle","bootRun"]
