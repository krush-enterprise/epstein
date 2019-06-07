(ns epstein.config
  (:require [clojure.string :as str]
            [clojure.edn :as edn]))

;; Don't duplicate Heroku env vars in config files. I don't have a super good reason for preferring the heroku method, duplicated local config vars just never worked out as cleanly as I wanted, but I think it still makes sense to have them for some things.
;; I can only use config variables specified in files or in BUILD_CONFIG_WHITELIST in clojure at compile time or in clojurescript. APP_ENV must be in the whitelist.

(defn- kmap [f m]
  (into {} (for [[k v] m] [(f k) v])))

(def ^:private props
  (let [keywordize (fn [s]
                     (-> (str/lower-case s)
                         (str/replace "_" "-")
                         (str/replace "." "-")
                         keyword))
        read-system (partial kmap keywordize)
        file (fn [f]
               (edn/read-string
                (try
                  (slurp f)
                  (catch java.io.FileNotFoundException _))))
        config (file "config.edn")
        config-local (file "config-local.edn")
        app-env (or (keyword (System/getenv "APP_ENV"))
                    :dev)]
    (merge (:all config)
           (get config app-env)
           config-local
           (read-system (System/getenv))
           (read-system (System/getProperties))
           {:app-env app-env})))

(defn env [k]
  (get props k))
