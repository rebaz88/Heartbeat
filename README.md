#Heartbeat

A hearbeat implementation on a drone.

#Quality Attributes
- RQ1: The system shall be able to communicate. If the drone communication is lost, the drone shall be able to go back to the previous known locations.
- RQ2: The system shall be able to provide backup process when the drone flight system is down so that it can control the movement of the drone with in the flying zone untill the main process back to work again

#Features
- The drone sends its location to the controller
- The controller share listening to the notifications and location from the drone

#Installation & Running
- Clone the repository.
- javac *.java
- java RemoteController

######Open a new terminal browse to the same directory
- java DroneStarter

#Usage:

- Press S in order to switch the drone to move out of the zone
- Press T in order to move the drone again. The contrller should not be down
- Terminate the controller
  - If the drone is out of the zone, it should automatically return to the zone
  - If the drone is in the zone, it should remain in it is place and an alarm will
  be shown in the middle of the drone as a big red circle.

