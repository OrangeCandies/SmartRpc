package org.smartRpc;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        // 服务端启动
        new ClassPathXmlApplicationContext("server-spring.xml");
    }
}
