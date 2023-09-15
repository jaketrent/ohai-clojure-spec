; playing along with https://www.youtube.com/watch?v=5OuOnJXLxVE
(ns core
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as stest]))


(s/valid? string? "asdf")
(s/valid? string? 1)

(def short-string? (s/and string? #(< (count %) 5)))

(s/valid? short-string? "and")
(s/valid? short-string? "longer here")

(def col-of-short-string (s/coll-of short-string?))

(s/valid? col-of-short-string ["a" "b" "c"])
(s/valid? col-of-short-string ["a" "b" "c" 1 "longer again"])

(def short-string-or-number (s/or :short-string short-string?
                                  :is-number number?))

(s/valid? short-string-or-number "a")
(s/valid? short-string-or-number 1)
(s/explain short-string-or-number 1)
(s/explain short-string-or-number "asdfasdf")

(def f1-car {:team "BMW"
             :driver "Snookums"
             :starting-pos 1
             :positions [3 10 6]})

(s/def ::team string?)
(s/def ::driver string?)
(s/def ::starting-pos int?)
(s/def ::positions (s/coll-of int?))

(s/def ::f1-car-spec
  (s/keys :req-un [::team ::driver ::starting-pos ::positions]))

(s/explain ::f1-car-spec f1-car)
(s/conform ::f1-car-spec f1-car)

(defn scored? [last-pos min-pos]
  (< last-pos min-pos))

(scored? (first (:positions f1-car)) 10)

(s/fdef scored? 
        :args (s/cat :last-pos int?
                     :min-pos int?)
        :ret boolean?
        ; asserts conditions of return value (eg, less than 6); (currently fails because there's no guarantee of < 6 in code)
        :fn (fn [{:keys [args reg]}] 
              (let [last-pos (:last-pos args)]
                (< last-pos 6))))


(stest/instrument 'core/scored?)

(stest/check 'core/scored?)
