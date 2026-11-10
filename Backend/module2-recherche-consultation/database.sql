-- ============================================================================
-- Base de données MySQL (XAMPP / phpMyAdmin)
-- Module 2 : Recherche & Consultation de Voyages et Billets (Données Tunisiennes)
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
-- Données initiales : 12 offres de voyages 100% tunisiennes
-- ----------------------------------------------------------------------------
INSERT INTO `voyages` (
  `id`, `code_voyage`, `titre`, `description`, `ville_depart`, `ville_destination`, `pays_destination`,
  `compagnie`, `type_transport`, `categorie`, `date_depart`, `date_arrivee`, `duree_minutes`, `escales`,
  `prix_base`, `prix_promo`, `en_promotion`, `places_disponibles`, `note_moyenne`, `nombre_avis`, `nombre_consultations`,
  `bagage_cabine_inclus`, `bagage_soute_inclus`, `annulation_gratuite`, `repas_inclus`, `wifi_disponible`, `image_url`, `statut`
) VALUES
(1, 'VOY-TUN-DJE-01', 'Vol Direct Tunis-Carthage → Djerba-Zarzis',
 'Liaison quotidienne rapide opérée par Tunisair Express en ATR 72-600. Idéal pour vos séjours à Djerba la douce, bagage cabine et soute inclus.',
 'Tunis', 'Djerba', 'Tunisie', 'Tunisair Express', 'VOL', 'CONFORT', '2026-11-20 07:30:00', '2026-11-20 08:25:00', 55, 0,
 180.00, 140.00, 1, 32, 4.8, 145, 420, 1, 1, 1, 0, 0,
 'https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&w=900&q=80', 'DISPONIBLE'),

(2, 'VOY-TUN-TOZ-02', 'Vol Saharien Tunis-Carthage → Tozeur-Nefta',
 'Envolez-vous vers les portes du désert tunisien et ses célèbres oasis du Djérid. Embarquement immédiat au terminal 2 de Tunis-Carthage.',
 'Tunis', 'Tozeur', 'Tunisie', 'Tunisair Express', 'VOL', 'CONFORT', '2026-11-22 14:00:00', '2026-11-22 15:05:00', 65, 0,
 220.00, 175.00, 1, 24, 4.7, 98, 310, 1, 1, 1, 1, 0,
 'https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?auto=format&fit=crop&w=900&q=80', 'DISPONIBLE'),

(3, 'VOY-TUN-SOUS-03', 'Train Express Climatisé Tunis Ville → Sousse Voyageurs',
 'Liaison ferroviaire rapide depuis la gare centrale de Tunis Place Barcelone jusqu au centre de Sousse. Sièges inclinables 1ère classe et grand confort.',
 'Tunis', 'Sousse', 'Tunisie', 'SNCFT', 'TRAIN', 'CONFORT', '2026-11-21 09:15:00', '2026-11-21 11:15:00', 120, 0,
 28.00, 22.00, 1, 58, 4.5, 320, 510, 1, 1, 1, 0, 0,
 'https://images.unsplash.com/photo-1474487548417-781cb71495f3?auto=format&fit=crop&w=900&q=80', 'DISPONIBLE'),

(4, 'VOY-TUN-SFAX-04', 'Autorail Express Tunis Ville → Sfax Gare',
 'Train grande ligne moderne reliant la capitale à Sfax. Climatisation intégrale, prises électriques individuelles et espace bagages volumineux.',
 'Tunis', 'Sfax', 'Tunisie', 'SNCFT', 'TRAIN', 'CONFORT', '2026-11-23 06:45:00', '2026-11-23 10:00:00', 195, 0,
 36.00, 29.00, 1, 45, 4.4, 260, 380, 1, 1, 1, 0, 0,
 'https://images.unsplash.com/photo-1510511459019-5dda7724fd87?auto=format&fit=crop&w=900&q=80', 'DISPONIBLE'),

(5, 'VOY-SFX-GAB-05', 'Autocar Grand Tourisme Climatisé Sfax → Gabès',
 'Liaison routière directe assurée par la SNTRI. Véhicules neufs grand confort avec climatisation, suspension pneumatique et soute spacieuse.',
 'Sfax', 'Gabès', 'Tunisie', 'SNTRI', 'BUS', 'ECONOMIQUE', '2026-11-24 08:30:00', '2026-11-24 10:40:00', 130, 0,
 24.00, 18.00, 1, 40, 4.3, 110, 195, 1, 1, 0, 0, 0,
 'https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&w=900&q=80', 'DISPONIBLE'),

