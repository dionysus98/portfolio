(ns portfolio.web.spec
  (:require [portfolio.validation.interface :as v]))

(defn register-web-specs! []
  (do
    (v/register-spec!
     :fe/form-context
     [:map
      {:closed false}
      [:request [:map-of :keyword :any]]
      [:fdt [:map-of :keyword :any]]
      [:schema :keyword]
      [:valid? :boolean]
      [:base-uri :string]
      [:parse-int [:fn #(fn? %)]]])
    (v/register-spec!
     :fe/view-params
     [:map
      {:closed false}
      [:user [:map-of :keyword :any]]
      [:accesses [:vector :keyword]]
      [:authorized? :boolean]])

    #_(v/register-kv-spec! bank-spec)

    #_(v/register-spec! :form-bank form-bank-spec)))
