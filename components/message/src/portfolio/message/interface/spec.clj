(ns portfolio.message.interface.spec
  (:require [portfolio.validation.interface :as v]))

(def message
  [:map
   {:closed false}
   [:notification/id :uuid]
   [:to [:or v/email-regex [:vector v/email-regex]]]
   [:subject v/not-empty-string]
   [:body
    [:or
     [:vector [:map {:closed false}
               [:type :string]
               [:content :any]
               [:content-type {:optional true} v/not-empty-string]]]
     v/not-empty-string]]])

(v/register-spec! :se/message-body message)