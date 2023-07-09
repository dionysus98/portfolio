(ns portfolio.email.interface
  (:require [portfolio.email.core :as core]))

(def config (atom {:email {:host     nil
                           :ssl-port nil
                           :tls-port nil
                           :from     {:email nil
                                      :name  nil}
                           :password nil}}))

(defn init! [conf]
  (swap! config assoc :email conf))

(defn send-email! [params]
  (core/send-email! @config params))