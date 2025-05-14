-- Fonction pour générer un sel aléatoire
DELIMITER $$
CREATE FUNCTION IF NOT EXISTS generate_salt() 
RETURNS VARCHAR(32)
BEGIN
    RETURN MD5(RAND());
END$$
DELIMITER ;

-- Suppression de la colonne Salt si elle existe
ALTER TABLE utilisateurs
DROP COLUMN IF EXISTS Salt;

-- Mise à jour du mot de passe d'Alice (password123)
UPDATE utilisateurs 
SET MotDePasse = (SELECT SHA2('password123', 256))
WHERE Nom = 'Alice';

-- Mise à jour du mot de passe de Bob (password456)
UPDATE utilisateurs 
SET MotDePasse = (SELECT SHA2('password456', 256))
WHERE Nom = 'Bob';

-- Vérification des mots de passe mis à jour
SELECT ID, Nom, Email, LEFT(MotDePasse, 20) as "Hash (premiers caractères)" 
FROM utilisateurs 
WHERE Nom IN ('Alice', 'Bob');

-- Procédure stockée pour la vérification du mot de passe
DELIMITER $$
DROP PROCEDURE IF EXISTS verify_password$$
CREATE PROCEDURE verify_password(
    IN p_email VARCHAR(100),
    IN p_password VARCHAR(100),
    OUT p_user_id INT,
    OUT p_is_valid BOOLEAN
)
BEGIN
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

-- Procédure stockée pour l'ajout/modification d'un utilisateur avec mot de passe sécurisé
CREATE PROCEDURE update_user_password(
    IN p_user_id INT,
    IN p_new_password VARCHAR(100)
)
BEGIN
    DECLARE v_salt VARCHAR(32);
    SET v_salt = generate_salt();
    
    UPDATE utilisateurs 
    SET 
        Salt = v_salt,
        MotDePasse = SHA2(CONCAT(p_new_password, v_salt), 256)
    WHERE ID = p_user_id;
END$$

-- Réinitialisation du mot de passe d'Alice
UPDATE utilisateurs 
SET Salt = generate_salt(),
    MotDePasse = NULL
WHERE Nom = 'Alice';

-- Mise à jour du mot de passe avec le nouveau système
UPDATE utilisateurs
SET MotDePasse = SHA2(CONCAT('password123', Salt), 256)
WHERE Nom = 'Alice';

-- Pour les autres utilisateurs qui auraient besoin d'une réinitialisation
-- Créer une procédure de réinitialisation
DELIMITER $$
CREATE PROCEDURE reset_user_password(
    IN p_user_id INT,
    IN p_new_password VARCHAR(100)
)
BEGIN
    DECLARE v_salt VARCHAR(32);
    SET v_salt = generate_salt();
    
    UPDATE utilisateurs 
    SET Salt = v_salt,
        MotDePasse = SHA2(CONCAT(p_new_password, v_salt), 256)
    WHERE ID = p_user_id;
END$$
DELIMITER ;

-- Exemple d'utilisation:
-- CALL reset_user_password(1, 'nouveau_mot_de_passe'); 