(ns ooof.ui.rename
  (:require
    [ooof.ui.main :as mui]
    [ooof.config  :as config]
    [seesaw.core :refer :all]))

(def WIDTH 400)

(def before-name
  (doto (label :size [WIDTH :by 15])
    (mui/set-font!)))

(def new-name-text
  (doto (text)
    (mui/set-font!)
    (.setBackground (config/get-background :even))
    (.setForeground (config/get-foreground :file))
    (.setCaretColor (config/get-foreground :file))))

(def rename-dialog
  (custom-dialog
    :parent mui/main-frame
    :title   "RENAME"
    :height  70
    :width   WIDTH
    :content (doto
               (vertical-panel)
               (add! before-name)
               (add! new-name-text))))
