# Robo Gaggia Simulator 

[Nick DiPatri](ndipatri@gmail.com)

[LinkedIn](https://www.linkedin.com/in/ndipatri/)

![Robo Gaggia](media/gaggia1.png)

## MQTT Testing Harness ##

This desktop-based simulator pretends that it is the Robo Gaggia device: it emits telemetry and changes state as commands are sent by the [Robo Gaggia Multiplatform Mobile Application](https://github.com/ndipatri/RoboGaggiaMultiplatform) using MQTT.

This simulator also represents each 'state' of Robo Gaggia by playing a video clip taken from the real Robo Gaggia.

This desktop app is built using [Compose Multiplatform UI Framework](https://www.jetbrains.com/lp/compose-multiplatform/) technology.  It is JVM-based.


## MQTT Broker ##

Before you run this desktop application, you must run the [MQTT Broker](mqtt/mqttBroker.sh) script.  Both the [Robo Gaggia Multiplatform Mobile Application](https://github.com/ndipatri/RoboGaggiaMultiplatform) and this simulator communicate via this local broker. 



## Additional Notes ##

Like all simulators, this one must be updated as the behavior of the real [Robo Gaggia](https://github.com/ndipatri/RoboGaggia) is changed.


