from unittest import TestCase
from ..YoloDetector import Detector

'''
06.Jan.2019     Vahe Grigoryan

This class tests methods from 'Detector' class
'''

class TestDetector(TestCase):

    def test_chooseTheRightAngel(self):
        d = Detector()
        br = [10, 12]
        tl = [321, 241]
        self.assertEqual(d.chooseTheRightAngel(tl,br), (round(br[0]), round(br[1])))

        tl = [321, 0]
        self.assertEqual(d.chooseTheRightAngel(tl, br), (round(br[0]), round(tl[1])))

        tl = [0, 0]
        self.assertEqual(d.chooseTheRightAngel(tl, br), (round(tl[0]), round(tl[1])))

        tl = [0, 241]
        self.assertEqual(d.chooseTheRightAngel(tl, br), (round(tl[0]), round(br[1])))

    def test_transformcoords(self):
        d = Detector()
        self.assertEqual(d.transformcoords(640,20000), (0,0))
        self.assertEqual(d.transformcoords(17,87000), (17,17))

