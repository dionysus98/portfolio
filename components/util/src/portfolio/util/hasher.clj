(ns portfolio.util.hasher
  (:import [javax.crypto Cipher]
           [javax.crypto.spec SecretKeySpec]
           [java.security MessageDigest]
           [java.util Base64 Base64$Encoder Base64$Decoder]))

(defn KEY ^SecretKeySpec [^String SECRET]
  (let [sha (MessageDigest/getInstance "SHA-1")
        ba  (->> (.digest sha (.getBytes SECRET "UTF-8"))
                 (take 16)
                 byte-array)]
    (SecretKeySpec. ba "AES")))

(def ^Base64$Encoder b64-encoder (.withoutPadding
                                  (Base64/getUrlEncoder)))

(def ^Base64$Decoder b64-decoder (Base64/getUrlDecoder))

(defn encrypter ^Cipher [secret] (doto (Cipher/getInstance "AES")
                                   (.init Cipher/ENCRYPT_MODE (KEY secret))))
(defn decrypter ^Cipher [secret] (doto (Cipher/getInstance "AES")
                                   (.init Cipher/DECRYPT_MODE (KEY secret))))

(defn -decrypt [^String key ^String s]
  (String.
   (->> (.decode b64-decoder s)
        (.doFinal (decrypter key)))
   "UTF-8"))

(defn encrypt [^String key ^String s]
  (->> (.doFinal (encrypter key) (.getBytes s "UTF-8"))
       (.encodeToString b64-encoder)))

(defn decrypt [^String key ^String s]
  (try
    (-decrypt key s)
    (catch Exception e
      (println e)
      nil)))