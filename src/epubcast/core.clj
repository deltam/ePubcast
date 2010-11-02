(ns epubcast.core
  "experiment server for publishing ePub on Podcast RSS."
  (:gen-class)
  (:use [clojure.java.io :only (file)]
        [clojure.contrib.io :only (reader writer)]
        [clojure.contrib.seq :only (rand-elt)]
        [compojure.core]
        [hiccup.core]
        [hiccup.form-helpers]
        [ring.middleware
         [session :only (wrap-session)]
         [file :only (wrap-file)]]
        [ring.adapter jetty]
        [ring.util.response :only (file-response)]
        [ring.middleware params stacktrace file file-info session]))


(defn static-files
  []
  (let [epub-seq (seq (. (file "./static") listFiles))]
    (for [ep epub-seq]
      {:name (. ep getName) :length (. ep length)})))


(defn main-html
  "assembling stub html"
  []
  (html [:h1 "ePubcast"]
        [:div {:align "right"}
         [:a {:href "/epub"} "epub feeds"]]
        [:div {:align "left"}
         [:blockquote {:style "border-style:solid"}
;          [:p
;           [:a {:href "/file/susu.epub"} "susu.epub"]]
          (for [ep (static-files)]
            [:p
             [:a {:href (str "file/" (:name ep))} (:name ep)]])]]))


(defn epub-rss
  "./staticに入っているファイルを返すためのPodcastRSSを返す"
  []
  (str "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
       (html
        [:rss {:xmlns:itunes "http://www.itunes.com/dtds/podcast-1.0.dtd" :version "2.0"}
         [:channel
          [:title "ePubcast exp"]
          [:link "http://www.example.com/podcasts/everything/index.html"]
          [:language "ja"]
;               [:copyright "&#x2117; &amp; &#xA9; 2005 John Doe &amp; Family"]
          [:itunes:subtitle "ePubをPodcastで配信するサーバの実験"]
          [:itunes:author "deltam"]
          [:itunes:summary "実験中デース"]
          [:description "実験実験！！！"]
          [:itunes:owner
           [:itunes:name "deltam the YutoLisper"]
           [:itunes:email "deltam@gmail.com"]]
;               [:itunes:image {:href "http://example.com/podcasts/everything/AllAboutEverything.jpg"}]
          [:itunes:category {:text "Technology"}
           [:itunes:category {:text "Gadgets"}]]
          [:itunes:category {:text "TV &amp; Film"}]

          ;; items
          [:item
           [:title "スウスウと砂漠と運び屋 on podcast"]
;                [:title "Susu for ePubcast"]
           [:itunes:author "deltam"]
           [:itunes:subtitle "little story in desert"]
           [:itunes:summary "昔書いたラノベ処女作をさらす"]
           [:enclosure {:url "http://localhost:8080/file/susu_plain.epub"
                        :length "100549"
                        :type "application/epub+zip"}]
           [:guid "http://localhost:8080/file/susu_plain.epub"]
           [:pubDate "Mon, 4 Oct 2010 19:00:00 GMT"]
           [:itunes:duration "7:04"]
           [:itunes:keywords "salt, pepper, shaker, exciting"]]

          [:item
           [:title "SuSu"]
           [:itunes:author "deltam"]
           [:itunes:subtitle "little story in desert"]
           [:itunes:summary "昔書いたラノベ処女作をさらす"]
           [:enclosure {:url "http://localhost:8080/file/susu.epub"
                        :length "89427"
                        :type "application/epub+zip"}]
           [:guid "http://localhost:8080/file/susu.epub"]
           [:pubDate "Wed, 15 Jun 2005 19:00:00 GMT"]
           [:itunes:duration "7:04"]
           [:itunes:keywords "salt, pepper, shaker, exciting"]]
          ]])))


(defroutes main-routes
  "routing main page, podcast rss, static file link."
  ; defaultで表示するHTML
  (GET "/" []
       (main-html))
  ; Podcast RSSを返す
  (GET "/epub" []
       (epub-rss))
  ; 静的ファイルをstaticフォルダから探して返す
  (GET ["/file/:filename" :filename #".*"] [filename]
       (file-response filename {:root "./static"}))
  (ANY "*" []
       {:status 404, :body "<h1>Page not found</h1>"}))


; temporary solution
; http://groups.google.com/group/compojure/browse_thread/thread/44a25e10c37f3b1b/d4a17cb99f84814f?pli=1
(defn wrap-charset [handler charset] 
  (fn [request] 
    (if-let [response (handler request)] 
      (if-let [content-type (get-in response [:headers "Content-Type"])] 
        (if (.contains content-type "charset") 
          response 
          (assoc-in response 
            [:headers "Content-Type"] 
            (str content-type "; charset=" charset))) 
        response)))) 
(wrap! main-routes (:charset "utf8"))


(defn -main
  "run jetty with port 8080"
  [& args]
  (run-jetty main-routes {:port 8080}))