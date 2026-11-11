/**
 * SkyVoyage Enterprise Complete Audit Script
 * Comprehensive verification of:
 * 1. All Routes (Direct + Through Gateway + Frontend)
 * 2. All Swagger UI & OpenAPI Docs
 * 3. Security (Keycloak IAM, JWT, RBAC, 401/403/200 protections)
 * 4. Docker (Containers, Ports, Networks, Health)
 */

const http = require('http');
const https = require('https');
const { execSync } = require('child_process');

const results = {
  routes: [],
  swagger: [],
  security: [],
  docker: []
};

function logResult(category, testName, passed, info = '') {
  results[category].push({ testName, passed, info });
  const badge = passed ? '✅ PASS' : '❌ FAIL';
  console.log(`[${badge}] [${category.toUpperCase()}] ${testName} -> ${info}`);
}

function req(options, postData = null) {
  return new Promise((resolve) => {
    const start = Date.now();
    try {
      const u = typeof options === 'string' ? new URL(options) : null;
      const opts = u ? {
        hostname: u.hostname,
        port: u.port || (u.protocol === 'https:' ? 443 : 80),
        path: u.pathname + u.search,
        method: 'GET',
        headers: {},
        timeout: 15000
      } : Object.assign({ timeout: 15000 }, options);

      const client = (opts.protocol === 'https:' ? https : http);
      const r = client.request(opts, (res) => {
        let body = '';
        res.on('data', chunk => body += chunk);
        res.on('end', () => {
          const duration = Date.now() - start;
          resolve({ status: res.statusCode, body, headers: res.headers, duration });
        });
      });
      r.on('error', (e) => resolve({ status: 0, error: e.message, duration: Date.now() - start }));
      r.on('timeout', () => { r.destroy(); resolve({ status: 408, error: 'Timeout', duration: Date.now() - start }); });
      if (postData) {
        r.write(typeof postData === 'string' ? postData : JSON.stringify(postData));
      }
      r.end();
    } catch (err) {
      resolve({ status: 0, error: err.message, duration: Date.now() - start });
    }
  });
}

