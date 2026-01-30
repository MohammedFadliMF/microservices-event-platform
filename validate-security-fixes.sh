#!/bin/bash
# Script de validation des corrections de sécurité
# Usage: bash validate-security-fixes.sh

set -e

echo "================================"
echo "🔍 VALIDATION DES CORRECTIONS"
echo "================================"
echo ""

# Couleurs
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

ERRORS=0
WARNINGS=0

# Fonction pour vérifier une condition
check() {
    local check_name=$1
    local condition=$2
    
    if eval "$condition"; then
        echo -e "${GREEN}✅${NC} $check_name"
    else
        echo -e "${RED}❌${NC} $check_name"
        ((ERRORS++))
    fi
}

warn() {
    local warn_name=$1
    local condition=$2
    
    if ! eval "$condition"; then
        echo -e "${YELLOW}⚠️${NC}  $warn_name"
        ((WARNINGS++))
    fi
}

# 1. VÉRIFIER LES MODIFICATIONS docker-compose.yml
echo "1️⃣  Vérification docker-compose.yml..."
echo "---"

check "Event Service URI utilise host.docker.internal" \
    "grep -q 'event-event-service' docker-compose.yml && \
     grep -A 10 'event-event-service:' docker-compose.yml | grep -q 'host.docker.internal:8080'"

check "Ticket Service URI utilise host.docker.internal" \
    "grep -q 'event-ticket-service' docker-compose.yml && \
     grep -A 10 'event-ticket-service:' docker-compose.yml | grep -q 'host.docker.internal:8080'"

check "Payment Service URI utilise host.docker.internal" \
    "grep -q 'event-payment-service' docker-compose.yml && \
     grep -A 10 'event-payment-service:' docker-compose.yml | grep -q 'host.docker.internal:8080'"

check "Gateway Service URI utilise host.docker.internal" \
    "grep -q 'event-gateway-service' docker-compose.yml && \
     grep -A 10 'event-gateway-service:' docker-compose.yml | grep -q 'host.docker.internal:8080'"

echo ""

# 2. VÉRIFIER LES PROPERTIES KEYCLOAK
echo "2️⃣  Vérification des properties..."
echo "---"

check "Event Service properties - token-uri" \
    "grep -q 'spring.security.oauth2.client.registration.keycloak.token-uri' \
     event-service/src/main/resources/application.properties"

check "Event Service properties - JWS algorithm" \
    "grep -q 'spring.security.oauth2.resourceserver.jwt.jws-algorithm' \
     event-service/src/main/resources/application.properties"

check "Event Service properties - Debug logging" \
    "grep -q 'logging.level.org.springframework.security.oauth2' \
     event-service/src/main/resources/application.properties"

check "Ticket Service properties - token-uri" \
    "grep -q 'spring.security.oauth2.client.registration.keycloak.token-uri' \
     ticket-service/src/main/resources/application.properties"

check "Payment Service properties - token-uri" \
    "grep -q 'spring.security.oauth2.client.registration.keycloak.token-uri' \
     payment-service/src/main/resources/application.properties"

check "Gateway Service properties - token-uri" \
    "grep -q 'spring.security.oauth2.client.registration.keycloak.token-uri' \
     gateway-service/src/main/resources/application.properties"

echo ""

# 3. VÉRIFIER LES CLASSES JWTDECODERCONFIGURATION
echo "3️⃣  Vérification des JwtSecurityConfig..."
echo "---"

check "Event Service - JwtSecurityConfig existe" \
    "test -f event-service/src/main/java/com/net/eventservice/config/JwtSecurityConfig.java"

check "Ticket Service - JwtSecurityConfig existe" \
    "test -f ticket-service/src/main/java/com/net/ticketservice/config/JwtSecurityConfig.java"

check "Payment Service - JwtSecurityConfig existe" \
    "test -f payment-service/src/main/java/com/net/paymentservice/config/JwtSecurityConfig.java"

check "Event Service - JwtSecurityConfig contient JwtDecoder bean" \
    "grep -q 'public JwtDecoder jwtDecoder' \
     event-service/src/main/java/com/net/eventservice/config/JwtSecurityConfig.java"

check "Event Service - JwtSecurityConfig utilise NimbusJwtDecoder" \
    "grep -q 'NimbusJwtDecoder' \
     event-service/src/main/java/com/net/eventservice/config/JwtSecurityConfig.java"

echo ""

# 4. VÉRIFIER LES CONFIGURATIONS EXISTENT
echo "4️⃣  Vérification des fichiers de configuration..."
echo "---"

check ".env existe et contient les secrets" \
    "test -f .env && grep -q 'EVENT_SERVICE_CLIENT_SECRET' .env"

check "SecurityConfig - Event Service" \
    "test -f event-service/src/main/java/com/net/eventservice/security/SecurityConfig.java"

check "SecurityConfig - Ticket Service" \
    "test -f ticket-service/src/main/java/com/net/ticketservice/security/SecurityConfig.java"

check "SecurityConfig - Payment Service" \
    "test -f payment-service/src/main/java/com/net/paymentservice/security/SecurityConfig.java"

echo ""

# 5. AVERTISSEMENTS
echo "5️⃣  Vérifications optionnelles..."
echo "---"

warn "Event Service - JwtAuthConverter existe" \
    "test -f event-service/src/main/java/com/net/eventservice/security/JwtAuthConverter.java"

warn "Ticket Service - JwtAuthConverter existe" \
    "test -f ticket-service/src/main/java/com/net/ticketservice/security/JwtAuthConverter.java"

warn "Gateway Service - SecurityConfig configuré" \
    "grep -q 'ReactiveJwtAuthenticationConverter' \
     gateway-service/src/main/java/com/net/gatewayservice/security/SecurityConfig.java"

echo ""

# 6. RÉSUMÉ
echo "================================"
echo "📊 RÉSUMÉ"
echo "================================"

if [ $ERRORS -eq 0 ] && [ $WARNINGS -eq 0 ]; then
    echo -e "${GREEN}✅ Toutes les corrections ont été appliquées correctement!${NC}"
    exit 0
elif [ $ERRORS -eq 0 ]; then
    echo -e "${GREEN}✅ Toutes les corrections requises appliquées${NC}"
    echo -e "${YELLOW}⚠️  $WARNINGS vérifications optionnelles non satisfaites${NC}"
    exit 0
else
    echo -e "${RED}❌ $ERRORS erreur(s) détectée(s)${NC}"
    echo -e "${YELLOW}⚠️  $WARNINGS avertissement(s)${NC}"
    exit 1
fi
