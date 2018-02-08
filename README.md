# Autonomous Highways -- Group Project Delta

The code to implement platooning in simulation and on LEGOⓇ Mindstorms robots.

[![Build Status](https://travis-ci.com/jackwickham/group-project-delta.svg?token=DtrLKaeqQLW7MbyBRvfb&branch=master)](https://travis-ci.com/jackwickham/group-project-delta)

## Building
To build all the code, just run `./gradlew build` (Linux) or `gradlew.bat build` (Windows).

To run the simulation, run `./gradlew :simulation:run` (Linux) or `gradlew.bat :simulation:run` (Windows).

### Building and Deploying for Mindstorms
To build the code, and upload it to a Mindstorms device that is connected via USB, execute `./gradlew :lego:deployUSB` (Linux) or `gradlew.bat :lego:deployUSB` (Windows).

To upload the code to a device that is on the same wifi network, execute `./gradlew :lego:deployWifi  -Pip=10.0.2.2` (replacing with the appropriate IP) or `gradlew.bat :lego:deployWifi -Pip=10.0.2.2` as appropriate.

To upload the code to all Mindstorms devices on the same wifi network, execute `./gradlew :lego:deployAll` (Linux) or `gradlew.bat :lego:deployAll` (Windows). 
