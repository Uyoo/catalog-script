#!/bin/bash
# kubectl create secret docker-registry nexus-login --docker-server=central.digitalkds.int:5000 --docker-username=admin --docker-password='elwlxjf12!@' -n NAMESPACE
kubectl create secret docker-registry nexus-login --docker-server=<레지스트리 주소> --docker-username=<ID> --docker-password='password' -n NAMESPACE