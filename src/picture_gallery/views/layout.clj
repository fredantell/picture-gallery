(ns picture-gallery.views.layout
  (:require [hiccup.page :refer [html5 include-css]])
  (:require [noir.session :as session]))

(defn common [& body]
  (html5
    [:head
     [:title "Welcome to picture-gallery"]
     (include-css "/css/screen.css")]
    [:body body]))
