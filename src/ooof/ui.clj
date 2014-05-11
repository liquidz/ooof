(ns ooof.ui
  (:require
    [ooof.config :as config]
    [seesaw.core :as seesaw]
    [seesaw.font :refer [font]])
  (:import
    [java.awt Dimension]))

(defn set-font!
  [elem]
  (.setFont elem (font :name (-> (config/get-config) :font :name)
                       :size (-> (config/get-config) :font :size))))

(defn set-no-grid!
  [table]
  (doto table
    (.setShowGrid false)
    (.setIntercellSpacing (Dimension. 0 0))))

(defn text
  []
  (doto (seesaw/text)
    (set-font!)
    (.setBackground (config/get-background :even))
    (.setForeground (config/get-foreground :file))
    (.setCaretColor (config/get-foreground :file))))
