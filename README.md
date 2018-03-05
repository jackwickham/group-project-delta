# Autonomous Highways – Group Project Delta

Implementing platooning for LEGOⓇ Mindstorms vehicles, testable both on the vehicles and in simulation.

[![Build Status](https://travis-ci.com/jackwickham/group-project-delta.svg?token=DtrLKaeqQLW7MbyBRvfb&branch=master)](https://travis-ci.com/jackwickham/group-project-delta)

## Project Structure
The code is structured for Gradle, with three subprojects. `lego` and `simulation` contain LEGO- and simulation-specific code respectively, while `common` has the core algorithm and communication code, as well as interfaces and utility functions, and is used by both.

## Running
We are using [Gradle](https://gradle.org/) 4.5 to manage building and deploying code.

> **Note**: In all commands below, you should replace `gradlew` with `./gradlew` on Linux and MacOS, and with `gradlew.bat` on Windows (sometimes `./gradlew.bat` is required). This will download an appropriate version of Gradle for you, and use it to run the command. There is no need to have Gradle installed.

To build all the code, just run `gradlew build` (that means `./gradlew build` on Linux and `gradlew.bat build` on Windows). All run or deploy commands will build the code automatically before running.

### Simulation
To run the headless simulation, which will drive a vehicle forwards and output a CSV of the vehicles' routes, run `gradlew :simulation:run`.

To run the simulation with a GUI, run `gradlew jfxRun`. It can be debugged by connecting a debugger to port 5005 if needed. Instructions for using the GUI can be found [alongside the GUI code](simulation/src/main/java/uk/ac/cam/cl/group_project/delta/simulation/gui/README.md).

### LEGO Mindstorms
To deploy the code to a Mindstorms device that is connected via USB, execute `gradlew :lego:deployUSB`.

To deploy the code to all Mindstorms devices on the same WiFi network as the computer, execute `gradlew :lego:deployAll`. It will print out all devices that it has deployed to. Occasionally it will fail to detect a device because that device had stopped advertising itself at the time that the command was run. This can usually be fixed by just running it again.

To deploy the code to a specific device on the same wifi network, execute `gradlew :lego:deployWifi  -Pip=10.0.2.2` (replacing with the appropriate IP). You can get a list of devices on the current network by running `gradlew :lego:detectWifiDevices`.

If the robots are running away, and need to be stopped (or if red lights come on and they turn evil), you can run
```bash
echo -n -e \\x00\\x00\\x00\\x0C\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00 | nc -4u -w1 10.0.2.255 5187
```
on Linux (and probably MacOS) to emergency stop all vehicles on the network. If you're running Windows, you can try the Linux subsystem, but otherwise you're doomed.

To aid with debugging, a Wireshark plugin has been provided which allows packets to be inspected and allows highlighting based on the packet type. The plugin and its documentation can be found in the [Wireshark directory](wireshark).

## Testing
All of the unit tests in the project can be run using `gradlew test`. For a more comprehensive check, which includes code style validation too, you can run `gradlew check`.

The tests for just one subproject can be run using `gradlew :simulation:test` (replacing `simulation` as appropriate).

Gradle will try to remember which tests have been run before, and won't rerun them if it doesn't think the code has changed. To force it to rerun all tests, use `gradlew clean test` to remove all cached builds and changes before testing.

All code pushed to this repository will be tested using `gradlew check` using Travis CI, and changes cannot be merged into `master` unless they pass all tests and checks.

## Documentation
The packet format used for inter-vehicle communication is defined in the [communications package](common/src/main/java/uk/ac/cam/cl/group_project/delta/algorithm/communications/README.md).
