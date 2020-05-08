#!/usr/bin/env python3
import threading

from GIAN_files.CameraIO import OpenCamera
from GIAN_files.YoloDetector import Detector
from GIAN_files.arduino_communicator import SerialToArduino
import sys
import time
import tkinter as tk


from GIAN_files.communication_organiser import CommOrg
from GIAN_files.java_communicator import JIO
from GIAN_files.joystick_communicator import JoystickIO

'''
07.Jan.2019     Vahe Grigoryan

The class starts the 'detector' and 'SerialToArduino' classes
The 'detector' object is passed as a argument to 'SerialToArduino' class for hand coordinates. 
'''

class Start_GIAN:
    # print('current dir: > ', os.listdir(os.curdir))
    def __init__(self, run_inplace):
        # arduinoCom = SerialToArduino()
        sys.stdout = open('GLOG.txt', 'w')
        sys.stderr = open('ELOG.txt', 'w')
        self.run_inplace = run_inplace
        self.detector =None
        self.joy = None
        self.javaComm = None
        if not run_inplace:
            self.javaComm = JIO()


        self.createDialogBox()



    def createDialogBox(self):
        tk.Button(text='With Detector', command=self.with_detector, height=3, width=21).pack()
        tk.Button(text='With Joy', command=self.with_joy, height=3, width=21).pack()
        tk._default_root.title("Select Navigation Method")
        tk.mainloop()

    def with_detector(self):
        self.cam = OpenCamera(self.javaComm)
        self.cam.start()
        self.detector = Detector()
        # detector.daemon = True
        self.detector.setup_detector(self.cam)
        self.detector.start()
        print("Detector Started")

        arduinoComm = SerialToArduino()

        if self.run_inplace:
            commOrganiser = CommOrg(arduinoComm, self.detector)
            commOrganiser.start_communication(False)
        else:
            commOrganiser = CommOrg(arduinoComm, self.detector, self.javaComm)
            commOrganiser.start_communication(True)
        tk._default_root.destroy()



    def with_joy(self):
        self.joy = JoystickIO()
        print("opening Joystick")

        arduinoComm = SerialToArduino()

        if self.run_inplace:
            commOrganiser = CommOrg(arduinoComm, joy=self.joy)
            commOrganiser.start_communication(False)
        else:
            commOrganiser = CommOrg(arduinoComm, java=self.javaComm, joy=self.joy)
            commOrganiser.start_communication(True)
        tk._default_root.destroy()


if len(sys.argv) == 1:
    gestdect = Start_GIAN(True)
elif sys.argv[1] == "withStack":
    gestdect = Start_GIAN(False)