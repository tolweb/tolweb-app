/*
 * ObjectCloner.java
 *
 * Created on July 29, 2003, 1:38 PM
 */

package org.tolweb.treegrow.main;

import java.io.*;

/**
 * Creates a deep copy of an object by using ObjectOutputStream and 
 * ObjectInputStream
 */
public class ObjectCloner {
   // so that nobody can accidentally create an ObjectCloner object
   private ObjectCloner(){}
   
   /**
    * Returns a deep copy of an object
    *
    * @param oldObj The object to copy
    * @return The new deep copy of that object
    */
   public static Object deepCopy(Object oldObj) throws Exception {
      ObjectOutputStream oos = null;
      ObjectInputStream ois = null;
      try {
         ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
         oos = new ObjectOutputStream(bos); 
         // serialize and pass the object
         oos.writeObject(oldObj);   
         oos.flush();
         
         ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray()); 
         ois = new ObjectInputStream(bin);
         // return the new object
         return ois.readObject(); 
      } catch (Exception e) {
         System.out.println("Exception in Cloning Object = " + e);
         throw(e);
      } finally  {
          if (oos != null) {
              oos.close();
          }
          if (ois != null) {
              ois.close();
          }
      }
   }
  
}

