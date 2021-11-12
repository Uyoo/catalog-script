#!/bin/bash
###########

echo "Deploy Jenkins"

kubectl apply -f jenkins/namespace.yml
kubectl apply -f jenkins/deployment.yml
kubectl apply -f jenkins/service.yml

echo "Finished Deploy Jenkins"