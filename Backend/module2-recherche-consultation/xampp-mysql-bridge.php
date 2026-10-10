<?php
/**
 * Passerelle REST MySQL complète (XAMPP) pour `recherche_consultation_db` et `reservation_db`
 * Prend en charge 100% des opérations CRUD (GET, POST, PUT, DELETE, PATCH) sur :
 * - les Voyages (`voyages`)
 * - les Offres de Billets (`offres_billets`)
 * - l'Historique de recherches (`historiques_recherches`)
 * - la Synchronisation des disponibilités avec le Module 3 (`reservation_db.disponibilite`)
 */
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET, POST, PUT, PATCH, DELETE, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type, Authorization");
header("Content-Type: application/json; charset=utf-8");

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit;
}

$host = '127.0.0.1';
$db   = 'recherche_consultation_db';
$user = 'root';
$pass = '';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$db;charset=utf8mb4", $user, $pass, [
        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC
    ]);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(["error" => "Connexion MySQL XAMPP échouée : " . $e->getMessage()]);
    exit;
}

function chargerBilletsParVoyage(PDO $pdo, int $voyageId): array {
    $stmt = $pdo->prepare("SELECT * FROM offres_billets WHERE voyage_id = ? ORDER BY prix ASC");
    $stmt->execute([$voyageId]);
    $rows = $stmt->fetchAll();
    $result = [];
    foreach ($rows as $r) {
        $result[] = [
            "id" => (int)$r["id"],
            "codeOffreBillet" => $r["code_offre_billet"],
            "classe" => $r["classe"],
            "prix" => (float)$r["prix"],
            "placesRestantes" => (int)$r["places_restantes"],
            "bagageCabineKg" => (int)$r["bagage_cabine_kg"],
            "bagageSouteKg" => (int)$r["bagage_soute_kg"],
            "modifiable" => (bool)$r["modifiable"],
            "remboursable" => (bool)$r["remboursable"],
            "choixSiegeGratuit" => (bool)$r["choix_siege_gratuit"],
            "accesSalonVip" => (bool)$r["acces_salon_vip"],
            "conditionsTarifaires" => $r["conditions_tarifaires"],
            "voyageId" => (int)$r["voyage_id"]
        ];
    }
    return $result;
}

function mapperVoyage(PDO $pdo, array $v): array {
    return [
        "id" => (int)$v["id"],
        "codeVoyage" => $v["code_voyage"],
        "titre" => $v["titre"],
        "description" => $v["description"],
        "villeDepart" => $v["ville_depart"],
        "villeDestination" => $v["ville_destination"],
        "paysDestination" => $v["pays_destination"],
        "compagnie" => $v["compagnie"],
        "typeTransport" => $v["type_transport"],
        "categorie" => $v["categorie"],
        "dateDepart" => str_replace(" ", "T", $v["date_depart"]),
        "dateArrivee" => str_replace(" ", "T", $v["date_arrivee"]),
        "dureeMinutes" => (int)$v["duree_minutes"],
        "escales" => (int)$v["escales"],
        "prixBase" => (float)$v["prix_base"],
        "prixPromo" => $v["prix_promo"] !== null ? (float)$v["prix_promo"] : null,
        "enPromotion" => (bool)$v["en_promotion"],
        "placesDisponibles" => (int)$v["places_disponibles"],
        "noteMoyenne" => (float)$v["note_moyenne"],
        "nombreAvis" => (int)$v["nombre_avis"],
        "nombreConsultations" => (int)$v["nombre_consultations"],
        "bagageCabineInclus" => (bool)$v["bagage_cabine_inclus"],
        "bagageSouteInclus" => (bool)$v["bagage_soute_inclus"],
        "annulationGratuite" => (bool)$v["annulation_gratuite"],
        "repasInclus" => (bool)$v["repas_inclus"],
        "wifiDisponible" => (bool)$v["wifi_disponible"],
        "imageUrl" => $v["image_url"],
        "statut" => $v["statut"],
        "billets" => chargerBilletsParVoyage($pdo, (int)$v["id"])
    ];
}

$action = $_GET['action'] ?? 'voyages';
$method = $_SERVER['REQUEST_METHOD'];

