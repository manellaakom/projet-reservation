/**
 * Module 2 — Recherche, Consultation, Comparaison & Suggestions d'Offres et Billets
 * Client Frontend connecté à l'API Spring Boot (http://localhost:8082/api/recherche)
 * avec moteur local synchronisé pour un fonctionnement instantané.
 */

const API_BASE_URL = "http://localhost:8082/api/recherche";
const XAMPP_MYSQL_API_URL = "http://localhost/api-recherche/index.php";
let backendConnecte = false;
let xamppMysqlConnecte = false;

// Catalogue initial (identique au DataInitializer Spring Boot)
let catalogueVoyages = [
    {
        id: 1,
        codeVoyage: "VOY-TUN-PAR-101",
        titre: "Vol Direct Tunis → Paris Charles de Gaulle",
        description: "Vol régulier direct opéré par Tunisair en Airbus A320neo. Repas chaud méditerranéen, bagage cabine 8 kg et bagage en soute 23 kg inclus dès le tarif standard.",
        villeDepart: "Tunis",
        villeDestination: "Paris",
        paysDestination: "France",
        compagnie: "Tunisair",
        typeTransport: "VOL",
        categorie: "CONFORT",
        dateDepart: "2026-11-15T08:30:00",
        dateArrivee: "2026-11-15T11:05:00",
        dureeMinutes: 155,
        escales: 0,
        prixBase: 620,
        prixPromo: 465,
        enPromotion: true,
        placesDisponibles: 28,
        noteMoyenne: 4.6,
        nombreAvis: 184,
        nombreConsultations: 245,
        bagageCabineInclus: true,
        bagageSouteInclus: true,
        annulationGratuite: true,
        repasInclus: true,
        wifiDisponible: false,
        imageUrl: "https://images.unsplash.com/photo-1502602898657-3e91760cbb34?auto=format&fit=crop&w=900&q=80",
        statut: "DISPONIBLE",
        billets: [
            {
                id: 101,
                codeOffreBillet: "BIL-PAR-101-ECO",
                classe: "ECONOMIQUE",
                prix: 465,
                placesRestantes: 20,
                bagageCabineKg: 8,
                bagageSouteKg: 23,
                modifiable: true,
                remboursable: false,
                choixSiegeGratuit: false,
                accesSalonVip: false,
                conditionsTarifaires: "Modification gratuite jusqu'à 48h avant le départ. Bagage 23 kg inclus."
            },
            {
                id: 102,
                codeOffreBillet: "BIL-PAR-101-BUS",
                classe: "BUSINESS",
                prix: 890,
                placesRestantes: 8,
                bagageCabineKg: 12,
                bagageSouteKg: 46,
                modifiable: true,
                remboursable: true,
                choixSiegeGratuit: true,
                accesSalonVip: true,
                conditionsTarifaires: "100% remboursable, accès Salon Privilège Tunis-Carthage et coupe-file sécurité."
            }
        ]
    },
    {
        id: 2,
        codeVoyage: "VOY-TUN-PAR-102",
        titre: "Vol Express Tunis → Paris Orly (Tarif Light)",
        description: "Solution économique idéale pour un court séjour à Paris. Arrivée à l'aéroport de Paris-Orly à 25 minutes du centre-ville via le métro ligne 14.",
        villeDepart: "Tunis",
        villeDestination: "Paris",
        paysDestination: "France",
        compagnie: "Transavia France",
        typeTransport: "VOL",
        categorie: "ECONOMIQUE",
        dateDepart: "2026-11-15T14:15:00",
        dateArrivee: "2026-11-15T16:45:00",
        dureeMinutes: 150,
        escales: 0,
        prixBase: 340,
        prixPromo: 295,
        enPromotion: true,
        placesDisponibles: 14,
        noteMoyenne: 4.4,
        nombreAvis: 142,
        nombreConsultations: 310,
        bagageCabineInclus: true,
        bagageSouteInclus: false,
        annulationGratuite: false,
        repasInclus: false,
        wifiDisponible: false,
        imageUrl: "https://images.unsplash.com/photo-1499856871958-5b9627545d1a?auto=format&fit=crop&w=900&q=80",
        statut: "DISPONIBLE",
        billets: [
            {
                id: 103,
                codeOffreBillet: "BIL-PAR-102-ECO",
                classe: "ECONOMIQUE",
                prix: 295,
                placesRestantes: 10,
                bagageCabineKg: 10,
                bagageSouteKg: 0,
                modifiable: false,
                remboursable: false,
                choixSiegeGratuit: false,
                accesSalonVip: false,
                conditionsTarifaires: "Tarif Basic : bagage cabine 10 kg inclus, bagage soute en supplément."
            },
            {
                id: 104,
                codeOffreBillet: "BIL-PAR-102-PLUS",
                classe: "PREMIUM_ECO",
                prix: 420,
                placesRestantes: 4,
                bagageCabineKg: 10,
                bagageSouteKg: 20,
                modifiable: true,
                remboursable: false,
                choixSiegeGratuit: true,
                accesSalonVip: false,
                conditionsTarifaires: "Tarif Plus : bagage en soute 20 kg + choix du siège standard inclus."
            }
        ]
    },
    {
        id: 3,
        codeVoyage: "VOY-TUN-IST-201",
        titre: "Vol Confort Tunis → Istanbul Airport",
        description: "Découvrez Istanbul avec Turkish Airlines, élue meilleure compagnie d'Europe. Écrans individuels, Wi-Fi à bord, gastronomie turque et franchise bagage généreuse de 30 kg.",
        villeDepart: "Tunis",
        villeDestination: "Istanbul",
        paysDestination: "Turquie",
        compagnie: "Turkish Airlines",
        typeTransport: "VOL",
        categorie: "PREMIUM",
        dateDepart: "2026-11-18T10:00:00",
        dateArrivee: "2026-11-18T14:50:00",
        dureeMinutes: 170,
        escales: 0,
        prixBase: 780,
        prixPromo: 640,
        enPromotion: true,
        placesDisponibles: 22,
        noteMoyenne: 4.9,
        nombreAvis: 265,
        nombreConsultations: 390,
        bagageCabineInclus: true,
        bagageSouteInclus: true,
        annulationGratuite: true,
        repasInclus: true,
        wifiDisponible: true,
        imageUrl: "https://images.unsplash.com/photo-1524231757912-21f4fe3a7200?auto=format&fit=crop&w=900&q=80",
        statut: "DISPONIBLE",
        billets: [
            {
                id: 105,
                codeOffreBillet: "BIL-IST-201-ECO",
                classe: "ECONOMIQUE",
                prix: 640,
                placesRestantes: 16,
                bagageCabineKg: 8,
                bagageSouteKg: 30,
                modifiable: true,
                remboursable: false,
                choixSiegeGratuit: true,
                accesSalonVip: false,
                conditionsTarifaires: "Franchise bagage soute 30 kg, repas chaud et système multimédia inclus."
            },
            {
                id: 106,
                codeOffreBillet: "BIL-IST-201-BUS",
                classe: "BUSINESS",
                prix: 1290,
                placesRestantes: 6,
                bagageCabineKg: 16,
                bagageSouteKg: 40,
                modifiable: true,
                remboursable: true,
                choixSiegeGratuit: true,
                accesSalonVip: true,
                conditionsTarifaires: "Accès au célèbre Lounge Turkish Airlines, sièges inclinables et remboursement intégral."
            }
        ]
    },
    {
        id: 4,
        codeVoyage: "VOY-TUN-ROM-301",
        titre: "Escapade Italienne Tunis → Rome Fiumicino",
        description: "Liaison rapide d'à peine 1h20 vers la capitale italienne. Idéal pour un week-end culturel ou des correspondances européennes.",
        villeDepart: "Tunis",
        villeDestination: "Rome",
        paysDestination: "Italie",
        compagnie: "ITA Airways",
        typeTransport: "VOL",
        categorie: "CONFORT",
        dateDepart: "2026-11-20T09:15:00",
        dateArrivee: "2026-11-20T10:35:00",
        dureeMinutes: 80,
        escales: 0,
        prixBase: 410,
        prixPromo: 350,
        enPromotion: true,
        placesDisponibles: 19,
        noteMoyenne: 4.7,
        nombreAvis: 118,
        nombreConsultations: 195,
        bagageCabineInclus: true,
        bagageSouteInclus: true,
        annulationGratuite: true,
        repasInclus: true,
        wifiDisponible: true,
        imageUrl: "https://images.unsplash.com/photo-1552832230-c0197dd311b5?auto=format&fit=crop&w=900&q=80",
        statut: "DISPONIBLE",
        billets: [
            {
                id: 107,
                codeOffreBillet: "BIL-ROM-301-ECO",
                classe: "ECONOMIQUE",
                prix: 350,
                placesRestantes: 15,
                bagageCabineKg: 8,
                bagageSouteKg: 23,
                modifiable: true,
                remboursable: false,
                choixSiegeGratuit: false,
                accesSalonVip: false,
                conditionsTarifaires: "Bagage soute 23 kg et collation italienne inclus."
            }
        ]
    },
    {
        id: 5,
        codeVoyage: "VOY-TUN-DXB-401",
        titre: "Expérience Prestige Tunis → Dubaï International",
        description: "Voyagez à bord du Boeing 777 d'Emirates en liaison directe vers Dubaï. Divertissement ICE primé, Wi-Fi haut débit, repas gastronomique et 2 bagages en soute.",
        villeDepart: "Tunis",
        villeDestination: "Dubaï",
        paysDestination: "Émirats Arabes Unis",
        compagnie: "Emirates",
        typeTransport: "VOL",
        categorie: "LUXE",
        dateDepart: "2026-12-01T15:00:00",
        dateArrivee: "2026-12-01T23:45:00",
        dureeMinutes: 345,
        escales: 0,
        prixBase: 1650,
        prixPromo: 1390,
        enPromotion: true,
        placesDisponibles: 7,
        noteMoyenne: 4.95,
        nombreAvis: 312,
        nombreConsultations: 420,
        bagageCabineInclus: true,
        bagageSouteInclus: true,
        annulationGratuite: true,
        repasInclus: true,
        wifiDisponible: true,
        imageUrl: "https://images.unsplash.com/photo-1512453979798-5ea266f8880c?auto=format&fit=crop&w=900&q=80",
        statut: "DERNIERES_PLACES",
        billets: [
            {
                id: 108,
                codeOffreBillet: "BIL-DXB-401-ECO",
                classe: "ECONOMIQUE",
                prix: 1390,
                placesRestantes: 5,
                bagageCabineKg: 10,
                bagageSouteKg: 35,
                modifiable: true,
                remboursable: true,
                choixSiegeGratuit: true,
                accesSalonVip: false,
                conditionsTarifaires: "35 kg en soute, modification et annulation sans frais."
            },
            {
                id: 109,
                codeOffreBillet: "BIL-DXB-401-FIRST",
                classe: "PREMIERE",
                prix: 3450,
                placesRestantes: 2,
                bagageCabineKg: 15,
                bagageSouteKg: 50,
                modifiable: true,
                remboursable: true,
                choixSiegeGratuit: true,
                accesSalonVip: true,
                conditionsTarifaires: "Suite privée Première Classe, service chauffeur privé et Salon Emirates."
            }
        ]
    },
    {
        id: 6,
        codeVoyage: "VOY-TUN-MRS-501",
        titre: "Traversée Ferry Tunis La Goulette → Marseille",
        description: "Voyagez en mer Méditerranée avec cabine privée vue mer et possibilité d'embarquer avec un bagage volumineux jusqu'à 50 kg.",
        villeDepart: "Tunis",
        villeDestination: "Marseille",
        paysDestination: "France",
        compagnie: "CTN Navigation",
        typeTransport: "BATEAU",
        categorie: "CONFORT",
        dateDepart: "2026-11-22T12:00:00",
        dateArrivee: "2026-11-23T09:00:00",
        dureeMinutes: 1260,
        escales: 0,
        prixBase: 540,
        prixPromo: null,
        enPromotion: false,
        placesDisponibles: 45,
        noteMoyenne: 4.3,
        nombreAvis: 89,
        nombreConsultations: 130,
        bagageCabineInclus: true,
        bagageSouteInclus: true,
        annulationGratuite: true,
        repasInclus: true,
        wifiDisponible: true,
        imageUrl: "https://images.unsplash.com/photo-1548574505-5e239809ee19?auto=format&fit=crop&w=900&q=80",
        statut: "DISPONIBLE",
        billets: [
            {
                id: 110,
                codeOffreBillet: "BIL-MRS-501-CAB",
                classe: "PREMIUM_ECO",
                prix: 540,
                placesRestantes: 45,
                bagageCabineKg: 15,
                bagageSouteKg: 50,
                modifiable: true,
                remboursable: true,
                choixSiegeGratuit: true,
                accesSalonVip: false,
                conditionsTarifaires: "Cabine privative 2 personnes avec hublot + pension complète incluse."
            }
        ]
    },
    {
        id: 7,
        codeVoyage: "VOY-DJE-BCN-601",
        titre: "Vol + Séjour Méditerranéen Djerba → Barcelone",
        description: "Formule combinée vol + transfert vers le cœur de Barcelone. Idéal pour découvrir la Sagrada Família et la côte catalane.",
        villeDepart: "Djerba",
        villeDestination: "Barcelone",
        paysDestination: "Espagne",
        compagnie: "Nouvelair",
        typeTransport: "SEJOUR_PACK",
        categorie: "PROMO",
        dateDepart: "2026-11-25T11:00:00",
        dateArrivee: "2026-11-25T13:10:00",
        dureeMinutes: 130,
        escales: 0,
        prixBase: 690,
        prixPromo: 499,
        enPromotion: true,
        placesDisponibles: 12,
        noteMoyenne: 4.75,
        nombreAvis: 96,
        nombreConsultations: 215,
        bagageCabineInclus: true,
        bagageSouteInclus: true,
        annulationGratuite: true,
        repasInclus: true,
        wifiDisponible: true,
        imageUrl: "https://images.unsplash.com/photo-1583422409516-2895a77efded?auto=format&fit=crop&w=900&q=80",
        statut: "DISPONIBLE",
        billets: [
            {
                id: 111,
                codeOffreBillet: "BIL-BCN-601-PACK",
                classe: "ECONOMIQUE",
                prix: 499,
                placesRestantes: 12,
                bagageCabineKg: 10,
                bagageSouteKg: 25,
                modifiable: true,
                remboursable: false,
                choixSiegeGratuit: true,
                accesSalonVip: false,
                conditionsTarifaires: "Pack Vol A/R + Navette Aéroport El Prat + Bagage 25 kg."
            }
        ]
    },
    {
        id: 8,
        codeVoyage: "VOY-PAR-LON-701",
        titre: "TGV Eurostar Direct Paris Gare du Nord → Londres St Pancras",
        description: "Reliez le cœur de Paris au centre de Londres en 2h15 sous la Manche. Enregistrement rapide, Wi-Fi gratuit et 2 bagages inclus sans restriction de liquides.",
        villeDepart: "Paris",
        villeDestination: "Londres",
        paysDestination: "Royaume-Uni",
        compagnie: "Eurostar",
        typeTransport: "TRAIN",
        categorie: "PREMIUM",
        dateDepart: "2026-11-16T13:13:00",
        dateArrivee: "2026-11-16T15:30:00",
        dureeMinutes: 137,
        escales: 0,
        prixBase: 310,
        prixPromo: 245,
        enPromotion: true,
        placesDisponibles: 34,
        noteMoyenne: 4.85,
        nombreAvis: 210,
        nombreConsultations: 275,
        bagageCabineInclus: true,
        bagageSouteInclus: true,
        annulationGratuite: true,
        repasInclus: true,
        wifiDisponible: true,
        imageUrl: "https://images.unsplash.com/photo-1513635269975-59663e0ac1ad?auto=format&fit=crop&w=900&q=80",
        statut: "DISPONIBLE",
        billets: [
            {
                id: 112,
                codeOffreBillet: "BIL-LON-701-STD",
                classe: "ECONOMIQUE",
                prix: 245,
                placesRestantes: 24,
                bagageCabineKg: 12,
                bagageSouteKg: 30,
                modifiable: true,
                remboursable: false,
                choixSiegeGratuit: true,
                accesSalonVip: false,
                conditionsTarifaires: "Eurostar Standard : 2 grandes valises + 1 bagage à main, échange sans frais."
            },
            {
                id: 113,
                codeOffreBillet: "BIL-LON-701-PREM",
                classe: "BUSINESS",
                prix: 480,
                placesRestantes: 10,
                bagageCabineKg: 15,
                bagageSouteKg: 45,
                modifiable: true,
                remboursable: true,
                choixSiegeGratuit: true,
                accesSalonVip: true,
                conditionsTarifaires: "Eurostar Plus / Premier : repas servi à la place et accès salon Business."
            }
        ]
    }
];

