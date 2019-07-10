(ns epstein.util
  #?(:cljs (:require-macros [epstein.macros :refer [env]]))
  (:require [clojure.string :as str]
            #?(:clj [epstein.config :refer [env]])))


;;;; General

(defn now []
  #?(:clj (System/currentTimeMillis)
     :cljs (.now js/Date)))

(defn seqify [x]
  (if (or (nil? x) (sequential? x))
    x [x]))



;;;; App-specific

(defn gen-id []
  (let [chars "0123456789abcdefghijklmnopqrstuvwxyz"]
    (apply str (take 8 (repeatedly (fn []
                                     (get chars (rand-int (count chars)))))))))

(defn image-url [handle-or-url & {w :width h :height :keys [fit sharpen progressive? placeholder?]}]
  (when-let [id (or handle-or-url (when placeholder?
                                    (str "https://krush-hyperdrive.herokuapp.com" "/images/no_picture.png")))] ; TODO Don't hard-code domain.
    (str "https://cdn.filestackcontent.com"
         (when (str/starts-with? id "http")
           (str "/" (env :filestack-api-key)))
         (when (or w h fit)
           (str "/resize="
                (str/join
                 ","
                 (remove nil?
                         [(when w
                            (str "w:" w))
                          (when h
                            (str "h:" h))
                          (when fit
                            (str "fit:" (name fit)))]))))
         (when sharpen
           (str "/sharpen=amount:" (if (= sharpen :default) 1 sharpen)))
         (when progressive?
           (str "/pjpg"))
         "/" id)))
