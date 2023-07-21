(ns portfolio.web.components.corner-bubble
  (:require [clojure.core.match :refer [match]]
            [portfolio.util.interface :as util]
            [portfolio.ui.interface :refer [style]]
            [portfolio.web.lib.ui.styles :refer [colors]]))

(def styles
  {:corner-bubble-icon-base {:str?       true
                             :cursor     "pointer"
                             :text-align "center"
                             :display    "block"
                             :color      (:primary colors)
                             :position   "fixed"}
   :corner-bubble-circle    {:background-color "whitesmoke"
                             :text-align       "center"
                             :width            "5rem"
                             :height           "5rem"
                             :margin           "0"
                             :padding          "0"
                             :border-radius    "50%"}
   :corner-bubble-base      {:z-index  0.2
                             :position "fixed"
                             :top      "0"
                             :left     "0"
                             :right    "0"
                             :bottom   "0"}})

(defn corner-bubble-icon [position icon]
  [:span.icon.material-icons
   {:class "input"
    :hx-trigger "mouseenter"
    :hx-get "/ui/corner-bubble/mouseenter"
    :style (style
            (merge (:corner-bubble-icon-base styles)
                   (match position
                     []      {:top  "57%"
                              :left "55%"}
                     [:left] {:top   "57%"
                              :right "55%"}
                     [:top]  {:bottom "57%"
                              :left   "55%"}
                     _       {:bottom "57%"
                              :right  "55%"})))}
   icon])

(defn corner-bubble [position icon]
  [:span (->> (:corner-bubble-base styles)
              (map (fn [[k v]]
                     {k (if (util/has? position k) "100" v)}))
              (into {})
              style)
   [:div (style (merge (:corner-bubble-circle styles)
                       {:transform (match position
                                     [] "translate(-50%, -50%)"
                                     [:left] "translate(50%, -50%)"
                                     [:top] "translate(-50%, 50%)"
                                     _ "translate(50%, 50%)")}))
    (corner-bubble-icon position icon)]])