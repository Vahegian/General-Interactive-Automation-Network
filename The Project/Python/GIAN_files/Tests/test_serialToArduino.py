from unittest import TestCase
from ..arduino_communicator import SerialToArduino

class ObjectWithHandPos():
    def __init__(self):
        self.handPos = [10,9]

class TestSerialToArduino(TestCase):
    def test_creation(self):
        hp = ObjectWithHandPos()
        a = SerialToArduino(hp)
        self.assertEqual(a.message_to_send, hp.handPos)