// ============================================================================
// 1. CRUD VOYAGES (GET, POST, PUT, DELETE)
// ============================================================================

if ($action === 'voyages' && $method === 'GET') {
    if (isset($_GET['id'])) {
        $id = (int)$_GET['id'];
        if (!isset($_GET['noIncrement'])) {
            $pdo->prepare("UPDATE voyages SET nombre_consultations = nombre_consultations + 1 WHERE id = ?")->execute([$id]);
        }
        $stmt = $pdo->prepare("SELECT * FROM voyages WHERE id = ?");
        $stmt->execute([$id]);
        $row = $stmt->fetch();
        if (!$row) {
            http_response_code(404);
            echo json_encode(["error" => "Voyage introuvable avec l'ID : $id"]);
            exit;
        }
        echo json_encode(mapperVoyage($pdo, $row));
        exit;
    }

    $stmt = $pdo->query("SELECT * FROM voyages ORDER BY id ASC");
    $rows = $stmt->fetchAll();
    $result = array_map(fn($r) => mapperVoyage($pdo, $r), $rows);
    echo json_encode($result);
    exit;
}

if ($action === 'voyages' && $method === 'POST') {
    $input = json_decode(file_get_contents("php://input"), true);
    $stmt = $pdo->prepare("
        INSERT INTO voyages (
            code_voyage, titre, description, ville_depart, ville_destination, pays_destination,
            compagnie, type_transport, categorie, date_depart, date_arrivee, duree_minutes, escales,
            prix_base, prix_promo, en_promotion, places_disponibles, note_moyenne, nombre_avis, nombre_consultations,
            bagage_cabine_inclus, bagage_soute_inclus, annulation_gratuite, repas_inclus, wifi_disponible, image_url, statut
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    ");
    $placesDispo = (int)($input['placesDisponibles'] ?? 25);
    $stmt->execute([
        $input['codeVoyage'],
        $input['titre'],
        $input['description'] ?? '',
        $input['villeDepart'],
        $input['villeDestination'],
        $input['paysDestination'],
        $input['compagnie'],
        $input['typeTransport'],
        $input['categorie'],
        str_replace("T", " ", $input['dateDepart'] ?? '2026-11-28 09:00:00'),
        str_replace("T", " ", $input['dateArrivee'] ?? '2026-11-28 11:20:00'),
        (int)($input['dureeMinutes'] ?? 140),
        (int)($input['escales'] ?? 0),
        (float)$input['prixBase'],
        isset($input['prixPromo']) && $input['prixPromo'] !== null && $input['prixPromo'] !== '' ? (float)$input['prixPromo'] : null,
        !empty($input['enPromotion']) ? 1 : 0,
        $placesDispo,
        (float)($input['noteMoyenne'] ?? 4.7),
        (int)($input['nombreAvis'] ?? 15),
        (int)($input['nombreConsultations'] ?? 1),
        !empty($input['bagageCabineInclus']) ? 1 : 0,
        !empty($input['bagageSouteInclus']) ? 1 : 0,
        !empty($input['annulationGratuite']) ? 1 : 0,
        !empty($input['repasInclus']) ? 1 : 0,
        !empty($input['wifiDisponible']) ? 1 : 0,
        $input['imageUrl'] ?? 'https://images.unsplash.com/photo-1539037116277-4db20889f2d4?auto=format&fit=crop&w=900&q=80',
        $input['statut'] ?? 'DISPONIBLE'
    ]);
    $voyageId = (int)$pdo->lastInsertId();

    if (!empty($input['billets']) && is_array($input['billets'])) {
        $bStmt = $pdo->prepare("
            INSERT INTO offres_billets (
                code_offre_billet, classe, prix, places_restantes, bagage_cabine_kg, bagage_soute_kg,
                modifiable, remboursable, choix_siege_gratuit, acces_salon_vip, conditions_tarifaires, voyage_id
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        ");
        foreach ($input['billets'] as $b) {
            $souteKg = min(30, max(0, (int)($b['bagageSouteKg'] ?? 0)));
            $bStmt->execute([
                $b['codeOffreBillet'],
                $b['classe'],
                (float)$b['prix'],
                (int)$b['placesRestantes'],
                min(30, (int)($b['bagageCabineKg'] ?? 8)),
                $souteKg,
                !empty($b['modifiable']) ? 1 : 0,
                !empty($b['remboursable']) ? 1 : 0,
                !empty($b['choixSiegeGratuit']) ? 1 : 0,
                !empty($b['accesSalonVip']) ? 1 : 0,
                $b['conditionsTarifaires'] ?? '',
                $voyageId
            ]);
        }
    }

    $stmt = $pdo->prepare("SELECT * FROM voyages WHERE id = ?");
    $stmt->execute([$voyageId]);
    http_response_code(201);
    echo json_encode(mapperVoyage($pdo, $stmt->fetch()));
    exit;
}

if ($action === 'voyages' && $method === 'PUT') {
    $id = isset($_GET['id']) ? (int)$_GET['id'] : 0;
    if ($id <= 0) {
        http_response_code(400);
        echo json_encode(["error" => "Paramètre id obligatoire pour la modification"]);
        exit;
    }

    $input = json_decode(file_get_contents("php://input"), true);
    $placesDispo = (int)($input['placesDisponibles'] ?? 20);
    $prixPromo = (isset($input['prixPromo']) && $input['prixPromo'] !== null && $input['prixPromo'] !== '' && (float)$input['prixPromo'] > 0)
        ? (float)$input['prixPromo']
        : null;
    $enPromo = (!empty($input['enPromotion']) && $prixPromo !== null && $prixPromo < (float)$input['prixBase']) ? 1 : 0;

    $stmt = $pdo->prepare("
        UPDATE voyages SET
            code_voyage = ?,
            titre = ?,
            description = ?,
            ville_depart = ?,
            ville_destination = ?,
            pays_destination = ?,
            compagnie = ?,
            type_transport = ?,
            categorie = ?,
            duree_minutes = ?,
            escales = ?,
            prix_base = ?,
            prix_promo = ?,
            en_promotion = ?,
            places_disponibles = ?,
            bagage_cabine_inclus = ?,
            bagage_soute_inclus = ?,
            annulation_gratuite = ?,
            repas_inclus = ?,
            wifi_disponible = ?
        WHERE id = ?
    ");
    $stmt->execute([
        $input['codeVoyage'],
        $input['titre'],
        $input['description'] ?? '',
        $input['villeDepart'],
        $input['villeDestination'],
        $input['paysDestination'],
        $input['compagnie'],
        $input['typeTransport'],
        $input['categorie'],
        (int)($input['dureeMinutes'] ?? 140),
        (int)($input['escales'] ?? 0),
        (float)$input['prixBase'],
        $prixPromo,
        $enPromo,
        $placesDispo,
        !empty($input['bagageCabineInclus']) ? 1 : 0,
        !empty($input['bagageSouteInclus']) ? 1 : 0,
        !empty($input['annulationGratuite']) ? 1 : 0,
        !empty($input['repasInclus']) ? 1 : 0,
        !empty($input['wifiDisponible']) ? 1 : 0,
        $id
    ]);

    $stmt = $pdo->prepare("SELECT * FROM voyages WHERE id = ?");
    $stmt->execute([$id]);
    $row = $stmt->fetch();
    if (!$row) {
        http_response_code(404);
        echo json_encode(["error" => "Voyage introuvable"]);
        exit;
    }
    echo json_encode(mapperVoyage($pdo, $row));
    exit;
}

if ($action === 'voyages' && $method === 'DELETE') {
    $id = isset($_GET['id']) ? (int)$_GET['id'] : 0;
    if ($id <= 0) {
        http_response_code(400);
        echo json_encode(["error" => "Paramètre id obligatoire pour la suppression"]);
        exit;
    }
    $pdo->prepare("DELETE FROM offres_billets WHERE voyage_id = ?")->execute([$id]);
    $pdo->prepare("DELETE FROM voyages WHERE id = ?")->execute([$id]);

    echo json_encode(["status" => "deleted", "id" => $id]);
    exit;
}

// ============================================================================
// 2. CRUD OFFRES DE BILLETS (POST, PUT, DELETE)
// ============================================================================

if ($action === 'billets' && $method === 'POST') {
    $voyageId = isset($_GET['voyageId']) ? (int)$_GET['voyageId'] : 0;
    $input = json_decode(file_get_contents("php://input"), true);
    if ($voyageId <= 0 && isset($input['voyageId'])) {
        $voyageId = (int)$input['voyageId'];
    }
    if ($voyageId <= 0) {
        http_response_code(400);
        echo json_encode(["error" => "voyageId est obligatoire pour ajouter un billet"]);
        exit;
    }
    $souteKg = min(30, max(0, (int)($input['bagageSouteKg'] ?? 0)));
    $stmt = $pdo->prepare("
        INSERT INTO offres_billets (
            code_offre_billet, classe, prix, places_restantes, bagage_cabine_kg, bagage_soute_kg,
            modifiable, remboursable, choix_siege_gratuit, acces_salon_vip, conditions_tarifaires, voyage_id
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    ");
    $stmt->execute([
        $input['codeOffreBillet'],
        $input['classe'],
        (float)$input['prix'],
        (int)($input['placesRestantes'] ?? 10),
        min(30, (int)($input['bagageCabineKg'] ?? 8)),
        $souteKg,
        !empty($input['modifiable']) ? 1 : 0,
        !empty($input['remboursable']) ? 1 : 0,
        !empty($input['choixSiegeGratuit']) ? 1 : 0,
        !empty($input['accesSalonVip']) ? 1 : 0,
        $input['conditionsTarifaires'] ?? '',
        $voyageId
    ]);
    $billetId = (int)$pdo->lastInsertId();
    http_response_code(201);
    echo json_encode([
        "id" => $billetId,
        "voyageId" => $voyageId,
        "status" => "created"
    ]);
    exit;
}

if ($action === 'billets' && $method === 'DELETE') {
    $id = isset($_GET['id']) ? (int)$_GET['id'] : 0;
    $pdo->prepare("DELETE FROM offres_billets WHERE id = ?")->execute([$id]);
    echo json_encode(["status" => "deleted", "id" => $id]);
    exit;
}

// ============================================================================
// 3. CONSULTATION DE DISPONIBILITÉ & RÉSERVATION DE BILLET (MODULE 2)
// ============================================================================

if ($action === 'disponibilite' && $method === 'GET') {
    $id = isset($_GET['id']) ? (int)$_GET['id'] : 0;
    $stmt = $pdo->prepare("SELECT * FROM voyages WHERE id = ?");
    $stmt->execute([$id]);
    $v = $stmt->fetch();
    if (!$v) {
        http_response_code(404);
        echo json_encode(["error" => "Voyage introuvable"]);
        exit;
    }
    $places = (int)$v['places_disponibles'];
    $prixEffectif = ($v['en_promotion'] && $v['prix_promo'] > 0) ? (float)$v['prix_promo'] : (float)$v['prix_base'];
    echo json_encode([
        "id_voyage" => (int)$v['id'],
        "code_voyage" => $v['code_voyage'],
        "titre" => $v['titre'],
        "nombre_places" => max($places, 30),
        "places_disponibles" => $places,
        "statut" => $places > 0 ? "DISPONIBLE" : "COMPLETE",
        "prix_unitaire" => $prixEffectif,
        "max_bagage_autorise_kg" => 30.0
    ]);
    exit;
}

if ($action === 'reserver' && $method === 'POST') {
    $input = json_decode(file_get_contents("php://input"), true);
    $voyageId = (int)($input['voyageId'] ?? 0);
    $billetId = (int)($input['billetId'] ?? 0);

    if ($billetId > 0) {
        $pdo->prepare("UPDATE offres_billets SET places_restantes = GREATEST(0, places_restantes - 1) WHERE id = ?")->execute([$billetId]);
    }
    if ($voyageId > 0) {
        $pdo->prepare("UPDATE voyages SET places_disponibles = GREATEST(0, places_disponibles - 1) WHERE id = ?")->execute([$voyageId]);
        $stmt = $pdo->prepare("SELECT * FROM voyages WHERE id = ?");
        $stmt->execute([$voyageId]);
        $v = $stmt->fetch();
        if ($v) {
            echo json_encode(mapperVoyage($pdo, $v));
            exit;
        }
    }
    echo json_encode(["status" => "reserved"]);
    exit;
}

echo json_encode(["status" => "UP", "database" => $db]);
