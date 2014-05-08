(ns ooof.util.file-test
  (:require
    [ooof.config :refer :all]
    [ooof.util.file :refer :all]
    [midje.sweet :refer :all]
    [clj-time.core :as t]
    [clj-time.coerce :as c]))

(facts "size->str should work fine."
  (fact "integer"
    (size->str 0) => "0 B"
    (size->str 198) => "198 B"
    (size->str 19876) => "19.8 KB"
    (size->str 1987654) => "1.9 MB"
    (size->str 198765432) => "198.7 MB"
    (size->str 19876543219) => "19.8 GB"
    (size->str 1987654321987) => "1.9 TB")
  (fact "not integer"
    (size->str nil) => nil
    (size->str "foo") => "foo"))

(facts "last-modified->str should work fine."
  (binding [*config-path* "test/file/config.edn"]
    (fact "long"
      (let [n (c/to-long (t/date-time 1984 12 9 11 22 33))]
        (last-modified->str n) => "1984/12/09 11:22"))

    (fact "not long"
      (last-modified->str nil) => nil
      (last-modified->str "foo") => "foo")))
