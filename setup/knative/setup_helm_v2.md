
helm init
helm init --service-account tiller
helm --upgrade
helm repo update

git clone https://github.com/istio/istio
cd istio

kubectl apply -f install/kubernetes/helm/helm-service-account.yaml

helm dep update install/kubernetes/helm/istio
helm install install/kubernetes/helm/istio-init --name istio-init --namespace istio-system
kubectl get crds | grep 'istio.io' | wc -l
-> 23
kubectl label namespace default istio-injection=enabled
helm install install/kubernetes/helm/istio --name istio --namespace istio-system

kubectl get all -n istio-system

Sample:
kubectl apply -f samples/bookinfo/platform/kube/bookinfo.yaml
kubectl apply -f samples/bookinfo/networking/bookinfo-gateway.yaml

kubectl exec -it $(kubectl get pod -l app=ratings -o jsonpath='{.items[0].metadata.name}') -c ratings -- curl productpage:9080/productpage | grep -o "<title>.*</title>"
-> <title>Simple Bookstore App</title>

export INGRESS_HOST=$(kubectl -n istio-system get service istio-ingressgateway -o jsonpath='{.status.loadBalancer.ingress[0].ip}')
-> none ?? 
export INGRESS_HOST=$(kubectl -n istio-system get service istio-ingressgateway -o jsonpath='{.status.loadBalancer.ingress[0].hostname}')
-> localhost
export INGRESS_PORT=$(kubectl -n istio-system get service istio-ingressgateway -o jsonpath='{.spec.ports[?(@.name=="http2")].port}')
-> 80
export SECURE_INGRESS_PORT=$(kubectl -n istio-system get service istio-ingressgateway -o jsonpath='{.spec.ports[?(@.name=="https")].port}')
export GATEWAY_URL=$INGRESS_HOST:$INGRESS_PORT

curl -s http://${GATEWAY_URL}/productpage | grep -o "<title>.*</title>"
curl -s http://localhost/productpage | grep -o "<title>.*</title>"

kubectl delete -f samples/bookinfo/platform/kube/bookinfo.yaml
kubectl delete -f samples/bookinfo/networking/bookinfo-gateway.yaml

https://github.com/istio/istio/releases
https://github.com/knative/serving/releases
https://istio.io/docs/setup/install/helm/

curl -L https://github.com/knative/serving/releases/download/{{< version >}}/serving.yaml \
  | sed 's/LoadBalancer/NodePort/' \
  | kubectl apply --filename -


curl -L https://github.com/knative/serving/releases/download/v0.9.0/serving.yaml \
  | sed 's/LoadBalancer/NodePort/' \
  | kubectl apply --filename -

https://github.com/knative/docs/blob/master/docs/install/Knative-with-Docker-for-Mac.md
https://github.com/knative/docs/blob/master/docs/serving/getting-started-knative-app.md
