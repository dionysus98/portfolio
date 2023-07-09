(ns portfolio.xtdb.specs
  (:require [portfolio.validation.interface :as v]))

(def db
  [:map {:closed false}
   [:engine [:enum :jdbc :rocksdb]]
   [:jdbc-uri :string]
   [:index-store :string]
   [:document-store :string]
   [:tx-log :string]
   [:lucene-store :string]])

(v/register-spec! :config/xtdb db)
