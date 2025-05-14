-- Désactiver les contraintes de clé étrangère temporairement
SET FOREIGN_KEY_CHECKS = 0;

-- Mettre à jour les références dans les tables dépendantes
UPDATE commandes SET ID_Utilisateur = NULL;
UPDATE mouvements_stock SET ID_Utilisateur = NULL;
UPDATE suivi_commande SET ID_Utilisateur = NULL;

-- Vider la table utilisateurs
DELETE FROM utilisateurs;

-- Créer des utilisateurs de test avec des mots de passe hashés
INSERT INTO utilisateurs (ID, Nom, Prenom, Email, Telephone, MotDePasse, TypeAcces, DateCreation, Actif) VALUES
(1, 'admin', 'admin', 'admin@aichi.fr', '0123456789', SHA2('admin', 256), 'admin', NOW(), 1),
(2, 'user', 'user', 'user@aichi.fr', '0123456789', SHA2('user', 256), 'user', NOW(), 1),
(3, 'Alice', 'Dupont', 'alice@aichi.fr', '0123456789', SHA2('alice', 256), 'admin', NOW(), 1);

-- Réactiver les contraintes
SET FOREIGN_KEY_CHECKS = 1;

-- Afficher les utilisateurs créés (sans les mots de passe)
SELECT ID, Nom, Email, TypeAcces FROM utilisateurs; 