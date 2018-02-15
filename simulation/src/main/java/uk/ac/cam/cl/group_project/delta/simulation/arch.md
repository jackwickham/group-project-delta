# Simulation System Architecture

## `PhysicsBody`

A representation of an object that may exist in our simulation. This base class implements no behaviour in its `update` function.

## `KinematicBody extends PhysicsBody`

A physical object that undergoes simple kinematic motion, with velocity and acceleration.

## `PhysicsCar extends KinematicBody`

The physical component of a car, that will (in future) implement more complex friction calculations in its implementation of `update`.

## `World`

Holds a collection of `PhysicsBody`s, representing the simulated world.
