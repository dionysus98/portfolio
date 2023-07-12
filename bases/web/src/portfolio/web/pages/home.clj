(ns portfolio.web.pages.home
  (:require [portfolio.web.components.elements :as el]
            [portfolio.web.components.layouts :refer [base-layout]]))

(defn home [& args]
  (->> (base-layout
        {}
        [:article [:button "helll"]]
        #_(el/card {:title "HELLO!!"
                    :content args}))))
