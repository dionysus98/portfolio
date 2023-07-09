(ns portfolio.scheduler.interface
  (:require [portfolio.scheduler.core :as core]))

(defn model-task [task-id f & args]
  (apply core/model-task task-id f args))

(defn schedule-task! [time-of-exec interval-duration task-id task-fn]
  (core/schedule-task! time-of-exec interval-duration task-id task-fn))

(defn schedule-daily-task-at-hour! [time-of-exec task-id task-fn]
  (core/schedule-daily-task-at-hour! time-of-exec task-id task-fn))

(defn cancel-task! [task-id]
  (core/cancel-task! task-id))