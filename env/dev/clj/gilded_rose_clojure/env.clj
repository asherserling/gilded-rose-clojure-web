(ns gilded-rose-clojure.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [gilded-rose-clojure.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[gilded-rose-clojure started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[gilded-rose-clojure has shut down successfully]=-"))
   :middleware wrap-dev})
