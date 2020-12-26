package com.bingo.classloader;

import java.io.*;

/**
 * @author: jiangjiabin
 * @date: Create in 22:12 2020/12/26
 * @description: 自定义类加载器
 * 1、继承ClassLoader
 * 2、走双亲委派 -> 覆盖findClass()
 *    不走 -> 覆盖loadClass
 *
 *
 * addition：
 *      线程上下文类加载器
 *      在Java中存在着很多的服务提供者接口SPI，全称Service Provider Interface，
 *      是Java提供的一套用来被第三方实现或者扩展的API，这些接口一般由第三方提供实现，常见的SPI有JDBC、JNDI等。
 *      这些SPI的接口（比如JDBC中的java.sql.Driver）属于核心类库，一般存在rt.jar包中，由根类加载器加载。
 *      而第三方实现的代码一般作为依赖jar包存放在classpath路径下，由于SPI接口中的代码需要加载具体的第三方
 *      实现类并调用其相关方法，SPI的接口类是由根类加载器加载的，Bootstrap类加载器无法直接加载
 *      位于classpath下的具体实现类。由于双亲委派模式的存在，Bootstrap类加载器也
 *      无法反向委托AppClassLoader加载SPI的具体实现类。在这种情况下，java提供了
 *      线程上下文类加载器用于解决以上问题。
 *
 *      线程上下文类加载器可以通过java.lang.Thread的getContextClassLoader()来获取，
 *      或者通过setContextClassLoader(ClassLoader cl)来设置线程的上下文类加载器。
 *      如果没有手动设置上下文类加载器，线程将继承其父线程的上下文类加载器，
 *      初始线程的上下文类加载器是系统类加载器（AppClassLoader），在线程中运行的代码可以通过
 *      此类加载器来加载类或资源。
 */
public class MyClassLoader extends ClassLoader {

    private String dir;


    /**
     * 指定类目录
     * @param dir 类所在目录
     */
    public MyClassLoader(String dir) {
        this.dir = dir;
    }

    /**
     * 指定类目录和父类加载器
     *
     * @param parent 父加载器
     * @param dir 类所在目录
     */
    public MyClassLoader(ClassLoader parent, String dir) {
        super(parent);
        this.dir = dir;
    }

    @Override
    protected Class<?> findClass(String name) {
        try {
            //转换为目录
            String file = dir + File.separator + name.replace(".", File.separator) + ".class";
            //构建输入流
            InputStream in = new FileInputStream(file);
            //构建字节输出流
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int len = -1;
            while ((len = in.read(buff)) != -1) {
                baos.write(buff, 0, len);
            }

            //读取到的字节码的二进制数据
            byte[] data = baos.toByteArray();

            in.close();
            baos.close();

            //将byte字节解析成虚拟机能够识别的Class对象
            return defineClass(name, data, 0, len);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        MyClassLoader myClassLoader = new MyClassLoader("E:\\SoftWare\\IDEA\\ideaProjects\\class-loader\\src\\main\\java\\com\\bingo\\classloader");
        Class<?> clazz = myClassLoader.loadClass("com.bingo.classloader.People");
        System.out.println(myClassLoader.getParent());
        System.out.println(clazz.newInstance());
    }
}
