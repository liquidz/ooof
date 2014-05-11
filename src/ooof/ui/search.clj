(ns ooof.ui.search
  (:require
    [ooof.ui.main :as mui]
    [ooof.config  :as config]
    [seesaw.core  :refer :all]))

(def search-text
  (doto (text)
    (mui/set-font!)
    (.setBackground (config/get-background :even))
    (.setForeground (config/get-foreground :file))
    (.setCaretColor (config/get-foreground :file))))

(def search-dialog
  (custom-dialog
    :parent  mui/main-frame
    :title   "SEARCH"
    :height  50
    :width   200
    :content search-text))
