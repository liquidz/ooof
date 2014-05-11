(ns ooof.command.goto
  (:require
    [ooof.ui.main      :as mui]
    [ooof.ui.goto      :as gui]
    [ooof.command.core :as cc]
    [me.raynes.fs      :as fs]
    [seesaw.core       :refer :all]))

(def target-table (atom nil))

(defn show-goto-dialog
  [src]
  (reset! target-table src)
  (let [dir (mui/cwd src)]
    (text! gui/goto-text dir)
    (selection! gui/goto-text [0 (count dir)]))
  (show! gui/goto-dialog))

(defn hide-goto-dialog
  [_]
  (reset! target-table nil)
  (hide! gui/goto-dialog))

(defn goto-inserted-directory
  [src]
  (let [dir (value gui/goto-text)]
    (if-not (fs/directory? dir)
      (alert (str dir " is not directory."))
      (do (cc/cd @target-table dir)
          (hide-goto-dialog nil)))))