let idsComparaison = [1, 2]; // 2 offres pré-sélectionnées pour démontrer le comparateur immédiatement

// ============================================================================
// FONCTIONS D'ENRICHISSEMENT & CALCUL DE SCORES (Miroir du Mapper Spring Boot)
// ============================================================================

function enrichirVoyage(v) {
    const prixEffectif = (v.enPromotion && v.prixPromo && v.prixPromo > 0) ? v.prixPromo : v.prixBase;
    const pourcentageReduction = (v.enPromotion && v.prixPromo && v.prixBase > 0 && v.prixPromo < v.prixBase)
        ? Math.round(((v.prixBase - v.prixPromo) / v.prixBase) * 100)
        : 0;

    let score = 55;
    if (v.noteMoyenne) score += (v.noteMoyenne - 3.5) * 13;
    score += Math.min(12, pourcentageReduction * 0.5);
    if (v.escales === 0) score += 6;
    if (v.bagageSouteInclus) score += 4;
    if (v.annulationGratuite) score += 4;
    if (v.repasInclus) score += 2;
    if (v.wifiDisponible) score += 2;
    if (prixEffectif < 450) score += 5;
    const scoreQualitePrix = Math.max(40, Math.min(99, Math.round(score)));

    const badges = [];
    if (v.enPromotion && pourcentageReduction > 0) badges.push(`Promo -${pourcentageReduction}%`);
    if (scoreQualitePrix >= 88) badges.push("Meilleur Rapport Qualité/Prix");
    if (v.noteMoyenne >= 4.8) badges.push("Coup de Cœur Voyageurs");
    if (v.escales === 0) badges.push("Trajet Direct");
    if (v.nombreConsultations >= 250) badges.push("Offre Tendance");
    if (v.placesDisponibles <= 8) badges.push("Dernières Places");

    return {
        ...v,
        prixEffectif,
        pourcentageReduction,
        scoreQualitePrix,
        dureeFormatee: formaterDuree(v.dureeMinutes),
        badgesRecommandation: badges
    };
}

