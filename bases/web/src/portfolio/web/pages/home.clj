(ns portfolio.web.pages.home
  (:require [portfolio.web.components.elements :as el]
            [portfolio.web.components.layouts :refer [base-layout]]))

;; (def sales-order-spec
;;   {:form-sales-order/has-price-approval v/yes-no
;;    :form-sales-order/product            v/uuid-string
;;    :form-sales-order/customer           v/uuid-string
;;    :form-sales-order/sale-price         v/int-regex
;;    :form-sales-order/price-approval     v/uuid-string
;;   ;;  :form-sales-order/customer-ref-no    v/not-empty-string
;;    :form-sales-order/tnc                v/not-empty-string
;;    :form-sales-order/advance            v/int-regex
;;    :form-sales-order/before-delievery   v/int-regex
;;    :form-sales-order/discount           v/int-regex
;;    :form-sales-order/balance            v/int-regex
;;    :form-sales-order/note               v/not-empty-string
;;    :form-sales-order/attachment         v/html-ip-attachment
;;    :form-sales-order/payment-utr        v/not-empty-string
;;    :form-sales-order/payment-amount     v/int-regex})

;; (def commission-spec
;;   {:form-commission/service-engineer v/not-empty-string})

;; (def commission-complete-spec
;;   {:form-commission-complete/date v/html-date})

;; (def qa-spec
;;   {:form-qa/result [:enum "Success" "Failure"]
;;    :form-qa/report v/html-ip-attachment})

;; (def production-spec
;;   {:form-production/engine-sr   v/not-empty-string
;;    :form-production/chassis-sr  v/not-empty-string
;;    :form-production/engine-pic  v/html-ip-attachment
;;    :form-production/chassis-pic v/html-ip-attachment})

;; (def approval-spec
;;   {:form-approval/note    v/not-empty-string
;;    :form-approval/reports v/html-ip-multiple-attachment})

;; (def dispatch-spec
;;   {:form-dispatch/dispatch-address v/not-empty-string
;;    :form-dispatch/vehicle-number v/not-empty-string
;;    :form-dispatch/driver-name v/not-empty-string
;;    :form-dispatch/driver-number [:string {:min 10 :max 10}]
;;    :form-dispatch/driver-id-proof v/html-ip-attachment})

;; (def form-sales-order-spec
;;   (v/map->spec sales-order-spec
;;                {:closed? false
;;                 :opts    [:form-sales-order/product
;;                           :form-sales-order/customer
;;                           :form-sales-order/sale-price
;;                           :form-sales-order/discount
;;                           :form-sales-order/price-approval
;;                           :form-sales-order/note
;;                           :form-sales-order/attachment
;;                           :form-sales-order/payment-utr
;;                           :form-sales-order/payment-amount]}))
;; (def form-commission-spec
;;   (v/map->spec commission-spec))

;; (def form-commission-complete-spec
;;   (v/map->spec commission-complete-spec))

;; (def form-qa-spec
;;   (v/map->spec qa-spec))

;; (def form-production-spec
;;   (v/map->spec production-spec))

;; (def form-approval-spec
;;   (v/map->spec approval-spec
;;                {:opts [:form-approval/note :form-approval/reports]}))

;; (def form-dispatch-spec
;;   (v/map->spec dispatch-spec))

;; (defn production-form [order-id]
;;   (-> {:id          :sales-order
;;        :title       "Add Production Report"
;;        :btn-label   "Start Production"
;;        :post        (str "/production/create/" order-id)
;;        :form-params {:hx-encoding "multipart/form-data"}}
;;       (form/form-box
;;        (form/input {:name "form-production/engine-sr"})
;;        (form/file {:name "form-production/engine-pic"})
;;        (form/input {:name "form-production/chassis-sr"})
;;        (form/file {:name "form-production/chassis-pic"}))))

;; (defn commission-form [order-id]
;;   (-> {:id          :sales-order
;;        :title       "Add Commission Details"
;;        :post        (str "/services/commission/assign-engineer/" order-id)
;;        :btn-label   "Assign Engineer"}
;;       (form/form-box
;;        (form/input {:name    "form-commission/service-engineer"
;;                     :label "Service Engineer Name"
;;                     :placeholder "Enter Engineer Name"}))))

