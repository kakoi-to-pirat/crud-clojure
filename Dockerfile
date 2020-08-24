FROM clojure

COPY . /usr/src/app
WORKDIR /usr/src/app

RUN lein uberjar

EXPOSE 3000

CMD java -cp target/medical-card.jar clojure.main -m medical-card.core