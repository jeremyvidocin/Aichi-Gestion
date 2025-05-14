-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : mer. 14 mai 2025 à 07:02
-- Version du serveur : 10.4.32-MariaDB
-- Version de PHP : 7.4.33

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `aichi_box`
--

DELIMITER $$
--
-- Procédures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `ajouter_article_commande` (IN `p_id_commande` INT, IN `p_id_article` INT, IN `p_quantite` INT)   BEGIN
    DECLARE v_reference VARCHAR(50);
    DECLARE v_designation VARCHAR(255);
    DECLARE v_prix_unitaire DECIMAL(10,2);
    
    -- Récupérer les informations de l'article
    SELECT Reference, Nom, PrixVente 
    INTO v_reference, v_designation, v_prix_unitaire
    FROM articles 
    WHERE ID = p_id_article;
    
    -- Insérer ou mettre à jour la ligne de commande
    INSERT INTO lignes_commande (
        ID_Commande, 
        ID_Article, 
        Reference, 
        Designation, 
        Quantite, 
        PrixUnitaireHT,
        TauxTVA
    )
    VALUES (
        p_id_commande,
        p_id_article,
        v_reference,
        v_designation,
        p_quantite,
        v_prix_unitaire,
        20.00
    )
    ON DUPLICATE KEY UPDATE 
        Quantite = Quantite + p_quantite;
        
    -- Mettre à jour les totaux de la commande
    UPDATE commandes c
    SET 
        c.MontantHT = (
            SELECT COALESCE(SUM(PrixUnitaireHT * Quantite), 0)
            FROM lignes_commande 
            WHERE ID_Commande = c.ID
        ),
        c.MontantTVA = (
            SELECT COALESCE(SUM(PrixUnitaireHT * Quantite * TauxTVA / 100), 0)
            FROM lignes_commande 
            WHERE ID_Commande = c.ID
        ),
        c.MontantTTC = (
            SELECT COALESCE(SUM(PrixUnitaireHT * Quantite * (1 + TauxTVA / 100)), 0)
            FROM lignes_commande 
            WHERE ID_Commande = c.ID
        )
    WHERE c.ID = p_id_commande;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `changer_statut_commande` (IN `p_id_commande` INT, IN `p_code_statut` VARCHAR(20), IN `p_commentaire` TEXT, IN `p_id_utilisateur` INT)   BEGIN
    DECLARE v_id_statut INT;
    
    -- Récupérer l'ID du statut
    SELECT ID INTO v_id_statut FROM statuts_commande WHERE Code = p_code_statut;
    
    -- Mettre à jour le statut de la commande
    UPDATE commandes SET ID_Statut = v_id_statut WHERE ID = p_id_commande;
    
    -- Ajouter une entrée dans le suivi des commandes
    INSERT INTO suivi_commande (ID_Commande, ID_Statut, Commentaire, ID_Utilisateur)
    VALUES (p_id_commande, v_id_statut, p_commentaire, p_id_utilisateur);
    
    -- Mettre à jour les dates spéciales selon le statut
    CASE p_code_statut
        WHEN 'VALIDEE' THEN 
            UPDATE commandes SET DateValidation = NOW() WHERE ID = p_id_commande;
        WHEN 'EXPEDIEE' THEN 
            UPDATE commandes SET DateExpedition = NOW() WHERE ID = p_id_commande;
        WHEN 'LIVREE' THEN 
            UPDATE commandes SET DateLivraison = NOW() WHERE ID = p_id_commande;
        ELSE BEGIN END;
    END CASE;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `creer_commande` (IN `p_id_client` INT, IN `p_commentaire` TEXT, OUT `p_id_commande` INT)   BEGIN
    DECLARE numero_commande VARCHAR(20);
    
    -- Générer un numéro de commande unique
    SET numero_commande = CONCAT('CMD', DATE_FORMAT(NOW(), '%Y%m%d'), LPAD((SELECT IFNULL(MAX(ID), 0) + 1 FROM commandes), 4, '0'));
    
    -- Insérer la commande
    INSERT INTO commandes (Numero, ID_Client, DateCommande, ID_Statut, Commentaire)
    VALUES (numero_commande, p_id_client, NOW(), 
            (SELECT ID FROM statuts_commande WHERE Code = 'RECUE'), 
            p_commentaire);
    
    -- Récupérer l'ID de la commande créée
    SET p_id_commande = LAST_INSERT_ID();
    
    -- Créer une entrée dans le suivi des commandes
    INSERT INTO suivi_commande (ID_Commande, ID_Statut, Commentaire)
    VALUES (p_id_commande, 
            (SELECT ID FROM statuts_commande WHERE Code = 'RECUE'), 
            'Création de la commande');
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `reset_user_password` (IN `p_user_id` INT, IN `p_new_password` VARCHAR(100))   BEGIN
    DECLARE v_salt VARCHAR(32);
    SET v_salt = generate_salt();
    
    UPDATE utilisateurs 
    SET Salt = v_salt,
        MotDePasse = SHA2(CONCAT(p_new_password, v_salt), 256)
    WHERE ID = p_user_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `update_user_password` (IN `p_user_id` INT, IN `p_new_password` VARCHAR(100))   BEGIN
    DECLARE v_salt VARCHAR(32);
    SET v_salt = generate_salt();
    
    UPDATE utilisateurs 
    SET 
        Salt = v_salt,
        MotDePasse = SHA2(CONCAT(p_new_password, v_salt), 256)
    WHERE ID = p_user_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `verify_password` (IN `p_email` VARCHAR(100), IN `p_password` VARCHAR(100), OUT `p_user_id` INT, OUT `p_is_valid` BOOLEAN)   BEGIN
    DECLARE v_stored_password VARCHAR(64);
    DECLARE v_salt VARCHAR(32);
    
    -- Récupérer le mot de passe hashé et le sel
    SELECT ID, MotDePasse, Salt 
    INTO p_user_id, v_stored_password, v_salt
    FROM utilisateurs 
    WHERE Email = p_email AND Actif = 1
    LIMIT 1;
    
    -- Vérifier si l'utilisateur existe et si le mot de passe correspond
    IF p_user_id IS NOT NULL AND v_stored_password = SHA2(CONCAT(p_password, v_salt), 256) THEN
        SET p_is_valid = TRUE;
    ELSE
        SET p_is_valid = FALSE;
        SET p_user_id = NULL;
    END IF;