;; (defn commission-completed-form [order-id]
;;   (-> {:id          :sales-order
;;        :title       "Add Commission Completion Details"
;;        :post        (str "/services/commission/complete/" order-id)
;;        :btn-label   "Submit"}
;;       (form/form-box
;;        (form/input {:name        "form-commission-complete/date"
;;                     :label       "Commissioning Complete Date"
;;                     :type        "date"
;;                     :placeholder "Commissing Complete Date"}))))

;; (defn qa-form [order-id]
;;   (-> {:id          :sales-order
;;        :title       "Add Qa Report"
;;        :post        (str "/qa/create/" order-id)
;;        :btn-label   "Submit QA Report"
;;        :form-params {:hx-encoding "multipart/form-data"}}
;;       (form/form-box
;;        (form/select {:name    "form-qa/result"
;;                      :options ["Success" "Failure"]})
;;        (form/file {:name "form-qa/report"}))))

;; (defn approval-form [order-id]
;;   [:div
;;    (-> {:id        :sales-order
;;         :title     "Add Any Remarks"
;;         :post      (str "/ui/form/order/approval/note/" order-id)
;;         :btn-label "Add Note"}
;;        (form/form-box
;;         (form/textarea {:name        "form-approval/note"
;;                         :placeholder "Enter any remarks"})))
;;    (-> {:id           :sales-order
;;         :title        "Add Reports"
;;         :post         (str "/ui/form/order/approval/report/" order-id)
;;         :btn-label "Add Reports"
;;         :form-params  {:hx-encoding "multipart/form-data"}}
;;        (form/form-box
;;         (form/file {:name "form-approval/reports"
;;                     :multiple "multiple"})))])

;; (defn ready-for-dispatch-form [order-id]
;;   (-> {:id          :sales-order
;;        :title       "Add Dispatch Details"
;;        :post        (str "/dispatch/details/add/" order-id)
;;        :btn-label   "Submit"
;;        :form-params {:hx-encoding "multipart/form-data"}}
;;       (form/form-box
;;        (form/input {:name "form-dispatch/dispatch-address"})
;;        (form/input {:name "form-dispatch/vehicle-number"})
;;        (form/input {:name "form-dispatch/driver-name"})
;;        (form/input {:name "form-dispatch/driver-number"})
;;        (form/file {:name "form-dispatch/driver-id-proof"}))))

;; (defn sales-order-form [params]
;;   (-> {:id          :sales-order
;;        :title       "Add New Sales Order"
;;        :buttons     [{:type :cancel
;;                       :href "/sales-order"}]
;;        :form-params {:hx-encoding "multipart/form-data"}}
;;       (merge params)
;;       (form-main-layout
;;        (form/select {:name      "form-sales-order/has-price-approval"
;;                      :label     "Has Price Approval?"
;;                      :hx-post   "/ui/form/sales-order/has-price-approval"
;;                      :hx-target "#sales-order-has-price-approval"
;;                      :options   ["Yes" "No"]})
;;        [:div.mb-3 {:id "sales-order-has-price-approval"}]
;;       ;;  (form/input {:name "form-sales-order/customer-ref-no"})
;;        [:div.mb-3 {:id "sales-order-note"}]
;;        (form/button {:hx-post   "/ui/form/sales-order/note"
;;                      :hx-target "#sales-order-note"}
;;                     "Add Notes" [:span.material-icons.ml-2 "note_add"])
;;        (form/input {:name  "form-sales-order/tnc"
;;                     :label "Terms & Conditions"})
;;        [:div.mb-3 {:id "sales-order-attachment"}]
;;        (when (util/has? (:accesses params) :ui/form-sales-order-files)
;;          (form/button {:hx-post   "/ui/form/sales-order/attachment"
;;                        :hx-target "#sales-order-attachment"}
;;                       "Attach Files" [:span.material-icons.ml-2 "attach_file"]))
;;        (form/list-field
;;         {:data   [:advance :before-delievery :balance]
;;          :label  "Payment Terms"
;;          :render (fn [s]
;;                    {:name          (str "form-sales-order/" (name s))
;;                     :placeholder   "Enter Amount (INR/USD)"
;;                     :key-alignment "start"
;;                     :label         (keyword->title s)})})
;;        [:div.mb-3 {:id "sales-order-payment-details"}]
;;        (form/button {:hx-post   "/ui/form/sales-order/payment-details"
;;                      :hx-target "#sales-order-payment-details"}
;;                     "Add Payment Details" [:span.material-icons.ml-2 "note_add"]))))

