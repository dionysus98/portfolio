(ns portfolio.web.components.form-components
  (:require [clojure.string :as str]
            [portfolio.util.interface :refer [keyword->str keyword->title remove-nil]]
            [portfolio.ui.interface :refer [style]]))

#_(defn serailize [label value data]
    (let [tns #(hash-map :label (-> % first label str)
                         :value (-> % first value str))]
      (map tns data)))

(defn serailize [label value data]
  (let [tns #(hash-map :label (-> % label str)
                       :value (-> % value str))]
    (mapv tns data)))

(defn label [params]
  (or (:label params)
      (-> params
          :name
          (str/split #"/")
          second
          keyword->str)))

(defn fdt [params]
  (let [field-name (-> params :name (str/replace "/" "-"))
        id (or (:hx-target params)
               (str "validation-" field-name))
        validate? (:validate? params)]
    {:name field-name
     :id id
     :validate? validate?}))

(defn input-field [parent-params field-data]
  [:input (-> {:type "text"
               :class "input"
               :placeholder (or (:placeholder parent-params)
                                (str "Enter " (label parent-params)))
               :hx-trigger "keyup delay:1000ms"
               :hx-sync "closest form:abort"
               :hx-post (when-not (false? (:validate? field-data))
                          (str "/ui/validation/" (:name parent-params)))}
              (assoc :hx-target (str "#" (:id field-data)))
              (merge parent-params)
              remove-nil)])

(defn input [params]
  (let [data (fdt params)]
    [:div.field
     [:label.has-text-weight-semibold {:class (:label-style params)} (label params)]
     [:div.control
      (input-field params data)]
     (when-not (false? (:validate? data))
       [:div {:id (:id data)}])]))

(defn key-value-input [{:keys [key-alignment] :as params}]
  (let [data (fdt params)]
    [:div.field
     [:div.is-flex.is-align-items-center.is-justify-content-space-between
      [:div (style {:text-align (or key-alignment "center")
                    :width "25%"})
       [:label {:class (:label-style params)} (label params)]]
      [:div.control (style {:width "75%"})
       (input-field params data)]]
     (when-not (false? (:validate? data))
       [:div {:id (:id data)}])]))

(defn list-field [{:keys [data label render]}]
  [:div.field
   [:label.has-text-weight-semibold label]
   (for [s data]
     (let [res (render s)]
       (if (map? res)
         (key-value-input res)
         res)))])

(defn checkbox-item [params data label value]
  [:div.field
   [:div.control
    [:label.checkbox-container
     label
     [:input (-> {:type "checkbox"
                  :value value
                  :hx-post (when-not (false? (:validate? data))
                             (str "/ui/validation/" (:name params)))}
                 (assoc :hx-target (str "#" (:id data)))
                 (merge params)
                 (dissoc :options)
                 remove-nil)]
     [:span.checkbox-checkmark]]]])

(defn checkbox [params]
  (let [data (fdt params)]
    [:div.field
     [:label.has-text-weight-semibold (label params)]
     [:div.control (style {:padding "0.75rem 0.5rem"
                           :background-color "#fefefe"})
      #_(checkbox-item params data "Select")
      (for [option (:options params)]
        (if (map? option)
          (checkbox-item params data (:label option) (:value option))
          (checkbox-item params data option option)))]
     (when-not (false? (:validate? data))
       [:div {:id (:id data)}])]))

(defn select [params]
  (let [field-name (-> params :name (str/split #"/") second)
        id (or (:hx-target params)
               (str "validation-" field-name))
        validate? (:validate? params)]
    [:div.field
     [:label.has-text-weight-semibold (label params)]
     [:div.control
      [:div.select
       [:select (-> {:hx-post (when-not (false? validate?)
                                (str "/ui/validation/" (:name params)))}
                    (assoc :hx-target (str "#" id))
                    (merge params)
                    (dissoc :options)
                    remove-nil)
        [:option {:value "select"} "Select"]
        (for [option (:options params)]
          (if (map? option)
            [:option {:value (:value option)
                      :selected (= (:value option) (:value params))}
             (:label option)]
            [:option {:value option
                      :selected (= option (:value params))}
             option]))]]]
     (when-not (false? (:validate? params))
       [:div {:id id}])]))

(defn file [params]
  (let [data (fdt params)]
    [:div.field
     [:label.has-text-weight-semibold {:class (:label-style params)} (label params)]
     [:div.file.has-name
      [:label.file-label
       (-> {:class "file-input"
            :type "file"
            :hx-post (str "/ui/form/file/" (:name params))
            :hx-trigger "change"
            :hx-target (str "#ui-" (:name data))}
           (merge params)
           (input-field data))
       [:span.file-cta
        [:span.file-icon.material-icons "file_upload"]
        [:span.file-label (str "Choose a " (if (:multiple params) "files" "file") "...")]]
       [:span.file-name "File"]]]
     [:div.mt-3 {:id (str "ui-" (:name data))}]
     (when-not (false? (:validate? data))
       [:div {:id (:id data)}])]))

(defn textarea [params]
  (let [data (fdt params)]
    [:div.field
     [:label.has-text-weight-semibold {:class (:label-style params)} (label params)]
     [:div.control
      [:textarea.textarea (-> {:hx-sync "closest form:abort"
                               :hx-post (when-not (false? (:validate? data))
                                          (str "/ui/validation/" (:name params)))}
                              (merge params)
                              (assoc :hx-target (str "#" (:id data)))
                              remove-nil)]]
     (when-not (false? (:validate? data))
       [:div {:id (:id data)}])]))

(defn dropdown [{:keys [id active? placeholder req-uri content]}]
  [:div.dropdown {:hx-post req-uri
                  :hx-swap "outerHTML"
                  :id id
                  :class (if active? "is-active" "")}
   [:div.dropdown-trigger
    [:button.button {:aria-haspopup "true", :aria-controls "dropdown-menu2"}
     [:span placeholder]
     [:span.icon.is-small
      [:i {:class "fas fa-angle-down", :aria-hidden "true"}]]]]
   [:div.dropdown-menu {:role "menu"}
    [:div (style {:margin-left "10rem"})
     [:div.dropdown-content
      (for [c content]
        [:div.dropdown-item {} c])]]]])

(defn button [params & body]
  [:button.button.is-info.mr-3.is-light
   (merge {:type "button" :hx-swap "innerHTML" :hx-sync "closest form:abort"} params)
   body])

(defn form-box [{:keys [id title post btn-label show-title?]
                 :as   params} & form-body]
  (let [slug        (name id)
        html-err-id (str slug "-form-error")
        hx-post     (or post (str "/" slug "/create"))]
    [:div.box.has-background-light.m-5.p-4
     (when-not (false? show-title?)
       [:p.subtitle (or title (keyword->title id))])
     [:form (merge {:hx-post   hx-post
                    :hx-target (str "#" html-err-id)}
                   (:form-params params))
      form-body
      [:article.message.is-danger {:id html-err-id}]
      (if btn-label
        [:button.button.is-info btn-label]
        [:button.button.is-info "Submit" [:span.material-icons.ml-2 "post_add"]])]]))