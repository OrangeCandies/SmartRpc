package org.smartRpc.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServerTest {
    public static void main(String[] args) {

        new ClassPathXmlApplicationContext("server-spring.xml");
    }
}