;; (defn order-block [accesses order]
;;   (let [m           #(when % (-> % t/date str))
;;         status      (:order/status order)
;;         o-id        (:xt/id order)
;;         btn-name    (case status
;;                       :new "Approve Order"
;;                       :approved "Start Production"
;;                       :production-started "Production Complete"
;;                       :production-completed "Start QA"
;;                       :qa-success "Ready for Delivery"
;;                       :qa-failure "Start Qa Corrections"
;;                       :qa-corrections-started "Finish Qa Corrections"
;;                       :qa-corrections-finished "Start Qa"
;;                       :delivery-approval "Approve Delivery"
;;                       :ready-for-dispatch "Dispatch"
;;                       :dispatched "Commission"
;;                       :commissioning "Commission Complete"
;;                       :commissioning-completed "Deliver"
;;                       :delivered "Close"
;;                       "OK")
;;         btns        [{:params {:class   "is-danger is-light is-small"
;;                                :hx-post (str "/ui/order/" o-id "/update-status/cancel")}
;;                       :body   [:span.is-flex.is-align-items-center.is-justify-content-center
;;                                [:span.material-icons.mr-2 "cancel"] "CANCEL"]}
;;                      {:params {:class   "is-ok is-light is-small"
;;                                :hx-post (str "/ui/order/" o-id "/update-status/ok")}
;;                       :body   [:span.is-flex.is-align-items-center.is-justify-content-center
;;                                [:span.material-icons.mr-2 "check_circle"] btn-name]}]
;;         has-access? (->> status
;;                          name
;;                          (str "ui-order/")
;;                          keyword
;;                          (util/has? accesses))
;;         cancel-btn  [(first btns)]]
;;     (el/flex-message
;;      {:title   status
;;       :buttons (case status
;;                  :cancelled []
;;                  :closed []
;;                  :approved cancel-btn
;;                  :qa-started cancel-btn
;;                  :commissioning cancel-btn
;;                  :commissioning-completed cancel-btn
;;                  :ready-for-dispatch cancel-btn
;;                  (when has-access? btns))
;;       :content {"Status"                status
;;                 "Product"               (-> order :order/product :product/name)
;;                 "Customer Ref No"       (-> order :order/customer :customer/ref-no)
;;                 "Order Ref No"          (-> order :order/ref-no)
;;                 "Production Start Date" (-> order :order/production :production/cat m)
;;                 "Production End Date"   (-> order :order/production :production/completed-at m)
;;                 "Terms & Condition"     (-> order :order/tnc)
;;                 "Price approval"        (when (:order/price-approval order) "Approved")
;;                 "Sale Price"            (:order/sale-price order)
;;                 "Date"                  (-> order :order/cat m)
;;                 "Qa Start Date"         (-> order :order/qa :qa/cat m)
;;                 "Qa End Date"           (-> order :order/qa :qa/completed-at m)
;;                 "T&C"                   (:order/tnc order)}})))

