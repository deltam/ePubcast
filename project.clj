(defproject epubcast "0.0.1-alpha"
  :description "server to do tech experiment for ePub publishing on Podcast."
  :main epubcast.core
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [compojure "0.4.1"]
                 [hiccup "0.2.6"]]
  :dev-dependencies [[ring/ring-core "0.2.5"]
                     [ring/ring-devel "0.2.5"]
                     [ring/ring-jetty-adapter "0.2.5"]
                     [ring/ring-httpcore-adapter "0.2.5"]])
;                 [ring/ring "0.2.5"]
;                 [ring/ring-devel "0.2.5"]
;                 [ring/ring-jetty-adapter "0.2.5"]
;                 [ring/ring-httpcore-adapter "0.2.5"]])
 
