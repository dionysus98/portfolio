(ns portfolio.xtdb.core
  (:require [portfolio.xtdb.specs]
            [portfolio.validation.interface :refer [with-validation]]
            [clojure.java.io :as io]
            [xtdb.api :as xt]
            [xtdb.rocksdb]
            [xtdb.jdbc]
            [xtdb.jdbc.psql]))

(defn start-xtdb! [conf]
  (with-validation :config/xtdb conf
    (let [engine (:engine conf)]
      (case engine
        :jdbc (xt/start-node
               {:xtdb.jdbc/connection-pool {:dialect {:xtdb/module 'xtdb.jdbc.psql/->dialect}
                                            :pool-opts {}
                                            :db-spec {:jdbcUrl (:jdbc-uri conf)}}
                :xtdb/tx-log {:xtdb/module 'xtdb.jdbc/->tx-log
                              :connection-pool :xtdb.jdbc/connection-pool}
                :xtdb/document-store {:xtdb/module 'xtdb.jdbc/->document-store
                                      :connection-pool :xtdb.jdbc/connection-pool}
                :xtdb/index-store {:kv-store {:xtdb/module 'xtdb.rocksdb/->kv-store
                                              :db-dir (io/file (:index-store conf))}}
                :xtdb.lucene/lucene-store {:db-dir (:lucene-store conf)}})
        :rocksdb (letfn [(kv-store [dir]
                           {:kv-store {:xtdb/module 'xtdb.rocksdb/->kv-store
                                       :db-dir (io/file dir)
                                       :sync? true}})]
                   (xt/start-node
                    {:xtdb/tx-log (kv-store (:tx-log conf))
                     :xtdb/document-store (kv-store (:document-store conf))
                     :xtdb/index-store (kv-store (:index-store conf))
                     :xtdb.lucene/lucene-store {:db-dir (:lucene-store conf)}}))))))

(defn tx->tx+ops
  [node tx-id]
  (-> node
      ;; All we need is this `true` to include the operations.
      (xt/open-tx-log (dec tx-id) true)
      iterator-seq
      first))

(defn new-id [conn]
  (let [id (random-uuid)
        ent (xt/entity (xt/db conn) id)]
    (if-not (nil? ent)
      (new-id conn)
      id)))

(defn add-id
  "Checks if document contains an :xt/id. if not, generate
  and assign a new UUID. Otherwise return as is."
  [doc]
  (if-not (contains? doc :xt/id)
    (assoc doc :xt/id (random-uuid))
    doc))

(defn import-batch! [conn db data]
  (let [with-ids (map add-id data)
        in-db #(xt/entity (xt/db db) (:xt/id %))
        merged-docs (mapv #(merge (in-db %) %) with-ids)
        ops (mapv #(vector :xtdb.api/put %) merged-docs)]
    (xt/submit-tx conn ops)))

(defn push! [conn doc]
  (xt/submit-tx conn [[::xt/put
                       doc]]))

(defn delete! [conn id]
  (xt/submit-tx conn [[::xt/delete
                       id]]))

(defn last-update-time [conn id]
  (let [tx (first (xt/entity-history (xt/db conn) id :desc))]
    (:xtdb.api/valid-time tx)))

(defn creation-time [conn id]
  (let [tx (first (xt/entity-history (xt/db conn) id :asc))]
    (:xtdb.api/valid-time tx)))
