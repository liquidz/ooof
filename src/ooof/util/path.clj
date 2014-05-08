(ns ooof.util.path
  (:require
    [clojure.string :as str]))


(def ^{:doc "OS name."}
  os-name
  (.. System getProperties (get "os.name")))

(def ^{:doc "True if current environment is Windows."}
  windows?
  (zero? (.indexOf os-name "Windows")))

(def ^{:doc "Path separator."}
  separator
  (if windows? "\\" "/"))

(defn normalize
  "Normalize file path."
  [s]
  (if (and (string? s) (.endsWith s separator))
    (str/join (drop-last s))
    s))

(defn join
  "Join file paths."
  [& s]
  (->> s (map normalize)
       (str/join separator)))

