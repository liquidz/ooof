(ns ooof.command
  (:require
    ooof.command.core
    ooof.command.search
    ooof.command.rename
    ooof.command.sendto
    ooof.command.goto
    ))

(defn get-command-fns*
  []
  (->> (all-ns)
       (filter #(let [^String n (str (ns-name %))]
                  (not= -1 (.indexOf n "ooof.command."))))
       (map ns-publics)
       (apply merge)))

(def get-command-fns (memoize get-command-fns*))
