(ns portfolio.web.lib.ui.htmx)

(defn style
  "Receives a map of style attributes as key-val and returns a map with a single key called :style, 
   holding all the style attributes in a string as required by html inline styles.   
   e.g: (style {:margin '45px' :text-align 'center}) -> {:style 'margin:45px; text-align:center;'}     
   "
  [& {:as keyvals}]
  (let [fmt-str #(let [[kwd val] %]
                   (str (name kwd) ":" val "; "))
        create-style (->> (dissoc keyvals :str?) (map fmt-str) (apply str) .trim)]
    (if (:str? keyvals)
      create-style
      {:style create-style})))

(defn g-fonts
  [families]
  [:link {:href (apply str "https://fonts.googleapis.com/css2?display=swap"
                       (for [f families]
                         (str "&family=" f)))
          :rel "stylesheet"}])

(defn base-html
  [{:base/keys [title
                description
                lang
                image
                icon
                url
                canonical
                font-families
                head
                html-tag-attr]}
   & html-contents]
  [:html
   (merge {:lang lang} html-tag-attr)
   [:head
    [:title title]
    [:meta {:name "description" :content description}]
    [:meta {:content title :property "og:title"}]
    [:meta {:content description :property "og:description"}]
    (when image
      [:meta {:content image :property "og:image"}])
    [:meta {:content "summary" :name "twitter:card"}]
    (when-some [url (or url canonical)]
      [:meta {:content url :property "og:url"}])
    (when-some [url (or canonical url)]
      [:link {:ref "canonical" :href url}])
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
    (when icon
      [:link {:rel "icon"
              :type "image/png"
              :sizes "16x16"
              :href icon}])
    [:meta {:charset "utf-8"}]
    (when (not-empty font-families)
      (list
       [:link {:href "https://fonts.googleapis.com", :rel "preconnect"}]
       [:link {:crossorigin "crossorigin",
               :href "https://fonts.gstatic.com",
               :rel "preconnect"}]
       (g-fonts font-families)))
    (apply list head)]
   html-contents])

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
                            [[:link {:rel "stylesheet" :href "/css/main.css"}]
                             [:link {:rel "stylesheet" :href "/css/pico-1.5.10/css/pico.min.css"}]
                             #_[:link
                                {:rel "stylesheet"
                                 :href "https://cdn.jsdelivr.net/npm/bulma@0.9.4/css/bulma-rtl.min.css"}]
                             [:script {:defer "defer" :src "/js/htmx@1.8.4.min.js"}]
                             [:script {:defer "defer" :src "/js/hyperscript@0.9.7.min.js"}]
                             [:script {:defer "defer" :src "/js/main.js"}]]
                            head))))]
    (apply base-html merged-opts body)))

(defn page [opts & body]
  (-> opts
      (merge {:base/title "Avy's Portfolio"
              :base/description "Avy's Portfolio WWeb Applicatoin"
              :base/font-families ["Material+Icons"]})
      (base body)))
