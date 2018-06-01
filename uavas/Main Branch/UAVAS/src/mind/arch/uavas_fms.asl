// A simple example with UAVAS framework in FMS RC-Sim, with basic PRY control.

//calibrated.

// initial plans
!calibrate.
!init.

// initiates the maneuverings
+!init : calibrated <-
	pitch(5); collective(5); yaw(5); roll(5); .wait(5000);
	!takeoff(3);
	!moveTowardRoad;
	!run.
-!init : true <-
	!!init.

// plan for take-off
+!takeoff(Meters) : true <-
	collective(9); .wait((1000*Meters)/5.4);
	collective(6);
	+takeoff.

// moving to another position
+!moveTowardRoad : takeoff <-
	yaw(1); .wait(1000); yaw(5); // redirects the UAV towards the road
	pitch(6); collective(7); .wait(1000); // moves towards the road
	pitch(4); .wait(1025); pitch(5); collective(5); // stops the UAV
	.wait(10000); yaw(1); .wait(900); yaw(5); // redirects linestraigh
	+moved.

// moves along the road
+!run : moved <-
	collective(9); .wait(3000); collective(5);
	pitch(6); collective(6); .wait(2000); pitch(4); collective(7); .wait(1000); pitch(5); .wait(60000);
	pitch(5); roll(5); yaw(5); collective(5).
	

// Plan to wait calibration... It's not a correct way to do this (calibration ins't part of autonomous development),
// but for now we are using it as it is.
+!calibrate : not calibrated <-
	.wait(3000); calibrateMinValue;
	.wait( 500); calibrateMaxValue;
	.wait( 500); calibrateMinValue;
	.wait( 500); calibrateMaxValue;
	.wait(3000); calibrateMidValue;
	+calibrated.
-!calibrate : true <-
	wait(1).