# Kanye West for President 2016

A silly little Twitter bot which learns from Kanye West lyrics and United States presidential inaugural addresses to create mind-blowing tweets.

Follow [@KanyeWest2016](https://twitter.com/KanyeWest2016) for the salient details!

## Running

To run this project locally, open up `src/president_kanye_west/generator.clj` into emacs and launch Cider using `M-x cider-jack-in`. You can see example outputs by running `(tweet-text)`.

To change source data, add your plaintext files to `resources/` and update the `(def files [...])` line to include them within `generator.clj`. If you wish to deploy this bot to Heroku or use it to tweet locally, you must set up a `profiles.clj` file in the root directory and include the Twitter API app consumer key, app consumer secret, user access token, and user access secret. Make sure you *do not* upload these to GitHub! You can then Tweet from your local machine by running `(status-update)` from the Cider REPL.

## Special thanks

Thanks to Kanye West for the inspirational lyrics, and to the NLTK data corpus for easy-to-use inaugural files.

Thanks to Twitter for being Twitter.

Thanks to all the people who follow this stupid bot and retweet its garbage.

Inspired by Carin Meier's [How I Start](https://howistart.org/posts/clojure/1) article on Clojure.

## License

Copyright © 2015 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
