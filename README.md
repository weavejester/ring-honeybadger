# Ring-Honeybadger

Ring middleware designed to log exceptions to [Honeybadger][1].

[1]: https://www.honeybadger.io

## Installation

To install, add the following to your project dependencies:

    [ring-honeybadger "0.1.0"]

## Usage

Add the middleware to your handler with a Honeybadger API key and
environment:

```clojure
(use 'ring.middleware.honeybadger)

(def app
  (wrap-honeybadger handler {:api-key "XXXXXXX" :env "development"}))
```

Any exception raised by your handler will be logged and then re-thrown.

## License

Copyright © 2013 James Reeves

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
