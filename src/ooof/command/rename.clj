(ns ooof.command.rename
  (:require
    [ooof.ui.main      :as mui]
    [ooof.ui.rename    :as rui]
    [ooof.command.core :refer [refresh]]
    [ooof.util.path    :as path]
    [seesaw.core       :refer :all]
    [seesaw.table      :as table]
    [clojure.java.io   :as io]))

(def target-table (atom nil))

(defn show-rename-dialog
  [src]
  (reset! target-table src)
  (let [current-name (:name (table/value-at src (selection src)))
        dot-pos      (.indexOf current-name ".")]
    (text! rui/before-name current-name)
    (text! rui/new-name-text current-name)

    ; select only filename
    (selection! rui/new-name-text
                [0 (if (<= dot-pos 0) (count current-name) dot-pos)]))

  (show! rui/rename-dialog))

(defn hide-rename-dialog
  [_]
  (reset! target-table nil)
  (hide! rui/rename-dialog))

(defn rename-file
  [new-name-text]
  (let [dir      (mui/cwd @target-table)
        new-file (io/file (path/join dir (text new-name-text)))
        old-file (io/file (path/join dir (text rui/before-name)))]
    (if (.exists new-file)
      (alert "already exists")
      (do (.renameTo old-file new-file)
          (refresh @target-table)
          (hide-rename-dialog nil)))))
