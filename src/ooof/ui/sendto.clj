(ns ooof.ui.sendto
  (:require
    [ooof.ui :as ui]
    [ooof.ui.main :as mui]
    [ooof.config  :as config]
    [seesaw.core  :refer :all]
    [seesaw.color :refer [color]])
  (:import
    [java.awt Dimension]))

(def sendto-table
  (doto (table :model [:columns [:name]])
    (ui/set-font!)
    (ui/set-no-grid!)
    (.setBackground (config/get-background :even))
    (.setForeground (config/get-foreground :file))
    (.setSelectionBackground (config/get-background :cursor))))

(def sendto-dialog
  (custom-dialog
    :parent mui/main-frame
    :title "SEND TO"
    :height 300
    :width 300
    :content (scrollable sendto-table)))
