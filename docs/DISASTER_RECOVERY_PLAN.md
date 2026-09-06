# Disaster Recovery (DR) and Business Continuity Plan

## Executive Summary
This document establishes the Disaster Recovery Plan (DRP) and Business Continuity Framework for the **Supplier Performance Rating System (SPRS)**. It defines target recovery metrics (RTO & RPO), failover architectures, multi-region replication strategies, incident response protocols, and step-by-step restoration procedures.

---

## 1. Key Recovery Metrics

* **Recovery Point Objective (RPO)**: **< 15 Minutes** (with MySQL binlog replication / automated automated hourly transaction snapshots) or **< 24 Hours** with daily full dumps.
* **Recovery Time Objective (RTO)**: **< 30 Minutes** for automated multi-container rebuild or secondary cloud region failover.
* **Service Level Objective (SLO)**: **99.95% Availability** in multi-node production deployment.

---

## 2. Disaster Scenarios & Mitigation Matrix

| Disaster Scenario | Impact | Severity | Mitigation & Recovery Strategy |
| :--- | :--- | :--- | :--- |
| **Database Corruption / Data Loss** | Data integrity compromised | Critical | Execute `deploy/scripts/restore.sh` from the latest S3/GCS snapshot; replay binlogs for PITR. |
| **Backend API Host Failure** | API unavailable | High | Kubernetes / ECS auto-replaces unhealthy pods based on Actuator `/actuator/health` liveness probes. |
| **Complete Cloud Region Outage** | Full platform unreachable | Critical | Reroute DNS (Route53 / Cloudflare) to Standby Secondary Region; promote cross-region Read Replica to Primary. |
| **Compromised Secrets / Security Breach** | Unauthorized access risk | Critical | Rotate `JWT_SECRET`, database passwords, and API keys via cloud secret manager; invalidate active sessions. |

---

## 3. High-Availability & Scalability Architecture

```
                       [ Global DNS / Cloudflare ]
                                    |
                    +---------------+---------------+
                    |                               |
       [ Primary Region (Active) ]     [ Secondary Region (Standby) ]
                    |                               |
          +---------+---------+                     |
          |                   |                     |
     [ Ingress LB ]     [ Ingress LB ]        [ Ingress LB ]
          |                   |                     |
     +----+----+         +----+----+           +----+----+
     | Nginx   |         | Nginx   |           | Nginx   |
     | Frontend|         | Frontend|           | Frontend|
     +----+----+         +----+----+           +----+----+
          |                   |                     |
     +----+----+         +----+----+           +----+----+
     | Backend |         | Backend |           | Backend |
     | Node 1  |         | Node 2  |           | Node DR |
     +----+----+         +----+----+           +----+----+
          \                   /                     |
           \                 /                      |
      [ Primary MySQL Database ]  ---- Async ----> [ Standby Replica ]
```

---

## 4. Disaster Recovery Execution Steps

### Phase 1: Triage and Incident Assessment (T+0 to T+5 min)
1. Detect outage via uptime monitors / Prometheus alerts / Actuator health check failure.
2. Confirm outage scope (Database level, Application node level, or Infrastructure level).
3. Notify the Incident Response Team via automated alert webhook (Slack / PagerDuty).

### Phase 2: System Failover & Restoration (T+5 to T+20 min)
1. **If Database Failure**:
   - Spin up fresh database volume or promote standby read replica.
   - Run `deploy/scripts/restore.sh <latest_backup.sql.gz>`.
   - Update `DB_HOST` in application environment variables.
2. **If Host / Cluster Failure**:
   - Trigger deployment pipeline (`.github/workflows/ci-cd.yml`) against DR cluster or secondary region.
   - Deploy Docker stack via `docker compose -f deploy/docker-compose.prod.yml up -d --build`.
3. **If Region Failure**:
   - Update DNS A/CNAME records to point traffic to the secondary region load balancer.

### Phase 3: Post-Recovery Verification (T+20 to T+30 min)
1. Verify `/actuator/health` returns `{"status":"UP"}` across all nodes.
2. Log into the SPRS Admin Dashboard and verify data consistency (recent supplier ratings, evaluations, and audit logs).
3. Issue an all-clear notification to stakeholders.

---

## 5. Regular DR Testing & Drills
* **Quarterly Tabletop Simulation**: Review access keys, runbook procedures, and failover steps with the devops team.
* **Bi-Annual Mock Restore**: Execute full restore into an isolated staging environment and verify end-to-end user workflows.
