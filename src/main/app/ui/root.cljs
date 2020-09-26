(ns app.ui.root
  (:require
    [app.application :refer [SPA]]
    [com.fulcrologic.fulcro.dom :refer [div ul li p h3 button b p]]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    [com.fulcrologic.fulcro.algorithms.merge :as merge]
    [taoensso.timbre :as log]))

(defsc Item
  [this {:item/keys [name]}]
  {:ident :item/id
   :query [:item/id :item/name :ui/clicked?]
   :use-hooks? true}
  (let [computed-prop (comp/get-computed this :computed-prop)]
    (log/info :computed-prop computed-prop)
    (li
      (button {:onClick (fn []
                          (log/info "Computed prop from callback" computed-prop)
                          (m/toggle!! this :ui/clicked?)
                          ;; The single ! variation works
                          #_(m/toggle! this :ui/clicked?))}
        name))))

(def ui-item (comp/factory Item {:keyfn :item/id}))

(defsc Root [_this {:root/keys [items]}]
  {:query [{:root/items (comp/get-query Item)}]
   :initial-state {:root/items []}
   :componentDidMount
   (fn [comp]
     (merge/merge-component! comp Item {:item/id 1 :item/name "Item 1" :ui/clicked? false} :append [:root/items]))}
  (div
    (p "Updating a child component that sets use-hooks? to true with transact!! makes it lose the computed props.")
    (p "Click on the button and see what happens to Item's computed props.")
    (ul
      (map #(ui-item (comp/computed % {:computed-prop :anything})) items))))
