(ns portfolio.web.config)

(def config (atom {:cookie-secret nil
                   :cookie-name nil
                   :dedug true}))