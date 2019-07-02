package com.lairdtech.lairdtoolkit.serialdevice;

public class decoder {

    public int decoderData (String s){
        int n = Integer.parseInt(s);
        int data = n % 10000;
        return data ;
    }

    public int decoderChannel (String s){
        int n = Integer.parseInt(s);
        int data = n / 10000;
        return data ;
    }

}
