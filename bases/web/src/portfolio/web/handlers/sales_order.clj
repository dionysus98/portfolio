;; (ns portfolio.web.handlers.sales-order
;;   (:require [io.pedestal.http.body-params :refer [body-params]]
;;             [io.pedestal.http.ring-middlewares :refer [multipart-params]]
;;             [portfolio.customer.interface :as cus]
;;             [portfolio.order.interface :as order]
;;             [portfolio.price-approval.interface :as pa]
;;             [portfolio.product.interface :as pr]
;;             [portfolio.production.interface :as prod]
;;             [portfolio.qa.interface :as qa]
;;             [portfolio.web.components.form-components :as form]
;;             [portfolio.web.handlers.core :as h]
;;             [portfolio.web.interceptors :as i]
;;             [portfolio.web.lib.response :as resp]
;;             [portfolio.web.pages.home :as views]))

;; (def sale-order
;;   {:name  ::sale-order
;;    :enter (fn [context]
;;             (let [_ (reset! h/test-atom context)
;;                   request (:request context)
;;                   params (h/view-params context :form/sales-order)
;;                   id (-> request :path-params :id parse-uuid)
;;                   q #(h/auth-filter context (% id))
;;                   order (-> (q order/get-order)
;;                             (update :order/product pr/get-product)
;;                             (update :order/customer cus/get-customer)
;;                             (assoc :order/qa qa/qa-by-order-id))]
;;               (->> {:order                  order
;;                     :order-notes            (q order/order-notes)
;;                     :order-attachments      (q order/order-atts)
;;                     :qa-attachments         (q order/order->qa-atts)
;;                     :production-attachments (q order/order->prod-atts)
;;                     :payments               (q order/order-payments)
;;                     :total-payments         (order/order-total-pyt-amount id)}
;;                    (views/sale-order params)
;;                    (h/res context))))})

;; (def sales-order
;;   {:name  ::sales-order
;;    :enter (fn [context]
;;             (let [_ (reset! h/test-atom context)
;;                   params (h/view-params context :form/sales-order)
;;                   af (partial h/auth-filter context)
;;                   merge-data (fn [order]
;;                                (-> order
;;                                    (update :order/product pr/get-product)
;;                                    (update :order/customer cus/get-customer)
;;                                    (assoc :order/production (order/order-production (:xt/id order)))))
;;                   orders (->> (af (order/all-orders))
;;                               (map merge-data))]
;;               (h/res context (views/sales-order params orders))))})

;; (def sales-order-form
;;   {:name  ::sales-order-form
;;    :enter (fn [context]
;;             (let [_ (reset! h/test-atom context)
;;                   params (h/view-params context :form/sales-order)]
;;               (if (:authorized? params)
;;                 (h/res context (views/sales-order-form params))
;;                 (throw (ex-info "Page Not Found" {})))))
;;    :error h/page-not-found})

;; (def create-sales-order
;;   {:name :create-sales-order
;;    :enter (fn [context]
;;             (->> #(do
;;                     (order/fe->create-order %)
;;                     (:base-uri %))
;;                  (h/form-ok->redirect context)))
;;    :error h/display-error})

;; (defn update-post-processing! [updated]
;;   (case (:order/status updated)
;;     :production-completed (->> (:xt/id updated)
;;                                order/order-production
;;                                :xt/id
;;                                prod/mark-as-completed!)
;;     :qa-started (qa/create-qa! {:qa/order (:xt/id updated)})
;;     nil))

;; (def update-order-status
;;   {:name ::update-order-status
;;    :enter (fn [context]
;;             (let [_ (reset! h/test-atom context)
;;                   request (:request context)
;;                   {:keys [id signal]} (:path-params request)
;;                   _updated (->> (keyword signal)
;;                                 (order/update-order-status! (parse-uuid id))
;;                                 update-post-processing!)
;;                   redirect (-> request :headers (get "hx-current-url"))]
;;               (assoc context :response (resp/ok "OK" "HX-Redirect" redirect))))
;;    :error h/display-error})

;; (def create-order-approval-note
;;   {:name :create-order-approval-note
;;    :enter (fn [context]
;;             (->> #(do
;;                     (order/fe->create-order-note %)
;;                     (-> % :request :headers (get "hx-current-url")))
;;                  (h/form-ok->redirect context)))
;;    :error h/display-error})

;; (def create-order-approval-reports
;;   {:name :create-order-approval-reports
;;    :enter (fn [context]
;;             (->> #(do
;;                     (order/fe->create-order-approval-reports %)
;;                     (-> % :request :headers (get "hx-current-url")))
;;                  (h/form-ok->redirect context)))
;;    :error h/display-error})

;; (defn has-price-approval []
;;   (let [model #(as-> (-> %
;;                          (update :price-approval/product pr/get-product)
;;                          (update :price-approval/customer cus/get-customer)) data
;;                  {:label (str (-> data :price-approval/product :product/name) " for "
;;                               (-> data :price-approval/customer :customer/ref-no)
;;                               " @ Rs."
;;                               (:price-approval/sale-price data))
;;                   :value (-> data :xt/id str)})
;;         pa (map model (pa/approved))]
;;     (form/select {:name    "form-sales-order/price-approval"
;;                   :options pa})))

;; (defn no-price-approval []
;;   (let [products  (->> (pr/all-products)
;;                        (form/serailize :product/name :xt/id))
;;         customers (->> (cus/all-customers)
;;                        (form/serailize :customer/company-name :xt/id))]
;;     [:div.mb-3
;;      (form/select {:name    "form-sales-order/customer"
;;                    :options customers})
;;      (form/input {:name "form-sales-order/sale-price"
;;                   :placeholder "Enter Amount in INR"})
;;      (form/select {:name    "form-sales-order/product"
;;                    :options products})
;;      (form/input {:name "form-sales-order/discount"
;;                   :placeholder "Discount in percentage"})]))

;; (defn payment-details []
;;   [:div.mb-3
;;    (form/input {:name "form-sales-order/payment-utr"
;;                 :label "UTR Number"})
;;    (form/input {:name "form-sales-order/payment-amount"
;;                 :label "Amount (in Rupees)"})])

;; (def ui-form-sales-order
;;   {:name ::ui-form-sales-order
;;    :enter (fn [context]
;;             (let [_ (reset! h/test-atom context)
;;                   request (:request context)
;;                   field (-> request :path-params :field keyword)
;;                   ip-name (str "form-sales-order/" (name field))
;;                   res (partial h/res context)]
;;               (case field
;;                 :attachment (res (form/file {:name ip-name}))
;;                 :note (res (form/textarea {:name  ip-name
;;                                            :placeholder "Enter any notes"}))
;;                 :has-price-approval (let [has-approval? (-> request
;;                                                             (get-in [:params "form-sales-order/has-price-approval"])
;;                                                             (= "Yes"))]
;;                                       (if has-approval?
;;                                         (res (has-price-approval))
;;                                         (res (no-price-approval))))
;;                 :payment-details (res (payment-details))
;;                 context)))})

;; (def routes
;;   ["/sales-order"
;;    ^:interceptors [`i/user-info `i/auth-filter]
;;    {:get `sales-order}

;;    ["/form"
;;     ^:interceptors [(body-params)]
;;     {:get `sales-order-form}]
;;    ["/create"
;;     ^:interceptors [(multipart-params)]
;;     {:post `create-sales-order}]

;;    ["/order/:id" {:get `sale-order}]])