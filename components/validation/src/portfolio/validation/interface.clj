(ns portfolio.validation.interface
  (:require [portfolio.validation.core :as core]
            [portfolio.validation.specs :as specs]
            [malli.core :as m]
            [malli.error :as me]))

(def schemas core/schemas)

(def registry core/registry)

(defn register-spec! [type schema]
  (core/register-spec! type schema))

(defn register-kv-spec! [specs]
  (core/register-kv-spec! specs))

(defn valid? [?schema data]
  (core/valid? ?schema data))

(def explain core/explain)

(def map->spec core/map->spec)

(defmacro with-validation [type data body]
  `(if (m/validate ~type ~data {:registry @registry})
     ~body
     (throw (ex-info "Invalid data."
                     {:explain (me/humanize (m/explain ~type ~data {:registry @registry}))}))))

;; Specs
(def email-regex specs/email-regex)
(def uri-regex specs/uri-regex)
(def slug-regex specs/slug-regex)
(def not-empty-string specs/not-empty-string)
(def int-regex  specs/int-regex)
(def number-regex  specs/number-regex)
(def yes-no specs/yes-no)
(def inst specs/inst)
(def html-ip-attachment specs/html-ip-attachment)
(def html-checkbox specs/html-checkbox)
(def uuid-string specs/uuid-string)
(def html-date specs/html-date)
(def html-ip-multiple-attachment specs/html-ip-multiple-attachment)