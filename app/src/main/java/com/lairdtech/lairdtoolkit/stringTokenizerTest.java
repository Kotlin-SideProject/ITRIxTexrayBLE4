package com.lairdtech.lairdtoolkit;

import com.lairdtech.lairdtoolkit.serialdevice.decoder;

import java.util.StringTokenizer;

public class stringTokenizerTest {
    public static void main(String[] args) {
        String s0 = "";
        String s1 = "2209\n12103\n10002\n132";
        String s2 = "78\n13281\n10003\n23287";
        String s3 = "\n20005\n20000\n23285\n2";
        String s4 = "2233\n20005\n20000\n23";
        String dataReceived = s0 + s1;
        StringTokenizer st  = new StringTokenizer(dataReceived,"\n");
//        System.out.println("dataReceived" + dataReceived);
        while (st.hasMoreTokens()){
            String s = st.nextToken();
            if(s.length()>7){
                s="0";
            }
            if (s.length() == 5) {
                decoder d = new decoder(s);
//                System.out.println("Next token : " + s);
                System.out.println("decoderData\t" + (int) d.decoderData());
            }else if(s.length() < 5){
                s0 = s;
                System.out.println("s0 = " + s);
            }
        }

        dataReceived = s0 + s2;
        st  = new StringTokenizer(dataReceived,"\n");
        while (st.hasMoreTokens()){
            String s = st.nextToken();
            if (s.length() == 5) {
                decoder d = new decoder(s);
//                System.out.println("Next token : " + s);
                System.out.println("decoderData\t" + (int) d.decoderData());
                s0 = "";
            }else if(s.length() < 5){
                s0 = s;
                System.out.println("s0 = " + s);
            }
        }

        dataReceived = s0 + s3;
        st  = new StringTokenizer(dataReceived,"\n");
        while (st.hasMoreTokens()){
            String s = st.nextToken();
            if (s.length() == 5) {
                decoder d = new decoder(s);
//                System.out.println("Next token : " + s);
                System.out.println("decoderData\t" + (int) d.decoderData());
            }else if(s.length() < 5){
                s0 = s;
                System.out.println("s0 = " + s);
            }
        }

        dataReceived = s0 + s4;
        System.out.println("4 dataReceived" + dataReceived);
        st  = new StringTokenizer(dataReceived,"\n");
        while (st.hasMoreTokens()){
            System.out.println("hasMoreTokens?"+st.hasMoreTokens());
            String s = st.nextToken();
            if (s.length() == 5) {
                decoder d = new decoder(s);
//                System.out.println("Next token : " + s);
                System.out.println("decoderData\t" + (int) d.decoderData());
            }else if(s.length() < 5){
                s0 = s;
                System.out.println("s0 = " + s);
            }
        }

    }
}
