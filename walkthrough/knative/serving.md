Watch both knative and kubernetes relevant artifacts:

`watch -n0.1 kubectl get configuration,revision,deployment,replicaset,route,pod`

Use kn cli to deploy first application:

```
kn service create quarkus-knative --image=maeddes/quarkus-hello
```

```
Service 'quarkus-knative' successfully created in namespace 'default'.
Waiting for service 'quarkus-knative' to become ready ...OK

Service URL:
http://quarkus-knative.default.example.com
```

the watch command should look like this:

```
Every 0.1s: kubectl get configuration,revision,deployment,replicaset,route,pod    macbookmhs: Wed Oct 30 12:51:13 2019

NAME                                                LATESTCREATED           LATESTREADY             READY   REASON
configuration.serving.knative.dev/quarkus-knative   quarkus-knative-6bwrv   quarkus-knative-6bwrv   True

NAME                                                 CONFIG NAME       K8S SERVICE NAME        GENERATION   READY   RE
ASON
revision.serving.knative.dev/quarkus-knative-6bwrv   quarkus-knative   quarkus-knative-6bwrv   1            True

NAME                                                     READY   UP-TO-DATE   AVAILABLE   AGE
deployment.extensions/quarkus-knative-6bwrv-deployment   1/1     1            1           54s

NAME                                                                DESIRED   CURRENT   READY   AGE
replicaset.extensions/quarkus-knative-6bwrv-deployment-6c59599f5b   1         1         1       55s

NAME                                        URL                                          READY   REASON
route.serving.knative.dev/quarkus-knative   http://quarkus-knative.default.example.com   True

NAME                                                    READY   STATUS    RESTARTS   AGE
pod/quarkus-knative-6bwrv-deployment-6c59599f5b-4n98v   2/2     Running   0          55s
```

After a while watch it going back to zero instances

```
NAME                                                     READY   UP-TO-DATE   AVAILABLE   AGE
deployment.extensions/quarkus-knative-6bwrv-deployment   0/0     0            0           9m29s

NAME                                                                DESIRED   CURRENT   READY   AGE
replicaset.extensions/quarkus-knative-6bwrv-deployment-6c59599f5b   0         0         0       9m29s

NAME                                        URL                                          READY   REASON
route.serving.knative.dev/quarkus-knative   http://quarkus-knative.default.example.com   True
```

(New window) Identify route

```
kubectl get route quarkus-knative -o jsonpath="{.status.url}"
http://quarkus-knative.default.example.com
```

Identify IP/host if Istio Ingress gateway

```
kubectl get svc istio-ingressgateway -n istio-system -o jsonpath="{.status.loadBalancer.ingress[0].ip}"
51.145.133.17
```

Compose it to cURL call

```
curl -H "Host: quarkus-knative.default.example.com" 51.145.133.17/hello
Hello World!
```

Wrap into while loop

```
while true; 
  do 
    curl -H "Host: quarkus-knative.default.example.com" 51.145.133.17/hello;
    sleep 1;
    echo;
  done

Hello World!
Hello World!
Hello World!
Hello World!
```

Kill instances

```
curl -H "Host: quarkus-knative.default.example.com" 51.145.133.17/fail
dial tcp 127.0.0.1:8080: connect: connection refused
```

Multiple revisions

```
kn service update demo --image=maeddes/quarkus-hello:v0.2
```

```
kn revision list
```

Split the traffic

```
kn service update demo --traffic @latest=25 --traffic quarkus-knative-zfxjl-1=75
```

Full switch of traffic

```
kn service update demo --traffic @latest=100 
```

Mass scale

Edit the configuration `kubectl edit ksvc quarkus-knative`

Set

```yaml
spec:
  template:
    spec:
      containerConcurrency: 2
```

Observe the change in the revisions!

```
kn revision list
NAME                    SERVICE           AGE   CONDITIONS   READY   REASON
quarkus-knative-csmr2   quarkus-knative   13m   4 OK / 4     True
quarkus-knative-fntql   quarkus-knative   29m   3 OK / 4     True
quarkus-knative-hj24b   quarkus-knative   27m   3 OK / 4     True
quarkus-knative-trwpc   quarkus-knative   13m   3 OK / 4     True
quarkus-knative-vkhnm   quarkus-knative   18m   3 OK / 4     True
```

Change a revision (complicated) in ksvc

```yaml
spec:
  release:
    revisions: ["quarkus-knative-fntql"]
    configuration:
      revisionTemplate:
        spec:
          container:
            image: docker.io/maeddes/quarkus-hello
```       

Split traffic

```yaml
  traffic:
  - latestRevision: false
    percent: 50
    revisionName: quarkus-knative-9l9h7
  - latestRevision: false
    percent: 50
    revisionName: quarkus-knative-5gbt5
```

Log output

`stern "quarkus-knative*"`

Cleanup

`kubectl delete ksvc quarkus-knative`


Alternative use yaml file:

```yaml
apiVersion: serving.knative.dev/v1
kind: Service
metadata:
  name: quarkus-knative
  namespace: default
spec:
  template:
    spec:
      containers:
      - image: docker.io/maeddes/quarkus-hello
        env:
        - name: property
          value: "value"
```
 
Backup
 
```yaml
apiVersion: serving.knative.dev/v1
kind: Service
metadata:
  name: quarkus-knative
  namespace: default
spec:
  template:
    spec:
      containerConcurrency: 5
      containers:
      - image: maeddes/quarkus-hello
```

```yaml
apiVersion: serving.knative.dev/v1
kind: Service
metadata:
  name: concurrency-demo
  namespace: default
spec:
  template:
    spec:
      containerConcurrency: 1
      containers:
      - image: maeddes/simpleweb:v0.1
```

