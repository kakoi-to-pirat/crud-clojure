# Client medical card

This is demo CRUD app in Clojure.Implemented the basic functionality of CRUD operations for a medical card of the client.

## Run dev mode

```lein with-profile dev run```

## Build container

```docker build -t clojure/client-card . ```

```docker run --rm -p 8080:3000 clojure/client-card```

Open [http://localhost:8080/](http://localhost:8080/)