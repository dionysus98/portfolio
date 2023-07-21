(ns portfolio.web.components.layouts
  (:require [portfolio.util.interface :refer [keyword->title] :as util]
            [portfolio.web.components.form-components :as form]
            [portfolio.web.components.header :refer [header]]
            [portfolio.web.lib.ui.htmx :refer [page]]
            [portfolio.ui.interface :refer [style]]
            [portfolio.web.lib.ui.states :refer [dashboard-items]]
            [portfolio.web.lib.ui.styles :refer [colors]]
            [portfolio.web.components.corner-bubble :refer [corner-bubble]]))

(defn base-layout [opts & body]
  (if (map? opts)
    (->> [:body.container-fluid
          (corner-bubble []  "home")
          (corner-bubble [:left] "send")
          (corner-bubble [:top] "code")
          (corner-bubble [:top :left] "read_more")
          [:div (style :margin "0"
                       :padding "0"
                       :position "relative"
                       :z-index "9999")
           body]]
         (page opts))
    (apply base-layout {} opts body)))

(defn side-panel [di selected-id]
  (let [li (fn [v]
             (as-> (name v) slug
               [:li
                [:a {:href (str "/" slug)
                     :class (if (= selected-id v) "is-active" "")}
                 (util/slug->title slug)]]))]
    [:aside.menu (style {:padding "0.45rem 0.65rem"
                         :min-height "100vh"})
     [:p.menu-label (style {:margin-top "0.75rem"}) "Dashboard"]
     [:ul.menu-list {:id "dashboard"}
      (map li di)]]))

(defn main-panel [{:keys [buttons id title authorized?]} & body]
  [:main (style {:padding "0 0.85rem"})
   [:div.is-flex.is-justify-content-space-between.is-align-items-center
    [:div [:h2.subtitle (or title (keyword->title id))]]
    [:div.is-flex.is-align-items-center
     (when (and authorized? (not-empty buttons))
       (for [button buttons]
         [:a.is-link.is-light.ml-2
          (merge button {:class (if (= (:type button) :cancel) "" "button")})
          (:label button)
          [:span.material-icons.ml-2 (case (:type button)
                                       :cancel "cancel"
                                       :edit "edit"
                                       :add "add_box"
                                       nil)]]))]]
   [:div.mt-4 {:id "main-panel-form"}]
   body])

#_{:clj-kondo/ignore [:unused-binding]}
(defn main-layout [{:keys [base-opts id title form? accesses] :as opts} & body]
  (let [di (->> dashboard-items
                (util/common-elements accesses)
                (map #(-> % name keyword))
                sort)]
    (base-layout
     base-opts
     (header (merge opts {:dashboard-items di :active-menu? false}))
     [:div {:style {:padding-top "4.9rem"
                    :margin      "0"}}
      [:div.columns
       [:div.column.is-2
        (merge
         (style
          {:padding "0 0 0 1.45rem"
           :background-color (str (:light-blue colors) "85")})
         {:id "side-panel-dashboard"})
        (side-panel di id)]
       [:div.column (style {:min-height "100vh"
                            :box-shadow "-5px 0 12px -10px #111"
                            :background (:light-background colors)})
        (main-panel opts body)]]])))

(defn form-main-layout [opts & body]
  (->> (form/form-box (assoc opts :show-title? false) body)
       (main-layout opts)))