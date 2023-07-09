(ns portfolio.web.pages.not-found
  (:require [portfolio.web.components.elements :as el]
            [portfolio.web.components.layouts :refer [base-layout]]
            [portfolio.web.lib.ui.htmx :as htmx]))

(defn not-found []
  (->> (el/hero {:class "is-fullheight"}
                [:div.is-flex.is-flex-direction-row.is-justify-content-center.is-flex-direction-column
                 (htmx/style {:transform "scale(2)"})
                 [:span.material-icons.has-text-centered "warning"]
                 [:p "Page Not Found!"]])
       (base-layout {})))