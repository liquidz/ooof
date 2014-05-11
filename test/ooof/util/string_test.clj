(ns ooof.util.string-test
  (:require
    [ooof.util.string :refer :all]
    [midje.sweet :refer :all]))

(fact "parse-execute-arg"
  (parse-execute-arg "a") => ["a"]
  (parse-execute-arg "a b") => ["a" "b"]
  (parse-execute-arg "a \"b c\"") => ["a" "b c"]
  (parse-execute-arg "/a/b/c") => ["/a/b/c"]
  )

