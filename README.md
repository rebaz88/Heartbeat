#Heartbeat

A simple GUI hearbeat implementation on a drone using Java.

#Feature
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

