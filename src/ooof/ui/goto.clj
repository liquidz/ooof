(ns ooof.ui.goto
  (:require
    [ooof.ui.main :as mui]
    [ooof.config  :as config]
    [seesaw.core  :refer :all]))

(def goto-text
  (doto (text)
    (mui/set-font!)
    (.setBackground (config/get-background :even))
    (.setForeground (config/get-foreground :file))
    (.setCaretColor (config/get-foreground :file))))

(def goto-dialog
  (custom-dialog
    :parent  mui/main-frame
    :title   "GOTO"
    :height  50
    :width   200
    :content goto-text))
