FROM openjdk:8-alpine

COPY target/uberjar/gilded-rose-clojure.jar /gilded-rose-clojure/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/gilded-rose-clojure/app.jar"]
