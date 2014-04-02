(ns picture-gallery.views.layout
  (:require [hiccup.page :refer [html5 include-css]])
  (:require [hiccup.element :refer [link-to]])
  (:require [noir.session :as session])
  (:require [hiccup.form :refer [form-to text-field password-field submit-button]]))


(defn logout-upload-links [user]
  [:div
   [:div (link-to "/logout" (str "logout " user))]
   [:div (link-to "/upload" "upload images")]])

(defn login-and-register-links []
  [:div (link-to "/register" (str "register"))
   (form-to [:post "/login"]
            (text-field {:placeholder "screen name"} "id")
            (password-field {:placeholder "password"} "pass")
            (submit-button "login"))])

(defn display-session-links []
  (if-let [user (session/get :user)]
    (logout-upload-links user)
    (login-and-register-links)))

(defn base [& body]
  (html5
    [:head
     [:title "Welcome to picture-gallery"]
     (include-css "/css/screen.css")]
    [:body body]))


(defn common [& content]
  (base
   (display-session-links)
   content))


