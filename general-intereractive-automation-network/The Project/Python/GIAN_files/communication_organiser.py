import threading
import time


class CommOrg:
    def __init__(self, arduino, detector=None, java=None, joy=None):
        self.detector = detector
        self.java = java
        self.arduino = arduino
        self.joy = joy
        self.coordsLock = threading.Lock()
        self.coords = [0,0,0,0]


    def start_communication(self, with_java):
        if not with_java:
            comThread = threading.Thread(target=self.comm_arduino_only)
            comThread.start()
        else:
            comThread = threading.Thread(target=self.communicate)
            comThread.daemon =True
            comThread.start()


    def comm_arduino_only(self):
        while True:
            if self.detector is not None:
                with self.detector.handPosLock:
                    handPos = self.detector.handPos
                self.arduino.send([handPos[0], handPos[1], 0, 0])
            elif self.joy is not None:
                coord = self.joy.getCoord()[0]
                self.arduino.send([coord[0], coord[1], 0, 0])
            # self.arduino.send_set_of_coords(self.arduino.coord_to_4_LEDS(handPos))
            # time.sleep(0.001)

    def communicate(self):
        arduinoThread = threading.Thread(target=self.comm_arduino_with_robot)
        arduinoThread.start()
        coords = [[0,0,0,0], 3]
        handPos = [0,0]
        joyCoord = [[0,0],0,0]
        # l = self.java.gateway.jvm.java.util.ArrayList()
        l = "11,11"
        while True:
            try:
                map = self.java.pop_from_stack()

                robotPos = map.get("robotInfo").split(',') # x,y,orientation, options
                if self.detector is not None:
                    with self.detector.handPosLock:
                        handPos = self.detector.handPos
                    # if coords[1] != 0:
                    coords = self.arduino.showCoords(handPos, [int(robotPos[0]), int(robotPos[1]), int(robotPos[2]),
                                                           int(robotPos[3])])
                elif self.joy is not None:
                    joyCoord = self.joy.getCoord()
                    handPos = joyCoord[0]
                    coords = self.arduino.showCoords(handPos, [int(robotPos[0]), int(robotPos[1]), int(robotPos[2]),
                                                               int(robotPos[3])])
                with self.coordsLock:
                    self.coords = coords[0]

                try:
                    l = str(handPos[0])+","+str(handPos[1])
                except:
                    print("couldn't clear the list")

                if self.detector is not None:
                    #robotLoc = self.arduino.make_robot_occ_space(robotPos)

                    with self.detector.showImgLock:
                        self.detector.showImg = map.get("img")

                    with self.detector.labelLock:
                        guess = self.detector.label+''

                    map["guess"] = guess
                    # if guess == "hand_ok":
                    #     coords[1] = 1
                    #     coords[0] = [0,0,3,4]
                map["joyOK"] = joyCoord[1]
                map["handPos"] = l

                self.java.push_to_stack(map)
            except:
                pass

    def comm_arduino_with_robot(self):
        while True:
            with self.coordsLock:
                coord = self.coords
            self.arduino.send(coord)

