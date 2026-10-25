-- ============================================================================
-- Base de données MySQL (XAMPP / phpMyAdmin)
-- Module 2 : Recherche & Consultation de Voyages et Billets
-- ============================================================================

CREATE DATABASE IF NOT EXISTS `recherche_consultation_db`
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `recherche_consultation_db`;

DROP TABLE IF EXISTS `offres_billets`;
DROP TABLE IF EXISTS `historiques_recherches`;
DROP TABLE IF EXISTS `voyages`;

-- ----------------------------------------------------------------------------
-- 1. Table `voyages`
-- ----------------------------------------------------------------------------
CREATE TABLE `voyages` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `code_voyage` VARCHAR(40) NOT NULL UNIQUE,
  `titre` VARCHAR(160) NOT NULL,
  `description` VARCHAR(1200) DEFAULT NULL,
  `ville_depart` VARCHAR(80) NOT NULL,
  `ville_destination` VARCHAR(80) NOT NULL,
  `pays_destination` VARCHAR(80) NOT NULL,
  `compagnie` VARCHAR(100) NOT NULL,
  `type_transport` VARCHAR(30) NOT NULL,
  `categorie` VARCHAR(30) NOT NULL,
  `date_depart` DATETIME NOT NULL,
  `date_arrivee` DATETIME NOT NULL,
  `duree_minutes` INT NOT NULL,
  `escales` INT NOT NULL DEFAULT 0,
  `prix_base` DECIMAL(10,2) NOT NULL,
  `prix_promo` DECIMAL(10,2) DEFAULT NULL,
  `en_promotion` TINYINT(1) NOT NULL DEFAULT 0,
  `places_disponibles` INT NOT NULL,
  `note_moyenne` DOUBLE NOT NULL DEFAULT 4.5,
  `nombre_avis` INT NOT NULL DEFAULT 0,
  `nombre_consultations` INT NOT NULL DEFAULT 0,
  `bagage_cabine_inclus` TINYINT(1) NOT NULL DEFAULT 1,
  `bagage_soute_inclus` TINYINT(1) NOT NULL DEFAULT 0,
  `annulation_gratuite` TINYINT(1) NOT NULL DEFAULT 0,
  `repas_inclus` TINYINT(1) NOT NULL DEFAULT 0,
  `wifi_disponible` TINYINT(1) NOT NULL DEFAULT 0,
  `image_url` VARCHAR(500) DEFAULT NULL,
  `statut` VARCHAR(30) NOT NULL DEFAULT 'DISPONIBLE',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- 2. Table `offres_billets`
-- ----------------------------------------------------------------------------
CREATE TABLE `offres_billets` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `code_offre_billet` VARCHAR(50) NOT NULL UNIQUE,
  `classe` VARCHAR(30) NOT NULL,
  `prix` DECIMAL(10,2) NOT NULL,
  `places_restantes` INT NOT NULL,
  `bagage_cabine_kg` INT NOT NULL DEFAULT 8,
  `bagage_soute_kg` INT NOT NULL DEFAULT 0,
  `modifiable` TINYINT(1) NOT NULL DEFAULT 0,
  `remboursable` TINYINT(1) NOT NULL DEFAULT 0,
  `choix_siege_gratuit` TINYINT(1) NOT NULL DEFAULT 0,
  `acces_salon_vip` TINYINT(1) NOT NULL DEFAULT 0,
  `conditions_tarifaires` VARCHAR(500) DEFAULT NULL,
  `voyage_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_billet_voyage` FOREIGN KEY (`voyage_id`) REFERENCES `voyages` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- 3. Table `historiques_recherches`
-- ----------------------------------------------------------------------------
CREATE TABLE `historiques_recherches` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `mot_cle` VARCHAR(255) DEFAULT NULL,
  `ville_depart` VARCHAR(80) DEFAULT NULL,
  `ville_destination` VARCHAR(80) DEFAULT NULL,
  `type_transport` VARCHAR(30) DEFAULT NULL,
  `classe_billet` VARCHAR(30) DEFAULT NULL,
  `budget_max` DECIMAL(10,2) DEFAULT NULL,
  `nombre_resultats` INT DEFAULT NULL,
  `date_recherche` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- Données initiales : 8 offres de voyages
-- ----------------------------------------------------------------------------
INSERT INTO `voyages` (
  `id`, `code_voyage`, `titre`, `description`, `ville_depart`, `ville_destination`, `pays_destination`,
  `compagnie`, `type_transport`, `categorie`, `date_depart`, `date_arrivee`, `duree_minutes`, `escales`,
  `prix_base`, `prix_promo`, `en_promotion`, `places_disponibles`, `note_moyenne`, `nombre_avis`, `nombre_consultations`,
  `bagage_cabine_inclus`, `bagage_soute_inclus`, `annulation_gratuite`, `repas_inclus`, `wifi_disponible`, `image_url`, `statut`
) VALUES
(1, 'VOY-TUN-PAR-101', 'Vol Direct Tunis → Paris Charles de Gaulle',
 'Vol régulier direct opéré par Tunisair en Airbus A320neo. Repas chaud méditerranéen, bagage cabine 8 kg et bagage en soute 23 kg inclus dès le tarif standard.',
 'Tunis', 'Paris', 'France', 'Tunisair', 'VOL', 'CONFORT', '2026-11-15 08:30:00', '2026-11-15 11:05:00', 155, 0,
 620.00, 465.00, 1, 28, 4.6, 184, 245, 1, 1, 1, 1, 0,
 'https://images.unsplash.com/photo-1502602898657-3e91760cbb34?auto=format&fit=crop&w=900&q=80', 'DISPONIBLE'),

