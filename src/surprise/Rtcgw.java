package surprise;

import java.net.*;
import java.io.*;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author Uuganbayar.S
 */
public class Rtcgw {
    private static String rtcgwAccount = "https://<RTCGW_URL>";
    
    public static Document URLReader(String url) {
        Document res = null;
        try {
            URL url1 = new URL(url);
            HttpURLConnection con = (HttpURLConnection)url1.openConnection();
            con.connect();
            
            SAXBuilder builder = new SAXBuilder();
            res = builder.build(con.getInputStream());
            con.disconnect();
        } catch (Exception ex) {
            res = new Document(new Element("response"));
            res.getRootElement().getChildren().add(new Element("returncode").setText("666"));
            res.getRootElement().getChildren().add(new Element("errormsg").setText("RTCGW error"));
        }
        
        return res;
    }
    
    public static Document AccountInquiry(String isdn) {
        StringBuilder url = new StringBuilder();
        url.append(rtcgwAccount);
        url.append("&TYPE=1&msisdn=976");
        url.append(isdn);
        
        return URLReader(url.toString());
    }
    
    public static Document BalanceModify(String isdn, float price) {
        StringBuilder url = new StringBuilder();
        url.append(rtcgwAccount);
        url.append("&TYPE=2&day=0&msisdn=976");
        url.append(isdn);
        url.append("&amount=");
        url.append(price * 1000);
        
        return URLReader(url.toString());
    }
    
    
    public static Document AccountReload(String isdn, long scratch) {
        StringBuilder url = new StringBuilder();
        url.append(rtcgwAccount);
        url.append("&TYPE=0&msisdn=976");
        url.append(isdn);
        url.append("&cardnumber=");
        url.append(scratch);
        
        return URLReader(url.toString());
    }
}
