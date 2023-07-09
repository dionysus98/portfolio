(ns portfolio.web.lib.ui.states)

(def dashboard-items
  [:ui-panel/products
   :ui-panel/customers
   :ui-panel/sales-order
   :ui-panel/services
   :ui-panel/price-approval
   :ui-panel/payments
  ;;  :ui-panel/notifications
   ])

#_(def schedule-stages
    [{:stage     1
      :name      :price-approval-request
      :dependent :sales}
     {:stage     2
      :name      :price-approved
      :dependent :director}
     {:stage     3
      :name      :new-sales-order-creation
      :dependent :sales}
     {:stage     4
      :name      :approved
      :dependent :director}
     {:stage     5
      :name      :production-started
      :dependent :production}
     {:stage     6
      :name      :production-completed
      :dependent :production}
     {:stage     7
      :name      :qa-started
      :dependent :qa}
     {:stage     8
      :name      :qa-failure
      :dependent :qa}
     {:stage     8.1
      :name      :qa-corrections-started
      :dependent :production}
     {:stage     8.2
      :name      :qa-corrections-finished
      :dependent :production}
     {:stage     9
      :name      :qa-success
      :dependent :qa}
     {:stage     10
      :name      :delivery-approval-request
      :dependent :qa}
     {:stage     11
      :name      :ready-for-dispatch
      :dependent :director}
     {:stage     12
      :name      :dispatched
      :dependent [:order-processor :sales]}
     {:stage     13
      :name      :delivered
      :dependent [:order-processor :sales]}])

(def schedule-stages
  [{:stage     1
    :name      :new
    :dependent :sales}
   {:stage     2
    :name      :approved
    :dependent :director}
   {:stage     3
    :name      :production-started
    :dependent :production}
   {:stage     4
    :name      :production-completed
    :dependent :production}
   {:stage     5
    :name      :qa-started
    :dependent :qa}
   {:stage     6
    :name      :qa-failure
    :dependent :qa}
   {:stage     6.1
    :name      :qa-corrections-started
    :dependent :production}
   {:stage     6.2
    :name      :qa-corrections-finished
    :dependent :production}
   {:stage     7
    :name      :qa-success
    :dependent :qa}
   {:stage     8
    :name      :delivery-approval-request
    :dependent :qa}
   {:stage     9
    :name      :ready-for-dispatch
    :dependent :director}
   {:stage     10
    :name      :dispatched
    :dependent [:order-processor :sales]}
   {:stage     11
    :name      :delivered
    :dependent [:order-processor :sales]}])