(ns picture-gallery.routes.upload
  (:require [compojure.core :refer [defroutes GET POST]]
            [hiccup.form :refer :all]
            [hiccup.element :refer [image]]
            [hiccup.util :refer [url-encode]]
            [noir.io :refer [upload-file resource-path]]
            [noir.session :as session]
            [noir.response :as resp]
            [noir.util.route :refer [restricted]]
            [clojure.java.io :as io]
            [ring.util.response :refer [file-response]]
            [picture-gallery.views.layout :as layout]
            [picture-gallery.models.db :as db]
            [picture-gallery.utils :refer [galleries gallery-path]])
  (:import [java.io File FileInputStream FileOutputStream]
           [java.awt.image AffineTransformOp BufferedImage]
           java.awt.RenderingHints
           java.awt.geom.AffineTransform
           javax.imageio.ImageIO))

(defn upload-page [info]
  (layout/common
   [:h2 "Upload an Image"]
   [:p info]
   (form-to {:enctype "multipart/form-data"}
            [:post "/upload"]
            (file-upload :file)
            (submit-button "upload"))))


(defn validate-uploaded-file [filename]
  (if (empty? filename)
    "Choose a file to upload"
    "Success!"))

;; (validate-uploaded-file) ;; 

(defn handle-upload [{:keys [filename :as file]}]
  (println file)
  (upload-page (validate-uploaded-file filename)))

(defroutes upload-routes
  (GET "/upload" [info] (upload-page info))
  (POST "/upload" {params :params} (handle-upload params)))


