import cv2
import time
import tkinter as tk

'''
05.Jan.2019     Vahe Grigoryan

The script opens a web Camera and when letter 'q' is pressed
it stores the frame that the web camera captured
'''

def saveImage(frame, i):
    isSaved = cv2.imwrite(str(i) + '.jpg', frame)
    print(isSaved)

def startCapture(video_capture):
    i=0
    while True:
        # time.sleep(0.5)
        ret, frame = video_capture.read()
        cv2.imshow('Detected', frame)
        if cv2.waitKey(1) & 0xFF == ord('q'):
            i = i + 1
            saveImage(frame, i)
            # break

def selectMainCam():
    video_capture = cv2.VideoCapture(0)
    tk._default_root.destroy()
    print("Internal Camera selected")
    startCapture(video_capture)


def selectSecondaryCam():
    video_capture = cv2.VideoCapture(1)
    tk._default_root.destroy()
    print("External Camera selected")
    startCapture(video_capture)

def createDialogBox():
    tk.Button(text='Internal', command=selectMainCam, height=3, width=21).pack()
    tk.Button(text='External', command=selectSecondaryCam, height=3, width=21).pack()
    tk._default_root.title("Select Camera")
    tk.mainloop()

if __name__ == '__main__':
    createDialogBox()



