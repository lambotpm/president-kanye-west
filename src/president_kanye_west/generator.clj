(ns president-kanye-west.generator
  (:require [twitter.api.restful :as twitter]
            [twitter.oauth :as twitter-oauth]
            [overtone.at-at :as overtone]
            [environ.core :refer [env]]))

(defn word-chain
  "Generate a chain of words from our corpus"
  [word-transitions]
  (reduce (fn [r t] (merge-with clojure.set/union r
                                (let [[a b c] t]
                                  {[a b] (if c #{c} #{})})))
          {}
          word-transitions))

(defn text->word-chain
  [s]
  (let [words (clojure.string/split s #"[\s|\n]")
        word-transitions (partition-all 3 1 words)]
    (word-chain word-transitions)))

(defn chain->text
  "Helper function which turns our result chain into a string with spaces"
  [chain]
  (apply str (interpose " " chain)))

(defn walk-chain
  "Walk the chain until we don't have any more suffixes"
  [prefix chain result]
  (let [suffixes (get chain prefix)]
    (if (empty? suffixes)
      result
      (let [suffix (first (shuffle suffixes))
            new-prefix [(last prefix) suffix]
            result-with-spaces (chain->text result)
            result-char-count (count result-with-spaces)
            suffix-char-count (inc (count suffix))
            new-result-char-count (+ result-char-count suffix-char-count)]
        (if (>= new-result-char-count 140)
          result
          (recur new-prefix chain (conj result suffix)))))))

(defn generate-text
  [start-phrase word-chain]
  (let [prefix (clojure.string/split start-phrase #" ")
        result-chain (walk-chain prefix word-chain prefix)
        result-text (chain->text result-chain)]
    result-text))

(defn process-file
  "Read our file from disk"
  [fname]
  (text->word-chain
   (slurp (clojure.java.io/resource fname))))

(def files ["inaugural-modern.txt" "kanye-west-lyrics.txt"])
(def functional-president-west (apply merge-with clojure.set/union (map process-file files)))

;; TODO update this prefix list with more... presidential things
;; I think I need to manually go through the Kanye verses a bit
(def prefix-list ["On this" "My fellow" "For everywhere" "To the" "As we"
                  "Today, America" "In reaffirming" "We dare"
                  "Encouraging responsibility" "America, at"
                  "May those" "And so," "For Congress,"
                  "America is" "The world" "For man" "We dare" "And yet"
                  "Let every" "This much" "To those" "To our" "Finally, to"
                  "But neither" "So let" "Let both" "For every" "Even now,"
                  "They came" "First, justice" "Liberty was" "Justice requires"])

(defn end-at-last-punctuation
  "Fix the generated text's punctuation a bit"
  [text]
  (let [trimmed-to-last-punct (apply str (re-seq #"[\s\w]+[^.!?,]*[.!?,]" text))
        trimmed-to-last-word (apply str (re-seq #".*[^a-zA-Z]+" text))
        result-text (if (empty? trimmed-to-last-punct)
                      trimmed-to-last-word
                      trimmed-to-last-punct)
        cleaned-text (clojure.string/replace result-text #"[,| ]$" ".")]
    (clojure.string/replace cleaned-text #"\"" "'")))

(defn tweet-text
  "Randomly choose a prefix from our list and generate our mashup text!"
  []
  (let [text (generate-text (-> prefix-list shuffle first) functional-president-west)]
    (end-at-last-punctuation text)))

(def my-creds (twitter-oauth/make-oauth-creds (env :app-consumer-key)
                                              (env :app-consumer-secret)
                                              (env :user-access-token)
                                              (env :user-access-secret)))
(defn status-update
  []
  (let [tweet (tweet-text)]
    (println "generated tweet is :" tweet)
    (println "char count is: " (count tweet))
    (when (not-empty tweet)
      (try (twitter/statuses-update :oauth-creds my-creds
                                    :params {:status tweet})
           (catch Exception e (println "Error: " (.getMessage e)))))))

(def my-pool (overtone/mk-pool))

(defn -main
  "Update the status every 4 hours"
  [& args]
  (println "Started up")
  (println (tweet-text))
  (overtone/every (* 1000 60 60 4) #(println (status-update)) my-pool))
