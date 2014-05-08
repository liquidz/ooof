(ns ooof.ui.search
  (:require
    [seesaw.core :refer :all]))

(def search-text (text))

(def search-dialog
  (custom-dialog
    :title   "SEARCH"
    :height  50
    :width   200
    :content search-text))
