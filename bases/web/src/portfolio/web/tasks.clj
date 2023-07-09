(ns portfolio.web.tasks
  (:require [io.pedestal.log :as log]
            [portfolio.util.interface :as util]
            [portfolio.scheduler.interface :as sch]
            [tick.core :as t]))

(defn greeter! [exec-time]
  (log/info :msg "Running task :greeter"
            :exec-at exec-time
            :greet "HELLO!!"))

(defn start-tasks! [_node]
  (sch/schedule-task! (t/instant (util/now->future :seconds 10))
                      (t/new-duration 600 :seconds)
                      :greeter
                      greeter!))

(defn stop-tasks! []
  (sch/cancel-task! :greeter))