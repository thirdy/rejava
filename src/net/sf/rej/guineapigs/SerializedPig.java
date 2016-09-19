/* Copyright (C) 2004-2007 Sami Koivu
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.sf.rej.guineapigs;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.GregorianCalendar;

public class SerializedPig extends SerializedPigParent implements Serializable {
    //int value;
    byte byteValue = 12;
    //boolean boolVal;
    //long longValue = 123456789012345678L;
    //String stringField = "asdf";
    //Object[][] bytes = {{11, 41, 96, 5, 7}, {2, 2}, {3, 3, 3}, {4, 4, 4, 4}};
    //short shortValue = 123;
    //char charValue = 'v';
    //SerializedPig next;
    public static void main(String[] args) {
        try {
            SerializedPig list1 = new SerializedPig();
            SerializedPig list2 = new SerializedPig();
            //list1.value = 17;
            //list1.next = list2;
            //list1.boolVal = true;
            //list2.value = 19;
            //list2.next = null;
            //list2.boolVal = false;

            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SerializedPig.class.getName()));
            out.writeObject(new GregorianCalendar());
            out.writeObject(list1);
            out.writeObject(list2);
            out.writeObject("break");
            out.writeBoolean(true);
            out.writeObject("break");
            out.writeBoolean(false);
            out.writeObject("break");
            out.writeByte(12);
            out.writeObject("break");
            out.writeBytes("test");
            out.writeObject("break");
            out.writeChar(13);
            out.writeObject("break");
            out.writeChars("sami");
            out.writeObject("break");
            out.writeDouble(Math.PI);
            out.writeObject("break");
            out.writeFloat(1.234f);
            out.writeObject("break");
            out.writeInt(14);
            out.writeObject("break");
            out.writeLong(15);
            out.writeObject("break");
            out.writeShort(16);
            out.writeObject("break");
            out.writeUTF("UTF");
            out.writeObject("break");
            out.writeObject("String");
            out.writeObject("String");
            out.writeUnshared("String");
            out.writeUnshared("String");
            out.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

class SerializedPigParent implements Serializable {
	public String parentString = "asdf";
	public int parentInt = 91;
}
