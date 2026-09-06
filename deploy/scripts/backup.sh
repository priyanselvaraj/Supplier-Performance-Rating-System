#!/usr/bin/env bash
# ==============================================================================
# Supplier Performance Rating System (SPRS)
# Automated Database Backup Script with Compression & Retention Pruning
# ==============================================================================

set -euo pipefail

# Configuration
BACKUP_DIR="${BACKUP_DIR:-/backups}"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
DB_HOST="${DB_HOST:-mysql}"
DB_PORT="${DB_PORT:-3306}"
DB_NAME="${DB_NAME:-spr_system_db}"
DB_USER="${DB_USERNAME:-spruser}"
DB_PASSWORD="${DB_PASSWORD:-sprpassword}"
RETENTION_DAYS="${RETENTION_DAYS:-30}"

BACKUP_FILE="${BACKUP_DIR}/sprs_backup_${DB_NAME}_${TIMESTAMP}.sql.gz"

echo "=========================================================="
echo "SPRS Database Backup Process Initiated"
echo "Timestamp:   ${TIMESTAMP}"
echo "Database:    ${DB_NAME}"
echo "Target File: ${BACKUP_FILE}"
echo "=========================================================="

# Create backup directory if it does not exist
mkdir -p "${BACKUP_DIR}"

# Execute mysqldump with single-transaction and compress with gzip
echo "[1/3] Dumping and compressing database..."
export MYSQL_PWD="${DB_PASSWORD}"
mysqldump \
  --host="${DB_HOST}" \
  --port="${DB_PORT}" \
  --user="${DB_USER}" \
  --single-transaction \
  --quick \
  --lock-tables=false \
  --routines \
  --triggers \
  "${DB_NAME}" | gzip -c > "${BACKUP_FILE}"

# Verify backup integrity
echo "[2/3] Verifying backup archive integrity..."
if [ -s "${BACKUP_FILE}" ] && gzip -t "${BACKUP_FILE}"; then
    BACKUP_SIZE=$(du -h "${BACKUP_FILE}" | cut -f1)
    echo " Backup verified successfully! Size: ${BACKUP_SIZE}"
else
    echo " Error: Backup file is empty or corrupted!"
    exit 1
fi

# Prune old backups exceeding retention policy
echo "[3/3] Pruning backups older than ${RETENTION_DAYS} days..."
find "${BACKUP_DIR}" -type f -name "sprs_backup_${DB_NAME}_*.sql.gz" -mtime +"${RETENTION_DAYS}" -exec rm -f {} \;
echo " Retention policy applied."

echo "=========================================================="
echo "SPRS Database Backup Completed Successfully"
echo "=========================================================="
