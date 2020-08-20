(ns client-card.fixtures)

(def cards [{:id 1
             :full_name "Petr Alekseev"
             :gender "Male"
             :address "Egorova, 17"
             :birthday "1989-08-10"
             :id_policy 12002334421}
            {:id 2
             :full_name "Irina Savchenko"
             :gender "Female"
             :address "Egorova, 25"
             :birthday "1991-08-10"
             :id_policy 99812234700}])

(def new-card {:full_name "Nikita Prokopov"
               :gender "Male"
               :address "Egorova, 8"
               :birthday "1985-08-10"
               :id_policy 112211700})