(ns ooof.ui.sendto
  (:require
    [seesaw.core :refer :all]))

(def sendto-table
  (table :model [:columns [:name]]))

(def sendto-dialog
  (custom-dialog
    :title "SEND TO"
    :height 300
    :width 300
    :content (scrollable sendto-table)))
