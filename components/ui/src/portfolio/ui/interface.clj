(ns portfolio.ui.interface
  (:require [portfolio.ui.core :as core]))

(defn style
  "Receives a map of style attributes as key-val and returns a map with a single key called :style, 
   holding all the style attributes in a string as required by html inline styles.   
   e.g: (style {:margin '45px' :text-align 'center}) -> {:style 'margin:45px; text-align:center;'}     
   "
  [& {:as keyvals}]
  (core/style keyvals))

(defn base-html [opts & contents]
  (core/base-html opts contents))