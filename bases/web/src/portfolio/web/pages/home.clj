(ns portfolio.web.pages.home
  (:require [portfolio.ui.interface :refer [style]]
            [portfolio.web.components.layouts :refer [base-layout]]))

(defn home [& args]
  (base-layout
   [:main.container
    [:section (style :display "flex"
                     :justify-content "space-between"
                     :align-items "center")
     [:div (style :text-align "center" :flex 1 :margin-right "1rem")
      [:img.no-drag {:src "/img/display_first_name_light.png"}]
      [:img.no-drag {:style (style :margin-top "1rem" :str? true)
                     :src "/img/display_last_name_light.png"}]]
     [:article (style :text-align "center" :flex 2)
      [:p "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."]]]]))
