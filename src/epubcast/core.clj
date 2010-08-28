(ns epubcast.core
  (:gen-class)
    (:use [compojure.core]
          [hiccup.core]
          [hiccup.form-helpers]
          [ring.util.response :only (redirect)]
          [ring.adapter.jetty :only (run-jetty)]
          [clojure.contrib.duck-streams :only (reader writer)]
          [clojure.contrib.seq-utils :only (rand-elt)]))

(defn html-doc [id body]
  (html [:h1 "Trojure - tropy on clojure"]
        [:div {:align "right"}
         [:a {:href "/new"} "Create "]
         [:a {:href (str "/edit/" id)} "Edit "]
         [:a {:href "/"} "Random"]]
        [:div {:align "left"}
         [:blockquote {:style "border-style:solid"}
          body
          [:br]
          [:a {:href (str "/entry/" id)} "Permalink"]]]))

(defn main-body [entry]
  (let [text (entry :text)]
    (html-doc (entry :id)
              (html [:pre (entry :text)]))))

(defroutes main-routes
  (GET "/" []
       (main-body {:id "010" :text "epubcast"}))
  (ANY "*" []
       {:status 404, :body "<h1>Page not found</h1>"}))


(run-jetty main-routes {:port 8080})