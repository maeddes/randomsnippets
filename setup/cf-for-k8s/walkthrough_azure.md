* Login to Azure

```az login```

* Create Resource Group

```az group create --name cf-for-k8s-rg --location westeurope```

* Create Kubernetes Cluster (Use 3xE8_v3)

```
// az aks create --resource-group cf-for-k8s-rg --name cf-for-k8s --node-count 5 --node-vm-size Standard_D2s_v4 --generate-ssh-keys
az aks create --resource-group cf-for-k8s-rg --name cf-for-k8s --node-count 3 --node-vm-size Standard_E8_v3 --generate-ssh-keys
```
* Show groups

```
az group list -o table
```

* identify MC_cf-for-k8s-rg_cf-for-k8s_westeurope group or something alike

* create IP in exactly this group

```
az network public-ip create --resource-group MC_cf-for-k8s-rg_cf-for-k8s_westeurope --name CFPublicIP --sku Standard --allocation-method static --dns-name cffork8s --query publicIp.ipAddress -o tsv
```

* note down IP (in my case 20.73.193.196)

```
./hack/generate-values.sh -d 20.73.193.196.xip.io > cf-azure-values.yml

vi cf-azure-values.yml
```

* add:

```
load_balancer:
  enable: false
  static_ip: 20.73.193.196
  
  app_registry:
  hostname: https://index.docker.io/v1/
  #! often times the repository_prefix is just your `docker_user`
  repository_prefix: maeddes
  username: maeddes
  password: ****
```

* get K8s context (point kubectl to cluster) 
  
```
az aks get-credentials -n cf-for-k8s -g cf-for-k8s-rg
```

* CF for K8s

```
ytt -f config -f ./cf-azure-values.yml > kapp-rendered.yml
```

* Change service type from NodePort to LoadBalancer
 
```
sed -i '' 's/NodePort/LoadBalancer/g' kapp-rendered.yml
```

```
kapp deploy -a cf -f kapp-rendered.yml
```
