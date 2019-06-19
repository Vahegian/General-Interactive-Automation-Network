from evdev import InputDevice, categorize, ecodes
import evdev
import threading


class JoystickIO:
    def __init__(self):
        self.coord = [[0,0],0,0]
        devices = [evdev.InputDevice(path) for path in evdev.list_devices()]
        path = ''
        for device in devices:
            print(device.path, device.name, device.phys)
            path = device.path
        try:
            self.device = evdev.InputDevice(str(path))
            print(self.device.capabilities(verbose=True))
            self.coordLock = threading.Lock()
            JoyThread = threading.Thread(target=self.listen_to_joystick)
            JoyThread.start()
        except:
            print('No Joystick is Detected')

    def listen_to_joystick(self):
        coord = [[0,0],0,0]
        for event in self.device.read_loop():
            if event.type == ecodes.EV_ABS:
                if event.code == 0: # left/right
                    coord[0][0] = 11 -self.transform(event.value)
                if event.code == 1: # up/down
                    coord[0][1] = self.transform(event.value)
                # print(self.coord)
                # print(categorize(event), event.value, event.code)
                with self.coordLock:
                    self.coord = coord
            if event.type == ecodes.EV_KEY:
                if event.code == 304:
                    coord[1] = event.value
                # print(categorize(event),event.value, event.code)
            with self.coordLock:
                self.coord = coord
                # print(self.coord)

    def transform(self, value):
        return round((value - 32767) * (11 - 0) / ((-32513) - 32767) + 0)

    def getCoord(self):
        with self.coordLock:
            return self.coord


# a=JoystickIO()