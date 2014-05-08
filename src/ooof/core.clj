(ns ooof.core
  (:require
    [ooof.ui.main      :as mui]
    [ooof.ui.search    :as sui]
    [ooof.ui.rename    :as rui]
    [ooof.ui.sendto    :as stui]
    [ooof.event        :as event]
    [ooof.command.core :refer [cd go-home]]
    [seesaw.core       :refer [request-focus!]])
  (:gen-class))

(defn init
  []
  (mui/show-frame)

  ;; bind events
  (doseq [tbl [mui/left-table mui/right-table]]
    (event/listen-key-event! tbl :section :main)
    (event/listen-focus-event! tbl))
  (event/listen-key-event! sui/search-text :section :search)
  (event/listen-key-event! rui/new-name-text :section :rename)
  (event/listen-key-event! stui/sendto-table :section :sendto)

  ;(let [home (System/getenv "HOME")]
  ;  (cd mui/left-table home)
  ;  (cd mui/right-table home))
  (go-home mui/left-table)
  (go-home mui/right-table)

  (request-focus! mui/left-table))

(defn -main
  [& args]
  (init))