(6, 'VOY-TUN-TAB-06', 'Autocar Confort Montagne Tunis → Tabarka / Aïn Draham',
 'Départ de la gare Bab Alioua pour traverser les magnifiques forêts de chênes-lièges de la Kroumirie jusqu à la côte de Corail de Tabarka.',
 'Tunis', 'Tabarka', 'Tunisie', 'SNTRI', 'BUS', 'ECONOMIQUE', '2026-11-25 07:00:00', '2026-11-25 09:50:00', 170, 0,
 22.00, 17.00, 1, 35, 4.6, 85, 240, 1, 1, 1, 0, 0,
 'https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=900&q=80', 'DISPONIBLE'),

(7, 'VOY-TUN-MRS-07', 'Traversée Maritime Ferry Tunis La Goulette → Marseille',
 'Embarquez à bord du navire amiral Tanit de la CTN. Cabines privées climatisées avec salle d eau, restaurants gastronomiques et jusqu à 50 kg de bagages autorisés.',
 'Tunis', 'Marseille', 'France', 'Compagnie Tunisienne de Navigation', 'BATEAU', 'CONFORT', '2026-11-26 12:00:00', '2026-11-27 09:00:00', 1260, 0,
 540.00, 450.00, 1, 60, 4.6, 215, 430, 1, 1, 1, 1, 1,
 'https://images.unsplash.com/photo-1548574505-5e239809ee19?auto=format&fit=crop&w=900&q=80', 'DISPONIBLE'),