async function runAudit() {
  console.log('================================================================================');
  console.log('  🔍 AUDIT DE CONFORMITÉ SKYVOYAGE : ROUTES, SWAGGER, SÉCURITÉ & DOCKER 🔍     ');
  console.log('================================================================================\n');

  // ============================================================================
  // PILIER 1 : AUDIT DOCKER (CONTENEURS, RÉSEAUX, SANTÉ)
  // ============================================================================
  console.log('--- [1/4] AUDIT DE L\'INFRASTRUCTURE DOCKER ---');
  try {
    const psOut = execSync('docker ps --format "{{.Names}}##{{.Status}}##{{.Ports}}"', { encoding: 'utf8' });
    const containers = psOut.trim().split('\n').filter(Boolean);
    logResult('docker', 'Nombre total de conteneurs actifs', containers.length >= 12, `${containers.length} conteneurs en cours d'exécution`);

    const requiredContainers = [
      'skyvoyage-eureka',
      'skyvoyage-gateway',
      'skyvoyage-bagages',
      'skyvoyage-paiement',
      'skyvoyage-reservation',
      'module2-recherche-consultation',
      'module2-keycloak',
      'skyvoyage-redis',
      'module2-mysql',
      'module2-rabbitmq',
      'module5-support-client-app-1',
      'module5-support-client-database-1'
    ];

    for (const name of requiredContainers) {
      const found = containers.find(c => c.startsWith(name));
      if (found) {
        const parts = found.split('##');
        logResult('docker', `Conteneur: ${name}`, true, `${parts[1]}`);
      } else {
        logResult('docker', `Conteneur: ${name}`, false, 'Non trouvé');
      }
    }
  } catch (e) {
    logResult('docker', 'Inspection Docker', false, e.message);
  }

  // ============================================================================
  // PILIER 2 : AUDIT DES DOCUMENTATIONS SWAGGER & OPENAPI
  // ============================================================================
  console.log('\n--- [2/4] AUDIT DE LA DOCUMENTATION SWAGGER & OPENAPI ---');
  const swaggerTests = [
    { name: 'Gateway Swagger UI', url: 'http://localhost:8080/swagger-ui/index.html' },
    { name: 'Module 1 (Bagages) Swagger UI', url: 'http://localhost:8081/swagger-ui/index.html' },
    { name: 'Module 1 (Bagages) OpenAPI JSON', url: 'http://localhost:8081/v3/api-docs' },
    { name: 'Module 2 (Recherche) Swagger UI', url: 'http://localhost:8082/swagger-ui/index.html' },
    { name: 'Module 2 (Recherche) OpenAPI JSON', url: 'http://localhost:8082/v3/api-docs' },
    { name: 'Module 3 (Réservation FastAPI) Docs', url: 'http://localhost:8003/docs' },
    { name: 'Module 3 (Réservation FastAPI) OpenAPI JSON', url: 'http://localhost:8003/openapi.json' },
    { name: 'Module 4 (Paiement) Actuator Root', url: 'http://localhost:8084/actuator' },
    { name: 'Discovery Eureka REST API', url: 'http://localhost:8761/eureka/apps' }
  ];

  for (const t of swaggerTests) {
    const res = await req(t.url);
    logResult('swagger', t.name, res.status === 200, `HTTP ${res.status} (${res.duration}ms)`);
  }

  // ============================================================================
  // PILIER 3 : AUDIT DE SÉCURITÉ (KEYCLOAK IAM & RBAC)
  // ============================================================================
  console.log('\n--- [3/4] AUDIT DE SÉCURITÉ IAM & RBAC ---');
  // 1. OIDC Discovery
  const oidcRes = await req('http://localhost:8180/realms/skyvoyage/.well-known/openid-configuration');
  logResult('security', 'Keycloak OIDC Discovery Endpoint', oidcRes.status === 200, `HTTP ${oidcRes.status}`);

  // 2. Direct Access Grants - Voyageur
  const voyParams = new URLSearchParams({
    grant_type: 'password',
    client_id: 'skyvoyage-client',
    username: 'voyageur',
    password: 'user'
  });
  const voyTokenRes = await req({
    hostname: 'localhost',
    port: 8180,
    path: '/realms/skyvoyage/protocol/openid-connect/token',
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
      'Content-Length': Buffer.byteLength(voyParams.toString())
    }
  }, voyParams.toString());

  let tokenVoyageur = null;
  if (voyTokenRes.status === 200) {
    try {
      tokenVoyageur = JSON.parse(voyTokenRes.body).access_token;
      logResult('security', 'Direct Access Grant Voyageur', !!tokenVoyageur, 'Token JWT valide délivré');
    } catch {
      logResult('security', 'Direct Access Grant Voyageur', false, 'Parse error');
    }
  } else {
    logResult('security', 'Direct Access Grant Voyageur', false, `HTTP ${voyTokenRes.status}`);
  }

  // 3. Direct Access Grants - Admin
  const admParams = new URLSearchParams({
    grant_type: 'password',
    client_id: 'skyvoyage-client',
    username: 'admin',
    password: 'admin'
  });
  const admTokenRes = await req({
    hostname: 'localhost',
    port: 8180,
    path: '/realms/skyvoyage/protocol/openid-connect/token',
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
      'Content-Length': Buffer.byteLength(admParams.toString())
    }
  }, admParams.toString());

  let tokenAdmin = null;
  if (admTokenRes.status === 200) {
    try {
      tokenAdmin = JSON.parse(admTokenRes.body).access_token;
      logResult('security', 'Direct Access Grant Administrateur', !!tokenAdmin, 'Token JWT valide délivré');
    } catch {
      logResult('security', 'Direct Access Grant Administrateur', false, 'Parse error');
    }
  } else {
    logResult('security', 'Direct Access Grant Administrateur', false, `HTTP ${admTokenRes.status}`);
  }

  // 4. Protection Anonyme sur Module 1 (401 Unauthorized)
  const anonBagage = await req('http://localhost:8081/api/bagages');
  logResult('security', 'RBAC: Rejet Anonyme sur /api/bagages', anonBagage.status === 401, `HTTP ${anonBagage.status} (Attendu 401)`);

  // 5. Accès Voyageur sur Module 1 (200 OK)
  if (tokenVoyageur) {
    const voyBagage = await req({
      hostname: 'localhost',
      port: 8081,
      path: '/api/bagages',
      method: 'GET',
      headers: { 'Authorization': `Bearer ${tokenVoyageur}` }
    });
    logResult('security', 'RBAC: Accès Voyageur autorisé sur /api/bagages', voyBagage.status === 200, `HTTP ${voyBagage.status}`);

    // 6. Restriction Statistiques pour Voyageur (403 Forbidden)
    const voyStats = await req({
      hostname: 'localhost',
      port: 8081,
      path: '/api/bagages/statistiques',
      method: 'GET',
      headers: { 'Authorization': `Bearer ${tokenVoyageur}` }
    });
    logResult('security', 'RBAC: Statistiques interdites pour Voyageur', voyStats.status === 403, `HTTP ${voyStats.status} (Attendu 403)`);

    // 7. Restriction Suppression Voyage pour Voyageur (403 Forbidden sur Module 2)
    const voyDelete = await req({
      hostname: 'localhost',
      port: 8082,
      path: '/api/recherche/voyages/99999',
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${tokenVoyageur}` }
    });
    logResult('security', 'RBAC: Suppression Voyage interdite pour Voyageur', voyDelete.status === 403, `HTTP ${voyDelete.status} (Attendu 403)`);
  }

  // 8. Accès Admin aux Statistiques (200 OK)
  if (tokenAdmin) {
    const admStats = await req({
      hostname: 'localhost',
      port: 8081,
      path: '/api/bagages/statistiques',
      method: 'GET',
      headers: { 'Authorization': `Bearer ${tokenAdmin}` }
    });
    logResult('security', 'RBAC: Statistiques autorisées pour Admin', admStats.status === 200, `HTTP ${admStats.status}`);
  }

  // 9. Requête Prévol CORS (OPTIONS)
  const corsPreflight = await req({
    hostname: 'localhost',
    port: 8082,
    path: '/api/recherche/voyages',
    method: 'OPTIONS',
    headers: {
      'Origin': 'http://localhost:4200',
      'Access-Control-Request-Method': 'GET'
    }
  });
  logResult('security', 'CORS: Traitement requête prévol (OPTIONS)', corsPreflight.status === 200 || corsPreflight.status === 204, `HTTP ${corsPreflight.status}`);

  // ============================================================================
  // PILIER 4 : AUDIT DE TOUTES LES ROUTES (SERVICES, GATEWAY, FRONTEND)
  // ============================================================================
  console.log('\n--- [4/4] AUDIT DE TOUTES LES ROUTES DE L\'ÉCOSYSTÈME ---');

  const routesTests = [
    // Module 2 (Recherche)
    { name: 'Module 2: Catalogue complet (GET /api/recherche/voyages)', url: 'http://localhost:8082/api/recherche/voyages' },
    { name: 'Module 2: Détail voyage (GET /api/recherche/voyages/6)', url: 'http://localhost:8082/api/recherche/voyages/6' },
    { name: 'Module 2: Suggestions intelligentes (GET /api/recherche/suggestions)', url: 'http://localhost:8082/api/recherche/suggestions' },
    { name: 'Module 2: Statistiques catalogue (GET /api/recherche/statistiques)', url: 'http://localhost:8082/api/recherche/statistiques' },
    { name: 'Module 2: Comparateur d\'offres (GET /api/recherche/comparer?ids=1,2)', url: 'http://localhost:8082/api/recherche/comparer?ids=1,2' },
    
    // Module 3 (FastAPI Réservation)
    { name: 'Module 3: Healthcheck HTTP (GET /health)', url: 'http://localhost:8003/health' },
    { name: 'Module 3: Healthcheck Base MySQL (GET /health/db)', url: 'http://localhost:8003/health/db' },

    // Module 4 (Paiement)
    { name: 'Module 4: Health Actuator (GET /actuator/health)', url: 'http://localhost:8084/actuator/health' },
    { name: 'Module 4: Actuator Discovery (GET /actuator)', url: 'http://localhost:8084/actuator' },

    // Module 5 (Support Client)
    { name: 'Module 5: API REST Tickets (GET /api/support-requests)', url: 'http://localhost:8000/api/support-requests' },
    { name: 'Module 5: API Statistiques Tickets (GET /api/support-requests/stats)', url: 'http://localhost:8000/api/support-requests/stats' },
    { name: 'Module 5: Interface Twig (GET /home)', url: 'http://localhost:8000/home' },

    // Socle Technique
    { name: 'Config Server: Santé (GET /actuator/health)', url: 'http://localhost:8888/actuator/health' },
    { name: 'Eureka: Registre des instances (GET /eureka/apps)', url: 'http://localhost:8761/eureka/apps' },
    { name: 'Gateway: Santé Actuator (GET /actuator/health)', url: 'http://localhost:8080/actuator/health' },

    // Frontend Angular
    { name: 'Frontend: Page d\'accueil (GET /)', url: 'http://localhost:4200/' },
    { name: 'Frontend: Page Recherche Voyages (GET /voyages)', url: 'http://localhost:4200/voyages' },
    { name: 'Frontend: Page Gestion Bagages (GET /bagages)', url: 'http://localhost:4200/bagages' },
    { name: 'Frontend: Page Paiements (GET /paiements)', url: 'http://localhost:4200/paiements' },
    { name: 'Frontend: Portail Connexion Pro (GET /login)', url: 'http://localhost:4200/login' }
  ];

  for (const r of routesTests) {
    const res = await req(r.url);
    logResult('routes', r.name, res.status === 200 || res.status === 302, `HTTP ${res.status} (${res.duration}ms)`);
  }

  // Résumé Global
  const allTests = [...results.docker, ...results.swagger, ...results.security, ...results.routes];
  const passed = allTests.filter(t => t.passed).length;
  const total = allTests.length;
  const percentage = Math.round((passed / total) * 100);

  console.log('\n================================================================================');
  console.log(`📊 BILAN COMPLET DE L'AUDIT : ${passed} / ${total} VÉRIFICATIONS VALIDÉES (${percentage}%)`);
  console.log('================================================================================');
  console.log(`  🐳 Docker Infra      : ${results.docker.filter(r=>r.passed).length}/${results.docker.length}`);
  console.log(`  📖 Swagger & OpenAPI : ${results.swagger.filter(r=>r.passed).length}/${results.swagger.length}`);
  console.log(`  🛡️ Sécurité & RBAC   : ${results.security.filter(r=>r.passed).length}/${results.security.length}`);
  console.log(`  🌐 Routes Système    : ${results.routes.filter(r=>r.passed).length}/${results.routes.length}`);
  console.log('================================================================================\n');
}

runAudit();