END$$

--
-- Fonctions
--
CREATE DEFINER=`root`@`localhost` FUNCTION `generate_salt` () RETURNS VARCHAR(32) CHARSET utf8mb4 COLLATE utf8mb4_general_ci  BEGIN
    RETURN MD5(RAND());
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Structure de la table `articles`
--

CREATE TABLE `articles` (
  `ID` int(11) NOT NULL,
  `Reference` varchar(50) DEFAULT NULL,
  `Nom` varchar(100) DEFAULT NULL,
  `Description` text DEFAULT NULL,
  `Categorie` varchar(50) DEFAULT NULL,
  `PrixVente` decimal(10,2) DEFAULT NULL,
  `PrixAchat` decimal(10,2) DEFAULT NULL,
  `QuantiteEnStock` int(11) DEFAULT NULL,
  `SeuilAlerte` int(11) DEFAULT 5,
  `DateCreation` datetime DEFAULT current_timestamp(),
  `DateModification` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `articles`
--

INSERT INTO `articles` (`ID`, `Reference`, `Nom`, `Description`, `Categorie`, `PrixVente`, `PrixAchat`, `QuantiteEnStock`, `SeuilAlerte`, `DateCreation`, `DateModification`) VALUES
(1, 'ART00001', 'Ordinateur portable', 'Ordinateur portable haut de gamme', 'Informatique', 1200.00, 0.00, 50, 5, '2025-05-06 16:10:12', '2025-05-14 04:58:59'),
(2, 'ART00002', 'Smartphone', 'Smartphone dernier modèle', 'Smartphones', 800.00, NULL, 14, 5, '2025-05-06 16:10:12', '2025-05-14 00:58:37'),
(3, 'ART00003', 'Tablette', 'Tablette tactile', 'Tablettes', 300.00, NULL, 10, 5, '2025-05-06 16:10:12', '2025-05-14 00:58:37'),
(4, 'ART00004', 'Casque audio', 'Casque audio sans fil', 'Audio', 150.00, NULL, 19, 5, '2025-05-06 16:10:12', '2025-05-14 00:09:36'),
(5, 'ART00005', 'Souris sans fil', 'Souris sans fil ergonomique', 'Périphériques', 50.00, NULL, 50, 5, '2025-05-06 16:10:12', '2025-05-13 23:21:52'),
(6, 'ART00006', 'Tapis de souris', 'Tapis en tissu pour souris', 'Périphériques', 5.00, NULL, 120, 5, '2025-05-06 16:10:12', '2025-05-06 16:10:13'),
(7, 'ART18158', 'Cable USB', 'Cables USB ', 'Non catégorisé', 10.00, 0.00, 200, 5, '2025-05-12 16:06:58', '2025-05-12 16:06:58');

-- --------------------------------------------------------

--
-- Structure de la table `categories`
--

CREATE TABLE `categories` (
  `ID` int(11) NOT NULL,
  `Nom` varchar(100) NOT NULL,
  `Description` text DEFAULT NULL,
  `ParentID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `categories`
--

INSERT INTO `categories` (`ID`, `Nom`, `Description`, `ParentID`) VALUES
(1, 'Ordinateurs', 'Tous types d\'ordinateurs', NULL),
(2, 'Périphériques', 'Accessoires et périphériques informatiques', NULL),
(3, 'Smartphones', 'Téléphones intelligents', NULL),
(4, 'Tablettes', 'Tablettes tactiles', NULL),
(5, 'Audio', 'Équipement audio', NULL),
(6, 'Ordinateurs portables', 'Ordinateurs transportables', 1),
(7, 'Ordinateurs de bureau', 'Ordinateurs fixes', 1),
(8, 'Souris', 'Périphériques de pointage', 2),
(9, 'Claviers', 'Périphériques de saisie', 2),
(10, 'Écouteurs', 'Écouteurs audio', 5),
(11, 'Casques', 'Casques audio', 5);

-- --------------------------------------------------------

--
-- Structure de la table `clients`
--

CREATE TABLE `clients` (
  `ID` int(11) NOT NULL,
  `Nom` varchar(100) DEFAULT NULL,
  `Prenom` varchar(100) DEFAULT NULL,
  `Societe` varchar(100) DEFAULT NULL,
  `Adresse` varchar(255) DEFAULT NULL,
  `CodePostal` varchar(10) DEFAULT NULL,
  `Ville` varchar(100) DEFAULT NULL,
  `Pays` varchar(50) DEFAULT 'France',
  `Telephone` varchar(20) DEFAULT NULL,
  `Email` varchar(100) DEFAULT NULL,
  `DateInscription` datetime DEFAULT current_timestamp(),
  `Actif` tinyint(1) DEFAULT 1,
  `DateDerniereCommande` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `clients`
--

INSERT INTO `clients` (`ID`, `Nom`, `Prenom`, `Societe`, `Adresse`, `CodePostal`, `Ville`, `Pays`, `Telephone`, `Email`, `DateInscription`, `Actif`, `DateDerniereCommande`) VALUES
(1, 'Dubois', 'Jean', 'Dubois Électronique', '15 rue des Artisans', '75001', 'Paris', 'France', '01 23 45 67 89', 'jean.dubois@dubois-electronique.fr', '2024-07-05 16:56:13', 1, '2025-05-13 14:05:53'),
(2, 'Martin', 'Sophie', 'InfoTech Solutions', '8 avenue des Technologies', '69002', 'Lyon', 'France', '04 78 12 34 56', 'sophie.martin@infotech-solutions.fr', '2024-07-07 16:56:13', 1, NULL),
(3, 'Petit', 'Marie', 'Digital Services SARL', '25 boulevard de l\'Innovation', '33000', 'Bordeaux', 'France', '05 56 78 90 12', 'marie.petit@digital-services.fr', '2024-09-07 16:56:13', 1, '2025-05-14 00:09:18'),
(4, 'Leroy', 'Thomas', 'Leroy Informatique', '42 rue du Commerce', '44000', 'Nantes', 'France', '02 40 11 22 33', 'thomas.leroy@leroy-info.fr', '2024-07-08 16:56:13', 1, NULL),
(5, 'Garcia', 'Antoine', 'Tech Innovations', '3 rue de la République', '13001', 'Marseille', 'France', '04 91 23 45 67', 'a.garcia@tech-innovations.fr', '2025-02-27 16:56:13', 1, '2025-05-14 00:57:15'),
(6, 'Moreau', 'Claire', 'DataSys', '18 rue des Capucins', '59000', 'Lille', 'France', '03 20 45 67 89', 'claire.moreau@datasys.fr', '2024-11-17 16:56:13', 1, NULL),
(7, 'Bernard', 'Philippe', 'Serveurs Plus', '5 avenue Foch', '67000', 'Strasbourg', 'France', '03 88 34 56 78', 'p.bernard@serveurs-plus.fr', '2024-07-21 16:56:13', 1, NULL),
(8, 'Roux', 'Isabelle', 'Web Solutions', '12 place Bellecour', '69001', 'Lyon', 'France', '04 72 89 01 23', 'i.roux@websolutions.fr', '2024-10-06 16:56:13', 1, NULL),
(9, 'Lambert', 'Michel', 'Réseau Pro', '7 rue du Général de Gaul', '31000', 'Toulouse', 'Francee', '05 61 12 34 56', 'm.lambert@reseau-pro.fr', '2024-10-17 16:56:13', 1, NULL);

-- --------------------------------------------------------

--
-- Structure de la table `commandes`
--

CREATE TABLE `commandes` (
  `ID` int(11) NOT NULL,
  `Numero` varchar(20) DEFAULT NULL,
  `Reference` varchar(50) DEFAULT NULL,
  `ID_Client` int(11) DEFAULT NULL,
  `DateCommande` datetime DEFAULT current_timestamp(),
  `DateValidation` datetime DEFAULT NULL,
  `DateExpedition` datetime DEFAULT NULL,
  `DateLivraison` datetime DEFAULT NULL,
  `Statut` varchar(50) DEFAULT 'En attente',
  `ID_Statut` int(11) DEFAULT NULL,
  `MontantHT` decimal(10,2) DEFAULT 0.00,
  `TauxTVA` decimal(5,2) DEFAULT 20.00,
  `MontantTVA` decimal(10,2) DEFAULT 0.00,
  `MontantTTC` decimal(10,2) DEFAULT 0.00,
  `Commentaire` text DEFAULT NULL,
  `AdresseLivraison` text DEFAULT NULL,
  `ID_Utilisateur` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `commandes`
--

INSERT INTO `commandes` (`ID`, `Numero`, `Reference`, `ID_Client`, `DateCommande`, `DateValidation`, `DateExpedition`, `DateLivraison`, `Statut`, `ID_Statut`, `MontantHT`, `TauxTVA`, `MontantTVA`, `MontantTTC`, `Commentaire`, `AdresseLivraison`, `ID_Utilisateur`) VALUES
(1, 'CMD202505060001', NULL, 1, '2025-05-06 15:40:32', NULL, NULL, NULL, 'Livrée', 7, 5900.00, 20.00, 1180.00, 7080.00, NULL, NULL, NULL),
(2, 'CMD202505060002', NULL, 2, '2025-05-06 15:40:32', NULL, NULL, NULL, 'En cours', 6, 2850.00, 20.00, 570.00, 3420.00, NULL, NULL, NULL),
(3, 'CMD202505060003', NULL, 4, '2025-05-06 15:40:32', NULL, NULL, NULL, 'En attente', 2, 21150.00, 20.00, 4230.00, 25380.00, NULL, NULL, NULL),
(9, 'CMD202505130009', NULL, 3, '2025-05-13 16:43:23', NULL, NULL, NULL, 'En attente', 1, 150.00, 20.00, 30.00, 180.00, '', NULL, NULL),
(10, 'CMD202505140010', NULL, 3, '2025-05-14 00:09:18', NULL, NULL, NULL, 'En attente', 1, 150.00, 20.00, 30.00, 180.00, '', NULL, 1);

--
-- Déclencheurs `commandes`
--
DELIMITER $$
CREATE TRIGGER `after_insert_commande_update_client` AFTER INSERT ON `commandes` FOR EACH ROW BEGIN
    UPDATE clients
    SET DateDerniereCommande = NEW.DateCommande
    WHERE ID = NEW.ID_Client;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Structure de la table `commande_articles`
--

CREATE TABLE `commande_articles` (
  `id_commande` int(11) NOT NULL,
  `id_article` int(11) NOT NULL,
  `quantite` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Déchargement des données de la table `commande_articles`
--

INSERT INTO `commande_articles` (`id_commande`, `id_article`, `quantite`) VALUES
(1, 1, 2),
(1, 3, 1),
(2, 2, 3),
(2, 4, 1),
(3, 1, 2),
(3, 5, 4);

-- --------------------------------------------------------

--
-- Structure de la table `detailscommande`
--

CREATE TABLE `detailscommande` (
  `ID` int(11) NOT NULL,
  `ID_Commande` int(11) DEFAULT NULL,
  `ID_Article` int(11) DEFAULT NULL,
  `Quantite` int(11) DEFAULT NULL,
  `PrixUnitaire` decimal(10,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `detailscommande`
--

INSERT INTO `detailscommande` (`ID`, `ID_Commande`, `ID_Article`, `Quantite`, `PrixUnitaire`) VALUES
(1, 1, 1, 2, 1200.00),
(2, 1, 2, 1, 800.00),
(3, 2, 3, 1, 300.00),
(4, 2, 4, 2, 150.00),
(5, 3, 5, 3, 50.00);

-- --------------------------------------------------------

--
-- Structure de la table `factures`
--

CREATE TABLE `factures` (
  `ID` int(11) NOT NULL,
  `Numero` varchar(20) NOT NULL,
  `ID_Commande` int(11) NOT NULL,
  `DateFacture` datetime DEFAULT current_timestamp(),
  `DateEcheance` datetime DEFAULT NULL,
  `MontantHT` decimal(10,2) NOT NULL,
  `MontantTVA` decimal(10,2) NOT NULL,
  `MontantTTC` decimal(10,2) NOT NULL,
  `Statut` varchar(50) DEFAULT 'Non payée',
  `DatePaiement` datetime DEFAULT NULL,
  `ModePaiement` varchar(50) DEFAULT NULL,
  `ReferencePaiement` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `factures`
--

INSERT INTO `factures` (`ID`, `Numero`, `ID_Commande`, `DateFacture`, `DateEcheance`, `MontantHT`, `MontantTVA`, `MontantTTC`, `Statut`, `DatePaiement`, `ModePaiement`, `ReferencePaiement`) VALUES
(1, 'FACT202505060001', 1, '2025-05-06 15:40:32', NULL, 2700.00, 540.00, 3240.00, 'Payée', NULL, NULL, NULL),
(2, 'FACT202505060002', 2, '2025-05-06 15:40:32', NULL, 2550.00, 510.00, 3060.00, 'Non payée', NULL, NULL, NULL),
(3, 'FACT202505060003', 3, '2025-05-06 15:40:32', NULL, 2600.00, 520.00, 3120.00, 'Non payée', NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Structure de la table `lignes_commande`
--

CREATE TABLE `lignes_commande` (
  `ID` int(11) NOT NULL,
  `ID_Commande` int(11) NOT NULL,
  `ID_Article` int(11) NOT NULL,
  `Reference` varchar(50) DEFAULT NULL,
  `Designation` varchar(255) DEFAULT NULL,
  `Quantite` int(11) NOT NULL DEFAULT 1,
  `PrixUnitaireHT` decimal(10,2) NOT NULL,
  `TauxTVA` decimal(5,2) DEFAULT 20.00,
  `MontantHT` decimal(10,2) GENERATED ALWAYS AS (`PrixUnitaireHT` * `Quantite`) STORED,
  `MontantTVA` decimal(10,2) GENERATED ALWAYS AS (`PrixUnitaireHT` * `Quantite` * `TauxTVA` / 100) STORED,
  `MontantTTC` decimal(10,2) GENERATED ALWAYS AS (`PrixUnitaireHT` * `Quantite` * (1 + `TauxTVA` / 100)) STORED
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `lignes_commande`
--

INSERT INTO `lignes_commande` (`ID`, `ID_Commande`, `ID_Article`, `Reference`, `Designation`, `Quantite`, `PrixUnitaireHT`, `TauxTVA`) VALUES
(1, 1, 1, 'ART00001', 'Ordinateur portable', 4, 1200.00, 20.00),
(2, 2, 4, 'ART00004', 'Casque audio', 1, 150.00, 20.00),
(4, 1, 3, 'ART00003', 'Tablette', 1, 300.00, 20.00),
(5, 2, 2, 'ART00002', 'Smartphone', 3, 800.00, 20.00),
(13, 1, 2, 'ART00002', 'Smartphone', 1, 800.00, 20.00),
(14, 2, 3, 'ART00003', 'Tablette', 1, 300.00, 20.00),
(20, 9, 4, 'ART00004', 'Casque audio', 1, 150.00, 20.00),
(25, 3, 5, 'ART00005', 'Souris sans fil', 4, 50.00, 20.00),
(26, 3, 1, 'ART00001', 'Ordinateur portable', 13, 1200.00, 20.00),
(27, 3, 2, 'ART00002', 'Smartphone', 5, 800.00, 20.00),
(28, 3, 4, 'ART00004', 'Casque audio', 9, 150.00, 20.00),
(30, 10, 4, 'ART00004', 'Casque audio', 1, 150.00, 20.00);

--
-- Déclencheurs `lignes_commande`
--
DELIMITER $$
CREATE TRIGGER `after_delete_ligne_commande` AFTER DELETE ON `lignes_commande` FOR EACH ROW BEGIN
    DECLARE stock_avant INT;
    
    -- Récupérer le stock actuel
    SELECT QuantiteEnStock INTO stock_avant FROM articles WHERE ID = OLD.ID_Article;
    
    -- Insérer un mouvement de stock (retour en stock)
    INSERT INTO mouvements_stock (ID_Article, Type, Quantite, StockAvant, StockApres, Motif, Reference)
    VALUES (OLD.ID_Article, 'ENTREE', OLD.Quantite, stock_avant, stock_avant + OLD.Quantite, 
            'Annulation ligne commande', 
            (SELECT Numero FROM commandes WHERE ID = OLD.ID_Commande));
    
    -- Mettre à jour le stock dans la table articles
    UPDATE articles SET QuantiteEnStock = stock_avant + OLD.Quantite WHERE ID = OLD.ID_Article;
    
    -- Mettre à jour les totaux de la commande
    UPDATE commandes c
    SET c.MontantHT = COALESCE((
        SELECT SUM(l.MontantHT) FROM lignes_commande l WHERE l.ID_Commande = c.ID
    ), 0),
    c.MontantTVA = COALESCE((
        SELECT SUM(l.MontantTVA) FROM lignes_commande l WHERE l.ID_Commande = c.ID
    ), 0),
    c.MontantTTC = COALESCE((
        SELECT SUM(l.MontantTTC) FROM lignes_commande l WHERE l.ID_Commande = c.ID
    ), 0)
    WHERE c.ID = OLD.ID_Commande;
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `after_insert_ligne_commande` AFTER INSERT ON `lignes_commande` FOR EACH ROW BEGIN
    DECLARE stock_avant INT;
    
    -- Récupérer le stock actuel
    SELECT QuantiteEnStock INTO stock_avant FROM articles WHERE ID = NEW.ID_Article;
    
    -- Insérer un mouvement de stock
    INSERT INTO mouvements_stock (ID_Article, Type, Quantite, StockAvant, StockApres, Motif, Reference)
    VALUES (NEW.ID_Article, 'SORTIE', NEW.Quantite, stock_avant, stock_avant - NEW.Quantite, 
            'Commande', (SELECT Numero FROM commandes WHERE ID = NEW.ID_Commande));
    
    -- Mettre à jour le stock dans la table articles
    UPDATE articles SET QuantiteEnStock = stock_avant - NEW.Quantite WHERE ID = NEW.ID_Article;
    
    -- Mettre à jour les totaux de la commande
    UPDATE commandes c
    SET c.MontantHT = (
        SELECT COALESCE(SUM(l.MontantHT), 0) FROM lignes_commande l WHERE l.ID_Commande = c.ID
    ),
    c.MontantTVA = (
        SELECT COALESCE(SUM(l.MontantTVA), 0) FROM lignes_commande l WHERE l.ID_Commande = c.ID
    ),
    c.MontantTTC = (
        SELECT COALESCE(SUM(l.MontantTTC), 0) FROM lignes_commande l WHERE l.ID_Commande = c.ID
    )
    WHERE c.ID = NEW.ID_Commande;
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `after_update_ligne_commande` AFTER UPDATE ON `lignes_commande` FOR EACH ROW BEGIN
    DECLARE stock_avant INT;
    DECLARE diff_quantite INT;
    
    IF NEW.Quantite <> OLD.Quantite THEN
        -- Calculer la différence de quantité
        SET diff_quantite = NEW.Quantite - OLD.Quantite;
        
        -- Récupérer le stock actuel
        SELECT QuantiteEnStock INTO stock_avant FROM articles WHERE ID = NEW.ID_Article;
        
        -- Insérer un mouvement de stock
        INSERT INTO mouvements_stock (ID_Article, Type, Quantite, StockAvant, StockApres, Motif, Reference)
        VALUES (NEW.ID_Article, 
               IF(diff_quantite > 0, 'SORTIE', 'ENTREE'), 
               ABS(diff_quantite), 
               stock_avant, 
               stock_avant - diff_quantite, 
               'Modification commande', 
               (SELECT Numero FROM commandes WHERE ID = NEW.ID_Commande));
        
        -- Mettre à jour le stock dans la table articles
        UPDATE articles SET QuantiteEnStock = stock_avant - diff_quantite WHERE ID = NEW.ID_Article;
    END IF;
    
    -- Mettre à jour les totaux de la commande
    UPDATE commandes c
    SET c.MontantHT = (
        SELECT COALESCE(SUM(l.MontantHT), 0) FROM lignes_commande l WHERE l.ID_Commande = c.ID
    ),
    c.MontantTVA = (
        SELECT COALESCE(SUM(l.MontantTVA), 0) FROM lignes_commande l WHERE l.ID_Commande = c.ID
    ),
    c.MontantTTC = (
        SELECT COALESCE(SUM(l.MontantTTC), 0) FROM lignes_commande l WHERE l.ID_Commande = c.ID
    )
    WHERE c.ID = NEW.ID_Commande;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Structure de la table `livraisons`
--

CREATE TABLE `livraisons` (
  `ID` int(11) NOT NULL,
  `ID_Commande` int(11) NOT NULL,
  `DateExpedition` datetime DEFAULT NULL,
  `DateLivraisonPrevue` datetime DEFAULT NULL,
  `DateLivraisonReelle` datetime DEFAULT NULL,
  `NumeroSuivi` varchar(50) DEFAULT NULL,
  `Transporteur` varchar(100) DEFAULT NULL,
  `AdresseLivraison` text DEFAULT NULL,
  `Destinataire` varchar(255) DEFAULT NULL,
  `Telephone` varchar(20) DEFAULT NULL,
  `Instructions` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `mouvements_stock`
--

CREATE TABLE `mouvements_stock` (
  `ID` int(11) NOT NULL,
  `ID_Article` int(11) NOT NULL,
  `Type` enum('ENTREE','SORTIE') NOT NULL,
  `Quantite` int(11) NOT NULL,
  `StockAvant` int(11) NOT NULL,
  `StockApres` int(11) NOT NULL,
  `DateMouvement` datetime DEFAULT current_timestamp(),
  `Motif` varchar(100) DEFAULT NULL,
  `Reference` varchar(100) DEFAULT NULL,
  `ID_Utilisateur` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `mouvements_stock`
--

INSERT INTO `mouvements_stock` (`ID`, `ID_Article`, `Type`, `Quantite`, `StockAvant`, `StockApres`, `DateMouvement`, `Motif`, `Reference`, `ID_Utilisateur`) VALUES
(1, 1, 'SORTIE', 1, 10, 9, '2025-05-13 12:01:46', 'Modification commande', 'CMD202505060003', NULL),
(2, 1, 'SORTIE', 3, 9, 6, '2025-05-13 12:25:42', 'Modification commande', 'CMD202505060003', NULL),
(3, 2, 'SORTIE', 5, 20, 15, '2025-05-13 14:05:53', 'Commande', 'CMD202505060003', NULL),
(5, 4, 'SORTIE', 9, 30, 21, '2025-05-13 16:24:36', 'Commande', 'CMD202505060003', NULL),
(6, 1, 'SORTIE', 4, 6, 2, '2025-05-13 16:24:36', 'Modification commande', 'CMD202505060003', NULL),
(7, 1, 'SORTIE', 2, 2, 0, '2025-05-13 16:29:13', 'Modification commande', 'CMD202505060001', NULL),
(8, 1, 'SORTIE', 3, 0, -3, '2025-05-13 16:35:24', 'Modification commande', 'CMD202505060003', NULL),
(9, 4, 'SORTIE', 1, 21, 20, '2025-05-13 16:43:23', 'Commande', 'CMD202505130009', NULL),
(10, 5, 'ENTREE', 4, 50, 54, '2025-05-13 23:20:56', 'Annulation ligne commande', 'CMD202505060003', NULL),
(11, 1, 'ENTREE', 13, -3, 10, '2025-05-13 23:20:56', 'Annulation ligne commande', 'CMD202505060003', NULL),
(12, 2, 'ENTREE', 5, 15, 20, '2025-05-13 23:20:56', 'Annulation ligne commande', 'CMD202505060003', NULL),
(13, 4, 'ENTREE', 9, 20, 29, '2025-05-13 23:20:56', 'Annulation ligne commande', 'CMD202505060003', NULL),
(14, 5, 'SORTIE', 4, 54, 50, '2025-05-13 23:20:56', 'Commande', 'CMD202505060003', NULL),
(15, 1, 'SORTIE', 13, 10, -3, '2025-05-13 23:20:56', 'Commande', 'CMD202505060003', NULL),
(16, 2, 'SORTIE', 5, 20, 15, '2025-05-13 23:20:56', 'Commande', 'CMD202505060003', NULL),
(17, 4, 'SORTIE', 9, 29, 20, '2025-05-13 23:20:56', 'Commande', 'CMD202505060003', NULL),
(18, 5, 'ENTREE', 4, 50, 54, '2025-05-13 23:21:52', 'Annulation ligne commande', 'CMD202505060003', NULL),
(19, 1, 'ENTREE', 13, -3, 10, '2025-05-13 23:21:52', 'Annulation ligne commande', 'CMD202505060003', NULL),
(20, 2, 'ENTREE', 5, 15, 20, '2025-05-13 23:21:52', 'Annulation ligne commande', 'CMD202505060003', NULL),
(21, 4, 'ENTREE', 9, 20, 29, '2025-05-13 23:21:52', 'Annulation ligne commande', 'CMD202505060003', NULL),
(22, 5, 'SORTIE', 4, 54, 50, '2025-05-13 23:21:52', 'Commande', 'CMD202505060003', NULL),
(23, 1, 'SORTIE', 13, 10, -3, '2025-05-13 23:21:52', 'Commande', 'CMD202505060003', NULL),
(24, 2, 'SORTIE', 5, 20, 15, '2025-05-13 23:21:52', 'Commande', 'CMD202505060003', NULL),
(25, 4, 'SORTIE', 9, 29, 20, '2025-05-13 23:21:53', 'Commande', 'CMD202505060003', NULL),
(26, 4, 'SORTIE', 1, 20, 19, '2025-05-14 00:09:18', 'Commande', 'CMD202505140010', NULL),
(27, 4, 'ENTREE', 1, 19, 20, '2025-05-14 00:09:36', 'Annulation ligne commande', 'CMD202505140010', NULL),
(28, 4, 'SORTIE', 1, 20, 19, '2025-05-14 00:09:36', 'Commande', 'CMD202505140010', NULL),
(29, 1, 'SORTIE', 1, -3, -4, '2025-05-14 00:57:15', 'Commande', 'CMD202505140011', NULL),
(30, 3, 'SORTIE', 5, 15, 10, '2025-05-14 00:57:15', 'Commande', 'CMD202505140011', NULL),
(35, 1, 'ENTREE', 1, -4, -3, '2025-05-14 00:58:15', 'Annulation ligne commande', 'CMD202505140011', NULL),
(36, 3, 'ENTREE', 5, 10, 15, '2025-05-14 00:58:15', 'Annulation ligne commande', 'CMD202505140011', NULL),
(37, 3, 'SORTIE', 5, 15, 10, '2025-05-14 00:58:15', 'Commande', 'CMD202505140011', NULL),
(38, 3, 'ENTREE', 5, 10, 15, '2025-05-14 00:58:37', 'Annulation ligne commande', 'CMD202505140011', NULL),
(39, 3, 'SORTIE', 5, 15, 10, '2025-05-14 00:58:37', 'Commande', 'CMD202505140011', NULL),
(40, 2, 'SORTIE', 1, 15, 14, '2025-05-14 00:58:37', 'Commande', 'CMD202505140011', NULL);

-- --------------------------------------------------------

--
-- Structure de la table `statuts_commande`
--

CREATE TABLE `statuts_commande` (
  `ID` int(11) NOT NULL,
  `Code` varchar(20) NOT NULL,
  `Libelle` varchar(50) NOT NULL,
  `Description` text DEFAULT NULL,
  `Ordre` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `statuts_commande`
--

INSERT INTO `statuts_commande` (`ID`, `Code`, `Libelle`, `Description`, `Ordre`) VALUES
(1, 'RECUE', 'Reçue', 'Commande reçue mais non traitée', 10),
(2, 'EN_ATTENTE', 'En attente', 'Commande en attente de validation', 20),
(3, 'VALIDEE', 'Validée', 'Commande validée mais non préparée', 30),
(4, 'EN_PREPARATION', 'En préparation', 'Commande en cours de préparation', 40),
(5, 'EXPEDIEE', 'Expédiée', 'Commande expédiée', 50),
(6, 'EN_COURS', 'En cours de livraison', 'Commande en cours de livraison', 60),
(7, 'LIVREE', 'Livrée', 'Commande livrée au client', 70),
(8, 'ANNULEE', 'Annulée', 'Commande annulée', 999);

-- --------------------------------------------------------

--
-- Structure de la table `suivi_commande`
--

CREATE TABLE `suivi_commande` (
  `ID` int(11) NOT NULL,
  `ID_Commande` int(11) NOT NULL,
  `DateMiseAJour` datetime DEFAULT current_timestamp(),
  `ID_Statut` int(11) NOT NULL,
  `Commentaire` text DEFAULT NULL,
  `ID_Utilisateur` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `suivi_commande`
--

INSERT INTO `suivi_commande` (`ID`, `ID_Commande`, `DateMiseAJour`, `ID_Statut`, `Commentaire`, `ID_Utilisateur`) VALUES
(1, 1, '2025-05-06 15:40:32', 7, 'Migration initiale', NULL),
(2, 2, '2025-05-06 15:40:32', 6, 'Migration initiale', NULL),
(3, 3, '2025-05-06 15:40:32', 2, 'Migration initiale', NULL),
(9, 9, '2025-05-13 16:43:23', 1, 'Création de la commande', NULL),
(10, 10, '2025-05-14 00:09:18', 1, 'Création de la commande', NULL);

-- --------------------------------------------------------

--
-- Structure de la table `temp_commande_data`
--

CREATE TABLE `temp_commande_data` (
  `id_commande` int(11) DEFAULT NULL,
  `id_article` int(11) DEFAULT NULL,
  `quantite` int(11) DEFAULT NULL,
  `Reference` varchar(50) DEFAULT NULL,
  `Designation` varchar(100) DEFAULT NULL,
  `PrixUnitaireHT` decimal(10,2) DEFAULT NULL,
  `TauxTVA` decimal(4,2) NOT NULL DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `temp_commande_data`
--

INSERT INTO `temp_commande_data` (`id_commande`, `id_article`, `quantite`, `Reference`, `Designation`, `PrixUnitaireHT`, `TauxTVA`) VALUES
(1, 1, 2, 'ART00001', 'Ordinateur portable', 1200.00, 20.00),
(1, 3, 1, 'ART00003', 'Tablette', 300.00, 20.00),
(2, 2, 3, 'ART00002', 'Smartphone', 800.00, 20.00),
(2, 4, 1, 'ART00004', 'Casque audio', 150.00, 20.00),
(3, 1, 2, 'ART00001', 'Ordinateur portable', 1200.00, 20.00),
(3, 5, 4, 'ART00005', 'Souris sans fil', 50.00, 20.00),
(1, 2, 1, 'ART00002', 'Smartphone', 800.00, 20.00),
(2, 3, 1, 'ART00003', 'Tablette', 300.00, 20.00),
(2, 4, 2, 'ART00004', 'Casque audio', 150.00, 20.00),
(3, 5, 3, 'ART00005', 'Souris sans fil', 50.00, 20.00);

-- --------------------------------------------------------

--
-- Structure de la table `utilisateurs`
--

CREATE TABLE `utilisateurs` (
  `ID` int(11) NOT NULL,
  `Nom` varchar(100) DEFAULT NULL,
  `Prenom` varchar(100) DEFAULT NULL,
  `Email` varchar(100) DEFAULT NULL,
  `Telephone` varchar(20) DEFAULT NULL,
  `MotDePasse` varchar(100) DEFAULT NULL,
  `TypeAcces` varchar(50) DEFAULT NULL,
  `DateCreation` datetime DEFAULT current_timestamp(),
  `DerniereConnexion` datetime DEFAULT NULL,
  `Actif` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `utilisateurs`
--

INSERT INTO `utilisateurs` (`ID`, `Nom`, `Prenom`, `Email`, `Telephone`, `MotDePasse`, `TypeAcces`, `DateCreation`, `DerniereConnexion`, `Actif`) VALUES
(1, 'admin', 'admin', 'admin@aichi.fr', '0123456789', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'admin', '2025-05-13 18:30:30', NULL, 1),
(2, 'user', 'user', 'user@aichi.fr', '0123456789', '04f8996da763b7a969b1028ee3007569eaf3a635486ddab211d512c85b9df8fb', 'user', '2025-05-13 18:30:30', NULL, 1),
(3, 'Alice', 'Dupont', 'alice@aichi.fr', '0123456789', '2bd806c97f0e00af1a1fc3328fa763a9269723c8db8fac4f93af71db186d6e90', 'admin', '2025-05-13 18:30:30', NULL, 1);

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `articles`
--
ALTER TABLE `articles`
  ADD PRIMARY KEY (`ID`),
  ADD UNIQUE KEY `Reference` (`Reference`);

--
-- Index pour la table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `ParentID` (`ParentID`);

--
-- Index pour la table `clients`
--
ALTER TABLE `clients`
  ADD PRIMARY KEY (`ID`);

--
-- Index pour la table `commandes`
--
ALTER TABLE `commandes`
  ADD PRIMARY KEY (`ID`),
  ADD UNIQUE KEY `Numero` (`Numero`),
  ADD KEY `ID_Client` (`ID_Client`),
  ADD KEY `ID_Statut` (`ID_Statut`),
  ADD KEY `ID_Utilisateur` (`ID_Utilisateur`);

--
-- Index pour la table `commande_articles`
--
ALTER TABLE `commande_articles`
  ADD PRIMARY KEY (`id_commande`,`id_article`),
  ADD KEY `id_article` (`id_article`);

--
-- Index pour la table `detailscommande`
--
ALTER TABLE `detailscommande`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `ID_Commande` (`ID_Commande`),
  ADD KEY `ID_Article` (`ID_Article`);

--
-- Index pour la table `factures`
--
ALTER TABLE `factures`
  ADD PRIMARY KEY (`ID`),
  ADD UNIQUE KEY `Numero` (`Numero`),
  ADD UNIQUE KEY `ID_Commande` (`ID_Commande`);

--
-- Index pour la table `lignes_commande`
--
ALTER TABLE `lignes_commande`
  ADD PRIMARY KEY (`ID`),
  ADD UNIQUE KEY `commande_article` (`ID_Commande`,`ID_Article`),
  ADD KEY `ID_Article` (`ID_Article`);

--
-- Index pour la table `livraisons`
--
ALTER TABLE `livraisons`
  ADD PRIMARY KEY (`ID`),
  ADD UNIQUE KEY `ID_Commande` (`ID_Commande`);

--
-- Index pour la table `mouvements_stock`
--
ALTER TABLE `mouvements_stock`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `ID_Article` (`ID_Article`),
  ADD KEY `ID_Utilisateur` (`ID_Utilisateur`);

--
-- Index pour la table `statuts_commande`
--
ALTER TABLE `statuts_commande`
  ADD PRIMARY KEY (`ID`),
  ADD UNIQUE KEY `Code` (`Code`);

--
-- Index pour la table `suivi_commande`
--
ALTER TABLE `suivi_commande`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `ID_Commande` (`ID_Commande`),
  ADD KEY `ID_Statut` (`ID_Statut`),
  ADD KEY `ID_Utilisateur` (`ID_Utilisateur`);

--
-- Index pour la table `utilisateurs`
--
ALTER TABLE `utilisateurs`
  ADD PRIMARY KEY (`ID`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `articles`
--
ALTER TABLE `articles`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT pour la table `categories`
--
ALTER TABLE `categories`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT pour la table `clients`
--
ALTER TABLE `clients`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT pour la table `commandes`
--
ALTER TABLE `commandes`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT pour la table `detailscommande`
--
ALTER TABLE `detailscommande`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT pour la table `factures`
--
ALTER TABLE `factures`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT pour la table `lignes_commande`
--
ALTER TABLE `lignes_commande`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=39;

--
-- AUTO_INCREMENT pour la table `livraisons`
--
ALTER TABLE `livraisons`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `mouvements_stock`
--
ALTER TABLE `mouvements_stock`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=41;

--
-- AUTO_INCREMENT pour la table `statuts_commande`
--
ALTER TABLE `statuts_commande`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT pour la table `suivi_commande`
--
ALTER TABLE `suivi_commande`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT pour la table `utilisateurs`
--
ALTER TABLE `utilisateurs`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `categories`
--
ALTER TABLE `categories`
  ADD CONSTRAINT `categories_ibfk_1` FOREIGN KEY (`ParentID`) REFERENCES `categories` (`ID`) ON DELETE SET NULL;

--
-- Contraintes pour la table `commandes`
--
ALTER TABLE `commandes`
  ADD CONSTRAINT `commandes_ibfk_1` FOREIGN KEY (`ID_Client`) REFERENCES `clients` (`ID`),
  ADD CONSTRAINT `commandes_ibfk_2` FOREIGN KEY (`ID_Statut`) REFERENCES `statuts_commande` (`ID`),
  ADD CONSTRAINT `commandes_ibfk_3` FOREIGN KEY (`ID_Utilisateur`) REFERENCES `utilisateurs` (`ID`);

--
-- Contraintes pour la table `commande_articles`
--
ALTER TABLE `commande_articles`
  ADD CONSTRAINT `commande_articles_ibfk_1` FOREIGN KEY (`id_commande`) REFERENCES `commandes` (`ID`),
  ADD CONSTRAINT `commande_articles_ibfk_2` FOREIGN KEY (`id_article`) REFERENCES `articles` (`ID`);

--
-- Contraintes pour la table `detailscommande`
--
ALTER TABLE `detailscommande`
  ADD CONSTRAINT `detailscommande_ibfk_1` FOREIGN KEY (`ID_Commande`) REFERENCES `commandes` (`ID`),
  ADD CONSTRAINT `detailscommande_ibfk_2` FOREIGN KEY (`ID_Article`) REFERENCES `articles` (`ID`);

--
-- Contraintes pour la table `factures`
--
ALTER TABLE `factures`
  ADD CONSTRAINT `factures_ibfk_1` FOREIGN KEY (`ID_Commande`) REFERENCES `commandes` (`ID`) ON DELETE CASCADE;

--
-- Contraintes pour la table `lignes_commande`
--
ALTER TABLE `lignes_commande`
  ADD CONSTRAINT `lignes_commande_ibfk_1` FOREIGN KEY (`ID_Commande`) REFERENCES `commandes` (`ID`) ON DELETE CASCADE,
  ADD CONSTRAINT `lignes_commande_ibfk_2` FOREIGN KEY (`ID_Article`) REFERENCES `articles` (`ID`);

--
-- Contraintes pour la table `livraisons`
--
ALTER TABLE `livraisons`
  ADD CONSTRAINT `livraisons_ibfk_1` FOREIGN KEY (`ID_Commande`) REFERENCES `commandes` (`ID`) ON DELETE CASCADE;

--
-- Contraintes pour la table `mouvements_stock`
--
ALTER TABLE `mouvements_stock`
  ADD CONSTRAINT `mouvements_stock_ibfk_1` FOREIGN KEY (`ID_Article`) REFERENCES `articles` (`ID`),
  ADD CONSTRAINT `mouvements_stock_ibfk_2` FOREIGN KEY (`ID_Utilisateur`) REFERENCES `utilisateurs` (`ID`);

--
-- Contraintes pour la table `suivi_commande`
--
ALTER TABLE `suivi_commande`
  ADD CONSTRAINT `suivi_commande_ibfk_1` FOREIGN KEY (`ID_Commande`) REFERENCES `commandes` (`ID`) ON DELETE CASCADE,
  ADD CONSTRAINT `suivi_commande_ibfk_2` FOREIGN KEY (`ID_Statut`) REFERENCES `statuts_commande` (`ID`),
  ADD CONSTRAINT `suivi_commande_ibfk_3` FOREIGN KEY (`ID_Utilisateur`) REFERENCES `utilisateurs` (`ID`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
