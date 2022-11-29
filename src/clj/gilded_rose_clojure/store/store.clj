(ns gilded-rose-clojure.store.store)

(defn make-store [items]
  (assert (vector? items))
  (->> items
       (map (fn [item] (atom item)))
       vec))

(defn item-seq [store]
  (->> store
       (map deref)))

(defn make-store [items]
  (assert (vector? items))
  (->> items
       (map (fn [item] (atom item)))
       vec))

(defn item-seq [store]
  (->> store
       (map deref)))

(defn make-quality-degrader [degradation-rate]
  (fn [{:keys [quality sell-in]}]
    (let [base-degradation (if (> sell-in 0)
                             1
                             2)
          degradation (* base-degradation degradation-rate)]
      (max 0 (- quality degradation)))))

(def rules {"Aged Brie"
            {:quality (make-quality-degrader -1)}

            "Sulfuras, Hand of Ragnaros"
            {:quality :quality
             :sell-in identity
             :max-quality identity}

            "Backstage passes to a TAFKAL80ETC concert"
            {:quality (fn [{:keys [quality sell-in]}]
                        (condp >= sell-in
                          0 0
                          5 (+ 3 quality)
                          10 (+ 2 quality)
                          (inc quality)))}

            "Conjured Mana Cake"
            {:quality (make-quality-degrader 2)}

            :default
            {:quality (make-quality-degrader 1)
             :sell-in dec
             :max-quality #(min 50 %)}})

(defn get-updater [item key]
  (or (get-in rules [(:name item) key])
      (get-in rules [:default key])))

(defn update-item [item]
  (-> item
      (update-in [:sell-in] (get-updater item :sell-in))
      (assoc :quality ((get-updater item :quality) item))
      (update-in [:quality] (get-updater item :max-quality))))

(defn update-quality! [store]
  (doseq [item store]
    (reset! item (update-item @item))))