(ns portfolio.attachment.core
  (:require [portfolio.attachment.interface.spec]
            [portfolio.validation.interface :as v]
            [xtdb.api :as xt]))

(defn all-attachments [node]
  (map first (xt/q (xt/db node)
                   '{:find  [?e]
                     :where [[?e :attachment/src]]})))

(defn create-attachment! [node at]
  (v/with-validation :db/attachment at
    (do
      (->> (xt/submit-tx node [[::xt/put at]])
           (xt/await-tx node))
      at)))

(defn update-attachment! [node new-at]
  (when-some [at (xt/entity (xt/db node) (:xt/id new-at))]
    (let [updated (merge at new-at)]
      (v/with-validation :db/attachment updated
        (do
          (->> (xt/submit-tx node [[::xt/put updated]])
               (xt/await-tx node))
          updated)))))

(defn get-attachment [node id]
  (xt/entity (xt/db node) id))