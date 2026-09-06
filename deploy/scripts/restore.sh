#!/usr/bin/env bash
# ==============================================================================
# Supplier Performance Rating System (SPRS)
# Database Disaster Recovery & Restore Script
# ==============================================================================

set -euo pipefail

if [ "$#" -lt 1 ]; then
    echo "Usage: $0 <path-to-backup-file.sql.gz>"
    echo "Example: $0 /backups/sprs_backup_spr_system_db_20260906_120000.sql.gz"
    exit 1
fi

BACKUP_FILE="$1"
DB_HOST="${DB_HOST:-mysql}"
DB_PORT="${DB_PORT:-3306}"
DB_NAME="${DB_NAME:-spr_system_db}"
DB_USER="${DB_USERNAME:-spruser}"
DB_PASSWORD="${DB_PASSWORD:-sprpassword}"

if [ ! -f "${BACKUP_FILE}" ]; then
    echo "Error: Backup file '${BACKUP_FILE}' not found!"
    exit 1
fi

echo "=========================================================="
echo "SPRS Database Disaster Recovery Restore"
echo "Backup File: ${BACKUP_FILE}"
echo "Database:    ${DB_NAME}"
echo "Target Host: ${DB_HOST}:${DB_PORT}"
echo "=========================================================="

echo "[1/3] Verifying gzip archive integrity..."
gzip -t "${BACKUP_FILE}"
echo " Archive integrity OK."

echo "[2/3] Restoring database schema and records..."
export MYSQL_PWD="${DB_PASSWORD}"

# Decompress and stream directly into MySQL
gunzip -c "${BACKUP_FILE}" | mysql \
  --host="${DB_HOST}" \
  --port="${DB_PORT}" \
  --user="${DB_USER}" \
  "${DB_NAME}"

echo "[3/3] Verifying database connectivity and table state..."
TABLE_COUNT=$(mysql --host="${DB_HOST}" --port="${DB_PORT}" --user="${DB_USER}" "${DB_NAME}" -e "SHOW TABLES;" -s -N | wc -l)

echo "=========================================================="
echo " Database restoration completed successfully!"
echo " Total active tables: ${TABLE_COUNT}"
echo "=========================================================="
