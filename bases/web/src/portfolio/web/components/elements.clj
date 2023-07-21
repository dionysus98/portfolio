(ns portfolio.web.components.elements
  (:require [portfolio.ui.interface :as ui]
            [portfolio.util.interface :refer [remove-nil]]))

(defn flexbox [& body]
  [:div.is-align-items-center.is-justify-content-center.is-flex-wrap-wrap.is-flex-direction-row
   body])

(defn button [params]
  [:button.button params (:text params)])

(defn dropdown [params]
  [:div.dropdown.is-active
   [:div.dropdown-trigger
    [:button.button {:aria-haspopup "true" :aria-controls "dropdown-menu"}
     [:span (:default params)]
     [:span.icon.is-small
      [:i.fas.fa-angle-down {:aria-hidden "true"}]]]]
   [:div.dropdown-menu {:id "dropdown-menu"
                        :role "menu"}
    [:div.dropdown-content
     (:data params)]]])

(defn content-header
  "Params : {:title, :value }"
  [params]
  [:div.is-flex.is-justify-content-space-between.is-align-items-center
   [:p.title.is-4 (:title params)]])

(defn card
  "Params: {
   :title @string
   :content @hiccup-hmtl
   :actions @vector-of-maps; eg: [{:label @string :on-click @dispatch-func}]
   }"
  [params]
  [:div.card
   [:header.card-header
    [:p.card-header-title (:title params)]
    [:button.card-header-icon {:aria-label "more options"}
     [:span.icon.material-icons "keyboard_arrow_down"]]]
   [:div.card-content
    [:div.content (:content params)]]
   [:footer.card-footer
    (for [action (:actions params)]
      [:a
       {:on-click (:on-click action)
        :key (:label action)
        :class "card-footer-item"
        :href (:href action)}
       (:label action)])]])

(defn horizontal-level [contents]
  [:nav.level.is-mobile
   (for [content contents]
     [:div.level-item.has-text-centered {:key (:label content)}
      [:div
       [:p.heading (:label content)]
       [:p.title (:value content)]]])])

(defn hero [params & body]
  [:section.hero params
   [:div.hero-body.is-justify-content-center
    body]])

(defn table [{:keys [content headers row-component title style table-class] :as params}]
  (if (not-empty content)
    [:aside.menu {:style (or style {:margin "1rem 1rem"})}
     [:p.menu-label title]
     [:table.table {:class table-class}
      [:thead
       [:tr
        (map
         (fn [th]
           [:th {:key th} [:abbr {:title th} th]])
         headers)]]
      [:tbody
       (:first-row params)
       (doall (map row-component content))
       (:last-row params)]]]
    (hero {:class "is-fullheight"}
          [:div.is-flex.is-flex-direction-row.is-justify-content-center.is-flex-direction-column
           (ui/style {:transform "scale(2)"})
           [:span.material-icons.has-text-centered "warning"]
           [:p "No Content to display"]])))

(defn grouped-tags [tags]
  [:div.field.is-grouped.is-grouped-multiline
   (for [tag tags]
     [:div.control {:id (:value tag)}
      [:div.tags.has-addons
       [:a.tag.is-link (:label tag)]
       [:a.tag.is-delete {:_ (str "on click add .display-none to #" (:value tag))}]]])])

(defn message [{:keys [title buttons body class remove-header?]}]
  [:article.message {:class (or class "is-link")}
   (when-not remove-header?
     [:div.message-header
      [:p title]
      (when buttons
        [:div.is-flex.is-align-items-center.is-justify-content-space-between
         (for [button buttons]
           [:button.button.ml-2 (merge {:aria-label "delete"} (:params button))
            (:body button)])])])
   [:div.message-body
    body]])

(defn sorted-flex-message [{:keys [content] :as params}]
  (let [valid-content (->> content
                           (sort-by (:sort-by params))
                           (map second)
                           (filter #(-> % first val nil? false?)))]
    (-> params
        (merge
         {:body [:div.is-flex.is-flex-direction-row.is-justify-content-center
                 [:div
                  (->> valid-content
                       (map #(-> % keys first))
                       (map (fn [v]
                              [:p {:key v} [:strong v " "]])))]
                 [:div {:style {:margin-left "1.5rem"}}
                  (->> valid-content
                       (map #(-> % vals first))
                       (map (fn [v]
                              [:p {:key v} v])))]]})
        message)))

(defn flex-message [{:keys [content] :as params}]
  (let [valid-content (remove-nil content)]
    (-> params
        (merge
         {:body [:div.is-flex.is-flex-direction-row.is-justify-content-center
                 [:div
                  (->> valid-content
                       keys
                       (map (fn [v]
                              [:p {:key v} [:strong v " "]])))]
                 [:div {:style {:margin-left "1.5rem"}}
                  (->> valid-content
                       vals
                       (map (fn [v]
                              [:p {:key v} v])))]]})
        message)))

(defn columns [{:keys [class render content is-half? is-one-quarter? is-full?]}]
  [:div.columns.is-desktop.is-multiline (when class {:class class})
   (for [item content]
     [:div.column {:class (cond
                            is-full? "is-full"
                            is-half? "is-half"
                            is-one-quarter? "is-one-quarter"
                            :else "auto")}
      (render item)])])