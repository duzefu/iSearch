package common.functions.emailutil;

import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import server.info.config.ConfigFilePath;
import server.info.config.EmailInfo;
import server.info.entites.transactionlevel.UserEntity;

public class SendMail { 
	 
	    public static boolean send(UserEntity user, String random) {  
	  
	    	boolean ret=false;
	    	if(null==user||null==random||random.isEmpty()) return ret;
	    	String email=user.getEmail(), uname=user.getUsername();
	    	if(null==email||email.isEmpty()) return ret;
	        Properties props = new Properties();  
	        // 设置smtp服务器  
	        props.setProperty("mail.smtp.host", "smtp.sina.com");  
	        // 现在的大部分smpt都需要验证了  
	        props.put("mail.smtp.auth", "true");
	        //验证邮箱账号，密码
	        //MyAuthenticator auth = new MyAuthenticator("liuyang4288@sina.com","cccp@19891004");
	        MyAuthenticator auth = new MyAuthenticator(EmailInfo.emailAddress(), EmailInfo.passwd());
	        
	        Session s = Session.getDefaultInstance(props,auth);  
	        // 为了查看运行时的信息  
	        s.setDebug(true);  
	        // 由邮件会话新建一个消息对象  
	        Message message = new MimeMessage(s);  
	        try {  
	        	// 发件人  
	            InternetAddress from = new InternetAddress("bryanooo@sina.com");  
	            message.setFrom(from);  
	            // 收件人  
	            InternetAddress to = new InternetAddress(email);  
	            message.setRecipient(Message.RecipientType.TO, to);  
	            // 邮件标题  
	            message.setSubject("找回密码");
	            // 邮件内容,也可以使纯文本"text/plain"
	            
	            String content = "智搜用户您好，您的用户名是："+uname+"；本次用于找回密码的验证码是："+random+"，请使用该验证码在软件中重新设置密码。";  //message.setContent(content, "text/html;charset=GBK");
	            BodyPart bodyPart1 = new MimeBodyPart(); //新建一个存放信件内容的BodyPart对象 
	            bodyPart1.setContent(content, "text/html;charset=gb2312");//给BodyPart对象设置内容和格式/编码方式
	             
	            //设置邮件附件 
//	            BodyPart bodyPart2 = new MimeBodyPart();  
//	            String fileName="C:\\Users\\Administrator\\Desktop\\a.jpg";
//	            FileDataSource fileDataSource = new FileDataSource(fileName); 
//	            bodyPart2.setDataHandler(new DataHandler(fileDataSource));        
//	            bodyPart2.setFileName(fileName); 
//	             
	            Multipart mmp = new MimeMultipart();//新建一个MimeMultipart对象用来存放BodyPart对象(事实上可以存放多个)   
	            mmp.addBodyPart(bodyPart1); 
//	            mmp.addBodyPart(bodyPart2); 
	            
	            message.setContent(mmp);//把mm作为消息对象的内容 	              
	             
	            Transport.send(message);
	            ret=true;
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	  
	        return ret;
	    }
}