# epubcast

server to do tech experiment for ePub publishing on Podcast.

this program is under construction.

standing server as local.

publishing Podcast RSS, EPUBs from ./static folder.


ePubファイルのPodcast配信をするための実験サーバです。

ローカルでサーバを立ち上げ、staticフォルダに入っているEPUBファイルをPodcastRSSで配信します


## Usage

    $ mkdir static
    $ cp YOUR.epub ./static
    $ java -jar epubcast-0.0.1-alpha-standalone.jar
    
open http://localhost:8080/

regist Podcast address "http://localhost:8080/epub" on iTunes,

or click [added iTunes] link.


## Installation

FIXME: write

## TODO
* be able to deploy on Google AppEngine/Java


## License

Copyright (C) 2010 deltam(deltam@gmail.com)

Licensed under the MIT License (http://www.opensource.org/licenses/mit-license.php)