;; (defn form-to-render [accesses order]
;;   (let [status   (:order/status order)
;;         o-id     (:xt/id order)
;;         cmp-name (case status
;;                    :approved :form/production-report
;;                    :qa-started :form/qa-report
;;                    :ready-for-dispatch :form/dispatch
;;                    :commissioning :form/commission
;;                    :commissioning-completed :form/commission-completed
;;                         ;;  :new :form/approval
;;                         ;;  :delivery-approval :form/approval
;;                    nil)
;;         form     (some #{cmp-name} accesses)]
;;     (case form
;;       :form/production-report (production-form o-id)
;;       :form/qa-report (qa-form o-id)
;;       :form/approval (approval-form o-id)
;;       :form/commission (commission-form o-id)
;;       :form/commission-completed (commission-completed-form o-id)
;;       :form/dispatch (ready-for-dispatch-form o-id)
;;       nil)))

;; (defn att-table [title data]
;;   (when-not (empty? data)
;;     (el/table
;;      {:title         title
;;       :content       data
;;       :table-class   "is-fullwidth"
;;       :headers       ["Name" "File type" "Date"]
;;       :row-component (fn [o]
;;                        (let [src                (:attachment/src o)
;;                              {:keys [_ ext]} (util/file-info src)]
;;                          [:tr
;;                           [:th [:a {:href   (str "/ui/attachment/view/" (:xt/id o))
;;                                     :target "blank"} (:xt/id o)]]
;;                           [:td ext]
;;                           [:td (-> o :attachment/cat t/date str)]]))})))

;; (defn sale-order [params {:keys [order] :as data}]
;;   (-> {:id      :sales-order
;;        :title   "Order"
;;        :buttons [{:label "Add"
;;                   :type  :add
;;                   :href  "/sales-order/form"}]}
;;       (merge params)
;;       (main-layout
;;        [:div
;;         (order-block (:accesses params) order)
;;         (form-to-render (:accesses params) order)

;;         (when-not (empty? (:order-notes data))
;;           [:div.mx-2
;;            (let [notes (:order-notes data)
;;                  cn (count notes)]
;;              (el/columns
;;               {:content notes
;;                :is-full? (= cn 1)
;;                :is-half? (< 1 cn 10)
;;                :is-one-quarter? (>= cn 10)
;;                :render (fn [n]
;;                          (el/message {:key (str (:xt/id n))
;;                                       :class "is-dark"
;;                                       :title (str "Note created on " (-> n :note/cat t/date str))
;;                                       :body [:p (:note/text n)]}))}))])

;;         (when-not (util/has? (:user/roles (:user params)) :production)
;;           [:div
;;            (att-table "Order Attachments" (:order-attachments data))
;;            (att-table "Qa Reports" (:qa-attachments data))
;;            (att-table "Production Reports" (:production-attachments data))])

;;         (when-not (empty? (:payments data))
;;           (el/table
;;            {:title "Payments"
;;             :content       (:payments data)
;;             :table-class   "is-fullwidth"
;;             :headers       ["" "UTR No" "Amount" "Date" "Confirm"]
;;             :row-component (fn [pa]
;;                              (let [id (:xt/id pa)]
;;                                [:tr {:class (if (:payment/confirmed? pa)
;;                                               "has-background-primary-light" "")}
;;                                 [:td]
;;                                 [:th [:a {:href (str "/payments/payment/" id)} (:payment/utr pa)]]
;;                                 [:td (str "Rs. " (:payment/amount pa))]
;;                                 [:td (-> pa :payment/cat t/date str)]
;;                                 [:td [:button.button.is-success.is-light.is-small
;;                                       {:hx-post  (str "/ui/confirm-payment/" id)
;;                                        :disabled (or (:payment/confirmed? pa)
;;                                                      (false? (util/has? (:accesses params) :ui/payment-approval)))}
;;                                       [:span.material-icons "check_circle"]]]]))
;;             :last-row [:tr [:th "Total"] [:td] [:td (:total-payments data)] [:td] [:td]]}))])))

;; (defn sales-order [params orders]
;;   (-> {:id      :sales-order
;;        :buttons [{:label "Add"
;;                   :type  :add
;;                   :href  "/sales-order/form"}]}
;;       (merge params)
;;       (main-layout
;;        (let [m #(when % (-> % t/date str))
;;              production? (-> params :user :user/roles (util/has? :production))
;;              headers (if production?
;;                        ["Order Ref No"
;;                         "Customer Ref No"
;;                         "Production Start"
;;                         "Product"
;;                         "Status"
;;                         "Production End"]
;;                        ["Order Ref No"
;;                         "Customer Ref No"
;;                         "Product"
;;                         "Status"])]
;;          [:div.container
;;           (el/table
;;            {:content       orders
;;             :table-class   "is-fullwidth"
;;             :headers       headers
;;             :row-component (fn [{:keys [xt/id] :as o}]
;;                              (->> [:tr {:class (case (:order/status o)
;;                                                  :closed "has-background-primary-light"
;;                                                  :cancelled "has-background-warning-light"
;;                                                  :stuck "has-background-danger-light"
;;                                                  "")}
;;                                    [:th [:a {:href (str "/sales-order/order/" id)} (:order/ref-no o)]]
;;                                    [:td (-> o :order/customer :customer/ref-no)]
;;                                    (when production?
;;                                      [:td (-> o :order/production :production/cat m)])
;;                                    [:td (-> o :order/product :product/name)]
;;                                    [:td (-> o :order/status)]
;;                                    (when production?
;;                                      [:td (-> o :order/production :production/completed-at m)])]
;;                                   (remove nil?)
;;                                   (into [])))})]))))

(defn home [& args]
  (->> (base-layout
        {}
        (el/card {:title "HELLO!!"
                  :content args}))))
