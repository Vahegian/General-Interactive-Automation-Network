import sys

import cv2
from darkflow.net.build import TFNet
import time
import threading
from GIAN_files.CameraIO import OpenCamera
import os
from GIAN_files.java_communicator import JIO
from py4j.java_gateway import JavaGateway

'''
05.Jan.2019  Vahe Grigoryan

The file will use TFNet to do object detection on images captured from web Camera.
From the prediction the 'label' and 'hand position' will be stored in the class, which then 
can be accessed by other classes to be processed.

Further processing on the data will be performed to find more optimal coordinates. 
'''


class Detector(threading.Thread):

    def __init__(self):
        super().__init__()
        # print('current dir: > ', os.listdir(os.curdir))
        self.label = 'Dont Know'
        self.conflevel = '100'
        self.handPos = [0,0]
        self.robotPos = "7,2,3,0"
        self.gotResultFromYolo = False

        self.tl = None  # top left corner
        self.br = None  # bottom right corner
        self.img = None

        self.javaMap = None

        self.x_list, self.y_list = self.make_image_pieces()

        self.imgLock = threading.Lock()
        self.tlbrLock = threading.Lock()
        self.printLock = threading.Lock()
        self.gotResultFromYoloLock = threading.Lock()
        self.labelLock = threading.Lock()
        self.handPosLock = threading.Lock()
        self.robotPosLock = threading.Lock()
        self.javaMapLock = threading.Lock()

        self.showImgLock = threading.Lock()
        self.showImg = 0

        # if run_inplace:
            # self.jio = None
        # else:
        #     self.jio = JIO()

    '''     OBJECT DETECTION METHODS     '''

    def setup_detector(self, cam):
        # self.cam = OpenCamera(self.jio)
        # self.cam.start()
        print("10000000000000000000000000000000")
        self.cam = cam
        self.yoloNet = self.open_cnn_model()

        cv2showRectsThead = threading.Thread(target=self.cv2Drawrects)
        cv2showRectsThead.daemon = True
        cv2showRectsThead.start()

        with self.printLock:
            print("starting yoloDetector")

        yolopredictThread = threading.Thread(target=self.yoloGetPredictionAndLable)
        yolopredictThread.daemon = True
        yolopredictThread.start()

        # jioThread = threading.Thread(target=self.talk_to_java)
        # jioThread.daemon = True
        # jioThread.start()

    def open_cnn_model(self):
        options = {
            'model': 'cfg/giancfgs/tiny-yolo-gian-voc_2c.cfg',
            'load': 2125,
            'threshold': 0.42,
            'gpu': 0.7
        }
        return TFNet(options)

    def yoloGetPredictionAndLable(self):
        # time.sleep(0.01)
        with self.printLock:
            print("yoloGetPredictionAndLable started")
        while True:
            try:
                with self.imgLock:
                    img = self.img

                result = self.getYoloPredict(img)

                with self.tlbrLock:
                    self.tl = (result[0]['topleft']['x'], result[0]['topleft']['y'])
                    self.br = (result[0]['bottomright']['x'], result[0]['bottomright']['y'])

                with self.labelLock:
                    self.label = result[0]['label']
                    # with self.printLock:
                    #     print(self.label)
                # self.conflevel = result[0]['confidence']
                with self.gotResultFromYoloLock:
                    self.gotResultFromYolo = True
                # print(result)
            except:
                with self.gotResultFromYoloLock:
                    self.gotResultFromYolo = False
                # print("rect beyond limits")
            # time.sleep(0.01)
            # with self.printLock:
            #     print(self.label)
            #     print("FPS= {:.1f}".format(1/(time.time()-loopStartTime)), self.label, self.conflevel)

    def getYoloPredict(self, img):
        return self.yoloNet.return_predict(img)



    def cv2Drawrects(self):
        # img = None
        # tl =None
        # br =None
        # handPos = None
        # gotYoloGuess = None
        fps = "None"
        rect_opacity = 0.4
        with self.printLock:
            print("CV2Drawrects started")
        # i = 0
        while True:
            loopStartTime = time.time()
            img = self.cam.getFrame()
            with self.imgLock:
                # self.img = self.cam.getFrame()
                self.img = img
                # print(img.shape)

            with self.tlbrLock:
                tl = self.tl
                br = self.br

            # with self.handPosLock:
            #     handPos = self.handPos

            with self.gotResultFromYoloLock:
                gotYoloGuess = self.gotResultFromYolo

            with self.showImgLock:
                showImg = self.showImg
            # print("beggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg")
            if showImg==0:
                if gotYoloGuess:
                    # unedited_img = img.copy()
                    try:
                        cv2.rectangle(img, tl, br, (255, 255, 255), 1)
                        # img = cv2.addWeighted(unedited_img, rect_opacity, img, 1 - rect_opacity, 0)
                        # print(cv2.imwrite('test/'+str(loopStartTime) + '.jpg', img))
                    except:
                        print("using old img")
                        # img = unedited_img
            # if showImg==0:
            #     print("midddddddddddddddddddddddddddddddddddddddddddddddddddddddd")
            #     img = cv2.putText(img, str(handPos), (0, 10), cv2.FONT_HERSHEY_COMPLEX, 0.5,
            #                       (0, 0, 0), 1)

                cv2.putText(img, fps, (0, 100), cv2.FONT_HERSHEY_COMPLEX, 0.5,
                                  (0, 255, 0), 1)
                # print("ennnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn")
                cv2.imshow('Detector', img)
                if cv2.waitKey(1) & 0xFF == ord('q'):
                    break
            else:
                cv2.destroyAllWindows()
            # print(" FPS= {:.2f}".format(1 / (time.time() - loopStartTime)))
            time.sleep(0.1)
            fps = " FPS= {:.2f}".format(1 / (time.time() - loopStartTime))


            #loopStartTime = time.time()

            # print("FPS= {:.1f}".format(1/(time.time()-loopStartTime)), self.label, self.conflevel)

    '''     FIND DETECTED OBJECTS LOCATION ON IMAGE AND EXTRACT USEFULL COORDINATES   '''

    # processing coordinates
    def run(self):
        while True:
            time.sleep(0.1)
            with self.labelLock:
                label = self.label
            with self.tlbrLock:
                tl = self.tl
                br = self.br
            try:
                if label == "index_finger":
                    pos = self.chooseTheRightAngel(tl, br)
                    # pos = self.transformcoords(pos[0], pos[1])
                    pos = self.find_hand_pos(pos[0], pos[1])
                    if pos is not None:
                        with self.handPosLock:
                            self.handPos = pos
            except:
                pass

    def chooseTheRightAngel(self, tl, br):
        if tl[0] > 320:
            if tl[1] > 240:
                return (round(br[0]), round(br[1]))
            else:
                return (round(br[0]), round(tl[1]))
        elif tl[1] > 240:
            return (round(tl[0]), round(br[1]))
        else:
            return (round(tl[0]), round(tl[1]))

    def make_image_pieces(self):
        img_x = 640.0 / 12.0
        img_y = 480.0 / 12.0
        x_list = []
        y_list = []
        for i in range(0, 13):
            x_list.append(i * img_x)
            y_list.append(i * img_y)

        return x_list, y_list

    def find_hand_pos(self, x, y):
        for i in range(0, len(self.x_list) - 1):
            for j in range(0, len(self.y_list) - 1):
                if (x >= self.x_list[i] and x <= self.x_list[i + 1]) and (y >= self.y_list[j] and y <= self.y_list[j + 1]):
                    # by -11 and * -1 I switched the coordinates now (11,11) is (0,0)
                    return [(i-11)*-1, (j-11)*-1]


    def CalcRectsArea(self, tl, br):
        xSide = br[0] - tl[0]
        ySide = br[1] - tl[1]
        return round(xSide * ySide)
