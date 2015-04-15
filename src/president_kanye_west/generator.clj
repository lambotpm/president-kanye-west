(ns president-kanye-west.generator)

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
