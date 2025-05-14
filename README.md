##Connexion
   admin: admin@aichi.fr mdp = admin
   user : user@aichi.fr mdp = user
```markdown
# Aichi Gestion - Configuration sur XAMPP

Ce guide explique comment configurer le projet **Aichi Gestion** sur XAMPP. 

## Prérequis

Avant de commencer, assurez-vous que les éléments suivants sont installés sur votre machine :

- [XAMPP](https://www.apachefriends.org/index.html) (inclut Apache et MySQL)
- Java Development Kit (JDK) pour exécuter le projet
- Un IDE Java (par exemple : IntelliJ IDEA, Eclipse ou NetBeans)
- [Git](https://git-scm.com/) pour cloner le projet (facultatif)

---

## Étapes d'installation

### 1. Cloner le projet

Commencez par cloner le dépôt GitHub sur votre machine locale. Ouvrez un terminal et exécutez la commande suivante :

```bash
git clone https://github.com/jeremyvidocin/Aichi-Gestion.git
cd Aichi-Gestion
```

### 2. Configurer la base de données MySQL

1. **Démarrer XAMPP** : Lancez XAMPP et démarrez les services **Apache** et **MySQL**.
2. **Accéder à phpMyAdmin** : Ouvrez votre navigateur et accédez à [http://localhost/phpmyadmin](http://localhost/phpmyadmin).
3. **Créer une base de données** :
   - Cliquez sur **Nouvelle base de données**.
   - Nommez-la `aichi_box` et cliquez sur **Créer**.
4. **Importer le fichier SQL** :
   - Dans phpMyAdmin, sélectionnez la base de données `aichi_box`.
   - Allez dans l'onglet **Importer**.
   - Cliquez sur **Choisir un fichier** et sélectionnez le fichier SQL situé dans `src/aichi_box.sql` du projet.
   - Cliquez sur **Exécuter** pour importer les tables et les données.

---

### 3. Configurer le projet Java

1. **Ouvrir le projet dans un IDE** :
   - Ouvrez le projet dans votre IDE Java préféré (par exemple : IntelliJ IDEA ou Eclipse).
2. **Configurer les dépendances** :
   - Ajoutez le connecteur MySQL (`mysql-connector-java`) au projet. Ce fichier JAR peut être téléchargé depuis [MySQL Connector/J](https://dev.mysql.com/downloads/connector/j/).
3. **Mettre à jour les paramètres de connexion à la base de données** :
   - Localisez le fichier de configuration contenant les informations de connexion (par exemple, un fichier `.properties` ou une classe de configuration).
   - Configurez les paramètres comme suit :
     ```properties
     db.url=jdbc:mysql://localhost:3306/aichi_box
     db.username=root
     db.password=
     ```
   - Remplacez `root` et le mot de passe vide par vos propres informations si vous avez configuré un mot de passe MySQL.

---

### 4. Compiler et exécuter le projet

1. Compilez le projet dans votre IDE.
2. Exécutez le projet pour vérifier qu'il fonctionne correctement.
3. Accédez à l'application via votre navigateur ou interface utilisateur selon les configurations du projet.

---

## Problèmes courants

- **Erreur de connexion à la base de données** :
  - Assurez-vous que MySQL est en cours d'exécution dans XAMPP.
  - Vérifiez que les paramètres de connexion (nom de la base, utilisateur, mot de passe) sont corrects.
- **Fichier SQL manquant** :
  - Assurez-vous que le fichier `src/aichi_box (5).sql` a bien été importé dans phpMyAdmin.

---

## Contribution

Les contributions sont les bienvenues ! Si vous souhaitez contribuer à ce projet, veuillez :

1. Forker le dépôt.
2. Créer une branche pour vos modifications :
   ```bash
   git checkout -b feature/ma-feature
   ```
3. Soumettre une Pull Request.

---

