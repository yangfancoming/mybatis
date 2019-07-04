
package org.apache.ibatis.submitted.serializecircular;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class UtilityTester {

  public static void serializeAndDeserializeObject(Object myObject) {

    try {
      deserialzeObject(serializeObject(myObject));
    } catch (IOException e) {
      System.out.println("Exception: " + e.toString());
    }
  }

  private static byte[] serializeObject(Object myObject) throws IOException {
    try {
      ByteArrayOutputStream myByteArrayOutputStream = new ByteArrayOutputStream();

      // Serialize to a byte array
      try (ObjectOutputStream myObjectOutputStream = new ObjectOutputStream(myByteArrayOutputStream)) {
        myObjectOutputStream.writeObject(myObject);
      }

      // Get the bytes of the serialized object
      byte[] myResult = myByteArrayOutputStream.toByteArray();
      return myResult;
    } catch (Exception anException) {
      throw new RuntimeException("Problem serializing: " + anException.toString(), anException);
    }
  }

  private static Object deserialzeObject(byte[] aSerializedObject) {
    // Deserialize from a byte array
    try (ObjectInputStream myObjectInputStream = new ObjectInputStream(new ByteArrayInputStream(aSerializedObject))) {
      return myObjectInputStream.readObject();
    }
    catch (Exception anException) {
      throw new RuntimeException("Problem deserializing", anException);
    }
  }

}
