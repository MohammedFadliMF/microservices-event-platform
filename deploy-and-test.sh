#!/bin/bash
# Script de déploiement et test des corrections de sécurité
# Usage: bash deploy-and-test.sh

set -e

# Couleurs
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}  🚀 DÉPLOIEMENT ET TEST DES CORRECTIONS KEYCLOAK${NC}"
echo -e "${BLUE}════════════════════════════════════════════════════════════${NC}"
echo ""

# 1. VÉRIFIER LES PRÉREQUIS
echo -e "${YELLOW}1️⃣  Vérification des prérequis...${NC}"
echo "---"

# Vérifier Docker
if ! command -v docker &> /dev/null; then
    echo -e "${RED}❌ Docker n'est pas installé${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Docker installé${NC}"

# Vérifier docker-compose
if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}❌ docker-compose n'est pas installé${NC}"
    exit 1
fi
echo -e "${GREEN}✅ docker-compose installé${NC}"

# Vérifier Keycloak
echo "Vérification de Keycloak (timeout 5s)..."
if timeout 5 curl -s http://localhost:8080/realms/event-platform-realm/.well-known/openid-configuration &> /dev/null; then
    echo -e "${GREEN}✅ Keycloak accessible${NC}"
else
    echo -e "${YELLOW}⚠️  Keycloak non accessible sur localhost:8080${NC}"
    echo "   Assurez-vous que Keycloak est en cours d'exécution"
fi

# Vérifier le répertoire courant
if [ ! -f docker-compose.yml ]; then
    echo -e "${RED}❌ docker-compose.yml non trouvé${NC}"
    echo "   Assurez-vous d'être dans le répertoire microservices-event-platform"
    exit 1
fi
echo -e "${GREEN}✅ docker-compose.yml trouvé${NC}"

echo ""

# 2. ARRÊTER LES CONTENEURS EXISTANTS
echo -e "${YELLOW}2️⃣  Arrêt des conteneurs existants...${NC}"
echo "---"
docker compose down --remove-orphans 2>/dev/null || true
echo -e "${GREEN}✅ Conteneurs arrêtés${NC}"
echo ""

# 3. NETTOYER LES ANCIENNES IMAGES (optionnel)
echo -e "${YELLOW}3️⃣  Reconstruction des images Docker...${NC}"
echo "---"
docker compose build --no-cache
echo -e "${GREEN}✅ Images reconstruites${NC}"
echo ""

# 4. DÉMARRER LES CONTENEURS
echo -e "${YELLOW}4️⃣  Démarrage des conteneurs...${NC}"
echo "---"
docker compose up -d
echo -e "${GREEN}✅ Conteneurs démarrés${NC}"
echo ""

# 5. ATTENDRE QUE LES SERVICES SE STABILISENT
echo -e "${YELLOW}5️⃣  Attente de la stabilisation des services (30s)...${NC}"
echo "---"
for i in {1..30}; do
    echo -n "."
    sleep 1
done
echo ""
echo -e "${GREEN}✅ Attente terminée${NC}"
echo ""

# 6. VÉRIFIER LE STATUT DES SERVICES
echo -e "${YELLOW}6️⃣  Vérification du statut des services...${NC}"
echo "---"
docker compose ps
echo ""

# 7. VÉRIFIER LE HEALTH CHECK
echo -e "${YELLOW}7️⃣  Vérification des health checks...${NC}"
echo "---"

check_health() {
    local service=$1
    local port=$2
    
    if curl -s http://localhost:$port/actuator/health | grep -q '"status":"UP"'; then
        echo -e "${GREEN}✅${NC} $service est healthy"
        return 0
    else
        echo -e "${YELLOW}⚠️${NC}  $service n'est pas encore healthy"
        return 1
    fi
}

check_health "event-service" 8082 || true
check_health "ticket-service" 8083 || true
check_health "payment-service" 8084 || true

echo ""

# 8. TESTER LA CONNEXION À KEYCLOAK
echo -e "${YELLOW}8️⃣  Test de connexion à Keycloak...${NC}"
echo "---"

if curl -s http://localhost:8080/realms/event-platform-realm/protocol/openid-connect/certs | jq . &> /dev/null; then
    echo -e "${GREEN}✅ Keycloak JWK Set accessible${NC}"
else
    echo -e "${RED}❌ Keycloak JWK Set NON accessible${NC}"
    echo "   Assurez-vous que Keycloak est en cours d'exécution"
