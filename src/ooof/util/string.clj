(ns ooof.util.string
  (:require
    [clojure.string :as str]))


(defn parse-execute-arg
  [s]
  (->> (str "(" (str/replace s "/" "<slash>") ")")
       read-string
       (map #(str/replace (str %) "<slash>" "/"))))

