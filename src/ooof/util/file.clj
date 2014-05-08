(ns ooof.util.file
  (:require
    [ooof.config :refer :all]
    [cuma.core :refer [render]]
    [clj-time.coerce :as c]
    [clojure.string :as str]))

(def ^{:private true}
  DEFAULT_LAST_MODIFIED_FORMAT
  "$(date-format date \"yyyy/MM/dd HH:mm\")")

(defn size->str
  [n]
  (if (not (number? n))
    n
    (let [unit ["B" "KB" "MB" "GB" "TB"]
          x    (->> n str reverse (partition-all 3) reverse)]
      (str
        (->> x first reverse (apply str))
        (some->> x second last (str "."))
        " "
        (nth unit (dec (count x)) "")))))

(defn last-modified->str
  [n]
  (if (not (number? n))
    n
    (let [fmt (some-> (get-config) :format :last-modified)]
      (render (or fmt DEFAULT_LAST_MODIFIED_FORMAT) {:date (c/from-long n)}))))
