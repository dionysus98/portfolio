(ns portfolio.email.interface.spec
  (:require [portfolio.validation.interface :as v]))

(def email
  [:map
   {:closed false}
   [:from [:or v/email-regex [:map
                              [:name v/not-empty-string]
                              [:email v/email-regex]]]]
   [:to [:or v/email-regex [:vector v/email-regex]]]
   [:subject v/not-empty-string]
   [:body
    [:or
     [:vector [:map {:closed false}
               [:type :string]
               [:content :any]
               [:content-type {:optional true} v/not-empty-string]]]
     v/not-empty-string]]])

(v/register-spec! :db/email email)