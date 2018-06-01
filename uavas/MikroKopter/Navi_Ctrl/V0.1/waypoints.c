/*#######################################################################################*/
/* !!! THIS IS NOT FREE SOFTWARE !!!  	                                                 */
/*#######################################################################################*/
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// + Copyright (c) 2008 Ingo Busker, Holger Buss
// + Nur für den privaten Gebrauch / NON-COMMERCIAL USE ONLY
// + FOR NON COMMERCIAL USE ONLY
// + www.MikroKopter.com
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// + Es gilt für das gesamte Projekt (Hardware, Software, Binärfiles, Sourcecode und Dokumentation),
// + dass eine Nutzung (auch auszugsweise) nur für den privaten (nicht-kommerziellen) Gebrauch zulässig ist.
// + Sollten direkte oder indirekte kommerzielle Absichten verfolgt werden, ist mit uns (info@mikrokopter.de) Kontakt
// + bzgl. der Nutzungsbedingungen aufzunehmen.
// + Eine kommerzielle Nutzung ist z.B.Verkauf von MikroKoptern, Bestückung und Verkauf von Platinen oder Bausätzen,
// + Verkauf von Luftbildaufnahmen, usw.
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// + Werden Teile des Quellcodes (mit oder ohne Modifikation) weiterverwendet oder veröffentlicht,
// + unterliegen sie auch diesen Nutzungsbedingungen und diese Nutzungsbedingungen incl. Copyright müssen dann beiliegen
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// + Sollte die Software (auch auszugesweise) oder sonstige Informationen des MikroKopter-Projekts
// + auf anderen Webseiten oder sonstigen Medien veröffentlicht werden, muss unsere Webseite "http://www.mikrokopter.de"
// + eindeutig als Ursprung verlinkt werden
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// + Keine Gewähr auf Fehlerfreiheit, Vollständigkeit oder Funktion
// + Benutzung auf eigene Gefahr
// + Wir übernehmen keinerlei Haftung für direkte oder indirekte Personen- oder Sachschäden
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// + Die Portierung oder Nutzung der Software (oder Teile davon) auf andere Systeme (ausser der Hardware von www.mikrokopter.de) ist nur
// + mit unserer Zustimmung zulässig
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// + Die Funktion printf_P() unterliegt ihrer eigenen Lizenz und ist hiervon nicht betroffen
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// + Redistributions of source code (with or without modifications) must retain the above copyright notice,
// + this list of conditions and the following disclaimer.
// +   * Neither the name of the copyright holders nor the names of contributors may be used to endorse or promote products derived
// +     from this software without specific prior written permission.
// +   * The use of this project (hardware, software, binary files, sources and documentation) is only permitted
// +     for non-commercial use (directly or indirectly)
// +     Commercial use (for excample: selling of MikroKopters, selling of PCBs, assembly, ...) is only permitted
// +     with our written permission
// +   * If sources or documentations are redistributet on other webpages, out webpage (http://www.MikroKopter.de) must be
// +     clearly linked as origin
// +   * porting the sources to other systems or using the software on other systems (except hardware from www.mikrokopter.de) is not allowed
//
// +  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// +  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// +  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// +  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// +  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// +  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// +  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// +  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// +  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// +  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// +  POSSIBILITY OF SUCH DAMAGE.
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 
#include <string.h>
#include "91x_lib.h"
#include "waypoints.h"
#include "uart1.h"

// the waypoints list
#define WPLISTLEN 31

Waypoint_t WPList[WPLISTLEN];
u8 WPIndex = 0;		// list index of GPS point representig the current WP, can be maximal WPNumber
u8 POIIndex = 0;	// list index of GPS Point representing the current POI, can be maximal WPNumber
u8 WPNumber = 0;	// number of wp in the list can be maximal equal to WPLISTLEN

u8 WPList_Init(void)
{
 	return WPList_Clear();
}

