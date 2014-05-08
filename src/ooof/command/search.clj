(ns ooof.command.search
  (:require
    [ooof.ui.search :as sui]
    [seesaw.core    :refer :all]
    [seesaw.table   :as table]
    [clojure.string :as str]))

(def target-table (atom nil))

(defn show-search-dialog
  [src]
  (reset! target-table src)
  (show! sui/search-dialog))

(defn hide-search-dialog
  [_]
  (reset! target-table nil)
  (hide! sui/search-dialog))

(defn- search-row-in-range
  [table search-keyword row-range]
  (reduce
    #(if (not= -1 (.indexOf (:name (table/value-at table %2)) search-keyword))
       (reduced %2))
    nil row-range))

(defn- search-row
  [table ranges]
  (let [s (value sui/search-text)
        i (reduce
            #(if-let [i (search-row-in-range table s %2)] (reduced i))
            nil ranges)]
    (if i (do (selection! table i)
              (scroll! table :to [:row i]))
          (alert "not found"))))

(defn search-next
  [src]
  (let [t  (or @target-table src)
        cr (selection t)]
    (when @target-table (hide-search-dialog nil))
    (search-row t [; search below
                   (range (inc cr) (table/row-count t))
                   ; search above
                   (range 0 (inc cr))])))

(defn search-prev
  [src]
  (let [t  (or @target-table src)
        cr (selection t)]
    (search-row t [; search above
                   (reverse (range 0 cr))
                   ; search below
                   (reverse (range cr (table/row-count t)))
                   ])))
