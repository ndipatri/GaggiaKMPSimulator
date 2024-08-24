#!/bin/zsh

# Use this to just launch a local MQTT Broker which will be used
# by both the interactive desktop simulator AND the
# RoboGaggiaMultiplatform client.
# (https://github.com/ndipatri/RoboGaggiaMultiplatform)
#
# Install Mosquitto MQTT Broker: brew install mosquitto
# Configure to be open. Add the following listener to config file:
# 
#	listener 1883 0.0.0.0
#	allow_anonymous true
#

# Launch local MQTT Broker.
/opt/homebrew/opt/mosquitto/sbin/mosquitto --verbose -c /opt/homebrew/etc/mosquitto/mosquitto.conf
