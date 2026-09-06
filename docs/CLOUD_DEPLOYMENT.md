# Cloud Deployment Reference Architecture & Scalability Guide

## 1. Cloud Deployment Strategies Overview

The Supplier Performance Rating System (SPRS) is architected using cloud-native, containerized, and 12-factor application principles. The backend Spring Boot API is fully stateless (JWT-authenticated), enabling effortless horizontal scaling across cloud providers.

---

## 2. Cloud Provider Architectures

### A. Amazon Web Services (AWS) Deployment
```
                    [ Amazon Route 53 (DNS) ]
                                |
                    [ CloudFront CDN + WAF ]
                                |
             +------------------+------------------+
             |                                     |
    [ S3 Static Hosting ]               [ Application Load Balancer ]
   (Frontend React SPA)                            |
                                      +------------+------------+
                                      |                         |
                               [ ECS Fargate ]           [ ECS Fargate ]
                              (Backend Task 1)          (Backend Task 2)
                                      \                         /
                                       \                       /
                                [ Amazon RDS Aurora MySQL Multi-AZ ]
```
* **Frontend**: Hosted on Amazon S3 and distributed via AWS CloudFront CDN with TLS/SSL termination.
* **Backend**: AWS Elastic Container Service (ECS) Fargate with Application Load Balancer (ALB) and Auto Scaling based on CPU/Memory utilization (>70%).
* **Database**: Amazon RDS Aurora MySQL (Multi-AZ with automatic failover and read replicas).
* **Secrets**: AWS Secrets Manager dynamically injecting `JWT_SECRET` and database credentials into ECS task definitions.

---

### B. Google Cloud Platform (GCP) Deployment
```
                   [ Cloud DNS / Cloud Armor ]
                                |
                  [ Global External HTTPS LB ]
                                |
             +------------------+------------------+
             |                                     |
    [ Cloud Storage Bucket ]               [ Cloud Run Backend ]
      (Frontend Web SPA)               (Auto-scaling 0 to N instances)
                                                   |
                                       [ Serverless VPC Access ]
                                                   |
                                     [ Cloud SQL MySQL High-Availability ]
```
* **Frontend**: Google Cloud Storage static bucket behind Cloud CDN and HTTPS Load Balancer.
* **Backend**: Google Cloud Run service with container auto-scaling (1 to 20 instances) and custom VPC connector.
* **Database**: Google Cloud SQL MySQL 8.0 with automated point-in-time recovery and regional High-Availability (HA).
* **Secrets**: GCP Secret Manager mounted as environment variables.

---

### C. Microsoft Azure Deployment
* **Frontend**: Azure Static Web Apps with global edge distribution.
* **Backend**: Azure Container Apps (with KEDA autoscaling based on HTTP request concurrency).
* **Database**: Azure Database for MySQL Flexible Server with zone-redundant high availability.
* **Secrets**: Azure Key Vault integrated with managed identity.

---

## 3. Horizontal Pod Autoscaling (HPA) & Load Balancing

Because SPRS uses stateless JWT tokens (`Authorization: Bearer <token>`) and stateless API key filters for integrations, any backend node can process any request without session affinity (sticky sessions).

### Kubernetes HorizontalPodAutoscaler Specification
```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: sprs-backend-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: sprs-backend
  minReplicas: 2
  maxReplicas: 10
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 70
    - type: Resource
      resource:
        name: memory
        target:
          type: Utilization
          averageUtilization: 80
```

---

## 4. Production Security & Network Hardening
1. **Zero Public Database Exposure**: Place MySQL database instances strictly within private VPC subnets with no public IP.
2. **TLS 1.3 Termination**: Terminate TLS at the Ingress / Load Balancer level with modern cipher suites.
3. **Least Privilege Non-Root Containers**: Backend runs under UID `10001` (`spruser`), eliminating container escape vulnerabilities.
4. **Actuator Isolation**: In production, Actuator is restricted to `/health` and `/info` probes (`management.endpoints.web.exposure.include=health,info`), preventing unauthorized access to env or metrics data.
