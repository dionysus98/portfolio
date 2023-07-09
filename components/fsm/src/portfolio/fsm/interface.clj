(ns portfolio.fsm.interface
  (:require [portfolio.fsm.core :as core]))

#_{:clj-kondo/ignore [:unused-binding]}
(defn fsm [{:keys [ent stages actions signal initial-state assoc-key] :as params}]
  (core/fsm params))