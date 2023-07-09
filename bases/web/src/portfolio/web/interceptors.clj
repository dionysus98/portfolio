(ns portfolio.web.interceptors)

#_(def session
    (let [str->bytes #(.getBytes %)
          bytes->str  #(String. %)
          secret (or (:cookie-secret @config) "sldkfj23dk84akl2")
          cookie (str->bytes secret)
          valid? (and (bytes? cookie)
                      (= (bytes->str cookie) secret))]
      (log/info :msg "Checking cookie"
                :token valid?
                :secret secret)
      (middlewares/session
       {:store        (cookie/cookie-store {:key cookie})
        :cookie-name  (or (:cookie-name @config) "ops_session")
        :cookie-attrs {:path      "/"
                       :http-only true
                       :secure    (not (:debug @config))
                       :max-age   2147483647
                       :same-site :lax}})))

#_(def user-info
    {:name  :user-info
     :enter (fn [{:keys [request]
                  :as   context}]
              (let [_ (reset! h/test-atom context)
                    token (-> request :session :token)]
                (log/info :msg "associng user"
                          :token token)
                (assoc-in context [:request :authenticated-user] (token->user token))))
     :error (fn [ctx ex]
              (println ex)
              (assoc ctx :response (ring-resp/redirect "/login")))})

#_(def auth-filter
    {:name :auth-filter
     :enter (fn [context]
              (let [_ (reset! h/test-atom context)
                    user-id (-> context :request :authenticated-user :xt/id)
                    query-fn (partial authr/auth-query user-id)
                    ui-accesses (authr/user-ui-access user-id)
                    uri (-> context :request :uri (str/split #"/") second)
                    panel-type (->> uri (str "ui-panel/") keyword)
                    authorized? (util/has? ui-accesses panel-type)]
                (if authorized?
                  (-> context
                      (assoc-in [:request :auth-filter] query-fn)
                      (assoc-in [:request :user-ui-accesses] ui-accesses))
                  (throw (ex-info "Page Not Found" {})))))
     :error h/display-error})