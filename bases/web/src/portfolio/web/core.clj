(ns portfolio.web.core
  (:gen-class)
  (:require [cider.nrepl :refer [cider-nrepl-handler]]
            [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.log :as log]
            [juxt.clip.core :as clip]
            [nrepl.server :as nrepl]
            [portfolio.web.config :refer [config]]
            [portfolio.web.db :as db]
            [portfolio.web.routes :as routes]
            [portfolio.web.spec :as specs]
            [portfolio.web.tasks :as tasks]))

(set! *warn-on-reflection* true)

(def service-map
  {::http/routes         (route/expand-routes routes/routes)
   ::http/type           :jetty
   ::http/host           "0.0.0.0"
   ::http/port           8000
   ::http/join?          false
   ::http/resource-path  "public"
   ::http/secure-headers {:content-security-policy-settings {:object-src "none"}}})

(defn start-server [& {:keys [port]}]
  (log/info :msg "Starting Server"
            :service-map service-map
            :port port)
  (-> service-map
      (update ::http/port #(or port %))
      http/create-server
      http/start))

(defn stop-server [server]
  (http/stop server)
  nil)

(defn start-nrepl-server [port]
  (nrepl/start-server
   :port port
   :handler cider-nrepl-handler))

(defn stop-nrepl-server [server]
  (nrepl/stop-server server))

(def system nil)

(defn pre-start! [conf node]
  (log/info :msg "Starting base"
            :conf conf
            :node node)
  (reset! config conf)
  (reset! db/node node)
  (specs/register-web-specs!)
  (tasks/start-tasks! node))

(defn stop-app! []
  (tasks/stop-tasks!))

(defn start! [config]
  (let [sys (clip/start config)]
    (alter-var-root #'system (constantly sys))
    (.addShutdownHook
     (Runtime/getRuntime)
     (Thread. #(clip/stop config sys)))
    @(promise)))
