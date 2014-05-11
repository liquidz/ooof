(ns ooof.ui.main
  (:require
    [ooof.config     :as config]
    [seesaw.core     :refer :all]
    [seesaw.table    :refer [table-model value-at]]
    [seesaw.font     :refer [font]]
    [clojure.java.io :as io])
  (:import
    [javax.swing JTable]
    [java.awt Dimension]))

(def TITLE "ooof")

(defn set-font!
  [elem]
  (.setFont elem (font :name (-> (config/get-config) :font :name)
                       :size (-> (config/get-config) :font :size))))

(defn set-no-grid!
  [table]
  (doto table
    (.setShowGrid false)
    (.setIntercellSpacing (Dimension. 0 0))))

(defn file-table
  [& {:keys [id] :or {id (gensym)}}]
  (let [model (table-model :columns [:name :size :updated])]
    (doto (proxy [JTable] [model]
            (prepareRenderer [tcr row column]
              (let [cursor-row (selection this)
                    row-data   (value-at this row)]
                (doto (proxy-super prepareRenderer tcr row column)
                  (.setForeground
                    (config/get-foreground (cond (:selected? row-data) :selected
                                                 (:dir? row-data)      :directory
                                                 :else                 :file)))
                  (.setBackground
                    (config/get-background (cond (= row cursor-row)    :cursor
                                                 (= 0 (mod row 2))     :even
                                                 :else                 :odd)))))))
      (set-font!)
      (set-no-grid!)
      (.setBackground (config/get-background :even)))))

(defn current-dir-label
  []
  (let [font-size (-> (config/get-config) :font :size)
        margin    (-> (config/get-config) :current-dir-label :margin)]
    (doto (label :text ""
                 :size [10000 :by (+ font-size margin)]
                 :h-text-position :left)
      (set-font!)
      (.setBackground (config/get-background :even))
      (.setForeground (config/get-foreground :file)))))

(def left-label  (current-dir-label))
(def right-label (current-dir-label))
(def left-table  (file-table))
(def right-table (file-table))

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
    (add! (construct-pane :label left-label  :table left-table))
    (add! (construct-pane :label right-label :table right-table))))

(def main-frame
  (frame :title    TITLE
         :icon     (icon (io/resource "icon.png"))
         :content  (construct-content)
         :width 700
         :on-close :exit))

(defn show-frame
  []
  (invoke-later
    (-> main-frame
        pack!
        show!)))

