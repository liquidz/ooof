(ns ooof.command.core
  (:require
    [ooof.ui.main :as mui]
    [ooof.config :as config]
    [ooof.util.file :as file]
    [ooof.util.string :as string]
    [ooof.util.path :as path]
    [seesaw.core :refer :all]
    [seesaw.table :as table]
    [cuma.core  :refer [render]]
    [me.raynes.fs :as fs]
    [clojure.string :as str]
    [clojure.java.io :as io]))

(defn- file-list [dir]
  (seq (.listFiles (io/file dir))))

(defn- file->map
  [file]
  {:name      (.getName file)
   :size      (.length file)
   :updated   (.lastModified file)
   :dir?      (.isDirectory file)
   :file      file})

(defn- insert-file-item!
  [target position file-map & {:keys [name size updated dir? selected?]
                               :or   {name      (:name file-map)
                                      size      (:size file-map)
                                      updated   (:updated file-map)
                                      dir?      (:dir? file-map)
                                      selected? false}}]
  (table/insert-at!
    target 0
    {:name      name
     :size      (file/size->str size)
     :updated   (file/last-modified->str updated)
     :file      (:file file-map)
     :dir?      dir?
     :selected? selected?}))

(defn select-at!
  [src i]
  (selection! src i)
  (scroll! src :to [:row i]))

(defn- compare-file
  [f1 f2 & {:keys [key] :or {key :name}}]
  (let [[v1 v2] (map key [f1 f2])]
    (condp #(% %2) v1
      string? (neg? (.compareTo v1 v2))
      number? (< v1 v2)
      false)))

(defn- sort-files
  [ls]
  (let [x (group-by :dir? ls)
        dirs  (get x true)
        files (get x false)]
    (concat
      (sort #(compare-file %1 %2) dirs)
      (sort #(compare-file %1 %2) files))))

(defn refresh
  [src]
  (let [cursor (selection src)
        dir    (mui/cwd src)]
    (table/clear! src)

    (doseq [f (->> (file-list dir)
                   (map file->map)
                   sort-files
                   reverse)]
      (insert-file-item! src 0 f))
    (when-let [f (.getParentFile (io/file dir))]
      (insert-file-item! src 0 (file->map f) :name ".." :size "" :updated ""))

    (when (and cursor (>= cursor 0) (< cursor (table/row-count src)))
      (selection! src cursor))))

(defn cd
  [src dir]
  (let [l (if (= mui/left-table src) mui/left-label mui/right-label)]
    (text! l dir))
  ;; update table
  (mui/cwd! src dir)
  (refresh src)
  (select-at! src 0))

(defn- execute
  [cmd]
  (.start (ProcessBuilder. (into-array (string/parse-execute-arg cmd)))))

(defn enter
  [src & args]
  (let [cwd  (mui/cwd src)
        data (table/value-at src (selection src))
        file (:file data)]
    (if (.isDirectory file)
      (do
        (cd src (.getAbsolutePath file))
        (when (= ".." (:name data))
          ; 親ディレクトリに遷移してからカレントディレクトリを選択する
          (let [n (.getName (io/file cwd))
                i (reduce
                    (fn [_ i]
                      (if (= n (:name (table/value-at src i)))
                        (reduced i)))
                    nil
                    (range 0 (table/row-count src)))]
            (when i (select-at! src i)))))
      (let [s (render (:execute (config/get-config)) {:path (.getAbsolutePath file)})]
        (execute s)))))

(defn go-down
  "カーソル下のディレクトリへ移動する"
  [src]
  (let [data (table/value-at src (selection src))]
    (when (and (not= ".." (:name data)) (:dir? data))
      (enter src))))

(defn back-to-parent
  [src]
  (when (= ".." (:name (table/value-at src 0)))
    (selection! src 0)
    (enter src)))

(defn toggle-pane
  [src]
  (let [x (if (= mui/left-table src) mui/right-table mui/left-table)]
    (request-focus! x)))

(defn cursor-up
  ([src] (cursor-up src 1))
  ([src step]
   (let [i (- (selection src) step)
         i (if (neg? i) 0 i)]
     (select-at! src i))))

(defn cursor-down
  ([src] (cursor-down src 1))
  ([src step]
   (let [m (table/row-count src)
         i (+ (selection src) step)
         i (if (>= i m) (dec m) i)]
     (select-at! src i))))

(defn toggle-selection
  [src & [skip-cursor-down?]]
  (let [row  (selection src)
        data (table/value-at src row)]
    (table/update-at! src row (update-in data [:selected?] not))
    (when-not skip-cursor-down?
      (cursor-down src))))

(defn select-first
  [src]
  (select-at! src 0))

(defn select-last
  [src]
  (select-at! src (dec (table/row-count src))))

(defn go-home
  [src]
  (cd src (System/getenv "HOME")))

(defn- get-selected-rows
  [src]
  (->> (range 0 (table/row-count src))
       (map #(table/value-at src %))
       (filter #(:selected? %))))

(defn delete-selected-files
  [src]
  (let [selected-data (get-selected-rows src)]
    (if (empty? selected-data)
      (toggle-selection src true)
      (show! (dialog :content (str "delete " (count selected-data) " files.")
                     :width   300
                     :option-type :ok-cancel
                     :success-fn (fn [_]
                                   (doseq [data selected-data]
                                     ((if (:dir? data) fs/delete-dir fs/delete)
                                      (.getAbsolutePath (:file data))))
                                   (refresh src)))))))

(defn select-all
  [src]
  (doseq [i (range 0 (table/row-count src))]
    (table/update-at! src i (assoc (table/value-at src i) :selected? true))))

(defn deselect-all
  [src]
  (doseq [i (range 0 (table/row-count src))]
    (table/update-at! src i (assoc (table/value-at src i) :selected? false))))

(defn copy-selected-files
  [src]
  (let [dst           (if (= src mui/left-table) mui/right-table mui/left-table)
        selected-data (get-selected-rows src)]
    (if (empty? selected-data)
      (toggle-selection src true)
      (do
        (doseq [data selected-data]
          (let [dst-path (path/join (mui/cwd dst) (:name data))]
            (if (fs/exists? dst-path)
              ; FIXME
              (alert (str dst-path " is exists"))
              ((if (:dir? data) fs/copy-dir fs/copy)
               (.getAbsolutePath (:file data)) dst-path))))
        (deselect-all src)
        (refresh dst)))))

(defn move-selected-files
  [src]
  (let [dst           (if (= src mui/left-table) mui/right-table mui/left-table)
        selected-data (get-selected-rows src)]
    (if (empty? selected-data)
      (toggle-selection src true)
      (do
        (doseq [data selected-data]
          (let [dst-path (path/join (mui/cwd dst) (:name data))]
            (if (fs/exists? dst-path)
              ; FIXME
              (alert (str dst-path " is exists"))
              (do ((if (:dir? data) fs/copy-dir fs/copy)
                   (.getAbsolutePath (:file data)) dst-path)
                  ; FIXME
                  ((if (:dir? data) fs/delete-dir fs/delete)
                   (.getAbsolutePath (:file data)))))))
        (deselect-all src)
        (refresh src)
        (refresh dst)))))

(defn sendto
  [src target]
  (let [file (:file (table/value-at src (selection src)))]
    (-> (config/get-config)
        :sendto
        (get target)
        (render {:path (.getAbsolutePath file)})
        execute)))
