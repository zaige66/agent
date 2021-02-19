package com.test.agent;

import com.test.agent.test.TestInnerClass;

public class ClassHotLoadTest {

    public static void main(String[] args) throws InterruptedException {
        boolean flag = true;
        TestClass testClass = new TestClass();
        TestInnerClass innerClass = new TestInnerClass();
        while (flag){
            Thread.sleep(1000);
            testClass.sayHello(innerClass);
        }
    }
}
