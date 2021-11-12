#!/bin/bash
###########

echo "Delete Deployed Jenkins"

kubectl delete -f jenkins/service.yml
kubectl delete -f jenkins/deployment.yml
kubectl delete -f jenkins/namespace.yml

echo "Finished Delete Deploy Jenkins"