(ns ooof.command.sendto
  (:require
    [ooof.ui.sendto :as stui]
    [ooof.command.core :refer [sendto]]
    [ooof.config :as config]
    [seesaw.core :refer :all]
    [seesaw.table :as table]
    ;[cuma.core  :refer [render]]
    [clojure.string :as str]))

(def target-table (atom nil))

(defn show-sendto-dialog
  [src]
  (reset! target-table src)

  (table/clear! stui/sendto-table)
  (doseq [k (-> (config/get-config) :sendto keys)]
    (table/insert-at! stui/sendto-table 0 {:name k}))

  (selection! stui/sendto-table 0)
  (show! stui/sendto-dialog))

(defn hide-sendto-dialog
  [_]
  (reset! target-table nil)
  (hide! stui/sendto-dialog))

(defn sendto-by-selected
  [_]
  (let [name (:name (table/value-at stui/sendto-table (selection stui/sendto-table)))]
    (sendto @target-table name))
  (hide-sendto-dialog nil))