u8 WPList_Clear(void)
{
	u8 i;
	WPIndex = 0;	// real list position are 1 ,2, 3 ...
	POIIndex = 0;	// real list position are 1 ,2, 3 ...
	WPNumber = 0;	// no contents
	NaviData.WaypointNumber = WPNumber;
	NaviData.WaypointIndex = WPIndex;

	for(i = 0; i < WPLISTLEN; i++)
	{
		WPList[i].Position.Status = INVALID;
		WPList[i].Position.Latitude = 0;
		WPList[i].Position.Longitude = 0;
		WPList[i].Position.Altitude = 0;
		WPList[i].Heading = 361; 		// invalid value
		WPList[i].ToleranceRadius = 0;	// in meters, if the MK is within that range around the target, then the next target is triggered
		WPList[i].HoldTime = 0;			// in seconds, if the was once in the tolerance area around a WP, this time defines the delay before the next WP is triggered
		WPList[i].Event_Flag = 0;		// future implementation
		WPList[i].Type = POINT_TYPE_WP;
	}
	return TRUE;		
}

u8 WPList_GetCount(void)
{
 	return WPNumber; // number of points in the list
}

u8 WPList_Append(Waypoint_t* pwp)
{
 	if(WPNumber < WPLISTLEN) // there is still some space in the list
	{
		memcpy(&WPList[WPNumber], pwp, sizeof(Waypoint_t)); // copy wp data to list entry										// increment list length
		WPNumber++;
		NaviData.WaypointNumber = WPNumber;
		return TRUE;
	}
	else return FALSE;
}

// returns the pointer to the first waypoint within the list
Waypoint_t* WPList_Begin(void)
{
	WPIndex = 0; // set list position invalid
	
	if(WPNumber > 0) 
	{
		u8 i;
		// search for first wp in list
		for(i = 0; i < WPNumber; i++)
		{
			if((WPList[i].Type == POINT_TYPE_WP) && (WPList[i].Position.Status != INVALID))
			{
				WPIndex = i + 1;
				break;
			}
		}
	}
	if(WPIndex) // found a WP in the list
	{
		NaviData.WaypointIndex = WPIndex;
		// update index to POI
		if(WPList[WPIndex-1].Heading < 0) POIIndex = (u8)(-WPList[WPIndex-1].Heading);
		else POIIndex = 0;	
		return(&(WPList[WPIndex-1])); // if list is not empty return pointer to first waypoint in the list		
	}
	else
	{
		POIIndex = 0;
		NaviData.WaypointIndex = WPIndex;
		return NULL;
	}
}

// returns the last waypoint
Waypoint_t* WPList_End(void)
{
	WPIndex = 0; // set list position invalid
	if(WPNumber > 0)
	{
		// search backward!
		u8 i;
		for(i = 1; i <= WPNumber; i++)
		{
			if((WPList[WPNumber - i].Type == POINT_TYPE_WP) && (WPList[WPNumber - i].Position.Status != INVALID))
			{	
				WPIndex = WPNumber - i + 1;
				break;
			}
		}
	}
	if(WPIndex) // found a WP within the list
	{
		NaviData.WaypointIndex = WPIndex;
		if(WPList[WPIndex-1].Heading < 0) POIIndex = (u8)(-WPList[WPIndex-1].Heading);
		else POIIndex = 0;	
		return(&(WPList[WPIndex-1]));
	}
	else
	{
		POIIndex = 0;
		NaviData.WaypointIndex = WPIndex;
		return NULL;	
	}
}

// returns a pointer to the next waypoint or NULL if the end of the list has been reached
Waypoint_t* WPList_Next(void)
{
	u8 wp_found = 0;
		
	if(WPIndex < WPNumber) // if there is a next entry in the list
	{
		u8 i;
		for(i = WPIndex; i < WPNumber; i++)	// start search for next at next list entry
		{
			if((WPList[i].Type == POINT_TYPE_WP) && (WPList[i].Position.Status != INVALID)) // jump over POIs
			{
			 	wp_found = i+1;
				break;
			}
		}
	}
	if(wp_found)
	{
		WPIndex = wp_found; // update list position
		NaviData.WaypointIndex = WPIndex;
		if(WPList[WPIndex-1].Heading < 0) POIIndex = (u8)(-WPList[WPIndex-1].Heading);
		else POIIndex = 0;
		return(&(WPList[WPIndex-1]));	// return pointer to this waypoint
	}
	else return(NULL);
}	
 
Waypoint_t* WPList_GetAt(u8 index)
{
	if((index > 0) && (index <= WPNumber)) return(&(WPList[index-1]));	// return pointer to this waypoint
	else return(NULL);
}

Waypoint_t* WPList_GetPOI(void)
{
	return WPList_GetAt(POIIndex);	
}

