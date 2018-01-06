(ns demoaws.core
  (:require [clojure.data.json :as json]
            [clojure.string :as s]
            [clojure.java.io :as io]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [demoaws.lambda :refer [deflambda]]
            [demoaws.middleware :refer [ring-adapter simple-logger]]))

(defroutes app
  (GET "/" [] "<h1>Hello World</h1>")
  (POST "/" [] "<h1>Hello POST</h1>")
  (route/not-found "<h1>Page not found</h1>"))

;; TODO: figure out how to combine middleware lol

(deflambda app
  {:name demoaws.core.Greet
   :middleware ring-adapter})
