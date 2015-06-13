/**
 * SSHHelper.java
 * @author Ender
 */
package org.ender.sshutil;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

/**
 * @author Ender
 * @Des use JSch SessionShell to execute a list of commands.
 */
public class SSHHelper {
		
		public static final int PORT = 22;
		
		/**
		 * @author Ender
		 * @since 2015.5.25
		 * @param host
		 * @param username
		 * @param password
		 * @param port
		 * @param command
		 * @return
		 */
		public static String execCommand(String host, String username, String password, int port, String command){
			StringBuffer strbuf = new StringBuffer();
			try{
				JSch jsch=new JSch(); 
				Session session=jsch.getSession(username, host, port);
				session.setPassword(password);
				
				UserInfo ui=new MyUserInfo();
			    session.setUserInfo(ui);
			    
			    session.connect();
			    
			    Channel channel=session.openChannel("exec");
			    ((ChannelExec)channel).setCommand(command);
			    
			    channel.setInputStream(null);
			    
			    ((ChannelExec)channel).setErrStream(System.err);
			    
			    InputStream in=channel.getInputStream();
			    
			    channel.connect();

			    byte[] tmp=new byte[1024];
			    while(true){
			        while(in.available()>0){
			          int i=in.read(tmp, 0, 1024);
			          if(i<0)break;
			          //System.out.print(new String(tmp, 0, i));
			          strbuf.append(new String(tmp, 0, i));
			      }
				      if(channel.isClosed()){
				          if(in.available()>0) continue;
				          //System.out.println("exit-status: "+channel.getExitStatus());
				          //System.out.println("result: \n"+strbuf.toString());
				          break;
				      }
			      try{Thread.sleep(1000);}catch(Exception ee){}
			      }
			      channel.disconnect();
			      session.disconnect();			    
			}catch(Exception e){
				 System.out.println(e);
			     e.printStackTrace();
			}
			
			return strbuf.toString();
		}
		
		/**
		 * @author Ender
		 * @since 2015.5.25
		 * @param host
		 * @param username
		 * @param password
		 * @param command
		 * @return
		 */
		public static String execCommand(String host, String username, String password, String command){
			return execCommand(host, username, password, SSHHelper.PORT, command);
		}
		
		/**
		 * @author Ender
		 * @since 2015.5.28
		 * @param host
		 * @param username
		 * @param password
		 * @param port
		 * @param command
		 * @return
		 * @throws JSchException
		 * @throws IOException
		 */
		public static Map execCommandWithExitCode(String host, String username, String password, int port, String command) throws JSchException, IOException{
			StringBuffer strbuf = new StringBuffer();
			Map result = new HashMap();
			JSch jsch=new JSch(); 
			Session session=jsch.getSession(username, host, port);
			session.setPassword(password);
			
			UserInfo ui=new MyUserInfo();
		    session.setUserInfo(ui);
		    
		    session.connect();
		    
		    Channel channel=session.openChannel("exec");
		    ((ChannelExec)channel).setCommand(command);
		    
		    channel.setInputStream(null);
		    
		    ((ChannelExec)channel).setErrStream(System.err);
		    
		    InputStream in=channel.getInputStream();
		    
		    channel.connect();

		    byte[] tmp=new byte[1024];
		    while(true){
		        while(in.available()>0){
		          int i=in.read(tmp, 0, 1024);
		          if(i<0)break;
		          //System.out.print(new String(tmp, 0, i));
		          strbuf.append(new String(tmp, 0, i));
		      }
			      if(channel.isClosed()){
			          if(in.available()>0) continue;
			          //System.out.println("exit-status: "+channel.getExitStatus());
			          //System.out.println("result: \n"+strbuf.toString());
			          result.put("ExitCode", channel.getExitStatus());
			          result.put("Result", strbuf.toString());
			          break;
			      }
		      }
		      channel.disconnect();
		      session.disconnect();	
		      
		      return result;
		}
		
		/**
		 * @author Ender
		 * @since 2015.5.28
		 * @param host
		 * @param username
		 * @param password
		 * @param command
		 * @return
		 * @throws JSchException
		 * @throws IOException
		 */
		public static Map execCommandWithExitCode(String host, String username, String password, String command) throws JSchException, IOException{
			return execCommandWithExitCode(host, username, password, SSHHelper.PORT, command);
		}
}
