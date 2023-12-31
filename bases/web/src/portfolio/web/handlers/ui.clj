(ns portfolio.web.handlers.ui
  (:require [clojure.string :as str]
            [io.pedestal.http.body-params :refer [body-params]]
            [io.pedestal.http.ring-middlewares :refer [multipart-params]]
            [portfolio.web.components.dashboard :refer [dashboard]]
            [portfolio.web.components.elements :as el]
            [portfolio.web.handlers.core :as h]
            [portfolio.web.lib.ui.states :refer [dashboard-items]]))

(def validate
  {:name  ::validate
   :enter (fn [context]
            (let [_ (reset! h/test-atom context)
                  {:keys [valid?
                          error-id
                          explain]} (h/validate context)
                  res (partial h/res context)]
              (if valid?
                (res [:div {:id error-id}])
                (res [:div.error {:id error-id}
                      [:p explain]]))))})

(def view-attachment
  {:name ::view-attachment
   :enter (fn [context]
            (let [_ (reset! h/test-atom context)
                  {:keys [src
                          content-type]} (h/view-attachment context)]
              (-> (h/res context src :headers {"Content-Type" content-type})
                  (assoc-in [:response :body] src))))})

(def ui-form-file
  {:name :ui-form-file
   :enter (fn [context]
            (let [_ (reset! h/test-atom context)
                  {:keys [value error-id]} (h/form-file context)]
              (h/res context
                     [:div {:id error-id}
                      (el/grouped-tags
                       (map #(hash-map
                              :label (:filename %)
                              :value (-> % :filename
                                         (str/split #" ")
                                         str/join
                                         (str/replace "." "-")
                                         (str/replace "/" "-")))
                            value))])))})

(def ui-corner-bubble
  {:name :ui-corner-bubble
   :enter (fn [context]
            (let [_ (reset! h/test-atom context)
                  request (:request context)
                  selected (some-> request :path-params :event keyword)]
              (h/res context (dashboard selected dashboard-items))))})

(def routes
  ["/ui"

   ["/corner-bubble/:event"
    ^:interceptors [(body-params)]
    {:post `ui-corner-bubble}]

   ["/attachment/view/:attachment-id"
    {:get `view-attachment}]

   ["/validation"
    ["/:ent/:spec"
     ^:interceptors [(multipart-params) (body-params)]
     {:post `validate}]]

   #_["/dashboard/:item"
      ^:interceptors [(body-params)]
      {:post `ui-dashboard}]

   #_["/header/:set-active"
      ^:interceptors [(body-params)]
      {:post `ui-header}]

   #_["/form"
      ["/file/:form/:field"
       ^:interceptors [(multipart-params) (body-params)]
       {:post `ui-form-file}]
      ["/customer/:id/:form-type"
       ^:interceptors [(body-params)]
       {:post `ui-customer-form}]
      ["/sales-order/:field"
       ^:interceptors [(multipart-params)]
       {:post `ui-form-sales-order}]
      ["/payment/:field"
       ^:interceptors [(multipart-params)]
       {:post `ui-form-payment}]
      ["/order/approval/note/:order-id"
       ^:interceptors [(body-params)]
       {:post `create-order-approval-note}]
      ["/order/approval/report/:order-id"
       ^:interceptors [(multipart-params)]
       {:post `create-order-approval-reports}]]

   #_["/approve-price/:id"
      ^:interceptors [(body-params)]
      {:post `ui-approve-price}]

   #_["/confirm-payment/:id"
      ^:interceptors [(body-params)]
      {:post `ui-confirm-payment}]

   #_["/order/:id/update-status/:signal"
      ^:interceptors [(body-params)]
      {:post `update-order-status}]])