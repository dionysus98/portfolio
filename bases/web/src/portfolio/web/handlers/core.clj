(ns portfolio.web.handlers.core
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.walk :refer [keywordize-keys]]
            [hiccup.page :refer [html5]]
            [io.pedestal.log :as log]
            [portfolio.util.interface :as util]
            [portfolio.validation.interface :as v]
            [portfolio.attachment.interface :as attachment]
            [portfolio.web.config :refer [config]]
            [portfolio.web.lib.response :as resp]
            [portfolio.web.pages.not-found :refer [not-found]]))

(def test-atom (atom nil))

(defn form-context-map [context]
  (let [request (:request context)
        fdt (-> request :params keywordize-keys)
        ?schema (-> fdt first key namespace keyword)
        valid? (v/valid? ?schema fdt)
        type (-> request :uri (str/split #"/") second)
        parse-int #(->> %
                        (map (fn [[k v]]
                               {k (Integer/parseInt v)}))
                        (into {}))]
    {:request request
     :fdt fdt
     :schema ?schema
     :valid? valid?
     :parse-int parse-int
     :base-uri (str "/" type)}))

(defn validate [context]
  (let [request (:request context)
        uri (:uri request)
        error-id (util/path->slug uri)
        path-params (:path-params request)
        field-name (str (:ent path-params) "/" (:spec path-params))
        ?schema (keyword (str (:ent path-params) "/" (:spec path-params)))
        value (get (:params request) field-name)
        valid? (v/valid? ?schema value)]
    {:valid? valid?
     :error-id error-id
     :explain (first (v/explain ?schema value))}))

(defn form-file [context]
  (let [request (:request context)
        path-params (:path-params request)
        error-id (str "ui-" (:form path-params) "-" (:field path-params))
        field-name (str (:form path-params) "/" (:field path-params))
        _value (get (:params request) field-name)
        value (if (vector? _value) _value [_value])]
    {:value value
     :error-id error-id}))

(defn view-attachment [context]
  (let [path-params (-> context :request :path-params)
        atch (-> path-params
                 :attachment-id
                 parse-uuid
                 attachment/get-attachment)
        src (:attachment/src atch)
        info (util/file-info src)
        content-type (case (:ext info)
                       "jpg" "image/jpeg"
                       "jpeg" "image/jpeg"
                       "png" "image/png"
                       (str "application/" (:ext info)))]
    {:src (io/file src)
     :content-type content-type}))

(defn res [context body & {:keys [headers status]}]
  (let [data {:headers headers
              :status status}
        response (-> body
                     html5
                     (resp/ok "Content-Type" "text/html"))
        safe (fn [res k f]
               (update-in
                res [:response k]
                (fn [cur]
                  (if (k data)
                    (if (= 1 (util/arity f))
                      (f (k data))
                      (f cur (k data)))
                    cur))))]
    (-> context
        (assoc :response response)
        (safe :headers merge)
        (safe :status identity))))

(defn display-error [context ex]
  (log/info :msg "Page Not found"
            :error ex)
  (res context [:div.message-body (ex-message ex)
                (when (:debug @config)
                  {:cause (ex-data ex)
                   :error ex})]))

(defn page-not-found [context ex]
  (log/info :msg "Page Not found"
            :error ex)
  (res context (not-found)))

(defn form-ok->redirect [context fn->redirect-uri]
  (let [data (form-context-map context)]
    (if (:valid? data)
      (res context [:div.message-body "Success"] :headers {"HX-Redirect" (fn->redirect-uri data)})
      (res context [:div.message-body "Invalid! Please fill all details."]))))