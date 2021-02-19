package com.test.agent;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.HashMap;
import java.util.Map;

/**
 * 代理逻辑类
 */
public class AgentApplication {

    public static void agentmain(String agentArgs, Instrumentation inst)
            throws UnmodifiableClassException, IOException, ClassNotFoundException {
        System.out.println("Agent Main called");
        System.out.println("Agent agentArgs : " + agentArgs);

        // 将已加载的类转为map集合
        Class[] allLoadedClasses = inst.getAllLoadedClasses();
        Map<String,Class> classMap = new HashMap<>();
        for (Class allLoadedClass : allLoadedClasses) {
            classMap.put(allLoadedClass.getName(),allLoadedClass);
        }

        // 将需要热更新的类转为map集合
        Map<String, File> fileMap = getFileMap(agentArgs);
        for (Map.Entry<String, File> stringFileEntry : fileMap.entrySet()) {
            String className = stringFileEntry.getKey();
            File classFile = stringFileEntry.getValue();

            // 如果需要更新的类在已加载的类中才进行更新，如果该类还没被加载过，则无法进行热更新
            if (classMap.containsKey(className)) {
                byte[] bytes = fileToBytes(classFile);
                Class oldClass = classMap.get(className);
                System.out.println("执行热更新：" + className);
                ClassDefinition classDefinition = new ClassDefinition(oldClass, bytes);
                inst.redefineClasses(classDefinition);
            }else {
                System.out.println("vm 中未加载过该类：" + className);
            }
        }
    }

    /**
     * 获取文件map集合
     * @param baseDir
     * @return key：包名  value：file对象
     */
    private static Map<String,File> getFileMap(String baseDir){
        Map<String,File> retVal = new HashMap<>();

        // 处理文件路径名
        if (!baseDir.endsWith("/")){
            baseDir += "/";
        }

        File fileFolder = new File(baseDir);
        if(fileFolder.isDirectory()){
            Map<String,File> result = new HashMap<>();
            fileFolderRecurrence(fileFolder,result);

            for (Map.Entry<String, File> stringFileEntry : result.entrySet()) {
                String classFilePath = stringFileEntry.getKey();
                File classFile = stringFileEntry.getValue();
                retVal.put(fileName2PackageName(baseDir,classFilePath),classFile);
            }
        }else {
            throw new RuntimeException("请传入一个文件夹");
        }

        return retVal;
    }

    /**
     * 文件路径名转包名
     * @param baseDir 根路径
     * @param classFilePath 文件路径
     * @return
     */
    private static String fileName2PackageName(String baseDir,String classFilePath){
        String replace = classFilePath.replace(baseDir, "").replace("/", ".").replace(".class","");
        return replace;
    }

    /**
     * 递归寻找文件
     * @param baseFile
     * @param result key:文件路径   value：文件对象
     */
    private static void fileFolderRecurrence(File baseFile,Map<String,File> result){
        File[] files = baseFile.listFiles();
        for (File file : files) {
            if (file.isDirectory()){
             fileFolderRecurrence(file,result);
            }else {
                result.put(file.getPath(),file);
            }
        }
    }

    /**
     * 文件转字节数组
     * @param file
     * @return
     * @throws IOException
     */
    public static byte[] fileToBytes(File file) throws IOException {
        FileInputStream in = new FileInputStream(file);
        byte[] bytes = new byte[in.available()];
        in.read(bytes);
        in.close();
        return bytes;
    }
}
