(ns portfolio.web.lib.datetime
  (:require [cljc.java-time.instant :as instant]
            [cljc.java-time.local-date-time :as ldt]
            [cljc.java-time.zone-id :as zone-id]
            [cljc.java-time.zoned-date-time :as zdt]
            [clojure.string :as str])
  #?(:clj  (:import [java.util Date])))

(defn new-date
  "Create a Date object from milliseconds (defaults to now)."
  ([]
   #?(:clj  (Date.)
      :cljs (js/Date.)))
  ([millis]
   #?(:clj  (new Date millis)
      :cljs (js/Date. millis))))

(defn local-datetime->inst
  "Returns a inst based on the date/time given as time in the named (ISO) zone (e.g. Asia/Kolkata)."
  ([zone-name local-dt]
   (let [z      (zone-id/of zone-name)
         zdt    (ldt/at-zone local-dt z)
         millis (instant/to-epoch-milli (zdt/to-instant zdt))]
     (new-date millis)))
  ([zone-name yyyy month day hh mm ss]
   (let [local-dt (ldt/of yyyy month day hh mm ss)]
     (local-datetime->inst zone-name local-dt)))
  ([zone-name yyyy month day hh mm]
   (local-datetime->inst zone-name yyyy month day  hh mm 0)))

(defn html-datetime->inst [html-datetime]
  (let [{:keys [date time]} html-datetime
        model #(map parse-long (str/split %1 %2))
        zone ["Asia/Kolkata"]
        args (concat zone (model date #"-") (model time #":"))]
    (apply local-datetime->inst args)))

(comment
  (local-datetime->inst "Asia/Kolkata" 2023 4 22 3 4)
  (html-datetime->inst {:date "2023-04-21", :time "03:04"})
  :rcf)