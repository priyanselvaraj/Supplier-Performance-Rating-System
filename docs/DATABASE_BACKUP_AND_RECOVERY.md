# Database Backup and Recovery Guide

## Overview
The Supplier Performance Rating System (SPRS) utilizes a production-ready MySQL 8.0 relational database engine storing mission-critical supplier records, evaluation data, rating scorecards, audit events, workflow escalations, integration API logs, and user credentials. This document defines the automated backup strategy, retention policies, storage architectures, and disaster recovery procedures.

---

## 1. Backup Strategy Architecture

| Backup Type | Frequency | Tool / Mechanism | Retention Period | Storage Target |
| :--- | :--- | :--- | :--- | :--- |
| **Full Database Dump** | Daily (02:00 UTC) | `mysqldump` + `gzip` | 30 Days | Encrypted Local / Cloud Storage (AWS S3 / GCS / Azure Blob) |
| **Transaction Logs (Binlog)** | Continuous | MySQL Binary Logging (`binlog`) | 7 Days | Standby Replica / Cold Storage |
| **Point-in-Time Recovery (PITR)** | On-Demand | Base Backup + Binlog Replay | Up to 7 Days back | Staging / Emergency DR Instance |

---

## 2. Automated Backup Execution

The backup utility (`deploy/scripts/backup.sh`) encapsulates:
1. **Single-Transaction Isolation**: Executes `mysqldump --single-transaction --quick --lock-tables=false` to ensure zero database downtime or lock contention for active users.
2. **Routine and Trigger Inclusion**: Backs up stored procedures, functions, and triggers with `--routines --triggers`.
3. **Compression**: Streams SQL dumps directly through `gzip -c` into `.sql.gz` archives, reducing storage footprint by ~85%.
4. **Integrity Verification**: Verifies archive integrity using `gzip -t` immediately after dump completion.
5. **Retention Pruning**: Automatically prunes backups older than the configured `RETENTION_DAYS` (default: 30 days).

### Executing Backup via Docker
```bash
# Execute within the running database container
docker compose exec mysql /bin/bash -c "DB_PASSWORD=sprpassword /scripts/backup.sh"

# Or execute host script against Docker database port
BACKUP_DIR="./backups" DB_HOST="localhost" DB_PORT="3306" DB_PASSWORD="sprpassword" ./deploy/scripts/backup.sh
```

### Scheduled Cron Automation (Linux Host)
```cron
# Run daily SPRS backup at 02:00 AM UTC
0 2 * * * /opt/sprs/deploy/scripts/backup.sh >> /var/log/sprs_backup.log 2>&1
```

---

## 3. Database Restoration & Recovery Procedure

The recovery script (`deploy/scripts/restore.sh`) handles full decompression, atomic restoration, and table verification.

### Disaster Recovery Steps:
1. **Identify Target Backup**: Locate the most recent healthy backup archive (e.g., `sprs_backup_spr_system_db_20260906_020000.sql.gz`).
2. **Execute Restore Script**:
   ```bash
   ./deploy/scripts/restore.sh ./backups/sprs_backup_spr_system_db_20260906_020000.sql.gz
   ```
3. **Verification**:
   - The script performs a checksum validation on the gzip archive before unpacking.
   - It verifies that all database tables (`SHOW TABLES;`) are present and accessible.
   - The backend `ProductionEnvironmentValidator` and `/actuator/health` probe confirm database health upon startup.

---

## 4. Point-in-Time Recovery (PITR) Workflow
To recover data up to a specific timestamp between daily snapshots:
1. Restore the most recent daily snapshot prior to the incident timestamp using `restore.sh`.
2. Extract binary log entries from the binlog directory up to the desired timestamp:
   ```bash
   mysqlbinlog --stop-datetime="2026-09-06 14:30:00" /var/lib/mysql/binlog.0000* | mysql -u spruser -p spr_system_db
   ```
3. Restart backend instances and verify application state via the SPRS Admin Dashboard.
