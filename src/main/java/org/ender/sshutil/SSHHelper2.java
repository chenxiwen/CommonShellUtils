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

class SSHHelper2 {
	private StringBuffer strbuf = new StringBuffer();
	
	private Channel channel;
	private Session session;
	
	private String command;
	private String feedBack;
	
	private InputStream in;
	private OutputStream out;
	private BufferedWriter bw;
	private BufferedReader br;
	
	public void createChannel() throws JSchException{
		JSch jsch = new JSch();
		Session session = jsch.getSession("root", "172.16.65.224", 22);
		session.setPassword("netapp1!");
		MyUserInfo ui = new MyUserInfo();
		session.setUserInfo(ui);
		
		session.connect(1000);
		
		Channel channel = session.openChannel("shell");
		
		this.channel = channel;
		this.session = session;
	}
	
	public void close() throws IOException{
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
		//try{Thread.sleep(1000);}catch(Exception ee){}
		in = channel.getInputStream();
		out = channel.getOutputStream();
		bw = new BufferedWriter(new OutputStreamWriter(out));
		br = new BufferedReader(new InputStreamReader(in));
		return channel.isConnected();
	}
	
	public void shellExec() throws JSchException, IOException{
		
		String shellCommand  = this.command+"\n";
		System.out.println(bw.toString());
		bw.write(shellCommand);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bw.flush();
		
		String result = "";
		String line =null;
		while((line=br.readLine())!=null){
			if(line.contains("filer3240-3>")){
				break;
			}else if(line.equalsIgnoreCase(this.command)){
				continue;
			}else{
				result+=line;
			}
		}
		this.feedBack = result;
	}
	
	public void shellExec(String anchor) throws JSchException, IOException{
		strbuf.setLength(0);
		anchor = anchor.contains("\n")?anchor.substring(0, anchor.length()-1):anchor;
		anchor+=">";
		String shellCommand  = this.command+"\r\n";
		System.out.println("exec "+shellCommand);
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
	
	public void shellExec(String key, String confirm, String anchor) throws JSchException, IOException{
		strbuf.setLength(0);
		anchor = anchor.contains("\n")?anchor.substring(0, anchor.length()-1):anchor;
		anchor+=">";
		key = key.contains("\n")?key.substring(0, key.length()-1):key;
		String shellCommand  = this.command+"\r\n";
		System.out.println("exec "+shellCommand);
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
		System.out.println(strbuf.toString());
		//遇到了需要识别的字符串，继续传递指令
		bw.write(confirm+"\r\n");
		System.out.println("input: "+confirm);
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
		System.out.println(strbuf.toString());
		result = strbuf.toString();
		result = result.split(key)[0];
		this.feedBack = result;
	}
	
	public void shellExecWithConfirm(String key, String confirm) throws JSchException, IOException{
		
		String shellCommand  = this.command+"\n";
		System.out.println(bw.toString());
		bw.write(shellCommand); 
		bw.flush();
		
		String result = "";
		String line =null;
		while((line=br.readLine())!=null){
			if(line.contains(key)){
				bw.write(confirm+"\n");
				bw.flush();
			}else if(line.contains("filer3240-3>")){
				break;
			}else if(line.equalsIgnoreCase(this.command)){
				continue;
			}else{
				result+=line;
			}
		}
		this.feedBack = result;
	}
	
}
