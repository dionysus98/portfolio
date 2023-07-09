(ns portfolio.message.core
  (:require [clojure.data :as data]
            [portfolio.message.interface.spec]
            [clojure.string :as str]
            [hiccup.page :refer [html5]]
            [io.pedestal.log :as log]
            [portfolio.email.interface :as email]
            [tick.core :as t]
            [xtdb.api :as xt]))

(defn outline [recepient & body]
  [{:type "text/html"
    :content (html5
              [:body
               [:header
                "Dear " (str/capitalize recepient) ","]
               [:main body]
               [:footer
                [:p "Thank you" [:br] "Team Cleanland"]
                [:span [:i "Note: Do not reply to this mail as this is a system generated mail. For any queries regarding your order kindly call us at 8154002326 or email is at insidesales@thtpl.com"]]]])}])

(defn order-purchased [order]
  {:subject (str "Cleanland - New Purchase Order Received-P.O No. " (:order/ref-no order))
   :body (outline
          "Customer"
          [:p "Thank you for your purchase. We have received your purchase order and we will begin production shortly."]
          [:table {:border "1"}
           [:tr {:bgcolor "skyblue"}
            [:td "Machine Model & Name"]
            [:td "Order Placed Date"]
            [:td "Order Reference Number"]
            [:td "Estimated Delivery Date"]]
           [:tr
            [:td {:bgcolor "skyblue"} (-> order :order/product :product/name)]
            [:td (-> order :order/cat t/date str)]
            [:td (-> order :order/ref-no)]
            [:td (->> order :order/schedule vals (apply +))]]])})

(defn order-approval [order]
  {:subject (str "New PO No. " (:order/ref-no order) " _ Order Approval")
   :body (outline
          "Sir"
          [:p "We'd like to request that you please approve the Order reference number so that we can begin the production process."])})

(defn price-approval [order]
  (when-not (-> order :order/price-approval :price-approval/approved?)
    {:subject (str "Price Approval-P.O No. " (:order/ref-no order))
     :body (outline
            "Sir"
            [:p "We'd like to request that you please approve the Price so that we can place the sales order."])}))

(defn payment-approval [payment]
  {:subject (str/join " " ["Payment Approval-"
                           (-> payment :payment/order :order/ref-no)
                           (-> payment :payment/order :order/customer :customer/ref-no)
                           (-> payment :payment/utr)])
   :body (outline
          "Accounts Team"
          [:p "Kindly please cross check the amount received and approve the same on OPM system."])})

(defn production-started [order]
  (case (:receiver order)
    :customer {:subject (str "Cleanland - Production Started- P.O No. " (:order/ref-no order))
               :body (outline
                      "Customer"
                      [:p "We have begun production and anticipate completing the order as per p.o terms. We will notify you as soon as the order is complete."])}
    :production {:subject (str "Production Started- P.O No. " (:order/ref-no order))
                 :body (outline
                        "Sir"
                        [:p "The PO has been approved. Kindly please login and begin the production process."])}
    {:subject (str "Production Started- P.O No. " (:order/ref-no order))
     :body (outline
            "Sir"
            [:p "The PO has been approved."])}))

(defn production-completed [order]
  (case (:receiver order)
    :customer {:subject (str "Cleanland " (-> order :order/product :product/name) "-Production Completed/QC initiated")
               :body (outline
                      "Customer"
                      [:p "We have completed the production of your machine/machines and QC process is initiated."])}

    :service {:subject (str "Cleanland " (-> order :order/product :product/name) "-Production Completed/QC initiated")
              :body (outline
                     "Customer"
                     [:p "The production of order reference number is completed and machine is ready  for dispatch soon. Request you to please prepare for the training and commissioning of the machine, Visit the OPMS portal for more details of this order."])}

    {:subject (str (-> order :order/product :product/name) "-Production Completed/QC initiated")
     :body (outline
            "Customer"
            [:p "The production of machine is completed and QC process is initiated."])}))


(defn ready-for-dispatch [order]
  (case (:receiver order)
    :customer {:subject (str " Cleanland " (-> order :order/product :product/name) "- Ready for Dispatch")
               :body (outline
                      "Customer"
                      [:p "Your machine is ready for dispatch."]
                      [:p "Request you to arrange pick up from Cleanland factory OR wait for dispatch from Cleanland, whichever was agreed upon as per P.O."])}
    :dispatch {:subject (str (-> order :order/product :product/name) "- Dispatch Approved")
               :body (outline
                      "Team"
                      [:p "The machine dispatch is approved. Request you to please fill the required detail of this order in the OPM system"])}
    {:subject (str (-> order :order/product :product/name) "- Dispatch Approved")
     :body (outline
            "Sir"
            [:p "The machine dispatch is approved."])}))

