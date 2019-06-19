import time

import serial as port
import threading
import os

'''
03.Jan.2019 : Vahe Grigoryan

This class takes a object that has an array 'handPos'
and transmits the array via serial port to connected
devices.
'''
# set access privileges of serial ports
# ls /dev/serial/by-path
# sudo chmod 666 /dev/serial/by-path/****

class SerialToArduino:
    def __init__(self):
        super().__init__()
        self.lock = threading.Lock()
        self.message_to_sendLock = threading.Lock()
        # os.chdir(os.path.expanduser("~"))
        self.port = '/dev/serial/by-path/pci-0000:00:14.0-usb-0:2:1.0'
        # os.chmod('/dev/serial/by-path/pci-0000:00:14.0-usb-0:2:1.0',666)
        try:
            self.arduinoPort = port.Serial(self.port, 9600)
        except:
            print("Nothing is Connected to Serial Port > ",self.port)

    def send(self, coord):
        # print(1)
        if (self.arduinoPort.inWaiting() > 0):
            # print(2)
            self.arduinoPort.write(coord)
            # print(3)
            # time.sleep(0.01)
            # print(4)
            received_message = self.arduinoPort.readline()
            # print(received_message)
            self.arduinoPort.flush()



    def send_set_of_coords(self, coords):
        for coord in coords:
            self.send(coord)
            # time.sleep(0.01)

    def showCoords(self, handPos, robotPos):

        robotLoc = self.make_robot_occ_space(robotPos)

        if handPos[0] in robotLoc[0] and handPos[1] in robotLoc[1]:
            return [[robotPos[0], robotPos[1], robotPos[2], robotPos[3]], 0]
            # time.sleep(0.3)
        elif robotPos[3] == 4:
            if robotPos[0] > 5:
                return [[handPos[0], handPos[1], 3, robotPos[3]], 1]
            else:
                return [[handPos[0], handPos[1], 4, robotPos[3]], 1]
        else:
            return [[handPos[0], handPos[1], 0, 0], 2]

    def make_robot_occ_space(self, robotPos):
        if robotPos[2] == 1:
            return [[i for i in range(robotPos[0]-1, robotPos[0]+1)], [i for i in range(robotPos[1]-5, robotPos[1])]]
        elif robotPos[2] == 2:
            return [[i for i in range(robotPos[0] - 1, robotPos[0] + 1)],
                    [i for i in range(robotPos[1], robotPos[1]+5)]]
        elif robotPos[2] == 3:
            return [[i for i in range(robotPos[0], robotPos[0] + 5)],
                    [i for i in range(robotPos[1]-1, robotPos[1] + 1)]]
        elif robotPos[2] == 4:
            return [[i for i in range(robotPos[0]-5, robotPos[0])],
                    [i for i in range(robotPos[1] - 1, robotPos[1] + 1)]]





# class hand:
#     def __init__(self):
#         self.handPos = [0, 0]
#         self.robotPos = ["9","9","3","0"]
#         self.handPosLock = threading.Lock()
#         self.robotPosLock = threading.Lock()

# h = hand()
#
# a = SerialToArduino()

# for o in range(0,1000):
#     print(o)
#     for i in range(0,12):
#         for j in range(0,12):
#             for l in range(0, 10):
#                 a.send([j,i,4,0])
#                 time.sleep(0.01)
