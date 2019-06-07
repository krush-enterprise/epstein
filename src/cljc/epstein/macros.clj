(ns epstein.macros
  (:require [epstein.config :as config]))

;; For cljs. Some env vars are only available during compilation so use a macro to fetch them and inline them in the js output.
(defmacro env [k]
  (config/env k))
