(defproject laciniaclj "0.1.0-SNAPSHOT"
  :description "a tiny boardgamegeek clone in clojure w/ lacinia"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [com.walmartlabs/lacinia "0.37.0"]
                 [com.walmartlabs/lacinia-pedestal "0.14.0"]
                 [io.pedestal/pedestal.service "0.5.8"]
                 [io.pedestal/pedestal.jetty "0.5.8"]
                 [io.aviso/logging "0.2.0"]]
  :repl-options {:init-ns laciniaclj.core})
