(ns portfolio.web.components.dashboard
  (:require [portfolio.util.interface :refer [slug->title] :as util]
            [portfolio.web.lib.ui.states :refer [dashboard-items]]))

(defn dashboard [selected accesses]
  (let [li (fn [v]
             (as-> (name v) slug
               [:li
                [:a {:href (str "/" slug)
                     :class (if (= selected v) "is-active" "")}
                 (slug->title slug)]]))
        li-items (->> dashboard-items
                      (util/common-elements accesses)
                      (map #(-> % name keyword))
                      sort)]
    [:ul.menu-list {:id "dashboard"}
     (map li li-items)]))