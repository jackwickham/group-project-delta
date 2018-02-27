# Simulation GUI

## Overview

The application is divided into two panes, the side panel on the left and the
main view on the right.

### Side Panel

This pane contains a tabbed layout, where the current tab can be changed by
clicking the headers at the top of this pane.

#### Properties Tab

Upon selection of an object the "Properties" tab will display the object's
details, such as position and velocity.

#### Network Tab

In the "Network" tab a list of network messages is shown. Along the top of the 
tab are filters for the displayed messages, a tick indicates that that type of
message is to be shown.

The filters are:
-	**Emergency**: all messages tagged as emergency messages.
-	**Data**: all heartbeat data messages, which broadcast a vehicle's current
	state.
-	**Merges**: all messages pertaining to the merging of platoons.

>	**Note**: the filters will only affect messages received after the filter
>	was changed, previously received messages are not filtered.

New messages will be added to the top of the list.

Messages are of the format `[@<Time> <Vehicle ID>] <Message>`.

### Main View

The main view shows the current simulation state. It can be navigated using
click-and-drag to pan the view, and scrolling to zoom.

New objects can be added by right-clicking in the main view; the status of
existing objects can be acquired by left-clicking on the object and navigating
to the "Network" tab.

The following shortcut keys are available:
-	`P`: pause/unpause
-	`;`: step
-	`+`/`=`: reduce time warp factor
-	`-`/`_`: increase time warp factor

Clicking `P` will toggle the simulation between paused and playing the
simulation.

#### Platoons

Platooning information is indicated by lightly-coloured circles underneath the
cars. The colour of the circle indicates the platoon, all cars with the same
colour are part of the same platoon. If there is a ring around the circle, then
this car is the leader of its platoon.

Hovering over a car will provide information relevant to platooning.

#### Adding a New Car

1.	Right-click in the main view and select "Add object", this will open dialog
2.	Fill in the desired values for:
	-	the car's wheel base (distance between front and rear axles);
	-	the x- and y-coordinate of the car (initially the position clicked); and
	-	the controlling algorithm.
3.	Click "Confirm" to create a car, or "Cancel" at anytime to return to the
	main view without creating a car.
	
>	**Note**: a car with the manual control algorithm does not partake in any
>	communication over the network, and hence will not merge with other
>	platoons.

#### Controlling vehicles

The currently selected vehicle can be controlled using the W, A, S and D keys:
-	`W`: accelerate forward
-	`S`: brake
-	`A`: turn left
-	`D`: turn right

>	**Note**: only the leader of a platoon is likely to be controllable, because
>	the followers will make driving decisions based on their internal state
>	which will override user input.
