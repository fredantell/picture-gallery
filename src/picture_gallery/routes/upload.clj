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
            [picture-gallery.utils :refer [thumb-size thumb-prefix galleries gallery-path image-uri thumb-uri]])
  (:import [java.io File FileInputStream FileOutputStream]
           [java.awt.image AffineTransformOp BufferedImage]
           java.awt.RenderingHints
           java.awt.geom.AffineTransform
           javax.imageio.ImageIO))


(defn scale [img ratio width height]
  (let [scale (AffineTransform/getScaleInstance (double ratio) (double ratio))
        transform-op (AffineTransformOp.
                      scale AffineTransformOp/TYPE_BILINEAR)]
    (.filter transform-op img (BufferedImage. width height (.getType img)))))

(defn scale-image [file]
  (let [img (ImageIO/read file)
        img-width (.getWidth img)
        img-height (.getHeight img)
        ratio (/ thumb-size img-height)]
    (scale img ratio (int (* img-width ratio)) thumb-size)))

(defn save-thumbnail [{:keys [filename]}]
  (let [path (str (gallery-path) File/separator)]
    (ImageIO/write
     (scale-image (io/input-stream (str path filename)))
     "jpeg"
     (File. (str path thumb-prefix filename)))))

(defn upload-page [info]
  (layout/common
   [:h2 "Upload an Image"]
   [:p info]
   (form-to {:enctype "multipart/form-data"}
            [:post "/upload"]
            (file-upload :file)
            (submit-button "upload"))))

(defn upload-image [file]
  (noir.io/upload-file (gallery-path) file :create-path? true))

(defn validate-uploaded-file [filename file]
  (if (empty? filename)
    "Choose a file to upload"
    (try
      (upload-image file)
      (save-thumbnail file)
      #_(db/add-image (session/get :user) filename)
      (hiccup.element/image
       {:height "150px"}
       (str "/img/" (session/get :user) File/separator (url-encode filename)))
      (catch Exception ex
        (str "error uploading file " (.getMessage ex))))))

(defn serve-file [user-id file-name]
  (do  (println "Fn Serve-file tried to serve: " file-name)
       (file-response (str (gallery-path) File/separator file-name))))

(defn handle-upload [file]
  ;;file is the :file portion of a ring response map
  (let [filename (:filename file)]
    (println file)
    (println filename)
    (upload-page (validate-uploaded-file filename file))))

(defroutes upload-routes
  (GET "/upload" [info] (restricted (upload-page info)))
  (POST "/upload" {params :params} (restricted (handle-upload (:file  params))))
  (GET "/img/:user-id/:file-name" [user-id file-name] (serve-file user-id file-name)))


