(ns portfolio.scheduler.core
  (:require [chime.core-async :refer [chime-ch]]
            [chime.core :as chime]
            [tick.core :as t]
            [clojure.core.async :refer [<! <!! go-loop]]
            [portfolio.util.interface :as util]
            [io.pedestal.log :as log]))

(def tasks (atom {}))

(defn run-async-task! [chimes task-id task-fn]
  (swap! tasks assoc task-id
         (<!! (go-loop []
                (when-let [msg (<! chimes)]
                  (task-fn msg)
                  (recur))))))

(defn schedule-async-task!
  "Params: @instant @duration @keyword @fn   
   Example: (schedule-task! #time/instant '2023-02-20T13:41:00Z' #time/duration 'PT24H' :my-task #(println %))
   "
  [time-of-exec interval-duration task-id task-fn]
  (let [chimes (->> interval-duration
                    (chime/periodic-seq time-of-exec)
                    (chime-ch))]
    (run-async-task! chimes task-id task-fn)))

(defn run-task! [chimes task-id task-fn err-handler]
  (let [scheduler (chime/chime-at chimes task-fn {:error-handler err-handler})]
    (swap! tasks assoc task-id scheduler)))

(defn schedule-task!
  "Params: @instant @duration @keyword @fn   
   Example: (schedule-task! #time/instant '2023-02-20T13:41:00Z' #time/duration 'PT24H' :my-task #(println %))
   "
  [time-of-exec interval-duration task-id task-fn & {:keys [err-handler]}]
  (let [def-err-handler (fn [err]
                          (log/info :msg "Error on running task"
                                    :task/id task-id
                                    :error err)
                          true)
        chimes (chime/periodic-seq time-of-exec interval-duration)]
    (run-task! chimes task-id task-fn (or err-handler def-err-handler))))

(defn schedule-daily-task-at-hour!
  "Params: @string @keyword @fn
   time-of-exec HH:MM:SS (should be written in 24hr format)
   task-id is the id used to close! the buffer by storing the ch in an atom called tasks
   task-fn would get the execution time(instant) of the task as it's arguement

   Example: (schedule-daily-task-at-hour! '18:00:00' :my-task #(println %))
   "
  [time-of-exec task-id task-fn & {:keys [err-handler]}]
  (let [_start (-> (t/time time-of-exec)
                   (t/on (t/zoned-date-time))
                   t/instant)
        start-time (if (t/<= _start (t/instant (t/now)))
                     (t/instant (util/from->future _start :days 1))
                     _start)
        interval (t/new-duration 1 :days)]
    (if err-handler
      (schedule-task! start-time interval task-id task-fn :err-handler err-handler)
      (schedule-task! start-time interval task-id task-fn))))

(defn model-task
  [task-id f & args]
  (fn [exec-time]
    (log/info :msg "Running task"
              :id task-id
              :at exec-time)
    (apply f args)))

(defn cancel-task! [task-id]
  (when-some [task (task-id @tasks)]
    (.close task)))

(comment
  (schedule-task! (t/instant (util/now->future :seconds 30))
                  (t/new-duration 10 :seconds)
                  :my-task-seconds
                  #(prn "Chiming at:" %))

  (cancel-task! :my-task-seconds)

  (schedule-daily-task-at-hour! "15:30:15" :my-task-days #(prn "Chiming at:" %)))