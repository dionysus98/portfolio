(ns portfolio.validation.core
  (:require [malli.core :as m]
            [malli.error :as me]
            [malli.registry :as mr]))

(def schemas (atom {}))

(def registry (atom {}))

(defn register-spec! [type schema]
  (do
    (swap! schemas assoc type schema)
    (reset! registry (mr/composite-registry m/default-registry @schemas))))

(defn register-kv-spec! [specs]
  (->> specs
       (map (fn [[k v]]
              (register-spec! k v)))
       doall))

(defn map->spec [spec-map & params]
  (let [opts-coll (->> params first :opts not-empty)
        closed? (or (->> params first :closed?) false)]
    `[:map {:closed ~closed?}
      ~@(->> spec-map
             (map (fn [[k v]]
                    (if (and opts-coll (some #(= k %) opts-coll))
                      [k {:optional true} v]
                      [k v]))))]))

(defn valid? [?schema data]
  (m/validate ?schema data {:registry @registry}))

(defn explain [?schema data & get-first?]
  (let [err-vec (me/humanize (m/explain ?schema data {:registry @registry}))]
    (if (first get-first?)
      (first err-vec)
      err-vec)))

