(ns portfolio.util.interface
  (:require [portfolio.util.core :as core]
            [portfolio.util.hasher :as hash]))

(defn rand-str [len]
  (core/rand-str len))

(defmacro ?
  "A useful debugging tool when you can't figure out what's going on:
  wrap a form with ?, and the form will be printed alongside
  its result. The result will still be passed along."
  [val]
  `(let [x# ~val]
     (prn '~val '~'is x#)
     x#))

(defn map->nsmap
  "Adds specified namespace to non-namespaced keys. Takes map and
  a namespace keyword as arguments"
  [m ns]
  (core/map->nsmap m ns))

(defn nsmap->map
  "Removes specified namespace from keys. Takes map and namespace
  keyword as argument. If no namespace is specified, removes all
  namespaces"
  ([m] (core/nsmap->map m))
  ([m ns] (core/nsmap->map m ns)))

(defn lower-keys [m]
  (core/lower-keys m))

(defn sum-maps [m1 m2]
  (core/sum-maps m1 m2))

(defn diff-maps [m1 m2]
  (core/diff-maps m1 m2))

(defn substract-maps [m1 m2]
  (core/substract-maps m1 m2))

(defn take-rand [n coll]
  (core/take-rand n coll))

(defn now [] (core/now))

(defn format-date [date format]
  (core/format-date date format))

(defn whole? [float-num]
  (core/whole? float-num))

(defn now->future
  [duration-type value]
  (core/now->future duration-type value))

(defn from->future
  [from duration-type value]
  (core/from->future from duration-type value))

(defn remove-nil [map]
  (core/remove-nil map))

(defn remove-items  [col items-to-remove]
  (core/remove-by-predicate col items-to-remove))

(defn and-coll [coll]
  (core/and-coll coll))

(defn has? [coll item-to-find]
  (core/has? coll item-to-find))

(defn str->bytes [^String v]
  (core/str->bytes v))

(defn remove-by-predicate
  "Removes key-val pairs by predicate
   eg: (remove-by-predicate even? {:a 2 :b 8 :c 1}) =>> {:c 1}
   "
  [predicate m]
  (core/remove-by-predicate predicate m))

(def keyword->str core/keyword->str)

(defn path->slug [s]
  (core/path->slug s))

(defn title->slug [title]
  (core/title->slug title))

(def slug->title core/slug->title)

(def keyword->title core/keyword->title)

(defn extract [m keys-to-extract]
  (core/extract m keys-to-extract))

(defn stream->bytes [is]
  (core/stream->bytes is))

(def common-elements core/common-elements)

(defn set-interval [time-in-ms f]
  (core/set-interval time-in-ms f))

(defn periodic-seq
  [start duration-or-period]
  (core/periodic-seq start duration-or-period))

(defn file-info [file-str]
  (core/file-info file-str))

(defn set-xt-id-key [m key-to-update]
  (core/set-xt-id-key m key-to-update))

(defn save-file [file-obj path-to-save & {:keys [file-name]}]
  (core/save-file file-obj path-to-save :file-name file-name))

(defn lower-case-map-vals [m]
  (core/lower-case-map-vals m))

(defn arity
  "Returns the maximum arity of:
    - anonymous functions like `#()` and `(fn [])`.
    - defined functions like `map` or `+`.
    - macros, by passing a var like `#'->`.

  Returns `:variadic` if the function/macro is variadic."
  [f]
  (core/arity f))

(defn encrypt [^String key ^String s]
  (hash/encrypt key s))

(defn decrypt [^String key ^String s]
  (hash/decrypt key s))

(defn partialr
  "Takes a function f and fewer than the normal arguments to f, and
   returns a fn that takes a variable number of additional args. When
   called, the returned function calls f with additional args + args."
  ([f] (core/partialr f))
  ([f arg1] (core/partialr f arg1))
  ([f arg1 & more] (apply core/partialr f arg1 more)))