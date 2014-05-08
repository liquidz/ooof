(defproject ooof "0.0.1"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [seesaw "1.4.4"]
                 [clj-time "0.7.0"]
                 [cuma "0.0.8"]
                 [me.raynes/fs "1.4.4"]
                 ]

  :profiles {:dev {:dependencies [[midje "1.6.3" :exclusions [org.clojure/clojure]]
                                  [org.clojars.runa/conjure "2.1.3"]]}}

  :plugins [[lein-midje "3.1.3"]
            [lein-kibit "0.0.8"]
            [lein-bikeshed "0.1.6"]
            [jonase/eastwood "0.1.0"]
            [codox "0.6.6"]]

  :main ooof.core
  )
