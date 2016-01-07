package surprise;

public class Request {
        public String sender;
        public String recipient;
        public String greeting;
        public boolean isScratch;
        public long value;
        
        private String sms;
        public int status;
        public String parseError;
        
        public Request(String sender, String sms) {
            this.sender = (sender.startsWith("976") ? sender = sender.substring(3) : sender);
            this.parseError = Error.INVALID_REQUEST;
            this.sms = sms.replaceAll("\n", " ").trim();
            this.status = Request.Status.New;
        }
        
        public boolean Parse() {
            boolean success = false;
            recipient = "";
            greeting = "";
            isScratch = false;
            value= 0;
            
            try {
                // recipient
                int i = 0;
                while (i < sms.length() && sms.charAt(i) != ' ') recipient += sms.charAt(i++);
                while (i < sms.length() && sms.charAt(i) == ' ') i++;
                
                // is scratch?
                if (sms.charAt(i) == 's' || sms.charAt(i) == 'S')   {
                    isScratch= true;
                    i++;
                }
                
                // value
                StringBuilder svalue = new StringBuilder();
                while (i < sms.length() && sms.charAt(i) != ' ') svalue.append(sms.charAt(i++));
                while (i < sms.length() && sms.charAt(i) == ' ') i++;
                if (svalue.length() > 0) value = Long.parseLong(svalue.toString());
                
                // greeting
                if (i < sms.length()) greeting = sms.substring(i);
                
                // additional checks
                Integer.parseInt(recipient);
                
                if (recipient.length() != 8)
                    this.parseError = Error.INVALID_REQUEST;
                else if (greeting.length() > 100)
                    this.parseError = Error.INVALID_GREETING;
                else if (!this.isScratch && (value < 300 || 1000 < value || value % 100 != 0))
                    this.parseError = Error.INVALID_UNIT_SIZE;
                else
                    success = true;
                
            } catch (Exception ex) {
                success = false;
            }
            
            return success;
        }
        
        public class Status {
            public static final int New = 0;
            public static final int Done = 1;
            public static final int ParseFailed = -1;
        }
    }