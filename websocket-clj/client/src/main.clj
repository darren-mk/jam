(require '[gniazdo.core :as ws])

(def sample-ws-url
  "wss://ws-postman.eu-gb.mybluemix.net/ws/echo")

(def socket
  (ws/connect sample-ws-url
    :on-receive #(prn 'received %)))

(ws/send-msg socket "hello")
;; received "hello"

(ws/close socket)
