// Rules
//======
// calculate distance
distance(LtP,LnP,HtP,LtC,LnC,HtC,D) :- D = math.sqrt(
   math.sqrt((LtP-LtC)*(LtP-LtC)) +
   math.sqrt((LnP-LnC)*(LnP-LnC)) +
   math.sqrt((HtP-HtC)*(HtP-HtC))).

// check if can safely reach a location from current position
can_reach(LtP,LnP,HtP) :- fuel(F) &
     at(LtC,LnC,HtC) &
     fuel_efficiency(E) &
     distance(LatP,LonP,HeiP,LatC,LonC,HeiC,D) &
     E/2 <= D/F.

// Initial Goals
//==============
!test.
//!initialize. // to update beliefs
//!mission. // today's mission

// Plans
//======

+!test : true <-
	//checkLocation;
	addWayPoint(20.10412,40.12043,1.04912);
	addWayPoint(19.01924,8.49281,5.10296);
	//addWayPoint(59.50293,89.20582,78.19553);
	setHome(19.01924,8.49281,5.10296);
	?home(Lt,Ln,Ht);
	goHome;
	uavasPrint(Ln).
	//!!test.

// check the systems before beginning the mission
+!initialize : true <-
     checkEnvironment;
     checkBattery;
     checkLocation;
     setHome(-30,-51.1,0).

+!mission
  :  system(ok) // believes the systems are all OK
  <- !photograph(-23.36,-48.07,100);
     goHome.

-!mission
  :  system(ok)
  <- goHome.
  
+!photograph(LtP,LnP,HtP)
  :  canReach(LtP,LnP,HtP)
  <- addWaypoint(LtP,LnP,HtP);
     !at(LtP,LnP,HtP);
     shoot. // take the photograph

// the conditions for the mission have not been met
-!photograph(LtP,LnP,HtP) : true
  <- goHome.

+!at(LtP, LnP, HtP)
  :  at(LtC, LnC, HtC) &
     (LtP \== LtC | LnP \== LnC | HtP \== HtC) &
     canReach(LtP, LnP, HtP)
  <- .wait(100); // wait 1 second
     !!at(LtP, LnP, HtP). //check again if already there

+!at(LtP, LnP, HtP) // the agent believes it is there
  :  at(LtP, LnP, HtP).