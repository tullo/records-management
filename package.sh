#!/usr/bin/env bash
mvn clean package -o -DskipTests=true -P fullBuild

## profiles / rm-server:
# wipeDB
# appWarLocationSetting
# enterprise
# amp-with-solr
# fullBuild
