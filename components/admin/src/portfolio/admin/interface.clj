(ns portfolio.admin.interface
  (:require [portfolio.admin.pathom2 :as p2]))

(def p2-vars [p2/new-id
              p2/new-ids
              p2/entity-by-id
              p2/config
              p2/push-any
              p2/resolvers
              p2/mutations])
