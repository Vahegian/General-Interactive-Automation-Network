import threading
import cv2
import tkinter as tk

'''
30 Dec. 2018 : Vahe Grigoryan

The class constructor asks user to chose a camera option and
with a new thread it captures frames from the selected camera.
The captured frames can be obtained with a call to "getFrame" method
'''




class OpenCamera(threading.Thread):
    def __init__(self, JIO):
        self.jio = JIO
        threading.Thread.__init__(self)
        self.lock = threading.Lock()
        self.img = None
        self.video_capture = None
        # self.createDialogBox()
        if JIO == None:
            self.createDialogBox()
        else:
            self.chose_the_right_camera()

    def chose_the_right_camera(self):
        with self.jio.stack_lock:
            choice = self.jio.pop_from_stack()
        if choice.get("cam") == 1:
            self.selectCam(1)
        else:
            self.selectCam(0)

    def selectCam(self, id):
        self.video_capture = cv2.VideoCapture(id)
        print("Camera {} selected".format(id))

    def createDialogBox(self):
        tk.Button(text='Internal', command=self.selectMainCam, height=3, width=21).pack()
        tk.Button(text='External', command=self.selectSecondaryCam, height=3, width=21).pack()
        tk._default_root.title("Select Camera")
        tk.mainloop()

    def selectMainCam(self):
        self.video_capture = cv2.VideoCapture(0)
        tk._default_root.destroy()
        print("Internal Camera selected")

    def selectSecondaryCam(self):
        self.video_capture = cv2.VideoCapture(1)
        tk._default_root.destroy()
        print("External Camera selected")

    def getFrame(self):
        self.lock.acquire()
        picture = self.img
        self.lock.release()
        return picture

    def run(self):
        while True:
            ret, frame = self.video_capture.read()
            # self.img = cv2.resize(frame, (100, 100), interpolation=cv2.INTER_AREA)
            self.img = cv2.resize(frame, (640, 480), interpolation=cv2.INTER_AREA)
            self.lock.acquire()
            self.img = frame
            self.lock.release()