# Client medical card

This is demo CRUD app in Clojure.Implemented the basic functionality of CRUD operations for a medical card of the client.

## Run dev mode

Start in container: ```docker compose up```

Connect your relp client to [http://localhost:7888](http://localhost:7888)

Start dev-server in repl: ```(-dev-main)```

Stop dev-server in repl: ```(stop-server)```

OR

Start local: ```lein with-profile dev run```

## Build container to production

```docker build -t clojure/client-card . ```

## Run production-container

```docker run --rm -p 8080:3000 clojure/client-card```

Open [http://localhost:8080](http://localhost:8080)


## Compile application

```lein uberjar```

## Run compiled application

```java -cp target/client-card.jar clojure.main -m client-card.core```

## Add githooks

```lein githooks install```