## tutorial

https://starkandwayne.com/blog/running-cloud-foundry-on-kubernetes-using-kubecf/

## kind

https://kind.sigs.k8s.io/
https://medium.com/@jmpinto/deploying-cloudfoundry-on-a-local-kubernetes-9103a57bf713
https://hub.docker.com/r/kindest/node/tags
https://github.com/kubernetes-sigs/kind/releases

kind create cluster --name kubecf --image kindest/node:latest
kind create cluster --name kubecf --image kindest/node:v1.15.11

node_ip=$(kubectl get node kind-control-plane --output jsonpath='{ .status.addresses[?(@.type == "InternalIP")].address }')


## kubecf

https://kubecf.suse.dev/docs/getting-started/kubernetes-deploy/

### cf operator

https://github.com/cloudfoundry-incubator/kubecf/releases
https://kubecf.s3.amazonaws.com/index.html
https://cf-operators.s3.amazonaws.com/helm-charts/index.html

kubectl create ns cf-operator

helm install cf-operator --namespace cf-operator --set "global.operator.watchNamespace=kubecf"     https://s3.amazonaws.com/cf-operators/release/helm-charts/cf-operator-4.5.6%2B0.gffc6f942.tgz

### kubecf

https://github.com/cloudfoundry-incubator/kubecf
https://github.com/cloudfoundry-incubator/kubecf/blob/master/deploy/helm/kubecf/values.yaml

cat << _EOF_  > values.yaml
system_domain: ${node_ip}.nip.io
services:
  router:
    externalIPs:
    - ${node_ip}
kube:
  service_cluster_ip_range: 10.100.0.0/16
  pod_cluster_ip_range: 192.16.0.0/16
features:
  eirini:
    enabled: true
_EOF_

Docker Desktop Windows working version:

helm install cf-operator --namespace cf-operator --set "global.operator.watchNamespace=kubecf" https://s3.amazonaws.com/cf-operators/release/helm-charts/cf-operator-4.5.6%2B0.gffc6f942.tgz

helm install kubecf --namespace kubecf --values values.yaml https://github.com/cloudfoundry-incubator/kubecf/releases/download/v2.2.2/kubecf-v2.2.2.tgz

~ » cat values.yaml
system_domain: system.kubecf

kube:
  service_cluster_ip_range: 10.100.0.0/16
  pod_cluster_ip_range: 192.168.0.0/16

features:
  eirini:
    enabled: true
    
  SYSTEM_DOMAIN=$(kubectl get secret var-system-domain -n kubecf -ojsonpath='{.data.value}' | base64 --decode)
  
  ADMIN_PASSWORD=$(kubectl get secret var-cf-admin-password -n kubecf -ojsonpath='{.data.password}' | base64 --decode)
  
  cf login -a https://api.$SYSTEM_DOMAIN --skip-ssl-validation -u admin -p $ADMIN_PASSWORD
  
  using /etc/hosts

### cf-for-k8s

https://starkandwayne.com/blog/deploy-cf-for-k8s-to-google-in-10-minutes/
https://tanzu.vmware.com/developer/guides/kubernetes/cf4k8s-gs/
https://github.com/cloudfoundry/cf-for-k8s/blob/master/docs/deploy.md
https://github.com/dbbaskette/tas-on-kind
https://github.com/nrekretep/k8s-dev-cluster/blob/master/cf-for-k8s.md
https://github.com/cloudfoundry/cf-for-k8s/blob/master/docs/deploy-local.md

Deploy all pre-reqs

1. Run on KIND

kapp: Error: waiting on reconcile deployment/cf-api-kpack-watcher (apps/v1) namespace: cf-system:
  Finished unsuccessfully (Deployment is not progressing: ProgressDeadlineExceeded (message: ReplicaSet "cf-api-kpack-watcher-58d96dcd45" has timed out progressing.))

Stuck here:
cf-system            pod/cf-api-deployment-updater-74f9c99b76-xsmxz   0/2     PodInitializing   0          8m29s
cf-system            pod/cf-api-kpack-watcher-58d96dcd45-cdvkb        0/2     PodInitializing   0          8m29s
cf-system            pod/cf-api-server-5698f6b86-qk6j9                0/5     PodInitializing   0          8m16s

Re-run:

kapp: Error: waiting on reconcile job/ccdb-migrate (batch/v1) namespace: cf-system:
  Finished unsuccessfully (Failed with reason BackoffLimitExceeded: Job has reached the specified backoff limit)

2. Run on Docker for Mac (K8s)

First run takes much time for Istio images, repeat of cf components hang in init phase long
All components come up, but no entry point defined. Everything as ClusterIP

Remove Ingress psrt in ytt call

## Azure

az aks create --resource-group mhs-rg --name cf-for-k8s --node-count 1 --kubernetes-version 1.15.11 --node-vm-size Standard_F8s_v2
az network public-ip create --resource-group mhs-rg --name CFPublicIP --sku Standard --allocation-method static --query publicIp.ipAddress -o tsv
./hack/generate-values.sh -d 51.137.10.208.xip.io > cf-values-mhs-azure.yml
istio_static_ip: 51.137.10.208

Failing on obtaining public IP for Istio Load Balancer

kubectl get events -n istio-system

2m30s       Warning   SyncLoadBalancerFailed         service/istio-ingressgateway                   Error syncing load balancer: failed to ensure load balancer: user supplied IP Address 51.137.10.208 was not found in resource group mc_mhs-rg_cf-for-k8s_westeurope

workaround: Create the cluster and then put the static IP into the generated resource group