fi
echo ""

# 9. OBTENIR UN TOKEN DE TEST
echo -e "${YELLOW}9️⃣  Obtention d'un token de test...${NC}"
echo "---"

# Vérifier si .env existe et charger les variables
if [ -f .env ]; then
    export $(cat .env | grep EVENT_SERVICE_CLIENT_SECRET)
    CLIENT_SECRET=$EVENT_SERVICE_CLIENT_SECRET
else
    echo -e "${YELLOW}⚠️  Fichier .env non trouvé, utilisation d'un secret par défaut${NC}"
    CLIENT_SECRET="Gt6m2v8wEQxPKikDPgCBZM55Lsw7umdF"
fi

TOKEN_RESPONSE=$(curl -s -X POST \
  http://localhost:8080/realms/event-platform-realm/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=event-service" \
  -d "client_secret=$CLIENT_SECRET" \
  -d "grant_type=client_credentials" 2>/dev/null || echo '{}')

if echo "$TOKEN_RESPONSE" | jq . &> /dev/null && echo "$TOKEN_RESPONSE" | grep -q "access_token"; then
    TOKEN=$(echo "$TOKEN_RESPONSE" | jq -r '.access_token')
    echo -e "${GREEN}✅ Token obtenu avec succès${NC}"
    echo "   Token: ${TOKEN:0:50}..."
    
    # Afficher le contenu du token
    echo ""
    echo "   Contenu du token:"
    echo "$TOKEN" | cut -d'.' -f2 | base64 -d 2>/dev/null | jq . 2>/dev/null || echo "   (impossible de décoder le token)"
else
    echo -e "${RED}❌ Impossible d'obtenir un token${NC}"
    echo "   Vérifiez:"
    echo "   1. Que Keycloak est en cours d'exécution"
    echo "   2. Que le client 'event-service' existe dans Keycloak"
    echo "   3. Que le client_secret est correct"
    TOKEN=""
fi

echo ""

# 10. TESTER LES ENDPOINTS API
echo -e "${YELLOW}🔟 Test des endpoints API...${NC}"
echo "---"

if [ ! -z "$TOKEN" ]; then
    echo "Test du endpoint /api/events avec le token..."
    RESPONSE=$(curl -s -w "\n%{http_code}" \
      -H "Authorization: Bearer $TOKEN" \
      http://localhost:8888/api/events)
    
    HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
    BODY=$(echo "$RESPONSE" | head -n-1)
    
    if [ "$HTTP_CODE" = "200" ]; then
        echo -e "${GREEN}✅ Endpoint /api/events accessible (HTTP 200)${NC}"
        echo "   Réponse: ${BODY:0:100}..."
    else
        echo -e "${RED}❌ Endpoint /api/events retourne HTTP $HTTP_CODE${NC}"
        echo "   Réponse: $BODY"
    fi
else
    echo -e "${YELLOW}⚠️  Skipping test du endpoint (pas de token)${NC}"
fi

echo ""

# 11. AFFICHER LES LOGS DE DEBUG
echo -e "${YELLOW}1️⃣1️⃣ Affichage des logs OAuth2...${NC}"
echo "---"

docker logs event-event-service 2>&1 | grep -i "oauth\|jwt\|keycloak\|security" | tail -10 || true

echo ""

# 12. RÉSUMÉ FINAL
echo -e "${BLUE}════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}  ✅ DÉPLOIEMENT TERMINÉ${NC}"
echo -e "${BLUE}════════════════════════════════════════════════════════════${NC}"
echo ""

echo -e "${GREEN}Prochaines étapes:${NC}"
echo ""
echo "1. ✅ Services démarrés"
echo "2. 🔄 Vérifiez les health checks avec: docker compose ps"
echo "3. 🧪 Testez l'API: curl -H \"Authorization: Bearer \$TOKEN\" http://localhost:8888/api/events"
echo "4. 📊 Vérifiez les logs: docker logs -f event-event-service | grep -i security"
echo ""
echo -e "${YELLOW}Ressources:${NC}"
echo "- Documentation complète : ANALYSE_SECURITE_ET_CORRECTIONS.md"
echo "- Guide de dépannage : GUIDE_DEPANNAGE.md"
echo "- Liste de vérification : CHECKLIST_CORRECTIONS.md"
echo ""

exit 0
