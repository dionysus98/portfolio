(ns portfolio.email.core
  (:require [portfolio.email.interface.spec]
            [postal.core :as p]
            [portfolio.validation.interface :as v]))

(defn send-email!
  "Params: {:to 'email-string' :subject 'string' :body 'string'}"
  [config params]
  (let [{:keys [host tls-port from password]} (:email config)]
    (v/with-validation :db/email (assoc params :from from)
      (-> {:host host
           :tls true
           :port tls-port
           :user (:email from)
           :pass password}
          (p/send-message
           (assoc params :from (str (:name from)  " <" (:email from) ">")))))))
