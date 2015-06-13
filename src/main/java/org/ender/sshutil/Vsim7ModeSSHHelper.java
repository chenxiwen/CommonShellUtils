/**
 * Vsim7ModeSSHHelper.java
 * @author Ender
 */
package org.ender.sshutil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * 
 * @author Ender
 * @since 2015.5.28
 * @Des use JSch SessionShell to execute a list of commands.
 */
public class Vsim7ModeSSHHelper {
	private StringBuffer strbuf = new StringBuffer();
	
	private Channel channel;
	private Session session;
	
	private String command;
	private String feedBack;
	
	private InputStream in;
	private OutputStream out;
	private BufferedWriter bw;
	private BufferedReader br;
	
	private String host;
	private String username;
	private String password;
	private int port;
	
	private Vsim7ModeSSHHelper(){
		//TODO nothing, just do not allow use this default constructor
		super();
	}
	
	public Vsim7ModeSSHHelper(String host, int port, String username, String password){
		this();
		this.host = host;
		this.username = username;
		this.password = password;
		this.port = port;
	}
	
	public Vsim7ModeSSHHelper(String host, String username, String password){
		this(host, SSHHelper.PORT, username, password);
	}
	
	/**
	 * @author Ender
	 * @since 2015.5.28
	 * @throws JSchException
	 */
	public void initChannel() throws JSchException{
		JSch jsch = new JSch();
		Session session = jsch.getSession(username, host, port);
		session.setPassword(password);
		MyUserInfo ui = new MyUserInfo();
		session.setUserInfo(ui);
		
		session.connect(3*1000);
		
		Channel channel = session.openChannel("shell");
		
		this.channel = channel;
		this.session = session;
	}
	
	/**
	 * @author Ender
	 * @since 2015.5.28
	 * @throws IOException
	 */
	public void destroy() throws IOException{
		this.br.close();
		this.bw.close();
		this.in.close();
		this.out.close();
		this.channel.disconnect();
		this.session.disconnect();
	}
	
	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
		this.feedBack = "";
	}

	public String getFeedBack() {
		return feedBack;
	}
	
	public boolean connectToHost() throws JSchException, IOException{
		channel.connect(1000);
		in = channel.getInputStream();
		out = channel.getOutputStream();
		bw = new BufferedWriter(new OutputStreamWriter(out));
		br = new BufferedReader(new InputStreamReader(in));
		return channel.isConnected();
	}
	
	/**
	 * @author Ender
	 * @since 2015.5.28
	 * @param anchor: to detect EOF then break
	 * @throws JSchException
	 * @throws IOException
	 */
	public void shellExec(String anchor) throws JSchException, IOException{
		strbuf.setLength(0);
		anchor = anchor.contains("\n")?anchor.substring(0, anchor.length()-1):anchor;
		anchor+=">";
		String shellCommand  = this.command+"\r\n";
		//System.out.println("exec "+shellCommand);
		bw.write(shellCommand);
		bw.flush();
		
		String result = "";	
		char c = (char)-1;
		while((c = (char)br.read())!=-1){
			strbuf.append(c);
			if(checkContainsKey(anchor)){
				break;
			}
		}
		result = strbuf.toString();
		if(result.indexOf(shellCommand)!=-1){
			result = result.substring(result.indexOf(shellCommand)+shellCommand.length());
		}	
		//System.out.println("result="+result);
		String[] resultarr = result.split(anchor);
		result = resultarr[0];
		//System.out.println(result);
		this.feedBack = result;
	}
	
	private boolean checkContainsKey(String key){
		return strbuf.toString().contains(key);
	}
	
	/**
	 * @author Ender
	 * @since 2015.5.28
	 * @param key: to detect the pause checkpoint
	 * @param confirm: to continue the task
	 * @param anchor: to detect the EOF then break
	 * @throws JSchException
	 * @throws IOException
	 */
	public void shellExec(String key, String confirm, String anchor) throws JSchException, IOException{
		strbuf.setLength(0);
		anchor = anchor.contains("\n")?anchor.substring(0, anchor.length()-1):anchor;// should use endwith, will modify later
		anchor+=">";
		key = key.contains("\n")?key.substring(0, key.length()-1):key;
		String shellCommand  = this.command+"\r\n";
		//System.out.println("exec "+shellCommand);
		bw.write(shellCommand);
		bw.flush();
		
		String result = "";
		char c = (char)-1;
		while((c = (char)br.read())!=-1){
			strbuf.append(c);
			if(checkContainsKey(key)){
				break;
			}
		}
		//System.out.println(strbuf.toString());
		//遇到了需要识别的字符串，继续传递指令
		bw.write(confirm+"\r\n");
		//System.out.println("input: "+confirm);
		bw.flush();
		//继续
		c = (char)-1;
		strbuf.setLength(0);
		while((c = (char)br.read())!=-1){
			strbuf.append(c);
			if(checkContainsKey(anchor)){
				break;
			}
		}
		//System.out.println(strbuf.toString());
		result = strbuf.toString();
		result = result.split(anchor)[0];
		this.feedBack = result;
	}
	
	public String getAnchor(){
		return SSHHelper.execCommand(host, username, password, port, "hostname");
	}
	
}