function formaterDuree(minutes) {
    if (!minutes || minutes <= 0) return "N/A";
    const h = Math.floor(minutes / 60);
    const m = minutes % 60;
    return h > 0 ? `${h}h ${String(m).padStart(2, "0")}min` : `${m}min`;
}

function formaterHeure(isoDate) {
    if (!isoDate) return "--:--";
    const d = new Date(isoDate);
    return d.toLocaleTimeString("fr-FR", { hour: "2-digit", minute: "2-digit" });
}

function formaterDate(isoDate) {
    if (!isoDate) return "";
    const d = new Date(isoDate);
    return d.toLocaleDateString("fr-FR", { day: "2-digit", month: "short", year: "numeric" });
}

function iconeTransport(type) {
    switch (type) {
        case "VOL": return "✈️ Vol";
        case "TRAIN": return "🚄 Train";
        case "BATEAU": return "⛴️ Ferry";
        case "SEJOUR_PACK": return "🏖️ Séjour Pack";
        case "BUS": return "🚌 Bus";
        default: return type;
    }
}

// ============================================================================
// INITIALISATION & SYNCHRONISATION API SPRING BOOT
// ============================================================================

async function initialiserApplication() {
    try {
        const res = await fetch(`${API_BASE_URL}/voyages`, { method: "GET" });
        if (res.ok) {
            const data = await res.json();
            if (Array.isArray(data) && data.length > 0) {
                catalogueVoyages = data;
                backendConnecte = true;
            }
        }
    } catch (_) {
        backendConnecte = false;
    }

    if (!backendConnecte) {
        try {
            const resMysql = await fetch(`${XAMPP_MYSQL_API_URL}?action=voyages`, { method: "GET" });
            if (resMysql.ok) {
                const dataMysql = await resMysql.json();
                if (Array.isArray(dataMysql) && dataMysql.length > 0) {
                    catalogueVoyages = dataMysql;
                    xamppMysqlConnecte = true;
                }
            }
        } catch (_) {
            xamppMysqlConnecte = false;
        }
    }

    peuplerListesFiltres();
    mettreAJourStatistiques();
    appliquerFiltres();
    filtrerBillets();
    mettreAJourBarreComparaison();
    rendreComparateur();
    rafraichirSuggestions();
}

function peuplerListesFiltres() {
    const departs = [...new Set(catalogueVoyages.map(v => v.villeDepart))].sort();
    const destinations = [...new Set(catalogueVoyages.map(v => v.villeDestination))].sort();
    const compagnies = [...new Set(catalogueVoyages.map(v => v.compagnie))].sort();

    const selDepart = document.getElementById("search-depart");
    selDepart.innerHTML = `<option value="">Toutes les villes de départ</option>` +
        departs.map(d => `<option value="${d}">${d}</option>`).join("");

    const selDest = document.getElementById("search-destination");
    selDest.innerHTML = `<option value="">Toutes les destinations</option>` +
        destinations.map(d => `<option value="${d}">${d}</option>`).join("");

    const selComp = document.getElementById("filter-compagnie");
    selComp.innerHTML = `<option value="">Toutes les compagnies</option>` +
        compagnies.map(c => `<option value="${c}">${c}</option>`).join("");
}

function mettreAJourStatistiques() {
    const totalBillets = catalogueVoyages.reduce((acc, v) => acc + (v.billets ? v.billets.length : 0), 0);
    const totalPromos = catalogueVoyages.filter(v => v.enPromotion).length;
    const totalVues = catalogueVoyages.reduce((acc, v) => acc + (v.nombreConsultations || 0), 0);

    document.getElementById("stat-voyages").textContent = catalogueVoyages.length;
    document.getElementById("stat-billets").textContent = totalBillets;
    document.getElementById("stat-promos").textContent = totalPromos;
    document.getElementById("stat-vues").textContent = totalVues.toLocaleString("fr-FR");
}

function mettreAJourLabelPrix() {
    const val = document.getElementById("filter-prix-max").value;
    document.getElementById("prix-max-display").textContent = `${Number(val).toLocaleString("fr-FR")} TND`;
}

// ============================================================================
// 1. RECHERCHE, FILTRAGE MULTICRITÈRES & TRI DES VOYAGES
// ============================================================================

function appliquerFiltres() {
    const motCle = document.getElementById("search-motcle").value.trim().toLowerCase();
    const villeDepart = document.getElementById("search-depart").value;
    const villeDestination = document.getElementById("search-destination").value;
    const typeTransport = document.getElementById("search-transport").value;
    const prixMax = Number(document.getElementById("filter-prix-max").value);
    const categorie = document.getElementById("filter-categorie").value;
    const classeBillet = document.getElementById("filter-classe").value;
    const compagnie = document.getElementById("filter-compagnie").value;
    const noteMin = Number(document.getElementById("filter-note").value);
    const directUniquement = document.getElementById("filter-direct").checked;
    const promoUniquement = document.getElementById("filter-promo").checked;
    const souteInclus = document.getElementById("filter-soute").checked;
    const annulationGratuite = document.getElementById("filter-annulation").checked;
    const tri = document.getElementById("sort-by").value;

    let resultats = catalogueVoyages.map(enrichirVoyage).filter(v => {
        if (motCle) {
            const match = [v.titre, v.description, v.villeDepart, v.villeDestination, v.paysDestination, v.compagnie, v.codeVoyage]
                .some(champ => champ && champ.toLowerCase().includes(motCle));
            if (!match) return false;
        }
        if (villeDepart && v.villeDepart !== villeDepart) return false;
        if (villeDestination && v.villeDestination !== villeDestination) return false;
        if (typeTransport && v.typeTransport !== typeTransport) return false;
        if (v.prixEffectif > prixMax) return false;
        if (categorie && v.categorie !== categorie) return false;
        if (compagnie && v.compagnie !== compagnie) return false;
        if (noteMin > 0 && v.noteMoyenne < noteMin) return false;
        if (directUniquement && v.escales > 0) return false;
        if (promoUniquement && !v.enPromotion) return false;
        if (souteInclus && !v.bagageSouteInclus) return false;
        if (annulationGratuite && !v.annulationGratuite) return false;
        if (classeBillet) {
            const aClasse = v.billets && v.billets.some(b => b.classe === classeBillet && b.placesRestantes > 0);
            if (!aClasse) return false;
        }
        return true;
    });

    // Tri
    const [champTri, dirTri] = tri.split("-");
    const facteur = dirTri === "desc" ? -1 : 1;

    resultats.sort((a, b) => {
        switch (champTri) {
            case "duree": return (a.dureeMinutes - b.dureeMinutes) * facteur;
            case "note": return (a.noteMoyenne - b.noteMoyenne) * facteur;
            case "popularite": return (a.nombreConsultations - b.nombreConsultations) * facteur;
            case "score": return (a.scoreQualitePrix - b.scoreQualitePrix) * facteur;
            case "dateDepart": return (new Date(a.dateDepart) - new Date(b.dateDepart)) * facteur;
            default: return (a.prixEffectif - b.prixEffectif) * facteur;
        }
    });

    document.getElementById("resultats-count").textContent = resultats.length;
    rendreListeVoyages(resultats);
}

