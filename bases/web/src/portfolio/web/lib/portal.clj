(ns portfolio.web.lib.portal
  (:require [portal.api :as p]))

(def p (p/open {:launcher :vs-code}))

; Add portal as a tap> target
(add-tap p/submit)

; Start tapping out values
(tap> [{:hello "d"
        :as 5}
       {:hello "d"
        :as 5}])

; Clear all values
(p/clear)

; Tap out more values
(tap> :world)

; bring selected value back into repl
(prn @p)

; Remove portal from tap> targetset
(remove-tap p/submit)

; Close the inspector when done
(p/close)

; View docs locally via Portal - jvm / node only
(p/docs)