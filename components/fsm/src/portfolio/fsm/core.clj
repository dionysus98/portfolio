(ns portfolio.fsm.core
  (:require [tilakone.core :as tk]
            [tilakone.schema :as tks]
            [portfolio.util.interface :refer [map->nsmap nsmap->map]]))

(defn fsm-model [stages]
  (let [tns-model #(map (fn [v] (map->nsmap v :tilakone.core)) %)]
    (for [s stages]
      (-> {::tk/name         (:name s)
           ::tk/transitions  (-> s :transitions tns-model)}
          (merge s)
          (dissoc :name :transitions)))))

(defn actions-model [fsm]
  (let [serailize-state (fn [v]
                          (as-> (nsmap->map v) s
                            (assoc s :transitions (map (fn [t] (nsmap->map t)) (:transitions s)))))]
    (-> (nsmap->map fsm)
        (update :process nsmap->map)
        (update-in [:process :states] #(map serailize-state %)))))

(defn gen-fsm [stages state actions]
  {::tk/states  stages
   ::tk/state   state
   ::tk/action! (fn [fsm] (actions (actions-model fsm)))})

(defn fsm
  [{:keys [ent stages actions signal initial-state assoc-key]}]
  (let [states          (-> stages fsm-model tks/validate-states)
        cur-state       (or (assoc-key ent) initial-state)
        data-to-process (merge (gen-fsm states cur-state actions) {:data ent})
        process         (tks/validate-process data-to-process)
        new-state       (tk/apply-signal process signal)
        updated         (assoc ent assoc-key (::tk/state new-state))]
    updated))