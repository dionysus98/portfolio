(ns portfolio.util.core
  (:require [clojure.core.async :as a
             :refer [<! alt! chan go-loop thread timeout]]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]
            [com.rpl.specter :as spr]
            [tick.core :as t]))

(defn rand-str [len]
  (apply str (take len (repeatedly #(str (rand-nth (seq "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890~!@#$%^&*()_+`-=[]}{|;:<>?/.,")))))))

(defn map->nsmap
  "Adds specified namespace to non-namespaced keys. Takes map and
  a namespace keyword as arguments"
  [m ns]
  (reduce-kv (fn [acc k v]
               (let [new-kw (if (and (keyword? k)
                                     (not (qualified-keyword? k)))
                              (keyword (name ns) (name k))
                              k)]
                 (assoc acc new-kw v)))
             {} m))

(defn nsmap->map
  "Removes specified namespace from keys. Takes map and namespace
  keyword as argument. If no namespace is specified, removes all
  namespaces"
  ([m] (reduce-kv (fn [acc k v]
                    (let [new-kw (keyword (name k))]
                      (assoc acc new-kw v)))
                  {} m))
  ([m ns] (reduce-kv (fn [acc k v]
                       (let [new-kw (if (= (name ns) (first (.split (str (symbol k)) "/")))
                                      (keyword (name k))
                                      k)]
                         (assoc acc new-kw v)))
                     {} m)))

(defn lower-keys [m]
  (spr/transform [spr/MAP-KEYS] #(let [a %]
                                   (keyword (str (str/lower-case (str (namespace a)))
                                                 (when-not (nil? (namespace a)) "/")
                                                 (str/lower-case (name a))))) m))

(defn sum-maps [m1 m2]
  (let [ks (flatten (concat (keys m1)
                            (keys m2)))]
    (into {}
          (for [k ks]
            (let [v1 (or (k m1) 0)
                  v2 (or (k m2) 0)]
              {k (+ v1 v2)})))))

(defn diff-maps [m1 m2]
  (let [ks (flatten (concat (keys m1)
                            (keys m2)))]
    (into {}
          (for [k ks]
            (let [v1 (or (k m1) 0)
                  v2 (or (k m2) 0)]
              {k (- v1 v2)})))))

(defn substract-maps [m1 m2]
  (let [ks (flatten (concat (keys m1)
                            (keys m2)))]
    (into {}
          (for [k ks]
            (let [v1 (or (k m1) 0)
                  v2 (or (k m2) 0)]
              {k (- v1 v2)})))))

(defn take-rand [n coll]
  (take n (shuffle coll)))

(defn now [] (java.util.Date.))

(defn format-date [date format]
  (.format (java.text.SimpleDateFormat. format) date))

(defn whole? [float-num]
  (let [i (int float-num)
        diff (- float-num i)]
    (= diff 0.0)))

;; Inst -> Instant
(defn now->future
  "Returns inst for the interval duration provided from now.
   Params {:minutes || :hours || :seconds || :days @number}
   eg: (now->future :hours 10)
   eg: (now->future :minutes 6)
   "
  [duration-type value]
  (-> (t/now) (t/>> (t/+ (t/new-duration value duration-type))) t/inst))

;; Inst -> Instant
(defn from->future
  "Returns inst for the interval duration provided from time inst.
   Params {:minutes || :hours @number}
   eg: (from->future #inst 'whatevertime' :hours 10)
   "
  [from duration-type value]
  (-> (t/inst from) (t/>> (t/+ (t/new-duration value duration-type))) t/inst))

(defn remove-nil [map]
  (into {}
        (remove #(nil? (val %))
                map)))

(defn remove-items [col items-to-remove]
  (remove (set items-to-remove) col))

(defn and-coll [coll]
  (every? identity coll))

(defn has? [coll item-to-find]
  (boolean (some (partial = item-to-find) coll)))

(defn str->bytes [^String v]
  (->> v
       (map (comp byte int))
       byte-array
       bytes))

(defn remove-by-predicate
  "Removes key-val pairs by predicate
   eg: (remove-by-predicate even? {:a 2 :b 8 :c 1}) =>> {:c 1}
   "
  [predicate m]
  (into {} (remove (comp predicate val) m)))

(defn keyword->str [k & capitalize?]
  (->> (-> k
           name
           (str/split #"-"))
       (map #(if (false? (first capitalize?))
               %
               (str/capitalize %)))
       (str/join " ")))

(defn path->slug [s]
  (->> (str/replace s "/" "-")
       rest
       (apply str)))

(defn title->slug [title]
  (str/join "-" (-> title str/lower-case (str/split #" "))))

(defn slug->title [slug & capitalize?]
  (if (-> capitalize? first false?)
    (str/join " " (-> slug (str/split #"-")))
    (str/join " " (-> slug str/capitalize (str/split #"-")))))

(defn keyword->title [kw & capitalize?]
  (-> kw name (slug->title capitalize?)))

(defn extract [m keys-to-extract]
  (->> keys-to-extract
       (map (fn [v] (when (contains? m v) {v (v m)})))
       (filter some?)
       (into {})))

(defn stream->bytes [is]
  (loop [b (.read is) accum []]
    (if (< b 0)
      accum
      (recur (.read is) (conj accum b)))))

(defn common-elements [& colls]
  (let [freqs (map frequencies colls)]
    (mapcat (fn [e] (repeat (apply min (map #(% e) freqs)) e))
            (apply set/intersection (map (comp set keys) freqs)))))

(defn set-interval
  [time-in-ms f]
  (let [stop (chan)]
    (go-loop []
      (alt!
        (timeout time-in-ms)
        (do (<! (thread (f)))
            (recur))
        stop :stop))
    stop))

; Move to notification
(defn periodic-seq
  "Params: [Instant java.time.temporal.TemporalAmount]
   Returns: Lazy sequence of periods from start time, each item in the sequence differ by iterval provided.
   "
  [start duration-or-period]
  (iterate #(.addTo duration-or-period %) start))

; Move to upload
(defn file-info [file-str]
  (let [src (-> file-str
                str/reverse
                (str/split #"\."))
        ext (-> src first str/reverse)
        file-name (-> src second str/reverse)]
    {:name file-name
     :ext ext
     :file-str file-str}))

(defn set-xt-id-key [m key-to-update]
  (update-keys m #(if (= % key-to-update) :xt/id %)))

(defn save-file [file-obj path-to-save & {:keys [file-name]}]
  (let [[in _file-name] ((juxt :tempfile :filename) file-obj)
        info (file-info _file-name)
        file-path (str path-to-save "/" (or file-name (:name info)) "." (:ext info))
        _copy (if (.exists (io/file path-to-save))
                (io/copy in (io/file file-path))
                (do
                  (io/make-parents file-path)
                  (io/copy in (io/file file-path))))]
    file-path))

(defn lower-case-map-vals [m]
  (spr/transform [spr/ALL] (fn [[k v]]
                             [k (str/trim (str/lower-case v))]) m))

(defn arity
  "Returns the maximum arity of:
    - anonymous functions like `#()` and `(fn [])`.
    - defined functions like `map` or `+`.
    - macros, by passing a var like `#'->`.

  Returns `:variadic` if the function/macro is variadic."
  [f]
  (let [func (if (var? f) @f f)
        methods (->> func class .getDeclaredMethods
                     (map #(vector (.getName %)
                                   (count (.getParameterTypes %)))))
        var-args? (some #(-> % first #{"getRequiredArity"})
                        methods)]
    (if var-args?
      :variadic
      (let [max-arity (->> methods
                           (filter (comp #{"invoke"} first))
                           (sort-by second)
                           last
                           second)]
        (if (and (var? f) (-> f meta :macro))
          (- max-arity 2) ;; substract implicit &form and &env arguments
          max-arity)))))

(defn partialr
  "Takes a function f and fewer than the normal arguments to f, and
 returns a fn that takes a variable number of additional args. When
 called, the returned function calls f with additional args + args."
  ([f] f)
  ([f arg1]
   (fn [& args] (apply f (concat args [arg1]))))
  ([f arg1 & more]
   (fn [& args] (apply f (concat args (concat [arg1] more))))))