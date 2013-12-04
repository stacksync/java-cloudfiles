#!/bin/sh
 
export CLASSPATH=lib/httpcore-4.1.jar:lib/commons-cli-1.1.jar:lib/httpclient-4.0.3.jar:lib/commons-lang-2.4.jar:lib/junit.jar:lib/commons-codec-1.3.jar:lib/commons-io-1.4.jar:lib/commons-logging-1.1.1.jar:lib/log4j-1.2.15.jar:dist/java-cloudfiles.jar:.

java com.rackspacecloud.client.cloudfiles.sample.FilesCli $@
