# Autonomous Highways – Group Project Delta

Implementing platooning for LEGOⓇ Mindstorms vehicles, testable both on the vehicles and in simulation.

[![Build Status](https://travis-ci.com/jackwickham/group-project-delta.svg?token=DtrLKaeqQLW7MbyBRvfb&branch=master)](https://travis-ci.com/jackwickham/group-project-delta)

## Project Structure
The code is structured for Gradle, with three subprojects. `lego` and `simulation` contain LEGO- and simulation-specific code respectively, while `common` has the core algorithm and communication code, as well as interfaces and utility functions, and is used by both.

## Running
We are using [Gradle](https://gradle.org/) 4.5 to manage building and deploying code. In all commands below, you should replace `gradle` with `./gradlew` on Linux and MacOS, and with `gradlew.bat` on Windows (sometimes `./gradlew.bat` is required). This will download an appropriate version of Gradle for you, and use it to run the command. There is no need to have Gradle installed.

To build all the code, just run `gradle build` (that means `./gradle build` on Linux and `gradlew.bat build` on Windows). All run or deploy commands will build the code automatically before running.

### Simulation
To run the headless simulation, which will drive a vehicle forwards and output a CSV of the vehicles' routes, run `gradle :simulation:run`.

To run the simulation with a GUI, run `gradle jfxRun`. It can be debugged by connecting a debugger to port 5005 if needed.

### LEGO Mindstorms
To deploy the code to a Mindstorms device that is connected via USB, execute `gradle :lego:deployUSB`.

To deploy the code to all Mindstorms devices on the same WiFi network as the computer, execute `gradle :lego:deployAll`. It will print out all devices that it has deployed to. Occasionally it will fail to detect a device because that device had stopped advertising itself at the time that the command was run. This can usually be fixed by just running it again.

To deploy the code to a specific device on the same wifi network, execute `gradle :lego:deployWifi  -Pip=10.0.2.2` (replacing with the appropriate IP). You can get a list of devices on the current network by running `gradle :lego:detectWifiDevices`.

If the robots are running away, and need to be stopped (or if red lights come on and they turn evil), you can run
```bash
echo -n -e \\x00\\x00\\x00\\x0C\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00 | nc -4u -w1 10.0.2.255 5187
```
on Linux (and probably MacOS) to emergency stop all vehicles on the network. If you're running Windows, you can try the Linux subsystem, but otherwise you're doomed.

## Testing
All of the unit tests in the project can be run using `gradle test`. For a more comprehensive check, which includes code style validation too, you can run `gradle check`.

The tests for just one subproject can be run using `gradle :simulation:test` (replacing `simulation` as appropriate).

Gradle will try to remember which tests have been run before, and won't rerun them if it doesn't think the code has changed. To force it to rerun all tests, use `gradle clean test` to remove all cached builds and changes before testing.
