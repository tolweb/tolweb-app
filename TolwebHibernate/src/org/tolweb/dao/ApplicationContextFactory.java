/*
 * ApplicationContextFactory.java
 *
 * Created on April 28, 2004, 10:13 AM
 */
package org.tolweb.dao;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;


/**
 * @author Richard Hightower (portions modified by Danny Mandel)
 * ArcMind Inc. http://www.arc-mind.com
 */
public class ApplicationContextFactory {
    private static String filename = null;
    private static boolean isClasspath;
    private static ApplicationContext context;

    public static void init(String f){
        init(f, true);
    }
    
    public static void init(String f, boolean isClp) {
        filename = f;
        isClasspath = isClp;
    }

    public static ApplicationContext getApplicationContext(){
        if (isClasspath) {
            if (context == null) {
                context = new ClassPathXmlApplicationContext(filename);
            }            
            return context;
        } else {
            if (context == null) {
                context = new FileSystemXmlApplicationContext(filename);
            }            
            return context;
        }
    }
}

