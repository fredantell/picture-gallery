(ns picture-gallery.routes.auth
  (:require [hiccup.form :refer :all]
            [compojure.core :refer :all]
            [picture-gallery.routes.home :refer :all]
            [picture-gallery.views.layout :as layout]
            [picture-gallery.models.db :as db]
            [noir.session :as session]
            [noir.response :as resp]
            [noir.validation :as vali]
            [noir.util.crypt :as crypt]))

(defn valid? [id pass pass1]
  (vali/rule (vali/has-value? id)
             [:id "user id is required"])
  (vali/rule (vali/min-length? pass 5)
             [:pass "password must be at least 5 charcters"])
  (vali/rule (= pass pass1)
             [:pass1 "entered passwords do not match"])
  (not (vali/errors? :id :pass :pass1)))

(defn error-item [[error]]
  [:div.error error])

(defn control [id label field]
  (list
   (vali/on-error id error-item)
   label field
   [:br]))

(defn registration-page [& [id]]
  (layout/base
   (form-to [:post "/register"]
            (control :id
                     (label "user-id" "user id")
                     (text-field "id" id))
            (control :pass
                     (label "pass" "password")
                     (password-field "pass"))
            (control :pass1
                     (label "pass1" "retype password")
                     (password-field "pass1"))
            (submit-button "create account"))))

(defn format-error [id ex]
  (cond (and (instance? org.postgresql.util.PSQLException ex)
             (= 0 (.getErrorCode ex)))
        (str "The user with id " id " already exists!")
        :else
        "An error has occured while processing the request."))

(defn handle-registration [id pass pass1]
  (if (valid? id pass pass1)
    (try  (do
            (db/create-user {:id id :pass (crypt/encrypt pass)})
            (session/put! :user id)
            (resp/redirect "/"))
          (catch Exception ex
            (vali/rule false [:id (format-error id ex)])
            (registration-page)))
    (registration-page id)))

(defn login-page [& [id]]
  (layout/base
   (form-to [:post "/login"]
            (control :id
                     (label "user-id" "user id")
                     (text-field "id" id))
            (control :pass
                     (label "pass" "password")
                     (password-field "pass"))
            (submit-button "login"))))

(defn handle-login [id pass]
  (let [user (db/get-user id)]
    (if (and user
             (crypt/compare pass (:pass user)))
      (do  (session/put! :user id)
           (resp/redirect "/"))
      (login-page id))))

(defn handle-logout []
  (session/clear!)
  (resp/redirect "/"))

(defroutes auth-routes
  (GET "/register" []
        (registration-page))
  (POST "/register" [id pass pass1]
        (handle-registration id pass pass1))
  (POST "/login" [id pass]
        (handle-login id pass))
  (GET "/logout" []
       ( handle-logout)))

