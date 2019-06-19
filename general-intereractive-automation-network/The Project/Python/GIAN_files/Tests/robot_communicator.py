import bluetooth
import time
import threading



class RobotBluetoothCom:
    def __init__(self):
        self.addr = "00:16:53:0B:BC:F4"
        self.port = 1
        self.sock=bluetooth.BluetoothSocket(bluetooth.RFCOMM )
        self.sock.connect((self.addr, self.port))


    def connect(self):
        self.sock.connect((self.addr, self.port))

    def sendToRobot(self, coords):
        # self.send("hi")
        self.sock.send(str.encode('::'+str(coords[0])+','+str(coords[1]), encoding='utf-8'))


    def receiveFromRobot(self):
        sent = str(self.sock.recv(64).decode("utf","ignore"))
        sent = sent.split(':')[1].split(',')
        return [int(sent[0]), int(sent[1]), int(sent[2]), int(sent[3])]

    def send(self, coords):
        t = threading.Thread(target=self.sendToRobot, args=(coords,))
        t.daemon =True
        t.start()

    def close_socket(self):
        self.sock.close()


# sock.send("hello")

# sock.send('o')

c = RobotBluetoothCom()

while True:
    print(1)
    # c.connect()
    print(c.receiveFromRobot())
    # time.sleep(0.1)
    # x = input("x")
    # y = input("y")
    print(2)
    # c.send([10,9])
    # c.sendToRobot([9,9])
    c.sendToRobot([10,9])

    # c.close_socket()
    print(3)
    time.sleep(0.1)

# c.sock.close()
