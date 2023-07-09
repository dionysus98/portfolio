(ns portfolio.validation.specs)

(def email-regex [:re {:error/message "Should be a valid Email Address"} #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}$"])

(def uri-regex [:re {:error/message "Should be a valid uri"} #"https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)"])

(def slug-regex [:re #"^[a-z0-9]+(?:-[a-z0-9]+)*$"])

(def int-regex [:re {:error/message "Should be a rounded number"} #"^\d+$"])

(def number-regex [:re {:error/message "Should be a number"} #"^-?\d+\.?\d*$"])

(def not-empty-string [:string {:min 1}])

(def yes-no [:enum "Yes" "No"])

(def inst [:fn {:error/message "Invalid time format! Required* inst"} inst?])

(def uuid-string [:fn {:error/message "Should be a valid id"} #(-> % parse-uuid uuid?)])

(def html-ip-attachment [:fn {:error/message "Invalid Attachment"}
                         #(and (contains? % :filename) (contains? % :tempfile))])

(def html-ip-multiple-attachment [:vector [:fn {:error/message "Invalid Attachment"}
                                           #(and (contains? % :filename) (contains? % :tempfile))]])

(def html-checkbox [:or {:error/message "Select any item"} not-empty-string [:vector string?]])

(def html-date [:re {:error/message "Select date"} #"^\d{4}\-(0[1-9]|1[012])\-(0[1-9]|[12][0-9]|3[01])$"])


