set -eux
az group create --name mhs-cf-for-k8s-test-rg --location westeurope
az aks create --resource-group mhs-cf-for-k8s-test-rg --name cf-for-k8s --node-count 1 --node-vm-size Standard_F8s_v2
az network public-ip show -g MC_mhs-cf-for-k8s-test-rg_cf-for-k8s_westeurope -n CFPublicIP --query ipAddress -o tsv
CF_ISTIO_IP=$(kubectl get svc istio-ingressgateway -n istio-system -o jsonpath="{.status.loadBalancer.ingress[0].ip}")
cf api --skip-ssl-validation https://api.$CF_ISTIO_IP.xip.io
CF_ADMIN_PASSWORD="$(bosh interpolate ../cf-for-k8s/cf-values.yml --path /cf_admin_password)"
cf auth admin $CF_ADMIN_PASSWORD

cf create-space dev -o system
cf target -o "system" -s "dev"
