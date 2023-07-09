(ns dev
  (:require [portfolio.pathom2.core :as pathom2]
            [clojure.edn :as edn]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.java.io :as io]
            [juxt.clip.repl :refer [system]]))

(def cli-options
  [["-c" "--config FILE" "Config file, found on the classpath, to use."]])

(defn -main
  [& args]
  (let [{{:keys [config]} :options} (parse-opts args cli-options)
        system-config (edn/read-string (slurp (io/resource config)))]
    (pathom2/start! system-config)))

(comment
  pathom2/system
  (pathom2/pre-start! (:config system)))

  ;;
