(ns pdfclj.core
  (:require
   [clj-pdf.core :refer [pdf]])
  (:import
   (java.io
    ByteArrayOutputStream
    ByteArrayInputStream)
   (org.apache.pdfbox.pdmodel
    PDDocument)
   (org.apache.pdfbox.pdmodel.encryption
    AccessPermission
    StandardProtectionPolicy)))


(with-open [stream (ByteArrayOutputStream.)]
  (pdf [{} [:phrase "abc"]] stream)
  (let [doc (PDDocument.load stream)

        ;;b (.toByteArray stream)
        
        ]
   nil))
