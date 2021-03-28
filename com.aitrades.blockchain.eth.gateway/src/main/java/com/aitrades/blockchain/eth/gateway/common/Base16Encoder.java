package com.aitrades.blockchain.eth.gateway.common;


import java.nio.charset.Charset;

public class Base16Encoder {

    private static final String data = "0123456789ABCDEF";

    public static String encode(String buffer) {
        return Base16Encoder.encode(buffer.getBytes(Charset.forName("UTF-8")));
    }

    public static String encode(byte[] buffer) {
        char[] out = new char[buffer.length * 2];
        for (int i = 0, j = 0; i < buffer.length; i++) {
            out[j++] = Base16Encoder.data.charAt((buffer[i] >> 4) & 0x0F);
            out[j++] = Base16Encoder.data.charAt(buffer[i] & 0x0F);
        }
        return new String(out).toUpperCase();
    }
}
