(ns ooof.config-test
  (:require
    [ooof.config :refer :all]
    [midje.sweet :refer :all]))

(fact "get-config should work fine."
  (binding [*config-path* "test/file/config.edn"]
    (:foo (get-config)) => "bar"))

(fact "get-key-binds should work fine."
  (binding [*config-path* "test/file/config.edn"]
    (get (get-key-binds) {:char "enter" :modifier #{}}) => 'enter))
