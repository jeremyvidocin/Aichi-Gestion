-- Mise à jour des clients existants avec des données réalistes
UPDATE clients 
SET 
    Nom = 'Dubois',
    Prenom = 'Jean',
    Societe = 'Dubois Électronique',
    Adresse = '15 rue des Artisans',
    CodePostal = '75001',
    Ville = 'Paris',
    Pays = 'France',
    Telephone = '01 23 45 67 89',
    Email = 'jean.dubois@dubois-electronique.fr'
WHERE ID = 1;

UPDATE clients 
SET 
    Nom = 'Martin',
    Prenom = 'Sophie',
    Societe = 'InfoTech Solutions',
    Adresse = '8 avenue des Technologies',
    CodePostal = '69002',
    Ville = 'Lyon',
    Pays = 'France',
    Telephone = '04 78 12 34 56',
    Email = 'sophie.martin@infotech-solutions.fr'
WHERE ID = 2;

UPDATE clients 
SET 
    Nom = 'Petit',
    Prenom = 'Marie',
    Societe = 'Digital Services SARL',
    Adresse = '25 boulevard de l\'Innovation',
    CodePostal = '33000',
    Ville = 'Bordeaux',
    Pays = 'France',
    Telephone = '05 56 78 90 12',
    Email = 'marie.petit@digital-services.fr'
WHERE ID = 3;

UPDATE clients 
SET 
    Nom = 'Leroy',
    Prenom = 'Thomas',
    Societe = 'Leroy Informatique',
    Adresse = '42 rue du Commerce',
    CodePostal = '44000',
    Ville = 'Nantes',
    Pays = 'France',
    Telephone = '02 40 11 22 33',
    Email = 'thomas.leroy@leroy-info.fr'
WHERE ID = 4;

-- Ajout de nouveaux clients pour plus de diversité
INSERT INTO clients (Nom, Prenom, Societe, Adresse, CodePostal, Ville, Pays, Telephone, Email, DateInscription, Actif) VALUES
('Garcia', 'Antoine', 'Tech Innovations', '3 rue de la République', '13001', 'Marseille', 'France', '04 91 23 45 67', 'a.garcia@tech-innovations.fr', NOW(), 1),
('Moreau', 'Claire', 'DataSys', '18 rue des Capucins', '59000', 'Lille', 'France', '03 20 45 67 89', 'claire.moreau@datasys.fr', NOW(), 1),
('Bernard', 'Philippe', 'Serveurs Plus', '5 avenue Foch', '67000', 'Strasbourg', 'France', '03 88 34 56 78', 'p.bernard@serveurs-plus.fr', NOW(), 1),
('Roux', 'Isabelle', 'Web Solutions', '12 place Bellecour', '69001', 'Lyon', 'France', '04 72 89 01 23', 'i.roux@websolutions.fr', NOW(), 1),
('Lambert', 'Michel', 'Réseau Pro', '7 rue du Général de Gaulle', '31000', 'Toulouse', 'France', '05 61 12 34 56', 'm.lambert@reseau-pro.fr', NOW(), 1);

-- Mise à jour des dates d'inscription pour qu'elles soient plus réalistes
UPDATE clients 
SET DateInscription = DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 365) DAY)
WHERE ID <= 9; 