function rendreListeVoyages(voyages) {
    const conteneur = document.getElementById("voyages-grid");
    if (voyages.length === 0) {
        conteneur.innerHTML = `
            <div class="section-banner" style="justify-content:center; text-align:center; padding:40px;">
                <div>
                    <h3>Aucune offre ne correspond à vos critères de filtrage</h3>
                    <p style="margin:8px 0 16px;">Essayez d'augmenter le budget maximum ou de réinitialiser certains filtres.</p>
                    <button class="btn btn-primary" onclick="reinitialiserFiltres()">Réinitialiser tous les filtres</button>
                </div>
            </div>`;
        return;
    }

    conteneur.innerHTML = voyages.map(v => {
        const estDansComparateur = idsComparaison.includes(v.id);
        return `
        <article class="voyage-card ${estDansComparateur ? 'selected-compare' : ''}">
            <div class="voyage-img-wrap">
                <img src="${v.imageUrl}" alt="${v.titre}" loading="lazy" />
                <span class="transport-pill">${iconeTransport(v.typeTransport)}</span>
                ${v.enPromotion && v.pourcentageReduction > 0
                    ? `<span class="promo-ribbon">-${v.pourcentageReduction}% PROMO</span>`
                    : ""}
            </div>

            <div class="voyage-body">
                <div>
                    <div class="voyage-badges">
                        ${v.badgesRecommandation.map(b => {
                            const cls = b.includes("Qualité/Prix") ? "green" : (b.includes("Cœur") ? "gold" : "");
                            return `<span class="badge-tag ${cls}">${b}</span>`;
                        }).join("")}
                        <span class="badge-tag">${v.categorie}</span>
                    </div>

                    <h3 class="voyage-title">${v.titre}</h3>
                    <div class="voyage-meta">
                        🏢 <strong>${v.compagnie}</strong> • 📅 ${formaterDate(v.dateDepart)} • ⭐ <strong>${v.noteMoyenne}</strong>/5 (${v.nombreAvis} avis) • 👁️ ${v.nombreConsultations} vues
                    </div>

                    <div class="route-timeline">
                        <div class="route-point">
                            <div class="route-time">${formaterHeure(v.dateDepart)}</div>
                            <div class="route-city">${v.villeDepart}</div>
                        </div>
                        <div class="route-line">
                            <span class="route-duration">⏱️ ${v.dureeFormatee}</span>
                            <span class="route-stops">${v.escales === 0 ? "Direct sans escale" : `${v.escales} escale(s)`}</span>
                        </div>
                        <div class="route-point">
                            <div class="route-time">${formaterHeure(v.dateArrivee)}</div>
                            <div class="route-city">${v.villeDestination} (${v.paysDestination})</div>
                        </div>
                    </div>
                </div>

                <div class="services-list">
                    ${v.bagageCabineInclus ? `<span class="service-item">🎒 Cabine inclus</span>` : ""}
                    ${v.bagageSouteInclus ? `<span class="service-item">🧳 Soute inclus</span>` : ""}
                    ${v.annulationGratuite ? `<span class="service-item">🛡️ Annulation gratuite</span>` : ""}
                    ${v.repasInclus ? `<span class="service-item">🍽️ Repas à bord</span>` : ""}
                    ${v.wifiDisponible ? `<span class="service-item">📶 Wi-Fi</span>` : ""}
                    <span class="service-item">🎫 ${v.billets ? v.billets.length : 1} classe(s) de billet</span>
                </div>
            </div>

            <div class="voyage-pricing">
                <div class="score-pill">
                    <span>Score Qualité/Prix</span>
                    <span class="score-box">${v.scoreQualitePrix}/100</span>
                </div>

                <div>
                    ${v.enPromotion ? `<div class="price-old">${v.prixBase} TND</div>` : ""}
                    <div class="price-current">${v.prixEffectif} TND</div>
                    <div class="price-sub">par voyageur • ${v.placesDisponibles} places disp.</div>
                </div>

                <div class="card-actions">
                    <button class="btn btn-primary btn-sm" onclick="consulterOffreDetail(${v.id})">
                        👁️ Consulter & Billets
                    </button>
                    <button class="btn ${estDansComparateur ? 'btn-accent' : 'btn-secondary'} btn-sm" onclick="basculerComparaison(${v.id})">
                        ${estDansComparateur ? '✓ Dans le comparateur' : '⚖️ Comparer cette offre'}
                    </button>
                    <div style="display:flex; gap:6px;">
                        <button class="btn btn-ghost btn-sm" style="flex:1;" onclick="ouvrirModalModifierOffre(${v.id})" title="Modifier cette offre (PUT)">
                            ✏️ Modifier
                        </button>
                        <button class="btn btn-ghost btn-sm" style="flex:1; color:var(--danger); border-color:#fecaca;" onclick="supprimerOffreVoyage(${v.id})" title="Supprimer cette offre (DELETE)">
                            🗑️ Suppr.
                        </button>
                    </div>
                </div>
            </div>
        </article>`;
    }).join("");
}

function reinitialiserFiltres() {
    document.getElementById("search-motcle").value = "";
    document.getElementById("search-depart").value = "";
    document.getElementById("search-destination").value = "";
    document.getElementById("search-transport").value = "";
    document.getElementById("filter-prix-max").value = 1800;
    document.getElementById("filter-categorie").value = "";
    document.getElementById("filter-classe").value = "";
    document.getElementById("filter-compagnie").value = "";
    document.getElementById("filter-note").value = "0";
    document.getElementById("filter-direct").checked = false;
    document.getElementById("filter-promo").checked = false;
    document.getElementById("filter-soute").checked = false;
    document.getElementById("filter-annulation").checked = false;
    document.getElementById("sort-by").value = "prix-asc";
    mettreAJourLabelPrix();
    appliquerFiltres();
}

// ============================================================================
// 2. EXPLORATEUR & RECHERCHE DIRECTE DES BILLETS
// ============================================================================

function filtrerBillets() {
    const classe = document.getElementById("billet-classe").value;
    const bagageMin = Number(document.getElementById("billet-bagage").value);
    const tri = document.getElementById("billet-tri").value;
    const modifiable = document.getElementById("billet-modifiable").checked;
    const remboursable = document.getElementById("billet-remboursable").checked;

    const tousLesBillets = [];
    catalogueVoyages.forEach(v => {
        (v.billets || []).forEach(b => {
            tousLesBillets.push({
                ...b,
                voyageId: v.id,
                titreVoyage: v.titre,
                villeDepart: v.villeDepart,
                villeDestination: v.villeDestination,
                compagnie: v.compagnie,
                dateDepart: v.dateDepart
            });
        });
    });

    const filtres = tousLesBillets.filter(b => {
        if (classe && b.classe !== classe) return false;
        if (b.bagageSouteKg < bagageMin) return false;
        if (modifiable && !b.modifiable) return false;
        if (remboursable && !b.remboursable) return false;
        return true;
    });

    filtres.sort((a, b) => {
        if (tri === "prix-desc") return b.prix - a.prix;
        if (tri === "bagage-desc") return b.bagageSouteKg - a.bagageSouteKg;
        if (tri === "places-desc") return b.placesRestantes - a.placesRestantes;
        return a.prix - b.prix;
    });

    const conteneur = document.getElementById("billets-grid");
    conteneur.innerHTML = filtres.map(b => `
        <div class="billet-card">
            <div>
                <div class="billet-header">
                    <span class="classe-badge classe-${b.classe}">${b.classe}</span>
                    <strong style="font-size:20px; color:var(--primary-dark);">${b.prix} TND</strong>
                </div>
                <h4 style="margin:10px 0 4px; font-size:15px;">${b.titreVoyage}</h4>
                <div style="font-size:12.5px; color:var(--text-muted); margin-bottom:10px;">
                    🏢 ${b.compagnie} • Code : <code>${b.codeOffreBillet}</code>
                </div>
                <div class="services-list" style="margin-bottom:10px;">
                    <span class="service-item">🎒 Cabine ${b.bagageCabineKg} kg</span>
                    <span class="service-item">🧳 Soute ${b.bagageSouteKg} kg</span>
                    <span class="service-item">${b.modifiable ? '✅ Modifiable' : '❌ Non modifiable'}</span>
                    <span class="service-item">${b.remboursable ? '✅ Remboursable' : '❌ Non remboursable'}</span>
                    ${b.accesSalonVip ? `<span class="service-item">👑 Salon VIP</span>` : ""}
                </div>
                <p style="font-size:12px; color:var(--text-muted);">${b.conditionsTarifaires || ""}</p>
            </div>
            <div style="display:flex; justify-content:space-between; align-items:center; gap:8px; padding-top:10px; border-top:1px solid var(--border); flex-wrap:wrap;">
                <span style="font-size:12px; font-weight:600; color:var(--success);">${b.placesRestantes} billets restants</span>
                <div style="display:flex; gap:6px;">
                    <button class="btn btn-secondary btn-sm" onclick="reserverBillet(${b.voyageId}, ${b.id}, '${b.codeOffreBillet}', '${b.classe}', ${b.prix})">
                        🛒 Réserver
                    </button>
                    <button class="btn btn-primary btn-sm" onclick="consulterOffreDetail(${b.voyageId})">
                        Offre →
                    </button>
                </div>
            </div>
        </div>
    `).join("");
}

// ============================================================================
// 3. CONSULTATION DÉTAILLÉE D'UNE OFFRE + SUGGESTIONS SIMILAIRES
// ============================================================================

