# Autonomous Highways -- Group Project Delta

The code to implement platooning in simulation and on LEGOⓇ Mindstorms robots.

[![Build Status](https://travis-ci.com/jackwickham/group-project-delta.svg?token=DtrLKaeqQLW7MbyBRvfb&branch=master)](https://travis-ci.com/jackwickham/group-project-delta)

## Building
To build all the code, just run `./gradlew build` (Linux) or `gradlew.bat build` (Windows).

To run the simulation, run `./gradlew :simulation:run` (Linux) or `gradlew.bat :simulation:run` (Windows).

To build the code for the Mindstorms, and download it to a Mindstorms device that is connected via USB, execute `./gradlew :lego:deployMindstorms` (Linux) or `gradlew.bad :lego:deployMindstorms` (Windows).