(defn payment-pending [payment]
  (when (= (:payment/status payment) :outstanding)
    {:subject (str "Cleanland - Payment for " (-> payment :payment/order :order/product :product/name))
     :body (outline
            "Customer"
            [:p "Requesting you to please settle advance/balance payment against your order to initiate dispatch process."
             [:br] "Kindly ignore this mail if already cleared."])}))

(defn payment-completed [payment]
  {:subject (str "Cleanland " (-> payment :payment/order :order/product :product/name) "- Payment Status")
   :body (outline
          "Customer"
          [:p "Thank you for completing the payment. We will soon dispatch the machine with full instructions."])})

(defn dispatch-approval [order]
  {:subject (str "Dispatch Approval. P.O No. " (:order/ref-no order))
   :body (outline "Sir"
                  [:p "The Quality inspection is completed and machine is ready for dispatch. Request you to please approve the Dispatch of this order."])})

(defn dispatched [order]
  (case (:receiver order)
    :customer {:subject (str "Cleanland- Machine Dispatch Done- P.O No. " (:order/ref-no order))
               :body (outline
                      "Customer"
                      [:p "The machine had been dispatched successfully. Please find attached details for your reference."]
                      [:p "Product - " (-> order :order/product :product/name)]
                      [:p "Vehicle No - " (-> order :order/vehicle-number)]
                      [:p "Driver Mobile No - " (-> order :order/driver-number)])}
    :service {:subject (str "Cleanland- Machine Dispatch Done- P.O No. " (:order/ref-no order))
              :body (outline
                     "Team"
                     [:p "The machine is dispatched. Request you to please assign the engineer for commissioning & operator training."]
                     [:p "Also update on the service portal once our technician is assigned and completes the training in the OPM system."])}
    {:subject (str "Machine Dispatch Done- P.O No. " (:order/ref-no order))
     :body (outline
            "Sir"
            [:p "The machine had been dispatched successfully."])}))

(defn resolve-message [doc status]
  (case doc
    :order (case status
             :dispatched [dispatched]
             :delivery-approval [dispatch-approval]
             :ready-for-dispatch [ready-for-dispatch]
             :production-started [price-approval production-started]
             :production-completed [production-completed]
             :new [order-purchased order-approval]
             nil)
    :payment (case status
               :outstanding [payment-pending]
               :confirmation [payment-approval]
               :completed [payment-completed]
               nil)))

(defn build-message [node notification]
  (log/info :msg (str "Building Message for " notification))
  (try
    (let [pull #(xt/entity (xt/db node) %)
          nid (:xt/id notification)
          doc-id (:notification/doc-id notification)
          receiver-type (:notification/receiver-type notification)
          receiver (pull (:notification/receiver notification))
          data (-> (xt/db node (:notification/cat notification))
                   (xt/entity-history doc-id :desc {:with-docs? true}))
          apply-diff? (> (count data) 1)
          no-diff? (= (count data) 1)
          diff (cond
                 apply-diff? (->> data
                                  (take 2)
                                  (map :xtdb.api/doc)
                                  (apply data/diff))
                 no-diff?  (-> data first :xtdb.api/doc)
                 :else nil)
          updated? (= (count diff) 3)
          created? (map? diff)
          doc (cond
                updated? (merge (last diff) (first diff))
                created? diff)
          email (or (:user/email receiver) (:customer/email receiver))
          order #(-> (update % :order/product pull)
                     (assoc :status (:order/status %)
                            :entity/type :order))
          payment #(-> (update % :payment/order pull)
                       (update-in [:payment/order :order/product] pull)
                       (update-in [:payment/order :order/customer] pull)
                       (assoc :status (:payment/status %)
                              :entity/type :payment))
          data (cond
                 (inst? (:order/cat doc)) (order doc)
                 (inst? (:payment/cat doc)) (payment doc))
          resolved (resolve-message (:entity/type data) (:status data))
          build-email-body (fn [build]
                             (merge (build (assoc data :receiver receiver-type))
                                    {:notification/id nid
                                     :to email}))]
      (doall (map build-email-body resolved)))
    (catch Exception e (log/error :msg e))))

(defn send->update [msg-body]
  (log/info :msg (str "Send and update: " msg-body))
  (let [nid (:notification/id msg-body)
        email-body (dissoc msg-body :notification/id)]
    (try
      (email/send-email! email-body)
      (log/info :msg (str "Sent mail: " email-body))
      (log/info :msg (str "update-notification: " nid))
      (catch Exception e (log/info :msg e)))))