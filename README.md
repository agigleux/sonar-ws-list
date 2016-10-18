SonarQube APIs Extractor
========================

### Build Status

[![Build Status](https://travis-ci.org/henryju/sonar-protobuf.svg?branch=master)](https://travis-ci.org/henryju/sonar-protobuf)

### Usage

    java -jar ./sonar-ws-list-1.0.0-SNAPSHOT-jar-with-dependencies.jar -sq.url <SonarQube's URL> -admin.login <sq-login> -admin.password <sq-password>

Example:

    java -jar ./sonar-ws-list-1.0.0-SNAPSHOT-jar-with-dependencies.jar -sq.url http://localhost:9000/ -admin.login admin -admin.password admin
