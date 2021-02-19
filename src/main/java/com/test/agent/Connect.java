package com.test.agent;

import com.sun.tools.attach.VirtualMachine;

import java.util.Properties;

/**
 * 连接类，执行该main方法，将代理类加载到目标vm中去
 */
public class Connect {
    public static void main(String[] args) throws Exception {
        // 目标vm进程id，就是需要热更新的java进程id
        String pid = "96000";
        // 编写好的代理类
        String jarPath  = "/Users/kangxuan/self_workspace/agent/target/agent.jar";
        // 代理执行时的传参，这里我们需要传入准备好的class文件的文件夹
        String classFolder = "/Users/kangxuan/self_workspace/agent/hotLoad";

        VirtualMachine attach = VirtualMachine.attach(pid);
        attach.loadAgent(jarPath,classFolder);

    }
}
