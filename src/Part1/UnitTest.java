package Part1;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnitTest {

    public static void main(String[] args) {

        String a = "int";
        String b = "int";
        String c = "int";
        String d = "int1";

        /*
        Map.Entry<String,String> Pair = new AbstractMap.SimpleImmutableEntry<>("one","two");
        System.out.println(Pair.getKey()+":"+Pair.getValue());
        */

        /*
        UUID uuid1 = UUID.nameUUIDFromBytes(a.getBytes());
        UUID uuid2 = UUID.nameUUIDFromBytes(b.getBytes());
        UUID uuid3 = UUID.nameUUIDFromBytes(c.getBytes());
        UUID uuid4 = UUID.nameUUIDFromBytes(d.getBytes());
        */

        /*
        System.out.println(uuid1.toString());
        System.out.println(uuid2.toString());
        System.out.println(uuid3.toString());
        System.out.println(uuid4.toString());
        */

        /*
        System.out.println(uuid1.toString());
        String text = "test"+uuid1.toString();
        //String onlyTest = text.substring(0,4);
        //String onlyUUID = text.substring(4,text.length());
        int typeLength = text.length() - 36;
        String onlyTest = text.substring(0, typeLength);
        String onlyUUID = text.substring(typeLength, text.length());wrap1.getStringUUID()
        System.out.println(onlyTest);
        System.out.println(onlyUUID);
        */

        MyUUID wrap1 = new MyUUID(a);
        MyUUID wrap2 = new MyUUID(b);
        MyUUID wrap3 = new MyUUID(d);

        /*
        System.out.println(MyUUID.Decoder(wrap1));
        System.out.println(MyUUID.Decoder(wrap2));

        System.out.println(wrap1.getStringUUID());
        System.out.println(wrap2.getStringUUID());
        */

        //System.out.println(wrap1.toString());

        //System.out.println("UUID1 > UUID3 ? :"+wrap1.compareTo(wrap3));
        //System.out.println("UUID3 > UUID1 ? :"+wrap3.compareTo(wrap1));

        System.out.println(wrap1.toString());

    }
}
