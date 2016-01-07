package surprise;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.jdom.Document;

public class Surprise extends HttpServlet {
    public static final int TransPerDay = 3;
    public static final int FailurePerDay = 10;
    public static final String ShortNumber = "596";
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String msg = request.getParameter("msg");
        
        if (msg.equalsIgnoreCase("help")) {
            response.setContentType("text");
            response.getWriter().print(Error.HELP);
            return;
        }
        
        
        Request req = new Request(request.getParameter("sender"), msg);
        
        Logger logReq = new Logger(req.sender, ">", "S", Surprise.ShortNumber, 20, 0, msg);
        logReq.start();
        
        StringBuilder res = new StringBuilder();
        StringBuilder resR = new StringBuilder();
        
        if (req.Parse()) {
            if (req.isScratch) {
                Document rcpReload = Rtcgw.AccountReload(req.recipient, req.value);
                
                if (rcpReload.getRootElement().getChildText("returncode").equals("0")) {
                    long balance = Long.parseLong(rcpReload.getRootElement().getChildText("balance")) / 1000;
                    
                    resR.append(req.greeting);
                    resR.append(" (");
                    resR.append(req.sender);
                    resR.append("-s taniig ceneglej ");
                    resR.append(balance);
                    resR.append(" negjtei bolloo, uilchilgeenii duusax ognoo ");
                    resR.append(rcpReload.getRootElement().getChildText("enddate"));
                    resR.append(")");
                    Smsc.SendSMS(req.recipient, resR.toString());
                    
                    Logger logRcp = new Logger(req.recipient, "<", "R", req.sender, req.value, balance, resR.toString());
                    logRcp.start();
                    
                    res.append("Tanii ilgeesen kardaar ");
                    res.append(req.recipient);
                    res.append(" dugaar ceneglegdlee.");
                    
                    Logger logSdr = new Logger(req.sender, ">", "R", req.recipient, req.value, 0, "");
                    logSdr.start();
                } else {
                    res.append("Uuchlaarai, ");
                    res.append(req.recipient);
                    res.append(" dugaariig ceneglexed aldaa garlaa: ");
                    res.append(rcpReload.getRootElement().getChildText("errormsg"));
                }
            } else {
                if (DB.DayTransCount(req.sender) >= TransPerDay)
                    res.append(Error.DAY_LIMIT_EXCEEDED);
                if (DB.DayFailCount(req.sender) >= FailurePerDay)
                    res.append(Error.DAY_FAILURE_EXCEEDED);
                else {
                    // modify sender's balance
                    long taxedUnit = (long)(req.value * 1.10f);
                    Document sdrBalRes = Rtcgw.BalanceModify(req.sender, -(long)(taxedUnit - 20.0f));
                    long sdrBalance = Long.parseLong(sdrBalRes.getRootElement().getChildText("balance")) / 1000;
                    
                    if (sdrBalRes.getRootElement().getChildText("returncode").equals("0")) {
                        // modify recipient's balance
                        Document rcpBalRes = Rtcgw.BalanceModify(req.recipient, req.value);
                        long rcpBalance = Long.parseLong(rcpBalRes.getRootElement().getChildText("balance")) / 1000;
                        
                        if (rcpBalRes.getRootElement().getChildText("returncode").equals("0")) {
                            resR.append(req.greeting);
                            resR.append(" (Tand ");
                            resR.append(req.sender);
                            resR.append(" dugaaraas ");
                            resR.append(req.value);
                            resR.append(" negj ilgeelee)");
                            Smsc.SendSMS(req.recipient, resR.toString());
                            
                            Logger logRcp = new Logger(req.recipient, "<", "T", req.sender, req.value, rcpBalance, resR.toString());
                            logRcp.start();
                            
                            res.append("Tanii ilgeesen ");
                            res.append(req.value);
                            res.append(" negj ");
                            res.append(req.recipient);
                            res.append(" dansand orj, tanii dansnaas ");
                            res.append(taxedUnit);
                            res.append(" negj xasagdaj, ");
                            res.append(sdrBalance);
                            res.append(" negj uldlee.");
                            
                            Logger logSdr = new Logger(req.sender, ">", "T", req.recipient,
                                    -(long)(taxedUnit - 20.0f), sdrBalance, "");
                            logSdr.start();
                            
                        } else {
                            res.append("Uuchlaarai, ");
                            res.append(req.recipient);
                            res.append(" dugaar negj xuleen avaxad aldaa garlaa: ");
                            res.append(rcpBalRes.getRootElement().getChildText("errormsg"));
                            
                            // refund
                            Rtcgw.BalanceModify(req.sender, (long)(taxedUnit - 20.0f));
                        }
                    } else if (sdrBalRes.getRootElement().getChildText("returncode").equals("1"))
                        res.append(Error.INVALID_UNIT_SENDER);
                    else {
                        res.append("Uuchlaarai, Tanaas ");
                        res.append(taxedUnit);
                        res.append(" negj shiljuulexed aldaa garlaa: ");
                        res.append(sdrBalRes.getRootElement().getChildText("errormsg"));
                    }
                }
            }
        } else
            res.append(req.parseError);
        
        response.setContentType("text/html");
        response.getWriter().print(res.toString());
        
        Logger logRes = new Logger(req.sender, "<", "S", Surprise.ShortNumber, 0, 0, res.toString());
        logRes.start();
    }
   
    
    public static void main(String[] args) {
        Request req = new Request("97699099121", "99282133 1000");
        if (req.Parse()) {
            System.out.println("Sender:" + req.sender);
            System.out.println("Recipient:" + req.recipient);
            System.out.println("Value:" + req.value);
            System.out.println("Scratch:" + req.isScratch);
            System.out.println("Greeting:" + req.greeting);
        } else
            System.out.println(req.parseError);
    }
}
