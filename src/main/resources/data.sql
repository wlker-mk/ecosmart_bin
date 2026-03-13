INSERT INTO users (nom, prenom, email, password, points, role, telephone, carte_etudiante, est_etudiant) VALUES
('Admin', 'EcoSmart', 'admin@ecosmart.tg', 'admin123', 0, 'ADMIN', '+228 90000001', NULL, false),
('Agbeko', 'Kokou', 'agent@ecosmart.tg', 'agent123', 0, 'AGENT', '+228 90000002', NULL, false),
('Dupont', 'Jean', 'jean@gmail.com', 'pass123', 15, 'USER', '+228 91000001', NULL, false),
('Mensah', 'Afi', 'afi@gmail.com', 'pass123', 8, 'USER', '+228 91000002', NULL, false),
('Ndam', 'Pita', 'pita@gmail.com', 'pass123', 22, 'USER', '+228 91000003', NULL, false),
('Koffi', 'Amavi', 'amavi@universite.tg', 'pass123', 45, 'USER', '+228 92000001', 'ETU-2024-00123', true),
('Adzoa', 'Sena', 'sena@universite.tg', 'pass123', 30, 'USER', '+228 92000002', 'ETU-2024-00456', true),
('Tchapo', 'Luc', 'luc@universite.tg', 'pass123', 18, 'USER', '+228 92000003', 'ETU-2024-00789', true);

INSERT INTO bins (nom, localisation, etat, inclinaison_actuelle, inclinaison_seuil, nombre_depots, latitude, longitude) VALUES
('Borne Universite', 'Universite de Lome - Entree principale', 'PARTIEL', 21.5, 30.0, 34, 6.1722, 1.2314),
('Borne Marche Adidogome', 'Marche Adidogome - Zone A', 'VIDE', 5.0, 30.0, 8, 6.1367, 1.2000),
('Borne Hopital CHU', 'CHU Sylvanus Olympio - Parking', 'PLEIN', 31.2, 30.0, 67, 6.1375, 1.2224),
('Borne Tokoin', 'Carrefour Tokoin - Face station Total', 'VIDE', 2.1, 30.0, 3, 6.1540, 1.2180),
('Borne Agoe', 'Agoe-Nyive - Rue des Ecoles', 'PARTIEL', 19.8, 30.0, 28, 6.2050, 1.1950),
('Borne Be Beach', 'Plage de Be - Acces principal', 'VIDE', 0.0, 30.0, 0, 6.1100, 1.2600);

INSERT INTO rewards (nom, description, points_requis, disponible) VALUES
('Bon achat 1000 FCFA', 'Valable dans les supermarches partenaires de Lome', 10, true),
('Bon achat 2500 FCFA', 'Valable chez nos partenaires alimentaires', 25, true),
('Bon achat 5000 FCFA', 'Valable dans tous les supermarches partenaires', 50, true),
('Recharge telephone 500', 'Recharge Togocel ou Moov - 500 FCFA', 8, true),
('Recharge telephone 1000', 'Recharge Togocel ou Moov - 1000 FCFA', 15, true),
('T-shirt EcoSmart', 'T-shirt officiel EcoSmart - Taille au choix', 40, true),
('Stylo EcoSmart', 'Kit stylo + carnet EcoSmart recycle', 5, true),
('Reduction transport 20%', 'Code de reduction 20% sur les transports partenaires', 35, true),
('Attestation Eco-citoyen', 'Certificat numerique eco-citoyen engage', 100, true),
('Cadeau Surprise', 'Surprise offerte par nos partenaires valeur 3000 FCFA', 20, false);

INSERT INTO deposits (type_plastique, scan_resultat, points_gagnes, date_depot, user_id, bin_id) VALUES
('PET',   'ACCEPTE', 5,  NOW() - INTERVAL '10 days', 3, 1),
('HDPE',  'ACCEPTE', 4,  NOW() - INTERVAL '8 days',  3, 1),
(NULL,    'REFUSE',  0,  NOW() - INTERVAL '7 days',  3, 2),
('PP',    'ACCEPTE', 3,  NOW() - INTERVAL '5 days',  3, 1),
('PET',   'ACCEPTE', 5,  NOW() - INTERVAL '2 days',  3, 4),
('LDPE',  'ACCEPTE', 2,  NOW() - INTERVAL '9 days',  4, 2),
(NULL,    'REFUSE',  0,  NOW() - INTERVAL '6 days',  4, 2),
('PS',    'ACCEPTE', 2,  NOW() - INTERVAL '3 days',  4, 5),
('PET',   'ACCEPTE', 4,  NOW() - INTERVAL '1 days',  4, 5),
('PET',   'ACCEPTE', 5,  NOW() - INTERVAL '12 days', 5, 3),
('PVC',   'ACCEPTE', 3,  NOW() - INTERVAL '9 days',  5, 3),
('HDPE',  'ACCEPTE', 4,  NOW() - INTERVAL '7 days',  5, 3),
('PET',   'ACCEPTE', 5,  NOW() - INTERVAL '4 days',  5, 1),
(NULL,    'REFUSE',  0,  NOW() - INTERVAL '2 days',  5, 1),
('PP',    'ACCEPTE', 3,  NOW() - INTERVAL '1 days',  5, 3),
('PET',   'ACCEPTE', 10, NOW() - INTERVAL '11 days', 6, 1),
('HDPE',  'ACCEPTE', 8,  NOW() - INTERVAL '8 days',  6, 1),
('PP',    'ACCEPTE', 6,  NOW() - INTERVAL '6 days',  6, 2),
(NULL,    'REFUSE',  0,  NOW() - INTERVAL '4 days',  6, 2),
('PET',   'ACCEPTE', 10, NOW() - INTERVAL '1 days',  6, 4),
('PS',    'ACCEPTE', 4,  NOW() - INTERVAL '7 days',  7, 5),
('PET',   'ACCEPTE', 10, NOW() - INTERVAL '5 days',  7, 5),
(NULL,    'REFUSE',  0,  NOW() - INTERVAL '3 days',  7, 1),
('LDPE',  'ACCEPTE', 4,  NOW() - INTERVAL '1 days',  7, 1),
('AUTRE', 'ACCEPTE', 2,  NOW() - INTERVAL '6 days',  8, 3),
('PET',   'ACCEPTE', 10, NOW() - INTERVAL '4 days',  8, 3),
('PVC',   'ACCEPTE', 6,  NOW() - INTERVAL '2 days',  8, 4);

INSERT INTO reward_users (user_id, reward_id, date_echange, statut) VALUES
(3, 1, NOW() - INTERVAL '3 days', 'VALIDE'),
(6, 1, NOW() - INTERVAL '5 days', 'VALIDE'),
(6, 4, NOW() - INTERVAL '2 days', 'EN_ATTENTE'),
(5, 2, NOW() - INTERVAL '1 days', 'EN_ATTENTE'),
(7, 7, NOW() - INTERVAL '4 days', 'VALIDE'),
(4, 4, NOW() - INTERVAL '1 days', 'EN_ATTENTE');
