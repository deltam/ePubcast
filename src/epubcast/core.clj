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
  "./static 内のファイルの名前、サイズのMapシーケンスを返す"
  []
  (let [epub-seq (seq (. (file "./static") listFiles))]
    (for [ep epub-seq]
      {:name (. ep getName) :length (. ep length)})))


(defn main-html
  "assembling stub html"
  []
  (html [:h1 "ePubcast"]
        [:div {:align "right"}
         [:a {:href "/epub"} "epub feeds"]
         [:a {:href "itpc://localhost:8080/epub"} "[add iTunes]"]]
        [:div {:align "left"}
         [:blockquote {:style "border-style:solid"}
          [:p "list of ./static"]
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
           [:itunes:name "test"]
           [:itunes:email "test"]]
;               [:itunes:image {:href "http://example.com/podcasts/everything/AllAboutEverything.jpg"}]
          [:itunes:category {:text "Technology"}
           [:itunes:category {:text "Gadgets"}]]
          [:itunes:category {:text "TV &amp; Film"}]

          ;; items
          (for [ep (static-files)]
            (let [fullpath (str "http://localhost:8080/file/" (:name ep))]
              [:item
               [:title (:name ep)]
               [:itunes:author "Nobody"]
               [:itunes:subtitle "./static"
                [:itunes:summary "./static"]
                [:enclosure {:url fullpath
                             :length (str (:length ep))
                             :type "application/epub+zip"}]
                [:guid fullpath]
                [:pubDate "Mon, 4 Oct 2010 19:00:00 GMT"]
                [:itunes:duration "7:04"]
                [:itunes:keywords "test"]]]))
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