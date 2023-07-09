(ns portfolio.message.interface
  (:require [portfolio.message.core :as core]))

(def db (atom nil))

(defn init! [node]
  (reset! db node))

(defn build-message [notification]
  (core/build-message @db notification))

(defn send->update [msg-body]
  (core/send->update msg-body))