(8, 'VOY-TUN-GEN-08', 'Traversée Maritime Ferry Tunis La Goulette → Gênes',
 'Liaison maritime directe opérée par le ferry Carthage de la CTN reliant la Tunisie au port historique de Gênes en Italie.',
 'Tunis', 'Gênes', 'Italie', 'Compagnie Tunisienne de Navigation', 'BATEAU', 'CONFORT', '2026-11-28 14:00:00', '2026-11-29 13:00:00', 1380, 0,
 580.00, 490.00, 1, 50, 4.5, 178, 310, 1, 1, 1, 1, 1,
 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=900&q=80', 'DISPONIBLE'),

(9, 'VOY-TUN-PAR-09', 'Vol Régulier Tunis-Carthage → Paris Charles de Gaulle',
 'Ligne phare de la compagnie nationale Tunisair en Airbus A320neo. Repas chaud tunisien offert, 8 kg cabine et 23 kg en soute inclus dès la classe standard.',
 'Tunis', 'Paris', 'France', 'Tunisair', 'VOL', 'CONFORT', '2026-11-21 08:30:00', '2026-11-21 11:05:00', 155, 0,
 720.00, 590.00, 1, 28, 4.7, 410, 620, 1, 1, 1, 1, 0,
 'https://images.unsplash.com/photo-1502602898657-3e91760cbb34?auto=format&fit=crop&w=900&q=80', 'DISPONIBLE'),

(10, 'VOY-DJE-PAR-10', 'Vol Direct Djerba-Zarzis → Paris Orly',
 'Vol direct opéré par Nouvelair en Airbus A320. Idéal pour rejoindre la région parisienne depuis le sud tunisien avec ponctualité exemplaire.',
 'Djerba', 'Paris', 'France', 'Nouvelair', 'VOL', 'ECONOMIQUE', '2026-11-23 11:00:00', '2026-11-23 13:55:00', 175, 0,
 640.00, 510.00, 1, 20, 4.8, 230, 490, 1, 1, 0, 0, 0,
 'https://images.unsplash.com/photo-1499856871958-5b9627545d1a?auto=format&fit=crop&w=900&q=80', 'DISPONIBLE'),

(11, 'VOY-MIR-LYO-11', 'Vol Direct Monastir Habib Bourguiba → Lyon Saint-Exupéry',
 'Liaison directe Nouvelair reliant le Sahel tunisien au bassin rhodanien. Service chaleureux et options de bagages flexibles.',
 'Monastir', 'Lyon', 'France', 'Nouvelair', 'VOL', 'ECONOMIQUE', '2026-11-24 15:20:00', '2026-11-24 17:35:00', 135, 0,
 490.00, 395.00, 1, 22, 4.6, 135, 280, 1, 0, 0, 0, 0,
 'https://images.unsplash.com/photo-1549877452-9c387954fbc2?auto=format&fit=crop&w=900&q=80', 'DISPONIBLE'),

(12, 'VOY-TUN-IST-12', 'Vol Tunis-Carthage → Istanbul Nouvel Aéroport',
 'Vol régulier opéré par Tunisair vers Istanbul. Service complet avec repas chaud à bord et franchise bagage soute généreuse de 32 kg.',
 'Tunis', 'Istanbul', 'Turquie', 'Tunisair', 'VOL', 'PREMIUM', '2026-11-25 10:30:00', '2026-11-25 15:15:00', 165, 0,
 890.00, 740.00, 1, 16, 4.8, 340, 550, 1, 1, 1, 1, 1,
 'https://images.unsplash.com/photo-1524231757912-21f4fe3a7200?auto=format&fit=crop&w=900&q=80', 'DISPONIBLE');

-- ----------------------------------------------------------------------------
-- Données initiales : 12 offres de billets par classe (en DT)
-- ----------------------------------------------------------------------------
INSERT INTO `offres_billets` (
  `id`, `code_offre_billet`, `classe`, `prix`, `places_restantes`, `bagage_cabine_kg`, `bagage_soute_kg`,
  `modifiable`, `remboursable`, `choix_siege_gratuit`, `acces_salon_vip`, `conditions_tarifaires`, `voyage_id`
) VALUES
(101, 'BIL-DJE-01-ECO', 'ECONOMIQUE', 140.00, 24, 8, 20, 1, 0, 0, 0, 'Tarif Express : Bagage 20 kg en soute inclus. Modifiable sans frais.', 1),
(102, 'BIL-DJE-01-PREM', 'CONFORT', 190.00, 8, 10, 23, 1, 1, 1, 0, 'Tarif Flex : Bagage 23 kg, choix de siège et remboursement garanti.', 1),
(103, 'BIL-TOZ-02-ECO', 'ECONOMIQUE', 175.00, 18, 8, 20, 1, 0, 0, 0, 'Tarif Oasis : Bagage soute 20 kg inclus, collation offerte.', 2),
(104, 'BIL-SOUS-03-CONF', 'CONFORT', 22.00, 40, 10, 25, 1, 1, 1, 0, '1ère classe SNCFT : Siège spacieux inclinable, espace bagages dédié.', 3),
(105, 'BIL-SFAX-04-CONF', 'CONFORT', 29.00, 35, 10, 25, 1, 1, 1, 0, '1ère classe Autorail Express : Prise 220V et climatisation renforcée.', 4),
(106, 'BIL-GAB-05-ECO', 'ECONOMIQUE', 18.00, 40, 8, 20, 0, 0, 0, 0, 'Autocar Grand Tourisme : Bagage en soute 20 kg inclus.', 5),
(107, 'BIL-TAB-06-ECO', 'ECONOMIQUE', 17.00, 35, 8, 20, 1, 0, 0, 0, 'Autocar Kroumirie Confort : Traversée directe Bab Alioua Tabarka.', 6),
(108, 'BIL-MRS-07-CAB', 'CONFORT', 450.00, 45, 15, 30, 1, 1, 1, 0, 'Ferry Tanit CTN : Cabine privative avec lit et douche + 30 kg soute.', 7),
(109, 'BIL-GEN-08-CAB', 'CONFORT', 490.00, 40, 15, 30, 1, 1, 1, 0, 'Ferry Carthage CTN : Cabine extérieure vue mer + 30 kg soute.', 8),
(110, 'BIL-PAR-09-ECO', 'ECONOMIQUE', 590.00, 20, 8, 23, 1, 0, 1, 0, 'Tunisair Standard : Repas tunisien offert, bagage soute 23 kg.', 9),
(111, 'BIL-PAR-09-BUS', 'BUSINESS', 990.00, 8, 12, 30, 1, 1, 1, 1, 'Tunisair Espace Privilège : Salon VIP Carthage, 2x30 kg et repas fin.', 9),
(112, 'BIL-IST-12-ECO', 'ECONOMIQUE', 740.00, 12, 8, 30, 1, 0, 1, 0, 'Tunisair Istanbul : Franchise exceptionnelle 30 kg en soute.', 12);

-- ----------------------------------------------------------------------------
-- Données initiales : Historique de recherches tunisiennes
-- ----------------------------------------------------------------------------
INSERT INTO `historiques_recherches` (
  `mot_cle`, `ville_depart`, `ville_destination`, `type_transport`, `classe_billet`, `budget_max`, `nombre_resultats`
) VALUES
('Djerba', 'Tunis', 'Djerba', 'VOL', 'CONFORT', 250.00, 1),
('Sousse', 'Tunis', 'Sousse', 'TRAIN', 'CONFORT', 50.00, 1),
('Paris', 'Tunis', 'Paris', 'VOL', 'ECONOMIQUE', 750.00, 1),
('Marseille', 'Tunis', 'Marseille', 'BATEAU', 'CONFORT', 600.00, 1);
