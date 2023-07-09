(ns portfolio.web.routes
  (:require
   [portfolio.web.lib.response :as resp]
   [portfolio.web.handlers.home :as home]
   [portfolio.web.handlers.ui :as ui]))

(def echo
  {:name  ::echo
   :enter (fn [context]
            (let [_request (:request context)
                  response (resp/ok context)]
              (assoc context :response response)))})

(def routes
  [[["/" {:any `home/home}
     ["/echo" {:post `echo}]
     ui/routes]]])