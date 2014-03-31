(ns picture-gallery.views.layout
  (:require [hiccup.page :refer [html5 include-css]])
  (:require [hiccup.element :refer [link-to]])
  (:require [noir.session :as session]))


(defn base [& body]
  (html5
    [:head
     [:title "Welcome to picture-gallery"]
     (include-css "/css/screen.css")]
    [:body body]))


(defn common [& content]
  (base
   (if-let [user (session/get :user)]
     [:div
      [:span "Hi, " user "!"]
      [:span.logoutLink "Logout?"]]
     (link-to "/register" "register"))
   content))

