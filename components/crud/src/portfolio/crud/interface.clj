(ns portfolio.crud.interface
  (:require [xtdb.api :as xt]
            [portfolio.validation.interface :refer [with-validation]]))


(defmacro create! [schema]
  `(fn [~'node ~'data]
     (with-validation ~schema ~'data
       (do
         (xt/await-tx ~'node (xt/submit-tx ~'node [[::xt/put ~'data]]))
         (xt/entity (xt/db ~'node) (:xt/id ~'data))))))

(defmacro update! [schema]
  `(fn [~'node ~'id & ~'kvs]
     (let [e# (xt/entity (xt/db ~'node) ~'id)
           doc# (apply (partial assoc e#) ~'kvs)]
       (with-validation ~schema doc#
         (do
           (xt/await-tx ~'node (xt/submit-tx ~'node [[::xt/put doc#]]))
           (xt/entity (xt/db ~'node) (:xt/id doc#)))))))

(defmacro force! [schema]
  `(fn [~'node ~'id ~'doc]
     (if (= ~'id (:xt/id ~'doc))
       (with-validation ~schema ~'doc
         (do
           (xt/await-tx ~'node (xt/submit-tx ~'node [[::xt/put ~'doc]]))
           (xt/entity (xt/db ~'node) (:xt/id ~'doc))))
       (throw (ex-info "Provided ID and the ID in the document doesn't match." {})))))

(defmacro delete! []
  `(fn [~'node ~'id]
     (do
       (xt/await-tx ~'node (xt/submit-tx ~'node [[::xt/delete ~'id]])))))
