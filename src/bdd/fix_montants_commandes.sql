-- 1. Modification de la structure de la table lignes_commande pour assurer les calculs corrects
ALTER TABLE lignes_commande
    DROP COLUMN IF EXISTS MontantHT,
    DROP COLUMN IF EXISTS MontantTVA,
    DROP COLUMN IF EXISTS MontantTTC;

ALTER TABLE lignes_commande
    ADD COLUMN MontantHT DECIMAL(10,2) GENERATED ALWAYS AS (PrixUnitaireHT * Quantite) STORED,
    ADD COLUMN MontantTVA DECIMAL(10,2) GENERATED ALWAYS AS (PrixUnitaireHT * Quantite * TauxTVA / 100) STORED,
    ADD COLUMN MontantTTC DECIMAL(10,2) GENERATED ALWAYS AS (PrixUnitaireHT * Quantite * (1 + TauxTVA / 100)) STORED;

-- 2. Mise à jour des montants dans la table commandes
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
WHERE EXISTS (
    SELECT 1 FROM lignes_commande WHERE ID_Commande = c.ID
);

-- 3. Vérification des montants
SELECT 
    c.ID as ID_Commande,
    c.Numero,
    GROUP_CONCAT(CONCAT(a.Nom, ' (', lc.Quantite, ')')) as Articles,
    c.MontantHT,
    c.MontantTVA,
    c.MontantTTC
FROM commandes c
LEFT JOIN lignes_commande lc ON c.ID = lc.ID_Commande
LEFT JOIN articles a ON lc.ID_Article = a.ID
GROUP BY c.ID, c.Numero, c.MontantHT, c.MontantTVA, c.MontantTTC;

-- 4. Procédure stockée pour ajouter un article à une commande
DELIMITER $$

DROP PROCEDURE IF EXISTS ajouter_article_commande$$

CREATE PROCEDURE ajouter_article_commande(
    IN p_id_commande INT,
    IN p_id_article INT,
    IN p_quantite INT
)
BEGIN
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

DELIMITER ;

-- 5. Exemple d'utilisation de la procédure
-- CALL ajouter_article_commande(1, 1, 2); -- Ajoute 2 unités de l'article 1 à la commande 1 