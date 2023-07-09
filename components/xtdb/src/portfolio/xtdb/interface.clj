(ns portfolio.xtdb.interface
  (:require [portfolio.xtdb.core :as core]))

(defn start! [conf]
  (core/start-xtdb! conf))

(defn tx->tx+ops [node tx-id]
  (core/tx->tx+ops node tx-id))

(defn new-id [conn]
  (core/new-id conn))

(defn add-id
  "Checks if document contains an :xt/id. if not, generate
  and assign a new UUID. Otherwise return as is."
  [doc]
  (core/add-id doc))

(defn import-batch! [conn db data]
  (core/import-batch! conn db data))

(defn push! [conn doc]
  (core/push! conn doc))

(defn delete! [conn id]
  (core/delete! conn id))

(defn last-update-time [conn id]
  (core/last-update-time conn id))

(defn creation-time [conn id]
  (core/creation-time conn id))
