(ns gilded-rose-clojure.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[gilded-rose-clojure started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[gilded-rose-clojure has shut down successfully]=-"))
   :middleware identity})
