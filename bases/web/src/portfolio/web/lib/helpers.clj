(ns portfolio.web.lib.helpers
  (:require [clojure.string :as str]
            [tick.core :as t]))

(defn inst->html-date-time
  ([inst]
   (let [m #(str (% (t/instant inst)))]
     {:date (m t/date)
      :time (->> (str/split (m t/time) #":")
                 (take 2)
                 (str/join ":"))}))
  ([]
   (inst->html-date-time (t/inst))))

(defn html-datetime->inst
  ([html-datetime]
   (let [{:keys [date time]} html-datetime]
     (-> (str date "T" time) t/date-time t/instant t/inst)))
  ([]
   (html-datetime->inst (inst->html-date-time))))

(defn fmt-date [date]
  (when date
    (str (t/day-of-month date) " "
         (t/month date) " "
         (t/year date))))

(defn fmt-date-time [date]
  (when date
    (str (t/day-of-month date) " "
         (t/month date) " "
         (t/year date) " "
         (->> (-> date
                  t/instant
                  t/time
                  str
                  (str/split #":"))
              (take 2)
              (str/join ":")))))