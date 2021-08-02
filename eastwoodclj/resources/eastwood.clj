(println "* eastwood config is loaded *")

(disable-warning
 {:linter :suspicious-expression
  :for-macro 'clojure.core/or
  :if-inside-macroexpansion-of #{'clojure.core.async/go}
  ;; :within-depth 10
  :reason "https://github.com/jonase/eastwood/issues/411"})
