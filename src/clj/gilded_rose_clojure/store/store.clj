(ns gilded-rose-clojure.store.store)

(def initial-store-state
  [{:name "+5 Dexterity Vest", :quality 20, :sell-in 10}
   {:name "Aged Brie", :quality 0, :sell-in 2}
   {:name "Elixir of the Mongoose", :quality 7, :sell-in 5}
   {:name "Sulfuras, Hand of Ragnaros", :quality 80, :sell-in 0}
   {:name "Sulfuras, Hand of Ragnaros", :quality 80, :sell-in -1}
   {:name "Backstage passes to a TAFKAL80ETC concert", :quality 20, :sell-in 15}
   {:name "Backstage passes to a TAFKAL80ETC concert", :quality 49, :sell-in 10}
   {:name "Backstage passes to a TAFKAL80ETC concert", :quality 49, :sell-in 5}])


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

(defn increment-day-item [item]
  (-> item
      (update-in [:sell-in] (get-updater item :sell-in))
      (assoc :quality ((get-updater item :quality) item))
      (update-in [:quality] (get-updater item :max-quality))))

(defn increment-day-store [store]
  (->> store
       (map increment-day-item)
       vec))

(defn make-store [fixture]
  (ref fixture))

(defn increment-day! [store]
  (alter store increment-day-store))

(defn reset-store! [store] 
  (dosync
   (ref-set store initial-store-state)))

(defn add-item-store! [store item]
  (dosync
   (alter store conj item)))

(comment
  (let [store (make-store initial-store-state)]
    (dosync
     (increment-day! store)))
  
  (let [store (atom initial-store-state)]
    (add-item-store! store {:name "chummus" :sell-in 25 :quality 3})
    @store))