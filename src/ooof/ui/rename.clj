(ns ooof.ui.rename
  (:require
    [seesaw.core :refer :all]))

(def WIDTH 400)

(def before-name (label :size [WIDTH :by 15] ))
(def new-name-text (text))

(def rename-dialog
  (custom-dialog
    :title   "RENAME"
    :height  70
    :width   WIDTH
    :content (doto
               (vertical-panel)
               (add! before-name)
               (add! new-name-text))))
