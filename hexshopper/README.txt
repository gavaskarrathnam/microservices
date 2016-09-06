h1. Overview

Example for the Karaf Tutorial part 1 that implements a very small application to manage a list of tasks or to dos. 

It shows how to:

- Create bundles using maven and the maven bundle plugin
- Wire bundles using blueprint and OSGi services
- Use the whiteboard pattern and the pax-web whiteboard extender to publish Servlets

h1. Structure

cart-model - Service interface and model classes shared between persistence and ui
cart-persistence - Simple persistence implementation using an OSGi service and a in memory map
cart-view - Simple servlet based UI that connects to the persistence layer using an OSGi service reference and that offers the Servlet as an OSGi service for the pax web whiteboard extender to pickup and publish

h1. Build

mvn clean install

h1. Installation

On karaf 2.x
features:addurl mvn:com.hex.hexshopper/features/1.0.0-SNAPSHOT/xml
features:install shoppingcart

On karaf 3 and 4
feature:repo-add mvn:com.hex.hexshopper/features/1.0.0-SNAPSHOT/xml
feature:install hexshopper

on CXF
feature:repo-add cxf 3.1.0 (Or) feature:repo-add cxf 2.7.5
feature:install http cxf

for Gson
install -s mvn:com.google.code.gson/gson/1.7.1
(or)
install -s mvn:com.google.code.gson/gson/2.2.4