async function consulterOffreDetail(id) {
    let voyage = catalogueVoyages.find(v => v.id === id);
    if (!voyage) return;

    // Incrémenter les consultations (localement + appel API si disponible)
    voyage.nombreConsultations = (voyage.nombreConsultations || 0) + 1;
    if (backendConnecte) {
        try {
            const res = await fetch(`${API_BASE_URL}/voyages/${id}`);
            if (res.ok) {
                voyage = await res.json();
                const idx = catalogueVoyages.findIndex(item => item.id === id);
                if (idx >= 0) catalogueVoyages[idx] = voyage;
            }
        } catch (_) {}
    } else if (xamppMysqlConnecte) {
        try {
            const res = await fetch(`${XAMPP_MYSQL_API_URL}?action=voyages&id=${id}`);
            if (res.ok) {
                voyage = await res.json();
                const idx = catalogueVoyages.findIndex(item => item.id === id);
                if (idx >= 0) catalogueVoyages[idx] = voyage;
            }
        } catch (_) {}
    }

    mettreAJourStatistiques();
    appliquerFiltres();

    const v = enrichirVoyage(voyage);

    // Calculer les offres similaires et alternatives moins chères pour cette offre
    const autres = catalogueVoyages.filter(item => item.id !== v.id).map(enrichirVoyage);
    const similaires = autres
        .filter(item => item.villeDestination === v.villeDestination || item.typeTransport === v.typeTransport)
        .slice(0, 3);
    const moinsCheres = autres
        .filter(item => item.prixEffectif < v.prixEffectif)
        .sort((a, b) => a.prixEffectif - b.prixEffectif)
        .slice(0, 3);

    const body = document.getElementById("modal-detail-body");
    body.innerHTML = `
        <div style="display:grid; grid-template-columns: 280px 1fr; gap:20px; margin-bottom:20px;">
            <img src="${v.imageUrl}" alt="${v.titre}" style="width:100%; height:200px; object-fit:cover; border-radius:12px;" />
            <div>
                <div style="display:flex; justify-content:space-between; align-items:start; gap:10px; margin-bottom:8px; padding-right:36px;">
                    <div class="voyage-badges">
                        <span class="badge-tag">${iconeTransport(v.typeTransport)}</span>
                        <span class="badge-tag">${v.categorie}</span>
                        ${v.badgesRecommandation.map(b => `<span class="badge-tag green">${b}</span>`).join("")}
                    </div>
                    <div style="display:flex; gap:6px;">
                        <button class="btn btn-secondary btn-sm" onclick="fermerModalDetail(); ouvrirModalModifierOffre(${v.id});">
                            ✏️ Modifier l'offre
                        </button>
                        <button class="btn btn-ghost btn-sm" style="color:var(--danger); border-color:#fecaca;" onclick="fermerModalDetail(); supprimerOffreVoyage(${v.id});">
                            🗑️ Supprimer
                        </button>
                    </div>
                </div>
                <h2 style="font-size:22px; font-weight:800; margin-bottom:6px;">${v.titre}</h2>
                <p style="font-size:13.5px; color:var(--text-muted); margin-bottom:12px;">${v.description}</p>
                <div style="display:flex; gap:16px; flex-wrap:wrap; font-size:13px;">
                    <span>🏢 <strong>Compagnie :</strong> ${v.compagnie}</span>
                    <span>📍 <strong>Trajet :</strong> ${v.villeDepart} → ${v.villeDestination} (${v.paysDestination})</span>
                    <span>⏱️ <strong>Durée :</strong> ${v.dureeFormatee}</span>
                    <span>⭐ <strong>Note :</strong> ${v.noteMoyenne}/5 (${v.nombreAvis} avis)</span>
                    <span>👁️ <strong>Consultations :</strong> ${v.nombreConsultations}</span>
                    <span>💺 <strong>Places disp. :</strong> ${v.placesDisponibles}</span>
                    <span>🏆 <strong>Score Qualité/Prix :</strong> ${v.scoreQualitePrix}/100</span>
                </div>
            </div>
        </div>

        <h3 style="font-size:16px; font-weight:800; margin-bottom:10px;">🎫 Classes de Billets & Tarifs disponibles pour ce voyage</h3>
        <div style="display:grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap:12px; margin-bottom:16px;">
            ${(v.billets || []).map(b => `
                <div style="border:1px solid var(--border); border-radius:12px; padding:14px; background:#f8fafc;">
                    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:8px;">
                        <span class="classe-badge classe-${b.classe}">${b.classe}</span>
                        <div style="display:flex; align-items:center; gap:8px;">
                            <strong style="font-size:19px; color:var(--primary-dark);">${b.prix} TND</strong>
                            <button class="link-btn" style="color:var(--danger);" title="Supprimer ce tarif de billet" onclick="supprimerBilletOffre(${v.id}, ${b.id})">🗑️</button>
                        </div>
                    </div>
                    <div style="font-size:12px; color:var(--text-muted); margin-bottom:8px;">
                        🎒 Cabine : <strong>${b.bagageCabineKg} kg</strong> • 🧳 Soute : <strong>${b.bagageSouteKg} kg</strong> (Max 30kg Module 1)<br/>
                        ${b.modifiable ? '✅ Modifiable' : '❌ Non modifiable'} • ${b.remboursable ? '✅ Remboursable' : '❌ Non remboursable'}<br/>
                        💺 Places restantes : <strong>${b.placesRestantes}</strong>
                    </div>
                    <p style="font-size:12px; color:var(--text-muted); margin-bottom:10px;">${b.conditionsTarifaires || ""}</p>
                    <button class="btn btn-primary btn-sm" style="width:100%;" onclick="reserverBillet(${v.id}, ${b.id}, '${b.codeOffreBillet}', '${b.classe}', ${b.prix})">
                        🛒 Réserver ce billet (${b.prix} TND)
                    </button>
                </div>
            `).join("")}
        </div>

        <!-- Formulaire rapide d'ajout d'une classe de billet (CRUD POST Billet) -->
        <form onsubmit="ajouterBilletDepuisModal(event, ${v.id})" style="background:#f1f5f9; padding:12px 14px; border-radius:10px; margin-bottom:20px; display:flex; gap:10px; align-items:end; flex-wrap:wrap;">
            <div class="search-field" style="flex:1; min-width:130px;">
                <label>➕ Nouvelle classe billet</label>
                <select id="add-billet-classe" required>
                    <option value="ECONOMIQUE">ECONOMIQUE</option>
                    <option value="PREMIUM_ECO">PREMIUM_ECO</option>
                    <option value="BUSINESS">BUSINESS</option>
                    <option value="PREMIERE">PREMIERE</option>
                </select>
            </div>
            <div class="search-field" style="width:110px;">
                <label>Prix (TND)</label>
                <input type="number" id="add-billet-prix" required min="50" value="${v.prixEffectif + 150}" />
            </div>
            <div class="search-field" style="width:120px;">
                <label>Soute (max 30kg)</label>
                <input type="number" id="add-billet-soute" required min="0" max="30" value="25" />
            </div>
            <div class="search-field" style="width:100px;">
                <label>Places</label>
                <input type="number" id="add-billet-places" required min="1" value="10" />
            </div>
            <button type="submit" class="btn btn-secondary btn-sm">➕ Ajouter ce tarif billet</button>
        </form>

        <div style="border-top:1px solid var(--border); padding-top:16px;">
            <h3 style="font-size:15px; font-weight:800; margin-bottom:10px;">✨ Suggestions d'offres similaires & alternatives moins chères</h3>
            <div style="display:grid; grid-template-columns: repeat(auto-fit, minmax(230px, 1fr)); gap:12px;">
                ${[...similaires, ...moinsCheres].slice(0, 3).map(s => `
                    <div style="border:1px solid var(--border); border-radius:10px; padding:12px; display:flex; flex-direction:column; justify-content:space-between;">
                        <div>
                            <span class="badge-tag" style="margin-bottom:4px; display:inline-block;">
                                ${s.prixEffectif < v.prixEffectif ? '💰 Plus économique' : '🎯 Offre similaire'}
                            </span>
                            <h4 style="font-size:13.5px; font-weight:700;">${s.titre}</h4>
                            <div style="font-size:12px; color:var(--text-muted); margin:4px 0;">
                                ${s.compagnie} • ${s.dureeFormatee} • Score ${s.scoreQualitePrix}/100
                            </div>
                        </div>
                        <div style="display:flex; justify-content:space-between; align-items:center; margin-top:8px;">
                            <strong style="color:var(--primary-dark);">${s.prixEffectif} TND</strong>
                            <button class="btn btn-secondary btn-sm" onclick="consulterOffreDetail(${s.id})">Voir</button>
                        </div>
                    </div>
                `).join("")}
            </div>
        </div>
    `;

    document.getElementById("modal-detail").classList.remove("hidden");
}

function fermerModalDetail() {
    document.getElementById("modal-detail").classList.add("hidden");
}

function fermerModalDetailSiClicExterieur(e) {
    if (e.target.id === "modal-detail") fermerModalDetail();
}

// ============================================================================
// 4. COMPARATEUR D'OFFRES CÔTE-À-CÔTE
// ============================================================================

function basculerComparaison(id) {
    const idx = idsComparaison.indexOf(id);
    if (idx >= 0) {
        idsComparaison.splice(idx, 1);
        afficherToast("Offre retirée du comparateur");
    } else {
        if (idsComparaison.length >= 4) {
            afficherToast("Vous pouvez comparer jusqu'à 4 offres simultanément.");
            return;
        }
        idsComparaison.push(id);
        afficherToast("Offre ajoutée au comparateur ⚖️");
    }
    mettreAJourBarreComparaison();
    appliquerFiltres();
    rendreComparateur();
}

