package com.test.agent;

import com.test.agent.test.TestInnerClass;

public class TestClass {


    public void sayHello(TestInnerClass innerClass){
        System.out.println("hi");
        innerClass.sayInner();
    }
}
