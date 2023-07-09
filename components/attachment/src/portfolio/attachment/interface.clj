(ns portfolio.attachment.interface
  (:require [portfolio.attachment.core :as core]))

(def db (atom nil))

(defn init! [node]
  (when node (reset! db node)))

(defn create-attachment! [at]
  (core/create-attachment! @db at))

(defn update-attachment! [new-at]
  (core/update-attachment! @db new-at))

(defn get-attachment [id]
  (core/get-attachment @db id))

(defn all-attachments []
  (core/all-attachments @db))