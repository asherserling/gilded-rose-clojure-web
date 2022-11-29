(ns gilded-rose-clojure.store-test
  (:require [clojure.test :refer :all]
            [gilded-rose-clojure.store.store :as x]))

(deftest regular-item-decreases-by-1-before-
  (let [item {:name "foo" :quality 10 :sell-in 2}
        updated-item (x/increment-day-item item)]
    (is (= (:quality updated-item) 9))))

(deftest regular-item-decreases-by-2-after-sell-date
  (let [item (x/increment-day-item {:name "foo" :quality 10 :sell-in -1})]
    (is (= (:quality item) 8))))

(deftest make-quality-degrader-degrades-one-unit-when-not-passed-sell-date
  (let [degrader (x/make-quality-degrader 1)
        item {:quality 10 :sell-in 1}
        new-quality (degrader item)]
    (is (= 9 new-quality))))

(deftest quality-of-item-never-negative
  (let [item {:quality 0 :sell-in -1}
        after-a-long-time (nth (iterate x/increment-day-item item) 1000)]
    (is (not (< (:quality after-a-long-time) 0)))))

(def some-aged-brie {:name "Aged Brie" :quality 10 :sell-in 1})

(deftest aged-brie-increases-in-quality
  (let [updated-item (x/increment-day-item some-aged-brie)]
    (is (= (:quality updated-item) 11))))

(deftest aged-brie-doubles-increase-in-quality-after-sell-in
  (let [updated-item (x/increment-day-item {:name "Aged Brie" :quality 0 :sell-in -1})]
    (is (= (:quality updated-item) 2))))

(deftest quality-never-more-than-50
  (let [item (nth (iterate x/increment-day-item some-aged-brie) 1000)]
    (is (not (> (:quality item) 50)))))

(defn get-updated-conjured-quality-for-days [days initial-quality]
  (let [example-items (map
                       (fn [day] {:name "Conjured Mana Cake"
                                  :quality initial-quality
                                  :sell-in day})
                       days)]
    (map (comp :quality x/increment-day-item) example-items)))

(deftest conjured-items-degrade-two-before-sell-date
  (let [updated-qualities (get-updated-conjured-quality-for-days
                           [2 3 4 5 6 7 8 9 10]
                           10)]
    (is (every? #(= 8 %) updated-qualities))))

(deftest conjured-items-degrade-four-after-sell-date
  (let [update-qualities (get-updated-conjured-quality-for-days
                          [0 -1 -2 -3 -4 -5 -6]
                          10)]
    (is (every? #(= 6 %) update-qualities))))

(comment
  (run-tests)
  )