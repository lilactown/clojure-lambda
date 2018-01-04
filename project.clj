(defproject demoaws "0.1.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.amazonaws/aws-lambda-java-core "1.1.0"]
                 [com.amazonaws/aws-lambda-java-events "2.0.1"]
                 [org.clojure/data.json "0.2.6"]
                 [compojure "1.6.0"]]
  :source-paths ["src"]
  ;; :java-source-paths ["src/java"]
  :aot :all)
