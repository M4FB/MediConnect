#!/bin/bash
set -e

echo "========================================="
echo "  Deploying MediConnect to Kubernetes (Namespace: 2023800251)"
echo "========================================="

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo ""
echo "[1/6] Creating PostgreSQL configuration..."
kubectl apply -f postgres-configmap.yaml

echo ""
echo "[2/6] Creating PostgreSQL persistent volume claim..."
kubectl apply -f postgres-pvc.yaml

echo ""
echo "[3/6] Deploying PostgreSQL..."
kubectl apply -f postgres-deployment.yaml
kubectl apply -f postgres-service.yaml

echo ""
echo "[4/6] Waiting for PostgreSQL to be ready..."
kubectl wait --for=condition=ready pod -l app=postgres -n "2023800251" --timeout=120s

echo ""
echo "[5/6] Creating API configuration..."
kubectl apply -f api-configmap.yaml

echo ""
echo "[6/6] Deploying MediConnect API..."
kubectl apply -f api-deployment.yaml
kubectl apply -f api-service.yaml

echo ""
echo "========================================="
echo "  Deployment started! (API Pod will start once image is loaded/remote built)"
echo "========================================="
echo ""
echo "Pods:"
kubectl get pods -n "2023800251"
echo ""
echo "Services:"
kubectl get svc -n "2023800251"
echo ""
echo "API accessible at NodePort 30251"
echo "Swagger UI: http://<NODE_IP>:30251/swagger-ui/index.html"