function mettreAJourBarreComparaison() {
    const count = idsComparaison.length;
    document.getElementById("badge-comparateur").textContent = count;
    document.getElementById("floating-compare-count").textContent = count;

    const bar = document.getElementById("floating-compare-bar");
    if (count > 0) {
        bar.classList.remove("hidden");
    } else {
        bar.classList.add("hidden");
    }

    const chips = document.getElementById("floating-compare-chips");
    chips.innerHTML = idsComparaison.map(id => {
        const v = catalogueVoyages.find(item => item.id === id);
        if (!v) return "";
        return `<span class="compare-chip">${v.villeDepart} → ${v.villeDestination} (${v.compagnie}) <button onclick="basculerComparaison(${v.id})">✕</button></span>`;
    }).join("");
}

function chargerExempleComparaison() {
    idsComparaison = [1, 2, 4];
    mettreAJourBarreComparaison();
    appliquerFiltres();
    rendreComparateur();
    afficherToast("3 offres chargées dans le comparateur !");
}

function viderComparateur() {
    idsComparaison = [];
    mettreAJourBarreComparaison();
    appliquerFiltres();
    rendreComparateur();
}

function rendreComparateur() {
    const conteneur = document.getElementById("comparateur-container");
    const offres = idsComparaison
        .map(id => catalogueVoyages.find(v => v.id === id))
        .filter(Boolean)
        .map(enrichirVoyage);

    if (offres.length < 2) {
        conteneur.innerHTML = `
            <div class="section-banner" style="justify-content:center; text-align:center; padding:45px;">
                <div>
                    <h3>Sélectionnez au moins 2 offres pour lancer la comparaison côte-à-côte</h3>
                    <p style="margin:8px 0 16px;">Cliquez sur « ⚖️ Comparer cette offre » depuis la recherche ou chargez un exemple immédiat.</p>
                    <button class="btn btn-primary" onclick="chargerExempleComparaison()">
                        ⚡ Comparer un exemple de 3 offres maintenant
                    </button>
                </div>
            </div>`;
        return;
    }

    const moinsChere = [...offres].sort((a, b) => a.prixEffectif - b.prixEffectif)[0];
    const plusChere = [...offres].sort((a, b) => b.prixEffectif - a.prixEffectif)[0];
    const plusRapide = [...offres].sort((a, b) => a.dureeMinutes - b.dureeMinutes)[0];
    const mieuxNotee = [...offres].sort((a, b) => b.noteMoyenne - a.noteMoyenne)[0];
    const meilleurRapport = [...offres].sort((a, b) => b.scoreQualitePrix - a.scoreQualitePrix)[0];
    const economie = plusChere.prixEffectif - moinsChere.prixEffectif;

    conteneur.innerHTML = `
        <div class="synthesis-card">
            <h3 style="font-size:16px; font-weight:800; color:var(--primary-dark);">
                🤖 Synthèse Comparative & Recommandation Intelligente
            </h3>
            <p style="font-size:13.5px; margin-top:4px;">
                Notre algorithme recommande <strong>« ${meilleurRapport.titre} » (${meilleurRapport.compagnie})</strong> avec un score global de <strong>${meilleurRapport.scoreQualitePrix}/100</strong>.
                L'offre la plus économique est <strong>« ${moinsChere.titre} » à ${moinsChere.prixEffectif} TND</strong> (soit <strong>${economie} TND d'économie</strong> par rapport à l'offre la plus élevée).
            </p>
            <div class="synthesis-winners">
                <div class="winner-box">
                    <div class="winner-label">🏆 Meilleur Qualité / Prix</div>
                    <div class="winner-title">${meilleurRapport.titre}</div>
                    <div style="font-size:12px; color:var(--text-muted);">Score : ${meilleurRapport.scoreQualitePrix}/100 • ${meilleurRapport.compagnie}</div>
                </div>
                <div class="winner-box">
                    <div class="winner-label">💰 Tarif le moins cher</div>
                    <div class="winner-title">${moinsChere.titre}</div>
                    <div style="font-size:12px; color:var(--text-muted);">${moinsChere.prixEffectif} TND (${moinsChere.compagnie})</div>
                </div>
                <div class="winner-box">
                    <div class="winner-label">⚡ Trajet le plus rapide</div>
                    <div class="winner-title">${plusRapide.titre}</div>
                    <div style="font-size:12px; color:var(--text-muted);">Durée : ${plusRapide.dureeFormatee}</div>
                </div>
                <div class="winner-box">
                    <div class="winner-label">⭐ Meilleure note clients</div>
                    <div class="winner-title">${mieuxNotee.titre}</div>
                    <div style="font-size:12px; color:var(--text-muted);">Note : ${mieuxNotee.noteMoyenne}/5 (${mieuxNotee.nombreAvis} avis)</div>
                </div>
            </div>
        </div>

        <div class="comparison-table-wrap">
            <table class="comparison-table">
                <thead>
                    <tr>
                        <th>Critères de comparaison</th>
                        ${offres.map(o => `
                            <td>
                                <div style="display:flex; justify-content:space-between; align-items:start;">
                                    <span class="badge-tag">${iconeTransport(o.typeTransport)}</span>
                                    <button class="link-btn" style="color:var(--danger);" onclick="basculerComparaison(${o.id})">Retirer ✕</button>
                                </div>
                                <div style="font-weight:800; font-size:15px; margin:6px 0;">${o.titre}</div>
                                <div style="font-size:12px; color:var(--text-muted);">${o.compagnie} • ${o.codeVoyage}</div>
                            </td>
                        `).join("")}
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <th>💶 Prix effectif TTC</th>
                        ${offres.map(o => `
                            <td class="${o.id === moinsChere.id ? 'best-cell' : ''}">
                                <span style="font-size:18px; font-weight:800;">${o.prixEffectif} TND</span>
                                ${o.enPromotion ? `<span style="font-size:11.5px; text-decoration:line-through; margin-left:6px; color:#64748b;">${o.prixBase} TND</span>` : ""}
                                ${o.id === moinsChere.id ? `<div><span class="badge-tag green">💰 Moins cher</span></div>` : ""}
                            </td>
                        `).join("")}
                    </tr>
                    <tr>
                        <th>🏆 Score Qualité / Prix</th>
                        ${offres.map(o => `
                            <td class="${o.id === meilleurRapport.id ? 'best-cell' : ''}">
                                <strong>${o.scoreQualitePrix} / 100</strong>
                                ${o.id === meilleurRapport.id ? ` <span class="badge-tag green">Recommandé</span>` : ""}
                            </td>
                        `).join("")}
                    </tr>
                    <tr>
                        <th>⏱️ Durée & Escales</th>
                        ${offres.map(o => `
                            <td class="${o.id === plusRapide.id ? 'best-cell' : ''}">
                                <strong>${o.dureeFormatee}</strong> (${o.escales === 0 ? 'Direct' : o.escales + ' escale'})
                                ${o.id === plusRapide.id ? ` <div><span class="badge-tag green">⚡ Plus rapide</span></div>` : ""}
                            </td>
                        `).join("")}
                    </tr>
                    <tr>
                        <th>⭐ Note & Avis voyageurs</th>
                        ${offres.map(o => `
                            <td class="${o.id === mieuxNotee.id ? 'best-cell' : ''}">
                                ⭐ <strong>${o.noteMoyenne} / 5</strong> (${o.nombreAvis} avis)
                            </td>
                        `).join("")}
                    </tr>
                    <tr>
                        <th>🧳 Bagages inclus</th>
                        ${offres.map(o => `
                            <td>
                                ${o.bagageCabineInclus ? '✅ Bagage cabine' : '❌ Pas de cabine'}<br/>
                                ${o.bagageSouteInclus ? '✅ <strong>Bagage en soute inclus</strong>' : '⚠️ Soute en option'}
                            </td>
                        `).join("")}
                    </tr>
                    <tr>
                        <th>🛡️ Flexibilité & Services</th>
                        ${offres.map(o => `
                            <td>
                                ${o.annulationGratuite ? '✅ Annulation gratuite' : '❌ Non annulable sans frais'}<br/>
                                ${o.repasInclus ? '🍽️ Repas inclus' : '—'} • ${o.wifiDisponible ? '📶 Wi-Fi à bord' : 'Pas de Wi-Fi'}
                            </td>
                        `).join("")}
                    </tr>
                    <tr>
                        <th>🎫 Classes de billets</th>
                        ${offres.map(o => `
                            <td>
                                ${(o.billets || []).map(b => `<div style="font-size:12px; margin-bottom:3px;">• <strong>${b.classe}</strong> : ${b.prix} TND (${b.bagageSouteKg}kg)</div>`).join("")}
                            </td>
                        `).join("")}
                    </tr>
                    <tr>
                        <th>Action</th>
                        ${offres.map(o => `
                            <td>
                                <button class="btn btn-primary btn-sm" style="width:100%;" onclick="consulterOffreDetail(${o.id})">
                                    Consulter l'offre
                                </button>
                            </td>
                        `).join("")}
                    </tr>
                </tbody>
            </table>
        </div>
    `;
}

// ============================================================================
// 5. SYSTÈME DE SUGGESTIONS & RECOMMANDATIONS INTELLIGENTES
// ============================================================================

