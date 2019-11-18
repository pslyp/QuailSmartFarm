package com.pslyp.dev.quailsmartfarm.encrypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

    private static MD5 mInstance;

    public static synchronized MD5 getInstance() {
        if(mInstance == null) {
            mInstance = new MD5();
        }
        return mInstance;
    }

    public String create(String s) {
        try {
            // Create MD5 Hash
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(s.getBytes());
            byte digest[] = md.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (byte d : digest)
                hexString.append(String.format("%02x", d));

            return hexString.toString().toUpperCase();
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
