package com.davis.utilities.result.compare;

import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

/**
 * This software was created for
 * rights to this software belong to
 * appropriate licenses and restrictions apply.
 *
 * @author Samuel Davis created on 10/3/17.
 */
public class UtilTest {

    @Test
    public void uuidTest(){
        String name = "SamuelDavis";
        UUID samUuid = UUID.nameUUIDFromBytes(name.getBytes());
        String name2 = "SamuelDavis";
        UUID samUuid2 = UUID.nameUUIDFromBytes(name2.getBytes());
        String name3 = "SamuelDavis1";
        UUID samUuid4 = UUID.nameUUIDFromBytes(name3.getBytes());
        Assert.assertTrue(samUuid.equals(samUuid2));
        Assert.assertFalse(samUuid.equals(samUuid4));

    }


    //@Test
    public void formatTest(){
       System.out.println(tableString("Sam","Is","Awake", "Too", "Long"));
    }

    private String tableString(String... strings){
        String fromatString= "%s15";


        for(String s : strings){
            fromatString = fromatString+"%15";
        }
        return String.format(fromatString, strings);
    }
}
