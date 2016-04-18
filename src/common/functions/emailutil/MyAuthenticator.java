package common.functions.emailutil;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;


 public class MyAuthenticator extends Authenticator {
	 
	        private PasswordAuthentication pwAuth;

	        public MyAuthenticator(String user, String passwd) {
	            pwAuth = new PasswordAuthentication(user, passwd);
	        }

	        @Override
			protected PasswordAuthentication getPasswordAuthentication() {
	            return pwAuth;
	        }
	   }