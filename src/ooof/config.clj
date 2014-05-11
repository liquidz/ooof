(ns ooof.config
  (:require
    [clojure.edn :as edn]
    [seesaw.color :as color]
    ))

(declare edn-readers)
(def ^:dynamic *config-path* "config.edn")

(defn- include
  [s]
  (edn/read-string {:readers edn-readers} (slurp s)))

(def ^{:private true}
  edn-readers {'ooof.config/include include})

(def get-config (memoize #(include *config-path*)))

(defn get-key-binds
  ([] (get-key-binds :main))
  ([section] (-> (get-config) :keys section)))

(defn get-color
  [section-key target-key]
  (-> (get-config) :color section-key target-key color/color))

(def get-background (partial get-color :background))
(def get-foreground (partial get-color :foreground))