function rafraichirSuggestions() {
    const destFocus = document.getElementById("sugg-destination").value;
    let base = catalogueVoyages.map(enrichirVoyage);

    const tendances = [...base]
        .sort((a, b) => b.nombreConsultations - a.nombreConsultations)
        .slice(0, 4);

    const promos = [...base]
        .filter(v => v.enPromotion)
        .sort((a, b) => b.pourcentageReduction - a.pourcentageReduction)
        .slice(0, 4);

    const meilleurScore = [...base]
        .filter(v => !destFocus || v.villeDestination === destFocus)
        .sort((a, b) => b.scoreQualitePrix - a.scoreQualitePrix)
        .slice(0, 4);

    const petitsBudgets = [...base]
        .filter(v => v.prixEffectif <= 500)
        .sort((a, b) => a.prixEffectif - b.prixEffectif)
        .slice(0, 4);

    const conteneur = document.getElementById("suggestions-container");
    conteneur.innerHTML = `
        ${rendreBlocSuggestion("🏆 Recommandations au Meilleur Rapport Qualité / Prix" + (destFocus ? ` (${destFocus})` : ""), meilleurScore)}
        ${rendreBlocSuggestion("🔥 Offres Tendances (Les plus consultées par les voyageurs)", tendances)}
        ${rendreBlocSuggestion("💰 Meilleures Promotions & Réductions Immédiates", promos)}
        ${rendreBlocSuggestion("🎒 Sélection Petits Budgets (Moins de 500 TND)", petitsBudgets)}
    `;
}

function rendreBlocSuggestion(titre, liste) {
    return `
        <div class="suggestion-block">
            <h3>${titre}</h3>
            <div class="suggestion-cards-grid">
                ${liste.map(v => `
                    <div class="mini-card">
                        <div>
                            <div class="mini-card-img">
                                <img src="${v.imageUrl}" alt="${v.titre}" />
                                <span class="transport-pill">${iconeTransport(v.typeTransport)}</span>
                                ${v.enPromotion && v.pourcentageReduction > 0
                                    ? `<span class="promo-ribbon">-${v.pourcentageReduction}%</span>`
                                    : ""}
                            </div>
                            <div class="mini-card-body">
                                <div class="voyage-badges">
                                    <span class="badge-tag green">Score ${v.scoreQualitePrix}/100</span>
                                    <span class="badge-tag">⭐ ${v.noteMoyenne}</span>
                                </div>
                                <h4 style="font-size:15px; font-weight:800; margin-bottom:4px;">${v.titre}</h4>
                                <div style="font-size:12.5px; color:var(--text-muted);">
                                    ${v.compagnie} • ${v.dureeFormatee} • 👁️ ${v.nombreConsultations} vues
                                </div>
                            </div>
                        </div>
                        <div style="padding:12px 14px; border-top:1px solid var(--border); display:flex; justify-content:space-between; align-items:center; background:#f8fafc;">
                            <div>
                                ${v.enPromotion ? `<span style="font-size:11.5px; text-decoration:line-through; color:#94a3b8;">${v.prixBase} TND</span> ` : ""}
                                <strong style="font-size:17px; color:var(--primary-dark);">${v.prixEffectif} TND</strong>
                            </div>
                            <div style="display:flex; gap:6px;">
                                <button class="btn btn-secondary btn-sm" onclick="basculerComparaison(${v.id})" title="Comparer">⚖️</button>
                                <button class="btn btn-primary btn-sm" onclick="consulterOffreDetail(${v.id})">Consulter</button>
                            </div>
                        </div>
                    </div>
                `).join("")}
            </div>
        </div>
    `;
}

// ============================================================================
// NAVIGATION PAR ONGLETS & OPÉRATIONS CRUD COMPLÈTES (POST, PUT, DELETE)
// ============================================================================

function changerOnglet(nomOnglet) {
    document.querySelectorAll(".nav-tab").forEach(btn => {
        btn.classList.toggle("active", btn.dataset.tab === nomOnglet);
    });
    document.querySelectorAll(".tab-panel").forEach(panel => {
        panel.classList.toggle("active", panel.id === `tab-${nomOnglet}`);
    });
    if (nomOnglet === "comparateur") rendreComparateur();
    if (nomOnglet === "suggestions") rafraichirSuggestions();
    if (nomOnglet === "billets") filtrerBillets();
}

function ouvrirModalNouvelleOffre() {
    document.getElementById("edit-voyage-id").value = "";
    document.getElementById("modal-offre-title").textContent = "➕ Créer une nouvelle offre de voyage";
    document.getElementById("btn-submit-offre").textContent = "Enregistrer l'offre";
    document.getElementById("new-titre").value = "";
    document.getElementById("new-code").value = `VOY-TUN-${Math.floor(100 + Math.random() * 899)}`;
    document.getElementById("new-depart").value = "Tunis";
    document.getElementById("new-destination").value = "";
    document.getElementById("new-pays").value = "";
    document.getElementById("new-compagnie").value = "";
    document.getElementById("new-transport").value = "VOL";
    document.getElementById("new-categorie").value = "CONFORT";
    document.getElementById("new-prix-base").value = 520;
    document.getElementById("new-prix-promo").value = 390;
    document.getElementById("new-duree").value = 140;
    document.getElementById("new-places").value = 25;
    document.getElementById("new-description").value = "";
    document.getElementById("new-soute").checked = true;
    document.getElementById("new-annulation").checked = true;
    document.getElementById("new-repas").checked = true;
    document.getElementById("new-wifi").checked = false;
    document.getElementById("modal-nouvelle-offre").classList.remove("hidden");
}

function ouvrirModalModifierOffre(id) {
    const v = catalogueVoyages.find(item => item.id === id);
    if (!v) return;

    document.getElementById("edit-voyage-id").value = String(v.id);
    document.getElementById("modal-offre-title").textContent = `✏️ Modifier l'offre #${v.id} (${v.codeVoyage})`;
    document.getElementById("btn-submit-offre").textContent = "Mettre à jour l'offre";
    document.getElementById("new-titre").value = v.titre || "";
    document.getElementById("new-code").value = v.codeVoyage || "";
    document.getElementById("new-depart").value = v.villeDepart || "";
    document.getElementById("new-destination").value = v.villeDestination || "";
    document.getElementById("new-pays").value = v.paysDestination || "";
    document.getElementById("new-compagnie").value = v.compagnie || "";
    document.getElementById("new-transport").value = v.typeTransport || "VOL";
    document.getElementById("new-categorie").value = v.categorie || "CONFORT";
    document.getElementById("new-prix-base").value = v.prixBase || 500;
    document.getElementById("new-prix-promo").value = v.enPromotion && v.prixPromo ? v.prixPromo : "";
    document.getElementById("new-duree").value = v.dureeMinutes || 120;
    document.getElementById("new-places").value = v.placesDisponibles ?? 20;
    document.getElementById("new-description").value = v.description || "";
    document.getElementById("new-soute").checked = Boolean(v.bagageSouteInclus);
    document.getElementById("new-annulation").checked = Boolean(v.annulationGratuite);
    document.getElementById("new-repas").checked = Boolean(v.repasInclus);
    document.getElementById("new-wifi").checked = Boolean(v.wifiDisponible);
    document.getElementById("modal-nouvelle-offre").classList.remove("hidden");
}

function fermerModalNouvelleOffre() {
    document.getElementById("modal-nouvelle-offre").classList.add("hidden");
}

function fermerModalNouvelleOffreSiExterieur(e) {
    if (e.target.id === "modal-nouvelle-offre") fermerModalNouvelleOffre();
}

