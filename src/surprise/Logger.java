package surprise;

/**
 *
 * @author Uuganbayar.S
 */
public class Logger extends Thread {
    private String isdn;
    private String dir;
    private String type;
    private String partner;
    private long price;
    private long balance;
    private String data;
    
    public Logger(String isdn, String direction, String type,
            String partner, long price, long balance, String data) {
        this.isdn = isdn;
        this.dir = direction;
        this.type = type;
        this.partner = partner;
        this.price = price;
        this.balance = balance;
        this.data = data;
    }
    
    public void run() {
        DB.AddLog(this.isdn, this.dir, this.type, this.partner,
                this.price, this.balance, this.data);
    }
}
