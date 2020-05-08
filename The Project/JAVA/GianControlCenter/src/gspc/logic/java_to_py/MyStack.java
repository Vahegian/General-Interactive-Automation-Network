package gspc.logic.java_to_py;

import java.util.*;

public class MyStack {
    private String TAG = "MyStack << ";
    private Stack stack;
    private String[] data = {"0","0%0","[]"};

    private Map<String, Object> m;

    public MyStack() {
        stack = new Stack();
        m = new HashMap<String, Object>();
        m.put("cam", 0);
        m.put("handPos", "0,0");
        m.put("img", 0);
        m.put("guess", "None");
        m.put("robotInfo", "7,2,3,0");
        m.put("joyOK", 0);
        stack.push(m);
    }

    public Stack getStack() {
        return stack;
    }

    public Map<String, Object> popData(){
        try {
            if (stack.isEmpty()){
//                pushData(this.m);
                return this.m;
            }
            else {
                this.m = (Map<String, Object>) stack.pop();
                return this.m;
            }
        }catch (Exception e){
            System.err.println(TAG+e);
            return this.m;
        }
    }

    public void pushData(Map<String, Object> data) {
        try {
            if (!stack.isEmpty()) {
                stack.pop();
                stack.push(data);
                this.m = data;
            } else {
                stack.push(data);
                this.m = data;
            }
        }catch (Exception e){
            System.err.println(TAG+e);
        }

    }
}