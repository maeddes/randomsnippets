List suggested builders:

```
pack suggest-builders
Suggested builders:
	Cloud Foundry:     cloudfoundry/cnb:bionic         Ubuntu bionic base image with buildpacks for Java, NodeJS and Golang
	Cloud Foundry:     cloudfoundry/cnb:cflinuxfs3     cflinuxfs3 base image with buildpacks for Java, .NET, NodeJS, Python, Golang, PHP, HTTPD and NGINX
	Heroku:            heroku/buildpacks:18            heroku-18 base image with buildpacks for Ruby, Java, Node.js, Python, Golang, & PHP

Tip: Learn more about a specific builder with:
	pack inspect-builder [builder image]
  ```
  
Build an image:
`pack build maeddes/sample-java:v0.1 --builder cnbs/sample-builder:alpine`

Options:
`pack build maeddes/sample-java:v0.1 --builder cnbs/sample-builder:alpine --no-pull`

--

Steps:

- Detection -> Java Maven
- Restore -> Restore cached maven layer
- Build -> run maven - ? have not figured out when install and download is performed and when it is re-used
- Export - export all layers
- Caching -> Cache certain layers
