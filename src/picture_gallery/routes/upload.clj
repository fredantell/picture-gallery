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


(defn validate-uploaded-file [filename file]
  (println "Debug:\n Path: " (gallery-path)
               "\nFile Map: " file
               "\nOther map: " {:create-path true})
    (if (empty? filename)
    "Choose a file to upload"
    (try
      (noir.io/upload-file (gallery-path) file :create-path? true)
      (hiccup.element/image
       {:height "150px"}
       (str "/img/" (url-encode filename)))
      (catch Exception ex
        (str "error uploading file " (.getMessage ex))))))

;;(validate-uploaded-file "cats.jpg" {}) ;; "error uploading file "
;;(def mock-file-map { :file:size 0, :tempfile "tmpfilename" :content-type " application/octet-stream," :filenamede "name"})

;;(validate-uploaded-file "name"  mock-file-map)
(defn serve-file [file-name]
  (do  (println "Fn Serve-file tried to serve: " file-name)
       (file-response (str (gallery-path) File/separator file-name))))

(defn handle-upload [file]
  ;;file is the :file portion of a ring response map
  (let [filename (:filename file)]
    (println file)
    (println filename)
    (upload-page (validate-uploaded-file filename file))))

(defroutes upload-routes
  (GET "/upload" [info] (upload-page info))
  (POST "/upload" {params :params} (handle-upload (:file  params)))
  (GET "/img/:file-name" [file-name] (serve-file file-name)))


