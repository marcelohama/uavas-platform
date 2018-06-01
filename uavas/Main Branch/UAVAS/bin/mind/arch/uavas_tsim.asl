// == Rules ==
distance(LtP, LnP, HtP, LtC, LnC, HtC, D) :-
	D = math.sqrt(
		math.sqrt((LtP-LtC)*(LtP-LtC)) +
		math.sqrt((LnP-LnC)*(LnP-LnC)) +
		math.sqrt((HtP-HtC)*(HtP-HtC))
).

// == Pre-beliefs ==
missionIncomplete.
notInitiated.

// == Actions ==
!init.

// == Plans ==
+!init :
	myId(ID) & notInitiated <-
		registerId(ID);
		uavasInit;
		?home(X,Y,Z);
		-notInitiated;
		!assignTeam(X,Y,Z,ID);
		!initMission(ID).
-!init :
	notInitiated <-
		!!init.

+!assignTeam(X,Y,Z,ID) :
	status(ok) & ID == 1 <- // I'm the leader
		.wait(3000);
		.print("okay, lets go boys");
		.wait(300);
		request(2, letsMoveOn);
		.wait(300);
		request(3, letsMoveOn);
		.wait(300);
		request(4, letsMoveOn);
		+checkPoint(X,Y,15);
		+destiny(-17,10,15);
		+team(ok).
+!assignTeam(X,Y,Z,ID) :
	status(ok) & ID > 1 & lastRequest(Who, What) &  What == letsMoveOn <- // We are the assistents
		.print("MK",ID,": entering in atmosphere at coordinates ",X,";",Y,";",Z);
		if (ID == 2) {
			+checkPoint(X,15,7);
			+destiny(-20,15,10);
		}
		if (ID == 3) {
			+checkPoint(X,Y,10);
			+destiny(-15,10,10);
		}
		if (ID == 4) {
			+checkPoint(X,5,7);
			+destiny(-20,5,10);
		}
		+team(ok).
-!assignTeam(X,Y,Z,ID) :
	true <-
		!!assignTeam(X,Y,Z,ID).
		
+!initMission(ID) :
	status(ok) & team(ok) & destiny(Px,Py,Pz) & checkPoint(Pox,Poy,Poz) <-
		.print("MK",ID," initiating mission");
		moveTo(Pox,Poy,Poz);
		addWayPoint(Px,Py,Pz);
		checkLocation;
		!listenToDestiny(Px,Py,Pz);
		!photograph.
-!initMission(ID) :
	missionIncomplete <-
		!!initMission(ID).

+!listenToDestiny(Px,Py,Pz) :
	location(X,Y,Z) & missionIncomplete & distance(X,Y,Z,Px,Py,Pz,D) & D < 0.75 <-
		+atLocation.
-!listenToDestiny(Px,Py,Pz) :
	true <-
		checkLocation;
		!!listenToDestiny(Px,Py,Pz).

+!photograph :
	atLocation & myId(ID) & ID > 1 <-
		.wait(7000); // taking photograph...
		.print("got the photograph");
		-missionIncomplete;
		!getBackToHome.
+!photograph :
	atLocation & myId(ID) & ID == 1 <-
		.wait(10000); // taking photograph...
		-missionIncomplete;
		.print("lets go back to home");
		inform(2,letsGoHome);
		inform(3,letsGoHome);
		inform(4,letsGoHome);
		goHome.
-!photograph :
	missionIncomplete <-
		!!photograph.

+!getBackToHome :
	lastInform(Who, What) & What == letsGoHome <-
		.print("roger that");
		goHome.
-!getBackToHome :
	true <-
		!!getBackToHome.