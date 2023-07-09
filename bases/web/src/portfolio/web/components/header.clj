(ns portfolio.web.components.header
  (:require [portfolio.util.interface :refer [keyword->str]]
            [portfolio.web.lib.ui.htmx :refer [style]]
            [portfolio.web.lib.ui.styles :refer [colors]]))

(defn header [opts]
  (let [active-menu? (:active-menu? opts)
        auth? (not (false? (:authenticated-user? opts)))
        mto #(merge % {:hx-post (str "/ui/header/" (not active-menu?))
                       :hx-target "#main-header"})
        nav-item (fn [v]
                   [:a.navbar-item {:href (str "/" (name v))}
                    (keyword->str v)])
        class (if active-menu? "is-active" "")]
    [:nav.navbar (merge (style {:background-color (:primary colors)
                                :box-shadow "0px -0.5px 10px 1.45px #222"})
                        {:id "main-header"
                         :role "navigation"
                         :class "is-fixed-top"
                         :aria-label "main navigation"})

     [:div.navbar-brand
      [:a.navbar-item {:href "/"}
       ;; [:img {:width "70rem" :src "/img/white_bars.svg"}]
       [:img {:style "max-height: 50px;" :src "/img/cleanland.png"}]]
      (when auth?
        [:div (style {:position "fixed" :right "3px"})
         [:a.navbar-burger (merge (style {:color (:accent colors)
                                          :height "4.2rem"})
                                  (mto {:class class
                                        :role "button"
                                        :aria-label "menu"
                                        :aria-expanded "false"}))
          [:span {:aria-hidden "true"}]
          [:span {:aria-hidden "true"}]
          [:span {:aria-hidden "true"}]]])]
     (when auth?
       [:div.navbar-menu {:class class}
        [:div.navbar-end
         [:div.navbar-item
          [:p.has-text-info (-> opts :user :user/username)]]]
        [:div.navbar-start
         [:div.navbar-item.has-dropdown.is-hoverable
          [:a.navbar-link.is-arrowless (style {:color (:accent colors)})
           [:span.material-icons.info.mt-1 "more_vert"]]
          [:div.navbar-dropdown
           (for [item (:dashboard-items opts)]
             (nav-item item))]]
         [:div.navbar-item {:hx-post "/user/logout"}
          [:a.is-danger.is-light
           [:span.material-icons.danger.mt-1 "logout"]]]]])]))
