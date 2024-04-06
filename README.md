Go4Lunch est une application conçue pour être utilisée par l'ensemble des employés. Son objectif est de faciliter la recherche de restaurants à proximité. Les utilisateurs peuvent choisir un établissement
. De même, ils peuvent voir les choix de restaurants faits par les autres et décider de les rejoindre. Peu avant l'heure du déjeuner, l'application envoie une notification aux employés pour les encourager à se regrouper avec leurs collègues.




Authentification :
- Connexion sécurisée via Google ou Email/Mot de passe ou Twitter.

  
![Screenshot_20240128_234151](https://github.com/khaleddevfs/Go4Lunch-24/assets/94543178/fe5229d0-7af6-41c9-8364-ca8fc5215253)




Carte des Restaurants:

- Affichage des restaurants environnants sur une carte.

- Utilisation de punaises personnalisées pour indiquer l'emplacement des restaurants.

- Différenciation visuelle des restaurants choisis par les collègues.


![Screenshot_20240128_233943](https://github.com/khaleddevfs/Go4Lunch-24/assets/94543178/e25adc6b-3c28-4320-ba0e-7ada2ad42294)




Liste des Restaurants:

- Présentation détaillée des restaurants incluant nom, distance, image, adresse, nombre de collègues intéressés, horaires d'ouverture et avis.

![Screenshot_20240128_233917](https://github.com/khaleddevfs/Go4Lunch-24/assets/94543178/76167548-619f-4058-9a12-72b2b3798c14)



Fiche Détaillée d'un Restaurant:

- Informations détaillées sur le restaurant sélectionné.

- Options pour choisir le restaurant, appeler le restaurant, "aimer" le restaurant, et voir les collègues intéressés.

![Screenshot_20240128_234059](https://github.com/khaleddevfs/Go4Lunch-24/assets/94543178/934d11a5-3947-4191-a976-f9b82a1a847b)




Liste des Collègues:

- Affichage des choix de restaurants des collègues.

![WhatsApp Image 2024-04-06 à 01 52 50_23c5ec5c](https://github.com/khaleddevfs/Go4Lunch-24/assets/94543178/f4bb163f-9347-40e2-b039-208a5ff9312d)




Recherche de Restaurants:

- Fonction de recherche pour trouver des restaurants par nom.


![Screenshot_20240128_235231](https://github.com/khaleddevfs/Go4Lunch-24/assets/94543178/89492867-81d1-4b7b-b111-11d703f9d4a9)




Menu Latéral:

- Accès à des fonctionnalités supplémentaires comme voir le restaurant choisi pour le déjeuner, accéder aux paramètres, et se déconnecter.
  

![WhatsApp Image 2024-04-06 à 01 52 50_03f28eec](https://github.com/khaleddevfs/Go4Lunch-24/assets/94543178/b4ba506e-54a8-4089-8a52-df0f2ae4bd66)




Notifications:

- Envoi automatique de notifications pour rappeler les arrangements du déjeuner.


![Screenshot_20240129_004538](https://github.com/khaleddevfs/Go4Lunch-24/assets/94543178/d2ce6559-8f35-453e-9f76-06a444907ea4)




Traduction:

- Support du français et de l'anglais, au minimum.
  

![WhatsApp Image 2024-04-06 à 01 52 50_1525d6a7](https://github.com/khaleddevfs/Go4Lunch-24/assets/94543178/54a89793-f501-4d6c-85d1-f5931fa86aa2)



L’architecture MVVM 
- L'application utilise un modèle MVVM (Model-View-ViewModel) où les ViewModels interagissent avec les Repositories pour récupérer/modifier les données, tandis que les Activités et Fragments gèrent l'affichage de l'interface utilisateur. Les services de notification et l'intégration avec l'API Google Places jouent un rôle clé dans la fonctionnalité de l'application.


  
