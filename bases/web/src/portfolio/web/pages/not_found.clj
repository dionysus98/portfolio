(ns portfolio.web.pages.not-found
  (:require [portfolio.ui.interface :as ui]
            [portfolio.web.components.elements :as el]
            [portfolio.web.components.layouts :refer [base-layout]]))

(defn not-found []
  (->> (el/hero {:class "is-fullheight"}
                [:div.is-flex.is-flex-direction-row.is-justify-content-center.is-flex-direction-column
                 (ui/style {:transform "scale(2)"})
                 [:span.material-icons.has-text-centered "warning"]
                 [:p "Page Not Found!"]])
       (base-layout {})))