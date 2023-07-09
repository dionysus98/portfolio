(ns portfolio.admin.pathom2
  (:require [com.wsscode.pathom.connect :as pc]
            [io.pedestal.log :as log]
            [portfolio.xtdb.interface :as data]
            [xtdb.api :as xt]))

(pc/defresolver new-id
  [{:keys [db/km]} _]
  {::pc/output [:new-id]}
  {:new-id (data/new-id km)})

(pc/defresolver new-ids
  [{:keys [db/km]} {:keys [n]}]
  {::pc/input #{:n}
   ::pc/output [:new-ids]}
  {:new-ids (repeatedly n #(data/new-id km))})

(pc/defresolver entity-by-id
  [{:keys [db/km]} {:keys [xt/id]}]
  {::pc/input #{:xt/id}
   ::pc/output [:entity]}
  {:entity (xt/entity (xt/db km) id)})

(pc/defresolver config
  [{:keys [app/config]} _]
  {::pc/output [:config]}
  {:config (config)})

(pc/defmutation push-any
  [{:keys [db/km]} data]
  {::pc/sym `push/any
   ::pc/params #{:any/id :any/data}
   ::pc/output [:any/id]}
  (log/info :msg (str "push/any params: " data))
  (let [id (or (:any/id data) (data/new-id km))
        doc {:xt/id id :any/data (:any/data data)}
        _res (xt/submit-tx km [[::xt/put doc]])]
    {:any/id (:xt/id doc)}))

(pc/defresolver resolvers
  [{:keys [p/indexes]} _]
  {::pc/output [:resolvers]}
  {:resolvers (-> (indexes)
                  :com.wsscode.pathom.connect/index-io)})

(pc/defresolver mutations
  [{:keys [p/indexes]} _]
  {::pc/output [:mutations]}
  {:mutations (->> (indexes)
                   :com.wsscode.pathom.connect/index-mutations
                   (map (fn [[k v]]
                          {(keyword k) (select-keys v [:com.wsscode.pathom.connect/sym
                                                       :com.wsscode.pathom.connect/params
                                                       :com.wsscode.pathom.connect/output])})))})
