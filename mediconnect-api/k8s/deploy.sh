#!/bin/bash
set -e

echo "========================================="
echo "  Deploying MediConnect to Kubernetes (Namespace: 2023800251)"
echo "========================================="

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo ""
echo "[1/7] Creating PostgreSQL secrets..."
kubectl apply -f postgres-secret.yaml

echo ""
echo "[2/7] Creating PostgreSQL persistent volume claim..."
kubectl apply -f postgres-pvc.yaml

echo ""
echo "[3/7] Deploying PostgreSQL..."
kubectl apply -f postgres-deployment.yaml
kubectl apply -f postgres-service.yaml

echo ""
echo "[4/7] Waiting for PostgreSQL to be ready..."
kubectl wait --for=condition=ready pod -l app=postgres -n 2023800251 --timeout=120s

echo ""
echo "[5/7] Creating API config and secrets..."
kubectl apply -f api-configmap.yaml
kubectl apply -f api-secret.yaml

echo ""
echo "[6/7] Deploying MediConnect API..."
kubectl apply -f api-deployment.yaml
kubectl apply -f api-service.yaml

echo ""
echo "[7/7] Waiting for API to be ready..."
kubectl wait --for=condition=ready pod -l app=mediconnect-api -n 2023800251 --timeout=180s

echo ""
echo "========================================="
echo "  Deployment complete!"
echo "========================================="
echo ""
echo "Pods:"
kubectl get pods -n 2023800251
echo ""
echo "Services:"
kubectl get svc -n 2023800251
echo ""
echo "API accessible at NodePort 30080"
echo "Swagger UI: http://<NODE_IP>:30080/swagger-ui/index.html"
