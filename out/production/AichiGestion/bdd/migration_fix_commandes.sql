-- Script de migration pour corriger la gestion des commandes
-- À exécuter sur votre base de données MySQL/MariaDB (XAMPP)

-- 1. Sauvegarde des données existantes
CREATE TABLE IF NOT EXISTS temp_commande_data AS
SELECT 
    ca.id_commande,
    ca.id_article,
    ca.quantite,
    a.Reference,
    a.Nom as Designation,
    a.PrixVente as PrixUnitaireHT,
    20.00 as TauxTVA -- Taux par défaut
FROM commande_articles ca
JOIN articles a ON a.ID = ca.id_article
UNION
SELECT 
    dc.ID_Commande,
    dc.ID_Article,
    dc.Quantite,
    a.Reference,
    a.Nom as Designation,
    dc.PrixUnitaire as PrixUnitaireHT,
    20.00 as TauxTVA
FROM detailscommande dc
JOIN articles a ON a.ID = dc.ID_Article;

-- 2. Désactiver les triggers existants pour éviter les conflits
DROP TRIGGER IF EXISTS after_insert_ligne_commande;
DROP TRIGGER IF EXISTS after_update_ligne_commande;
DROP TRIGGER IF EXISTS after_delete_ligne_commande;

-- 3. Migration des données vers lignes_commande
INSERT IGNORE INTO lignes_commande (ID_Commande, ID_Article, Reference, Designation, Quantite, PrixUnitaireHT, TauxTVA)
SELECT 
    id_commande,
    id_article,
    Reference,
    Designation,
    quantite,
    PrixUnitaireHT,
    TauxTVA
FROM temp_commande_data;

-- 4. Mise à jour des totaux des commandes
UPDATE commandes c
SET 
    c.MontantHT = COALESCE((
        SELECT SUM(l.MontantHT) 
        FROM lignes_commande l 
        WHERE l.ID_Commande = c.ID
    ), 0),
    c.MontantTVA = COALESCE((
        SELECT SUM(l.MontantTVA) 
        FROM lignes_commande l 
        WHERE l.ID_Commande = c.ID
    ), 0),
    c.MontantTTC = COALESCE((
        SELECT SUM(l.MontantTTC) 
        FROM lignes_commande l 
        WHERE l.ID_Commande = c.ID
    ), 0);

-- 5. Recréation des triggers optimisés
DELIMITER $$

CREATE TRIGGER after_insert_ligne_commande 
AFTER INSERT ON lignes_commande
FOR EACH ROW 
BEGIN
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
END$$

CREATE TRIGGER after_update_ligne_commande 
AFTER UPDATE ON lignes_commande
FOR EACH ROW 
BEGIN
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
END$$

CREATE TRIGGER after_delete_ligne_commande 
AFTER DELETE ON lignes_commande
FOR EACH ROW 
BEGIN
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
END$$

DELIMITER ;

-- 6. Suppression des anciennes tables (à faire seulement après avoir vérifié que tout fonctionne)
-- DROP TABLE IF EXISTS commande_articles;
-- DROP TABLE IF EXISTS detailscommande;
-- DROP TABLE IF EXISTS temp_commande_data;

-- 7. Vérification finale
SELECT 
    c.ID as ID_Commande,
    c.Numero,
    c.MontantHT,
    c.MontantTVA,
    c.MontantTTC,
    COUNT(lc.ID) as NombreArticles
FROM commandes c
LEFT JOIN lignes_commande lc ON c.ID = lc.ID_Commande
GROUP BY c.ID, c.Numero, c.MontantHT, c.MontantTVA, c.MontantTTC; 