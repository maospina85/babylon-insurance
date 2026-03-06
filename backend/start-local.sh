#!/usr/bin/env bash
# Script de arranque local — lee variables de .env y lanza Spring Boot
set -a
source "$(dirname "$0")/.env"
set +a
exec "$(dirname "$0")/mvnw.cmd" spring-boot:run
