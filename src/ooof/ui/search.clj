(ns ooof.ui.search
  (:require
    [ooof.ui :as ui]
    [ooof.ui.main :as mui]
    [ooof.config  :as config]
    [seesaw.core  :refer :all]))

(def search-text (ui/text))

(def search-dialog
  (custom-dialog
    :parent  mui/main-frame
    :title   "SEARCH"
    :height  50
    :width   200
    :content search-text))
