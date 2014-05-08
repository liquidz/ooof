(ns ooof.ui.main
  (:require
    [ooof.config     :as config]
    [seesaw.core     :refer :all]
    [seesaw.color    :refer [color]]
    [seesaw.table    :refer [table-model value-at]]
    [seesaw.font :refer [font]]
    [clojure.java.io :as io])
  (:import
    [javax.swing JTable]
    [java.awt Dimension]))

(def TITLE "ooof")

(defn file-table
  [& {:keys [id] :or {id (gensym)}}]
  (let [model (table-model :columns [:name :size :updated])]
    (proxy [JTable] [model]
      (prepareRenderer [tcr row column]
        (let [c (proxy-super prepareRenderer tcr row column)
              cursor-row (selection this)
              row-data   (value-at this row)
              bg         (-> (config/get-config) :color :background)
              fg         (-> (config/get-config) :color :foreground)]

          (.setForeground
            c (color (get fg (cond
                               (:selected? row-data) :selected
                               (:dir? row-data)      :directory
                               :else                 :file))))
          (.setBackground
            c (color (get bg (cond
                               (= row cursor-row)    :cursor
                               (= 0 (mod row 2))     :even
                               :else                 :odd))))
          c)))))

(def left-label  (doto (label :text "" :size [10000 :by 15] :h-text-position :left)
                   (.setFont (font :name (-> (config/get-config) :font :name)
                                   :size (-> (config/get-config) :font :size)))
                   (.setBackground (-> (config/get-config) :color :background :even color))
                   (.setForeground (-> (config/get-config) :color :foreground :file color))))
(def right-label  (doto (label :text "" :size [10000 :by 15] :h-text-position :left)
                    (.setFont (font :name (-> (config/get-config) :font :name)
                                    :size (-> (config/get-config) :font :size)))
                    (.setBackground (-> (config/get-config) :color :background :even color))
                    (.setForeground (-> (config/get-config) :color :foreground :file color))))
(def left-table  (doto (file-table)
                   (.setBackground (-> (config/get-config) :color :background :even color))
                   (.setFont (font :name (-> (config/get-config) :font :name)
                                   :size (-> (config/get-config) :font :size)))
                   ; hide grid
                   (.setShowGrid false)
                   (.setIntercellSpacing (Dimension. 0 0))))
(def right-table (doto (file-table)
                   (.setBackground (-> (config/get-config) :color :background :even color))
                   (.setFont (font :name (-> (config/get-config) :font :name)
                                   :size (-> (config/get-config) :font :size)))
                   ; hide grid
                   (.setShowGrid false)
                   (.setIntercellSpacing (Dimension. 0 0))))

(def table-dir (atom {left-table "", right-table ""}))

(defn cwd
  [src]
  (get @table-dir src))

(defn cwd!
  [src dir]
  (swap! table-dir assoc src dir))

(defn construct-pane
  [& {:keys [label table]}]
  (let [st (scrollable table)]
    ; fill background color with JTable's background
    (.setBackground (.getViewport st) (.getBackground table))
    (doto (vertical-panel)
      (add! label)
      (add! st))))

(defn construct-content
  []
  (.setOpaque left-label true)
  (.setOpaque right-label true)

  (doto (grid-panel :columns 2 :hgap 5)
    (add! (construct-pane :label left-label :table left-table))
    (add! (construct-pane :label right-label :table right-table))))

(defn show-frame
  []
  (invoke-later
    (-> (frame :title    TITLE
               :icon     (icon (io/resource "icon.png"))
               :content  (construct-content)
               :on-close :exit)
        pack!
        show!)))

