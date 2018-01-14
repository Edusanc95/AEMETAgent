#!/bin/bash
# -*- ENCODING: UTF-8 -*-

sudo java -classpath jade.jar:./lib/jsoup-1.11.2.jar:./lib/jsoup-1.11.2-sources.jar:./lib/jxbrowser-6.18.jar:./lib/license.jar:./lib/jxbrowser-linux64-6.18.jar:AEMET.jar jade.Boot -host 127.0.0.1 -port  1099 "EnclaveAgent:domain.EnclaveAgent;ComunidadAutonomaAgent:domain.ComunidadAutonomaAgent;AemetAgent:domain.AemetAgent"
exit
