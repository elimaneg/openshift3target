# Sample application for OpenShift 3

This sample application will create and deploy a JBoss EAP application server as well as a MongoDB database. 

## Quick instructions to just get this working on an OpenShift 3 deployment as a normal user

````
$ oc login https://yourOpenShiftServer
$ oc new-project target
$ oc create -f https://raw.githubusercontent.com/gshipley/openshift3target/master/target-template.json
$ oc new-app target
````

