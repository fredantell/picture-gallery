(ns picture-gallery.views.layout
  (:require [hiccup.page :refer [html5 include-css]])
  (:require [hiccup.element :refer [link-to]])
  (:require [noir.session :as session])
  (:require [hiccup.form :refer [form-to text-field password-field submit-button]]))


(defn logout-links [user]
  [:div (link-to "/logout" (str "logout " user))])

(defn login-and-register-links []
  [:div (link-to "/register" (str "register"))
   (form-to [:post "/login"]
            (text-field {:placeholder "screen name"} "id")
            (password-field {:placeholder "password"} "pass")
            (submit-button "login"))])

(defn display-session-links []
  (if-let [user (session/get :user)]
    (logout-links user)
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

;; Testing/Scratch

(display-session-links) ;; 
(logout-links "Fredrik") ;; [:div [:a {:href #<URI /logout>} ("logout Fredrik")]]
(login-and-register-links) ;; [:div [:a {:href #<URI /register>}
;; ("register")] [:form {:method "POST", :action #<URI /login>}
;; [:input {:placeholder "screen name", :type "text", :name "id", :id
;; "id", :value nil}] [:input {:placeholder "password", :type
;; "password", :name "pass", :id "pass", :value nil}] [:input {:type
;; "submit", :value "login"}]]]





