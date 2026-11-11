# Lance automatiquement tous les tests du projet SkyVoyage.
# Usage : powershell -ExecutionPolicy Bypass -File tester_tout.ps1 [-SansDocker] [-GarderStack]
param([switch]$SansDocker, [switch]$GarderStack)

$ErrorActionPreference = "Continue"
$root = $PSScriptRoot
$results = [ordered]@{}
$env:MAVEN_OPTS = "-Djavax.net.ssl.trustStoreType=WINDOWS-ROOT -Djavax.net.ssl.trustStore=NUL"

function Record($name, $ok, $detail = "") { $results[$name] = @{ ok = $ok; detail = $detail }
    $c = if ($ok) { "Green" } else { "Red" }; Write-Host ("[{0}] {1} {2}" -f $(if ($ok) { "OK  " } else { "FAIL" }), $name, $detail) -ForegroundColor $c }

# ---------- 1. Tests Java (Maven) ----------
$java = @("discovery", "config-server", "gateway", "module1-Gestion des bagages", "module2-recherche-consultation", "module4-reservation-paiement")
foreach ($m in $java) {
    Push-Location (Join-Path $root "Backend\$m")
    mvn -q -B test *> "$env:TEMP\mvn-test.log"
    Record "Maven  $m" ($LASTEXITCODE -eq 0)
    Pop-Location
}

# ---------- 2. Tests Python (module 3) ----------
$m3 = Join-Path $root "Backend\module3-gestion-reservation"
$venv = Join-Path $env:TEMP "skyvoyage-venv"
if (-not (Test-Path "$venv\Scripts\python.exe")) { python -m venv $venv }
& "$venv\Scripts\python.exe" -m pip install -q -r "$m3\requirements.txt" -r "$m3\requirements-dev.txt" *> $null
Push-Location $m3
& "$venv\Scripts\python.exe" -m pytest -q tests *> "$env:TEMP\pytest.log"
Record "Pytest module3-gestion-reservation" ($LASTEXITCODE -eq 0)
Pop-Location

# ---------- 3. Front Angular ----------
Push-Location (Join-Path $root "Frontend")
$nodeVer = [version](node -v).TrimStart("v")
if ($nodeVer -lt [version]"22.22.3") {
    Record "Front Angular (build)" $false "Node $nodeVer trop ancien : installer Node >= 22.22.3"
} else {
    if (-not (Test-Path node_modules)) { npm ci --no-audit --no-fund *> $null }
    npx ng build *> "$env:TEMP\ng-build.log"; Record "Front Angular (build)" ($LASTEXITCODE -eq 0)
    npx ng test --watch=false *> "$env:TEMP\ng-test.log"; Record "Front Angular (tests)" ($LASTEXITCODE -eq 0)
}
Pop-Location

# ---------- 4. Stack Docker + tests via le Gateway ----------
function DockerReady { $j = Start-Job { docker info --format '{{.ServerVersion}}' 2>&1 }
    $ok = (Wait-Job $j -Timeout 20) -and ((Receive-Job $j) -match '^\d'); Remove-Job $j -Force; return $ok }

if ($SansDocker) { Write-Host "Docker ignore (-SansDocker)" }
elseif (-not (DockerReady)) { Record "Docker (moteur)" $false "ne repond pas : demarrer Docker Desktop" }
else {
    Push-Location $root
    docker compose up --build -d *> "$env:TEMP\compose-up.log"
    Record "docker compose up --build" ($LASTEXITCODE -eq 0)
    Write-Host "Attente du demarrage des services (max 5 min)..."
    $apps = "GATEWAY", "RECHERCHE-CONSULTATION", "GESTION-BAGAGES", "RESERVATION-PAIEMENT", "RESERVATION-SERVICE"
    $deadline = (Get-Date).AddMinutes(5); $registered = @()
    while ((Get-Date) -lt $deadline) {
        try { $xml = (Invoke-WebRequest "http://localhost:8761/eureka/apps" -UseBasicParsing -TimeoutSec 5).Content
              $registered = $apps | Where-Object { $xml -match "<name>$_</name>" } } catch {}
        if ($registered.Count -eq $apps.Count) { break }; Start-Sleep 10
    }
    Record "Eureka : services enregistres" ($registered.Count -eq $apps.Count) ("{0}/{1}: {2}" -f $registered.Count, $apps.Count, ($registered -join ","))

    function Http($name, $method, $url, $body = $null, $expect = @(200, 201)) {
        try { $p = @{ Uri = $url; Method = $method; UseBasicParsing = $true; TimeoutSec = 20 }
              if ($body) { $p.Body = ($body | ConvertTo-Json -Depth 5); $p.ContentType = "application/json" }
              $r = Invoke-WebRequest @p; $code = [int]$r.StatusCode; $content = $r.Content }
        catch { $code = if ($_.Exception.Response) { [int]$_.Exception.Response.StatusCode } else { 0 }; $content = "" }
        Record $name ($expect -contains $code) "HTTP $code"; return $content }

    $gw = "http://localhost:8080"
    Http "Config Server : recherche-consultation" GET "http://localhost:8888/recherche-consultation/default" | Out-Null
    Http "Gateway -> recherche (voyages)" GET "$gw/api/recherche/voyages" | Out-Null
    Http "Gateway -> paiements (liste)" GET "$gw/api/paiements" | Out-Null
    Http "Gateway -> bagages sans token (401 attendu)" GET "$gw/api/bagages" -expect @(401) | Out-Null
    Http "Gateway -> reservation : disponibilite voyage 1" GET "$gw/api/v1/disponibilites/voyage/1" | Out-Null
    $res = Http "Gateway -> reservation : creation" POST "$gw/api/v1/reservations" @{ id_user = 1; id_voyage = 1; nombre_voyageurs = 1; prix_total = 120
        voyageurs = @(@{ nom = "Test"; prenom = "Auto"; date_naissance = "1990-01-01"; numero_piece_identite = "X1" }) }
    if ($res) { $id = ($res | ConvertFrom-Json).id_reservation
        Http "Gateway -> reservation : lecture" GET "$gw/api/v1/reservations/$id" | Out-Null
        Http "Gateway -> reservation : confirmation" PUT "$gw/api/v1/reservations/$id/confirmer" @{ id_paiement = 1 } | Out-Null }
    Http "Module 4 : paiement bagage (Feign du module 1)" POST "http://localhost:8084/api/paiements/bagages" @{ bagageId = "B1"; reservationId = "1"; montant = 30; devise = "TND" } | Out-Null
    Http "Module 5 support (direct)" GET "http://localhost:8000/api/support-requests" -expect @(200, 401, 403) | Out-Null

    if (-not $GarderStack) { docker compose down *> $null }
    Pop-Location
}

# ---------- Resume ----------
$fail = @($results.GetEnumerator() | Where-Object { -not $_.Value.ok })
Write-Host ("`n=== {0} tests OK, {1} en echec ===" -f ($results.Count - $fail.Count), $fail.Count) -ForegroundColor $(if ($fail.Count) { "Red" } else { "Green" })
exit $fail.Count
