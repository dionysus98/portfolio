(ns portfolio.attachment.interface.spec
  (:require [portfolio.validation.interface :as v]))

(def attachment
  {:xt/id           :uuid
   :attachment/cat  v/inst
   :attachment/src  v/not-empty-string
   :attachment/note v/not-empty-string})

(v/register-spec! :db/attachment (v/map->spec attachment {:closed false :opts [:attachment/note]}))