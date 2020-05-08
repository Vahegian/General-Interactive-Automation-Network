from py4j.java_gateway import JavaGateway
import threading
import time

class JIO:
    def __init__(self):
        '''
        The method opens communication with java gateway to access java map stored
        on stack.
        '''
        self.stack_lock = threading.Lock()
        self.gateway = JavaGateway()
        self.stack = self.gateway.entry_point.getStack()
        # self.m = self.pop_from_stack()
        self.m = None

    def push_to_stack(self, item):
        '''
        If stack is empty new map will be pushed in it,
        otherwise existing map will be replaced
        '''
        try:
            if len(self.stack) < 1:
                self.stack.push(item)
            else:
                self.stack.pop()
                self.stack.push(item)
        except:
            print("couldn't push to stack")

    def pop_from_stack(self):
        '''
        If map is present in the stack it will be obtained and returned,
        otherwise previously obtained map will be returned
        '''
        try:
            if len(self.stack) > 0:
                self.m = self.stack.pop()
                return self.m
            else:
                # self.push_to_stack(self.m)
                return self.m
        except:
            print("couldn't pop map")
            return self.m

    def get_jvm(self):
        # returns running java virtual machine
        return self.gateway


# j=JIO()
# i = 0
# while True:
#     m = j.pop_from_stack()
#     print(i, ">> ",m)
#     i+=1
#     l = j.gateway.jvm.java.util.ArrayList()
#     l.add(i-1)
#     l.add(i)
#     m["handPos"] = l
#
#     time.sleep(0.1)