(ns user
  (:require [juxt.clip.repl :refer [start stop reset set-init! system]]
            [clojure.edn :as edn]
            [portal.api :as p]
            [clojure.java.io :as io]))

(defn system-config []
  (edn/read-string (slurp (io/resource "system.edn"))))

(set-init! system-config)

(defn start-system! [& _args]
  (start))

(comment
  (start)
  (stop)
  (reset)
  system

  (def p (p/open))

  (add-tap #'p/submit))


