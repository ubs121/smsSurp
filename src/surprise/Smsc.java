package surprise;

import java.net.*;
import java.io.*;
/**
 *
 * @author Uuganbayar.S
 */
public class Smsc {
    private static String smsUrl = "http://<SMSGW_URL>";
    
    public static String SendSMS(String to, String msg) {
        StringBuilder res = new StringBuilder();
        
        try {
            if (to.length() == 8) to = "976" + to;
            
            StringBuilder surl = new StringBuilder();
            surl.append(smsUrl);
            surl.append("&to=");
            surl.append(to);
            surl.append("&text=");
            surl.append(URLEncoder.encode(msg, "UTF-8"));
            
            URL url = new URL(surl.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            
            conn.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            
            String line = null;
            while ((line = reader.readLine()) != null) {
                res.append(line);
                res.append("\n");
            }
            
            reader.close();
            conn.disconnect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return res.toString();
    }
}
