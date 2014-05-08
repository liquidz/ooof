(ns ooof.event
  (:require
    [ooof.ui.main   :as mui]
    [ooof.command   :as cmd]
    [ooof.config    :as config]
    [seesaw.core    :refer :all]
    [seesaw.color   :as color]
    [seesaw.timer :as timer]
    [clojure.string :as str])
  (:import
    [java.awt.event KeyEvent InputEvent]))

(defn get-label-from-table-event
  [e]
  (if (= mui/left-table (.getSource e))
    mui/left-label
    mui/right-label))

(defn- check-modifier
  [i j k]
  (if (not (zero? (bit-and i j))) k))

(defn- parse-modifier
  [i]
  (->> [(check-modifier i InputEvent/SHIFT_DOWN_MASK :shift)
        (check-modifier i InputEvent/ALT_DOWN_MASK   :alt)
        (check-modifier i InputEvent/CTRL_DOWN_MASK  :ctrl)
        (check-modifier i InputEvent/META_DOWN_MASK  :meta)]
       (remove nil?)
       set))

(defn alphabet?
  [code]
  (or (and (>= code 65) (<= code 90))
      (and (>= code 97) (<= code 122))))

(defn- code->char
  [code]
  (if (alphabet? code)
    (str/lower-case (char code))
    (condp = code
      KeyEvent/VK_BACK_SPACE "bs"
      KeyEvent/VK_DELETE     "del"
      KeyEvent/VK_DOWN       "down"
      KeyEvent/VK_ENTER      "enter"
      KeyEvent/VK_ESCAPE     "esc"
      KeyEvent/VK_F1         "f1"
      KeyEvent/VK_F2         "f2"
      KeyEvent/VK_F3         "f3"
      KeyEvent/VK_F4         "f4"
      KeyEvent/VK_F5         "f5"
      KeyEvent/VK_F6         "f6"
      KeyEvent/VK_F7         "f7"
      KeyEvent/VK_F8         "f8"
      KeyEvent/VK_F9         "f9"
      KeyEvent/VK_F10        "f10"
      KeyEvent/VK_F11        "f11"
      KeyEvent/VK_F12        "f12"
      KeyEvent/VK_LEFT       "left"
      KeyEvent/VK_PAGE_DOWN  "page-down"
      KeyEvent/VK_PAGE_UP    "page-up"
      KeyEvent/VK_RIGHT      "right"
      KeyEvent/VK_SPACE      "space"
      KeyEvent/VK_TAB        "tab"
      KeyEvent/VK_UP         "up"
      nil)))

(defn- parse-key-event
  [e]
  {:char     (or (code->char (.getKeyCode e)) (str (.getKeyChar e)))
   :code     (.getKeyCode e)
   :modifier (parse-modifier (.getModifiersEx e)) })

(defn process-key-event
  [section ^KeyEvent e]
  (let [src  (.getSource e)
        evt  (parse-key-event e)
        fn-k (some->> (dissoc evt :code)
                     (get (config/get-key-binds section)))
        [fn-k & args] (if (sequential? fn-k) fn-k [fn-k])
        f (get (cmd/get-command-fns) fn-k)
        ]

    (if (some? f)
      (do (apply f src args)
          ; ignore default key event
          (.consume e)))))

(def last-key-state (atom {}))
(def t (timer/timer
         (fn [& args]
           (when-let [{section :section event :event} @last-key-state]
             (process-key-event section event)))
         :initial-delay (-> (config/get-config) :key-timer :initial-delay)
         :delay (-> (config/get-config) :key-timer :delay)
         :start? false))

(defn listen-key-event!
  [elem & {:keys [section] :or {section :main}}]
  (listen elem
          :key-pressed (fn [e]
                         (reset! last-key-state {:event e :section section})
                         (.start t)
                         (process-key-event section e))
          :key-released (fn [e]
                          (reset! last-key-state {})
                          (.stop t))
          :focus-lost (fn [e]
                        (reset! last-key-state {})
                        (.stop t))))

(defn listen-focus-event!
  [elem]
  (listen
    elem
    :focus-gained #(doto (get-label-from-table-event %)
                     (.setBackground (-> (config/get-config) :color :background :focus color/color))
                     (.setForeground (-> (config/get-config) :color :foreground :focus color/color))
                     )
    :focus-lost   #(doto (get-label-from-table-event %)
                     (.setBackground (-> (config/get-config) :color :background :even color/color))
                     (.setForeground (-> (config/get-config) :color :foreground :file color/color)))))