(2, 'VOY-TUN-PAR-102', 'Vol Express Tunis → Paris Orly (Tarif Light)',
 'Solution économique idéale pour un court séjour à Paris. Arrivée à l''aéroport de Paris-Orly à 25 minutes du centre-ville via le métro ligne 14.',
 'Tunis', 'Paris', 'France', 'Transavia France', 'VOL', 'ECONOMIQUE', '2026-11-15 14:15:00', '2026-11-15 16:45:00', 150, 0,
 340.00, 295.00, 1, 14, 4.4, 142, 310, 1, 0, 0, 0, 0,
 'https://images.unsplash.com/photo-1499856871958-5b9627545d1a?auto=format&fit=crop&w=900&q=80', 'DISPONIBLE'),

(3, 'VOY-TUN-IST-201', 'Vol Confort Tunis → Istanbul Airport',
 'Découvrez Istanbul avec Turkish Airlines, élue meilleure compagnie d''Europe. Écrans individuels, Wi-Fi à bord, gastronomie turque et franchise bagage généreuse de 30 kg.',
 'Tunis', 'Istanbul', 'Turquie', 'Turkish Airlines', 'VOL', 'PREMIUM', '2026-11-18 10:00:00', '2026-11-18 14:50:00', 170, 0,
 780.00, 640.00, 1, 22, 4.9, 265, 390, 1, 1, 1, 1, 1,
 'https://images.unsplash.com/photo-1524231757912-21f4fe3a7200?auto=format&fit=crop&w=900&q=80', 'DISPONIBLE'),

(4, 'VOY-TUN-ROM-301', 'Escapade Italienne Tunis → Rome Fiumicino',
 'Liaison rapide d''à peine 1h20 vers la capitale italienne. Idéal pour un week-end culturel ou des correspondances européennes.',
 'Tunis', 'Rome', 'Italie', 'ITA Airways', 'VOL', 'CONFORT', '2026-11-20 09:15:00', '2026-11-20 10:35:00', 80, 0,
 410.00, 350.00, 1, 19, 4.7, 118, 195, 1, 1, 1, 1, 1,
 'https://images.unsplash.com/photo-1552832230-c0197dd311b5?auto=format&fit=crop&w=900&q=80', 'DISPONIBLE'),

(5, 'VOY-TUN-DXB-401', 'Expérience Prestige Tunis → Dubaï International',
 'Voyagez à bord du Boeing 777 d''Emirates en liaison directe vers Dubaï. Divertissement ICE primé, Wi-Fi haut débit, repas gastronomique et 2 bagages en soute.',
 'Tunis', 'Dubaï', 'Émirats Arabes Unis', 'Emirates', 'VOL', 'LUXE', '2026-12-01 15:00:00', '2026-12-01 23:45:00', 345, 0,
 1650.00, 1390.00, 1, 7, 4.95, 312, 420, 1, 1, 1, 1, 1,
 'https://images.unsplash.com/photo-1512453979798-5ea266f8880c?auto=format&fit=crop&w=900&q=80', 'DERNIERES_PLACES'),

(6, 'VOY-TUN-MRS-501', 'Traversée Ferry Tunis La Goulette → Marseille',
 'Voyagez en mer Méditerranée avec cabine privée vue mer et possibilité d''embarquer avec un bagage volumineux jusqu''à 50 kg.',
 'Tunis', 'Marseille', 'France', 'CTN Navigation', 'BATEAU', 'CONFORT', '2026-11-22 12:00:00', '2026-11-23 09:00:00', 1260, 0,
 540.00, NULL, 0, 45, 4.3, 89, 130, 1, 1, 1, 1, 1,
 'https://images.unsplash.com/photo-1548574505-5e239809ee19?auto=format&fit=crop&w=900&q=80', 'DISPONIBLE'),

(7, 'VOY-DJE-BCN-601', 'Vol + Séjour Méditerranéen Djerba → Barcelone',
 'Formule combinée vol + transfert vers le cœur de Barcelone. Idéal pour découvrir la Sagrada Família et la côte catalane.',
 'Djerba', 'Barcelone', 'Espagne', 'Nouvelair', 'SEJOUR_PACK', 'PROMO', '2026-11-25 11:00:00', '2026-11-25 13:10:00', 130, 0,
 690.00, 499.00, 1, 12, 4.75, 96, 215, 1, 1, 1, 1, 1,
 'https://images.unsplash.com/photo-1583422409516-2895a77efded?auto=format&fit=crop&w=900&q=80', 'DISPONIBLE'),

