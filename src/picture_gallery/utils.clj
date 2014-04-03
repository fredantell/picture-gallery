(ns picture-gallery.utils
  (:require
   [noir.session :as session])
  (:import [java.io File FileInputStream FileOutputStream]))

(declare galleries)

(def galleries "galleries")
(defn gallery-path []
  (str galleries File/separator (session/get :user)))


(def thumb-size 150)
(def thumb-prefix "thumb_")

