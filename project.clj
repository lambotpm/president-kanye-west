(defproject president-kanye-west "0.1.0-SNAPSHOT"
  :description "Twitter bot for the Kanye West 2016 presidential campaign"
  :url "https://twitter.com/KanyeWest2016"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [overtone/at-at "1.2.0"]
                 [twitter-api "0.7.8"]
                 [environ "1.0.0"]]
  :main president-kanye-west.generator
  :min-lein-version "2.0.0"
  :plugins [[lein-environ "1.0.0"]])