(8, 'VOY-PAR-LON-701', 'TGV Eurostar Direct Paris Gare du Nord → Londres St Pancras',
 'Reliez le cœur de Paris au centre de Londres en 2h15 sous la Manche. Enregistrement rapide, Wi-Fi gratuit et 2 bagages inclus sans restriction de liquides.',
 'Paris', 'Londres', 'Royaume-Uni', 'Eurostar', 'TRAIN', 'PREMIUM', '2026-11-16 13:13:00', '2026-11-16 15:30:00', 137, 0,
 310.00, 245.00, 1, 34, 4.85, 210, 275, 1, 1, 1, 1, 1,
 'https://images.unsplash.com/photo-1513635269975-59663e0ac1ad?auto=format&fit=crop&w=900&q=80', 'DISPONIBLE');

-- ----------------------------------------------------------------------------
-- Données initiales : 13 offres de billets par classe
-- ----------------------------------------------------------------------------
INSERT INTO `offres_billets` (
  `id`, `code_offre_billet`, `classe`, `prix`, `places_restantes`, `bagage_cabine_kg`, `bagage_soute_kg`,
  `modifiable`, `remboursable`, `choix_siege_gratuit`, `acces_salon_vip`, `conditions_tarifaires`, `voyage_id`
) VALUES
(101, 'BIL-PAR-101-ECO', 'ECONOMIQUE', 465.00, 20, 8, 23, 1, 0, 0, 0, 'Modification gratuite jusqu''à 48h avant le départ. Bagage 23 kg inclus.', 1),
(102, 'BIL-PAR-101-BUS', 'BUSINESS', 890.00, 8, 12, 30, 1, 1, 1, 1, '100% remboursable, accès Salon Privilège Tunis-Carthage et bagage 30 kg (max autorisé Module 1).', 1),
(103, 'BIL-PAR-102-ECO', 'ECONOMIQUE', 295.00, 10, 10, 0, 0, 0, 0, 0, 'Tarif Basic : bagage cabine 10 kg inclus, bagage soute en supplément.', 2),
(104, 'BIL-PAR-102-PLUS', 'PREMIUM_ECO', 420.00, 4, 10, 20, 1, 0, 1, 0, 'Tarif Plus : bagage en soute 20 kg + choix du siège standard inclus.', 2),
(105, 'BIL-IST-201-ECO', 'ECONOMIQUE', 640.00, 16, 8, 25, 1, 0, 1, 0, 'Franchise bagage soute 25 kg, repas chaud et système multimédia inclus.', 3),
(106, 'BIL-IST-201-BUS', 'BUSINESS', 1290.00, 6, 12, 30, 1, 1, 1, 1, 'Accès au célèbre Lounge Turkish Airlines, sièges inclinables et bagage 30 kg.', 3),
(107, 'BIL-ROM-301-ECO', 'ECONOMIQUE', 350.00, 15, 8, 23, 1, 0, 0, 0, 'Bagage soute 23 kg et collation italienne inclus.', 4),
(108, 'BIL-DXB-401-ECO', 'ECONOMIQUE', 1390.00, 5, 10, 25, 1, 1, 1, 0, '25 kg en soute, modification et annulation sans frais.', 5),
(109, 'BIL-DXB-401-FIRST', 'PREMIERE', 3450.00, 2, 15, 30, 1, 1, 1, 1, 'Suite privée Première Classe, service chauffeur privé et bagage 30 kg.', 5),
(110, 'BIL-MRS-501-CAB', 'PREMIUM_ECO', 540.00, 45, 12, 30, 1, 1, 1, 0, 'Cabine privative 2 personnes avec hublot + bagage 30 kg inclus.', 6),
(111, 'BIL-BCN-601-PACK', 'ECONOMIQUE', 499.00, 12, 10, 25, 1, 0, 1, 0, 'Pack Vol A/R + Navette Aéroport El Prat + Bagage 25 kg.', 7),
(112, 'BIL-LON-701-STD', 'ECONOMIQUE', 245.00, 24, 10, 23, 1, 0, 1, 0, 'Eurostar Standard : bagage 23 kg + 1 bagage à main, échange sans frais.', 8),
(113, 'BIL-LON-701-PREM', 'BUSINESS', 480.00, 10, 12, 30, 1, 1, 1, 1, 'Eurostar Plus / Premier : repas servi à la place, bagage 30 kg et accès salon Business.', 8);

-- ----------------------------------------------------------------------------
-- Données initiales : Historique de recherches (pour analytique & suggestions)
-- ----------------------------------------------------------------------------
INSERT INTO `historiques_recherches` (
  `mot_cle`, `ville_depart`, `ville_destination`, `type_transport`, `classe_billet`, `budget_max`, `nombre_resultats`
) VALUES
('Paris', 'Tunis', 'Paris', 'VOL', 'ECONOMIQUE', 500.00, 2),
('Istanbul', 'Tunis', 'Istanbul', 'VOL', 'BUSINESS', 1500.00, 1),
('Rome week-end', 'Tunis', 'Rome', 'VOL', 'ECONOMIQUE', 400.00, 1);

