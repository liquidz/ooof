(ns ooof.config-test
  (:require
    [ooof.config :refer :all]
    [midje.sweet :refer :all]
    ))

;(binding [*config-path* "test/file/config.edn"]
;  (fact "get-config should work fine."
;    (:foo (get-config)) => "bar")
;
;  (fact "get-key-binds should work fine."
;    (get (get-key-binds) {:char "enter" :modifier #{}}) => 'enter)
;  )
