(ns portfolio.web.lib.ui.htmx
  (:require [portfolio.ui.interface :as ui]))

(defn base [opts & body]
  (let [default-opts #:base{:title "My Application"
                            :lang "en-US"
                            :icon "/img/logo.png"
                            :description "My Application Description"
                            :image "https://clojure.org/images/clojure-logo-120b.png"}
        merged-opts (-> default-opts
                        (merge opts)
                        (update
                         :base/head
                         (fn [head]
                           (concat
                            [[:link {:rel "stylesheet" :href "/css/pico-1.5.10/css/pico.min.css"}]
                             [:link {:rel "stylesheet" :href "/css/main.css"}]
                             #_[:link
                                {:rel "stylesheet"
                                 :href "https://cdn.jsdelivr.net/npm/bulma@0.9.4/css/bulma-rtl.min.css"}]
                             [:script {:defer "defer" :src "/js/htmx@1.8.4.min.js"}]
                             [:script {:defer "defer" :src "/js/hyperscript@0.9.7.min.js"}]
                             [:script {:defer "defer" :src "/js/main.js"}]]
                            head))))]
    (apply ui/base-html merged-opts body)))

(defn page [opts & body]
  (-> opts
      (merge {:base/title "Avy's Portfolio"
              :base/description "Avy's Portfolio WWeb Applicatoin"
              :base/font-families ["Material+Icons"]})
      (base body)))
