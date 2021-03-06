# Client medical card

This is demo CRUD app in Clojure.Implemented the basic functionality of CRUD operations for a medical card of the client.

[DEMO](https://mysterious-fjord-76492.herokuapp.com/)

## Run dev mode

Start in container: ```docker compose up```

Connect your relp client to [http://localhost:7888](http://localhost:7888)

Start dev-server in repl: ```(-dev-main)```

Stop dev-server in repl: ```(stop-server)```

OR

Run database: ```docker-compose start postgres```

Start local: ```lein with-profile dev run```

## Build container to production

```docker build -t clojure/client-card . ```

## Run production-container

```docker run --rm -p 8080:3000 clojure/client-card```

Open [http://localhost:8080](http://localhost:8080)


## Compile application

```lein uberjar```

## Run compiled application

```java -cp target/client-card.jar clojure.main -m server.core```

## Add githooks

```lein githooks install```

## Migrations

```core.db => (migrate-up)```

```core.db => (migrate-down)```

## Running the frontend tests
To run [cljs.test](https://github.com/clojure/clojurescript/blob/master/src/main/cljs/cljs/test.cljs) tests using headless chrome install karma and its plugins:

```
npm install -g karma
npm install -g karma-cli
npm install karma karma-cljs-test karma-chrome-launcher --save-dev

lein doo chrome-headless test once
```