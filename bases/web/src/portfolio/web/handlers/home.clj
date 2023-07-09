(ns portfolio.web.handlers.home
  (:require [portfolio.web.handlers.core :as h]
            [portfolio.web.pages.home :as views]))

(def home
  {:name  ::home
   :enter (fn [context]
            (let [_ (reset! h/test-atom context)]
              (h/res context (views/home [:h1 "AVINASH SRIDHAR"]))))})