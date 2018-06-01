#ifndef __GPS_H
#define __GPS_H

#include "ubx.h"
#include "waypoints.h"

typedef struct
{
	s16 Nick;
	s16 Roll;
	s16 Yaw;
}  __attribute__((packed)) GPS_Stick_t;

extern Waypoint_t* GPS_pWaypoint;

void GPS_Init(void);
void GPS_Navigation(gps_data_t *pGPS_Data, GPS_Stick_t* pGPS_Stick);
void CalcHeadFree(void);

#endif //__GPS_H

