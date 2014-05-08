(ns ooof.command.core-test
  (:require
    [ooof.ui :as ui]
    [ooof.config :refer :all]
    [ooof.command.core  :refer :all]
    [midje.sweet        :refer :all]
    [conjure.core       :refer :all]
    [seesaw.core        :as seesaw]
    [seesaw.table       :as table]
    [clojure.java.io    :as io]
    [clojure.java.shell :as shell]
    ))

(def HERE (.getAbsolutePath (io/file ".")))

(defn- test-table
  ([] (test-table HERE))
  ([dir]
   (let [t ui/left-table]
     (cd t dir)
     t)))

(binding [*config-path* "test/file/config.edn"]

  (fact "refresh should work fine."
    (let [t (test-table)
          n (table/row-count t)]
      (table/remove-at! t 0)
      (refresh t)
      (table/row-count t) => n))

  (fact "cd should work fine."
    (let [t (test-table HERE)]
      (zero? (table/row-count t))    => false
      ;(ui/cwd t)                     => HERE
      (:dir? (table/value-at t 0))   => true
      (instance? java.io.File (:file (table/value-at t 0)))   => true
      (nil? (seesaw/selection t))    => false))


  (facts "enter should work fine."
    (let [t      (test-table)
          before (table/value-at t 0)]
      (fact "directory"
        (seesaw/selection! t 0) ; select ".."
        (enter t)
        (not= before (table/value-at t 0)) => true)
      (fact "file"
        ; select file row
        (->> (range (table/row-count t))
             (filter #(not (:dir? (table/value-at t %))))
             first
             (seesaw/selection! t))
        (stubbing [shell/sh "execute"]
          (enter t) => "execute"))))

  (fact "cursor-up should work fine."
    (let [t (test-table)]
      (seesaw/selection! t 1)
      (cursor-up t)
      (seesaw/selection t) => 0
      (cursor-up t)
      (seesaw/selection t) => 0))

  (fact "cursor-down should work fine."
    (let [t (test-table)
          n (dec (table/row-count t))]
      (seesaw/selection! t (dec n))
      (cursor-down t)
      (seesaw/selection t) => n
      (cursor-down t)
      (seesaw/selection t) => n))

  (fact "toggle-selection should work fine."
    (let [t (test-table)]
      (:selected? (table/value-at t 0)) => false

      (toggle-selection t)
      (:selected? (table/value-at t 0)) => true
      (seesaw/selection t)              => 1

      (seesaw/selection! t 0)
      (toggle-selection t)
      (:selected? (table/value-at t 0)) => false))
  )