async function soumettreNouvelleOffre(e) {
    e.preventDefault();
    const editIdStr = document.getElementById("edit-voyage-id").value;
    const estModification = editIdStr !== "";
    const editId = estModification ? Number(editIdStr) : null;

    const prixBase = Number(document.getElementById("new-prix-base").value);
    const prixPromoVal = Number(document.getElementById("new-prix-promo").value);
    const enPromotion = prixPromoVal > 0 && prixPromoVal < prixBase;

    const existant = estModification ? catalogueVoyages.find(v => v.id === editId) : null;

    const payloadOffre = {
        id: estModification ? editId : (catalogueVoyages.reduce((m, x) => Math.max(m, x.id), 0) + 1),
        codeVoyage: document.getElementById("new-code").value.trim(),
        titre: document.getElementById("new-titre").value.trim(),
        description: document.getElementById("new-description").value.trim() || "Offre disponible au catalogue.",
        villeDepart: document.getElementById("new-depart").value.trim(),
        villeDestination: document.getElementById("new-destination").value.trim(),
        paysDestination: document.getElementById("new-pays").value.trim(),
        compagnie: document.getElementById("new-compagnie").value.trim(),
        typeTransport: document.getElementById("new-transport").value,
        categorie: document.getElementById("new-categorie").value,
        dateDepart: existant ? existant.dateDepart : "2026-11-28T09:00:00",
        dateArrivee: existant ? existant.dateArrivee : "2026-11-28T11:20:00",
        dureeMinutes: Number(document.getElementById("new-duree").value),
        escales: existant ? existant.escales : 0,
        prixBase,
        prixPromo: enPromotion ? prixPromoVal : null,
        enPromotion,
        placesDisponibles: Number(document.getElementById("new-places").value),
        noteMoyenne: existant ? existant.noteMoyenne : 4.7,
        nombreAvis: existant ? existant.nombreAvis : 25,
        nombreConsultations: existant ? existant.nombreConsultations : 40,
        bagageCabineInclus: true,
        bagageSouteInclus: document.getElementById("new-soute").checked,
        annulationGratuite: document.getElementById("new-annulation").checked,
        repasInclus: document.getElementById("new-repas").checked,
        wifiDisponible: document.getElementById("new-wifi").checked,
        imageUrl: existant ? existant.imageUrl : "https://images.unsplash.com/photo-1539037116277-4db20889f2d4?auto=format&fit=crop&w=900&q=80",
        statut: "DISPONIBLE",
        billets: existant && existant.billets && existant.billets.length > 0 ? existant.billets : [
            {
                id: Date.now(),
                codeOffreBillet: `BIL-${document.getElementById("new-code").value.trim()}-ECO`,
                classe: "ECONOMIQUE",
                prix: enPromotion ? prixPromoVal : prixBase,
                placesRestantes: Number(document.getElementById("new-places").value),
                bagageCabineKg: 8,
                bagageSouteKg: document.getElementById("new-soute").checked ? 23 : 0,
                modifiable: true,
                remboursable: document.getElementById("new-annulation").checked,
                choixSiegeGratuit: true,
                accesSalonVip: false,
                conditionsTarifaires: "Tarif standard (conforme limite 30kg Module 1)."
            }
        ]
    };

    if (estModification) {
        // CRUD UPDATE (PUT)
        if (backendConnecte) {
            try {
                const res = await fetch(`${API_BASE_URL}/voyages/${editId}`, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payloadOffre)
                });
                if (res.ok) {
                    const updated = await res.json();
                    const idx = catalogueVoyages.findIndex(v => v.id === editId);
                    if (idx >= 0) catalogueVoyages[idx] = updated;
                }
            } catch (_) {}
        } else if (xamppMysqlConnecte) {
            try {
                const res = await fetch(`${XAMPP_MYSQL_API_URL}?action=voyages&id=${editId}`, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payloadOffre)
                });
                if (res.ok) {
                    const updated = await res.json();
                    const idx = catalogueVoyages.findIndex(v => v.id === editId);
                    if (idx >= 0) catalogueVoyages[idx] = updated;
                }
            } catch (_) {}
        } else {
            const idx = catalogueVoyages.findIndex(v => v.id === editId);
            if (idx >= 0) catalogueVoyages[idx] = payloadOffre;
        }
        afficherToast(`Offre #${editId} mise à jour avec succès dans MySQL !`);
    } else {
        // CRUD CREATE (POST)
        if (backendConnecte) {
            try {
                const res = await fetch(`${API_BASE_URL}/voyages`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payloadOffre)
                });
                if (res.ok) {
                    const created = await res.json();
                    catalogueVoyages.unshift(created);
                } else {
                    catalogueVoyages.unshift(payloadOffre);
                }
            } catch (_) {
                catalogueVoyages.unshift(payloadOffre);
            }
        } else if (xamppMysqlConnecte) {
            try {
                const res = await fetch(`${XAMPP_MYSQL_API_URL}?action=voyages`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payloadOffre)
                });
                if (res.ok) {
                    const created = await res.json();
                    catalogueVoyages.unshift(created);
                } else {
                    catalogueVoyages.unshift(payloadOffre);
                }
            } catch (_) {
                catalogueVoyages.unshift(payloadOffre);
            }
        } else {
            catalogueVoyages.unshift(payloadOffre);
        }
        afficherToast("Nouvelle offre créée et enregistrée dans MySQL !");
    }

    peuplerListesFiltres();
    mettreAJourStatistiques();
    appliquerFiltres();
    filtrerBillets();
    rendreComparateur();
    rafraichirSuggestions();
    fermerModalNouvelleOffre();
}

async function supprimerOffreVoyage(id) {
    const v = catalogueVoyages.find(item => item.id === id);
    if (!v) return;

    if (backendConnecte) {
        try {
            await fetch(`${API_BASE_URL}/voyages/${id}`, { method: "DELETE" });
        } catch (_) {}
    } else if (xamppMysqlConnecte) {
        try {
            await fetch(`${XAMPP_MYSQL_API_URL}?action=voyages&id=${id}`, { method: "DELETE" });
        } catch (_) {}
    }

    catalogueVoyages = catalogueVoyages.filter(item => item.id !== id);
    idsComparaison = idsComparaison.filter(cid => cid !== id);

    peuplerListesFiltres();
    mettreAJourStatistiques();
    mettreAJourBarreComparaison();
    appliquerFiltres();
    filtrerBillets();
    rendreComparateur();
    rafraichirSuggestions();
    afficherToast(`Offre « ${v.titre} » supprimée de MySQL !`);
}

async function reserverBillet(voyageId, billetId, codeBillet, classe, prix) {
    if (backendConnecte) {
        try {
            await fetch(`${API_BASE_URL}/voyages/${voyageId}/places?placesReservees=1`, { method: "PATCH" });
        } catch (_) {}
    } else if (xamppMysqlConnecte) {
        try {
            const res = await fetch(`${XAMPP_MYSQL_API_URL}?action=reserver`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ voyageId, billetId })
            });
            if (res.ok) {
                const updatedVoyage = await res.json();
                const idx = catalogueVoyages.findIndex(v => v.id === voyageId);
                if (idx >= 0 && updatedVoyage && updatedVoyage.id) {
                    catalogueVoyages[idx] = updatedVoyage;
                }
            }
        } catch (_) {}
    } else {
        const v = catalogueVoyages.find(item => item.id === voyageId);
        if (v) {
            v.placesDisponibles = Math.max(0, (v.placesDisponibles || 1) - 1);
            const b = (v.billets || []).find(x => x.id === billetId);
            if (b) b.placesRestantes = Math.max(0, (b.placesRestantes || 1) - 1);
        }
    }

    mettreAJourStatistiques();
    appliquerFiltres();
    filtrerBillets();
    if (!document.getElementById("modal-detail").classList.contains("hidden")) {
        consulterOffreDetail(voyageId);
    }
    afficherToast(`✅ Billet ${codeBillet} (${classe} - ${prix} TND) réservé ! Places mises à jour dans MySQL.`);
}

async function ajouterBilletDepuisModal(e, voyageId) {
    e.preventDefault();
    const classe = document.getElementById("add-billet-classe").value;
    const prix = Number(document.getElementById("add-billet-prix").value);
    const souteKg = Math.min(30, Math.max(0, Number(document.getElementById("add-billet-soute").value)));
    const places = Number(document.getElementById("add-billet-places").value);

    const nouveauBillet = {
        id: Date.now(),
        codeOffreBillet: `BIL-${voyageId}-${classe.substring(0, 3)}-${Math.floor(10 + Math.random() * 89)}`,
        classe,
        prix,
        placesRestantes: places,
        bagageCabineKg: 8,
        bagageSouteKg: souteKg,
        modifiable: true,
        remboursable: classe === "BUSINESS" || classe === "PREMIERE",
        choixSiegeGratuit: true,
        accesSalonVip: classe === "BUSINESS" || classe === "PREMIERE",
        conditionsTarifaires: `Tarif ${classe} ajouté (Soute ${souteKg} kg <= 30 kg max Module 1).`
    };

    if (backendConnecte) {
        try {
            await fetch(`${API_BASE_URL}/voyages/${voyageId}/billets`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(nouveauBillet)
            });
        } catch (_) {}
    } else if (xamppMysqlConnecte) {
        try {
            await fetch(`${XAMPP_MYSQL_API_URL}?action=billets&voyageId=${voyageId}`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(nouveauBillet)
            });
        } catch (_) {}
    }

    const v = catalogueVoyages.find(item => item.id === voyageId);
    if (v) {
        v.billets = v.billets || [];
        v.billets.push(nouveauBillet);
    }

    mettreAJourStatistiques();
    filtrerBillets();
    consulterOffreDetail(voyageId);
    afficherToast(`Nouveau tarif billet ${classe} (${prix} TND) ajouté à l'offre !`);
}

async function supprimerBilletOffre(voyageId, billetId) {
    if (backendConnecte) {
        try {
            await fetch(`${API_BASE_URL}/billets/${billetId}`, { method: "DELETE" });
        } catch (_) {}
    } else if (xamppMysqlConnecte) {
        try {
            await fetch(`${XAMPP_MYSQL_API_URL}?action=billets&id=${billetId}`, { method: "DELETE" });
        } catch (_) {}
    }

    const v = catalogueVoyages.find(item => item.id === voyageId);
    if (v && v.billets) {
        v.billets = v.billets.filter(b => b.id !== billetId);
    }

    mettreAJourStatistiques();
    filtrerBillets();
    consulterOffreDetail(voyageId);
    afficherToast("Tarif de billet supprimé de MySQL !");
}

function afficherToast(message) {
    const t = document.getElementById("toast");
    t.textContent = message;
    t.classList.remove("hidden");
    clearTimeout(window._toastTimer);
    window._toastTimer = setTimeout(() => t.classList.add("hidden"), 3200);
}

document.addEventListener("DOMContentLoaded", initialiserApplication);

