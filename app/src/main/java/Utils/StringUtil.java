package Utils;

import java.io.*;
import java.util.Random;

public class StringUtil {
    //converts input stream into a string
    public static String getStringFromInputStream(InputStream is) throws IOException {
        StringBuilder sbuf = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sbuf.append(buf, 0, len);
        }
        return sbuf.toString();
    }

//    private static String getRandomID() {
//        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz";
//        StringBuilder salt = new StringBuilder();
//        Random rnd = new Random();
//        while (salt.length() < 8) { // length of the random string.
//            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
//            salt.append(SALTCHARS.charAt(index));
//        }
//        String saltStr = salt.toString();
//        return saltStr;
//    }

    public static void writeStringToStream(String resp, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(resp);
        sw.flush();
    }
}
