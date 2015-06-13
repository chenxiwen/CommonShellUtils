/**
 * @author Ender
 * @ClassName WindowsTelnetHelper
 */
package org.ender.telnetutil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;

import org.apache.commons.net.telnet.TelnetClient;

/**
 * @author Ender
 * @since 2015.5.29
 * @Des Connect to a windows via telnet channel and execute some commands
 */
public class WindowsTelnetHelper{
	
	private static final String prompt = ">";
	private static final char promptCharacter = '>';
	
	private static final String[] termTypeArr = {"VT100", "VT52", "VT220", "VTNT", "ANSI"};
	
	private static final int PORT = 23;
	
	private TelnetClient tc;
	
	private StringBuffer strbuf = new StringBuffer();
	private InputStream in;
	private OutputStream out;
	private BufferedWriter bw;
	private BufferedReader br;
	
	private String host;
	private String username;
	private String password;
	private int port;
	
	
	private String charset = null;
	
	/**
	 * @author Ender
	 * @since 2015.5.29
	 * @DES do not allow use this default constructor from outside
	 */
	private WindowsTelnetHelper(){
		super();
	}
	
	/**
	 * @author Ender
	 * @since2015.5.29
	 * @param host
	 * @param username
	 * @param password
	 */
	public WindowsTelnetHelper(String host, String username, String password){
		this(host, PORT, username, password);
	}
	
	/**
	 * @author Ender
	 * @since 2015.5.29
	 * @param host
	 * @param port
	 * @param username
	 * @param password
	 */
	public WindowsTelnetHelper(String host, int port, String username, String password){
		this();
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
	}
	
	/**
	 * @author Ender
	 * @since 2015.5.29
	 * @param termType Э�����ͣ�VT100��VT52��VT220��VTNT��ANSI 
	 * @return true if connected
	 * @throws SocketException
	 * @throws IOException
	 */
	public boolean connectToHost(String termType) throws SocketException, IOException{
		tc = getClient(termType);
		tc.connect(host, port);
		return tc.isConnected();
	}
	
	/**
	 * @author Ender
	 * @since 2015.5.29
	 * @return
	 * @throws SocketException
	 * @throws IOException
	 * @DES use VT220 as default termType
	 */
	public boolean connectToHost() throws SocketException, IOException{
		return connectToHost("VT220");
	}
	
	/**
	 * @author Ender
	 * @since 2015.5.29
	 * @param charset,  please pass null if you don't know it
	 * @throws UnsupportedEncodingException
	 */
	public void initIO(String charset) throws UnsupportedEncodingException{
		this.charset = null!=charset&&charset.trim().length()>0?charset:null;
		in = tc.getInputStream();
		br = charset==null?new BufferedReader(new InputStreamReader(in)):new BufferedReader(new InputStreamReader(in, this.charset));
		out = tc.getOutputStream();
		bw = charset==null?new BufferedWriter(new OutputStreamWriter(out)):new BufferedWriter(new OutputStreamWriter(out, this.charset));
	}
	
	/**
	 * @author Ender
	 * @since 2015.5.29
	 * @return true if login successful
	 * @throws IOException
	 */
	public boolean login() throws IOException{
		String feedback = readTill("login:");
		if(feedback.contains("login:")){
			writeCommand(username);
		}
		feedback = readTill("password:");
		if(feedback.contains("password:")){
			writeCommand(password);
		}
		String anchor = getAnchor();
		feedback = readTill(anchor);
		if(feedback.contains("Microsoft Telnet Server.")&&feedback.endsWith(anchor)){
//			System.out.println("login successful!");
			return true;
		}else{
			System.out.println(feedback);
			return false;
		}		
	}
	
	/**
	 * @author Ender
	 * @since 2015.5.29
	 * @param command, the one you want to execute on the host
	 * @return
	 * @throws IOException
	 */
	public String execCommand(String command) throws IOException{
		writeCommand(command);
		return readTill(getAnchor());
	}
	
	private  String execCommand(String host, int port, String username, String password, String command) throws SocketException, IOException{
		TelnetClient tc = getClient("VT220");
		tc.connect(host, port);
		in = tc.getInputStream();
		br = charset==null?new BufferedReader(new InputStreamReader(in)):new BufferedReader(new InputStreamReader(in, charset));
		out = tc.getOutputStream();
		bw = charset==null?new BufferedWriter(new OutputStreamWriter(out)):new BufferedWriter(new OutputStreamWriter(out, charset));
		String feedback = null;
		feedback = readTill("login:");
		System.out.println(feedback);
		if(feedback.contains("login:")){
			writeCommand(username);
		}
		feedback = readTill("password:");
		System.out.println(feedback);
		if(feedback.contains("password:")){
			writeCommand(password);
		}
		feedback = readTill(">");
		System.out.println(feedback);
		if(feedback.contains("Microsoft Telnet Server.")&&feedback.endsWith(prompt)){
			System.out.println("login successful!");
			writeCommand(command);
			feedback = readTill("Administrator>");
			System.out.println(feedback);
		}else{
			System.out.println(feedback);
		}		
		
		return null;
	}
	
	
	/**
	 * @author Ender
	 * @since 2015.5.29
	 * @param termType Э�����ͣ�VT100��VT52��VT220��VTNT��ANSI 
	 * @return
	 */
	private static TelnetClient getClient(String termType){		
		for (String termT : termTypeArr) {
			if(termT.equalsIgnoreCase(termType)){
				return new TelnetClient(termT);
			}else{
				//System.out.println("termT:"+termT+" ----- termType:"+termType);
				continue;
			}
		}
		return null;
	}
	
	/**
	 * @author Ender
	 * @since 2015.5.29
	 * @param key
	 * @return the output until key shows
	 * @throws IOException
	 */
	private String readTill(String key) throws IOException{
		strbuf.setLength(0);
		String result=null;
		int character = -1;
		//byte[] buffer = new byte[1024];
		while((character=br.read())!=-1){
			strbuf.append((char)character);
			//System.out.println("------------------------------------------------------->\n"+strbuf.toString());
			if(strbuf.toString().contains(key)){
				//System.out.println("-->"+strbuf.toString());
				result = strbuf.toString();
				break;
			}
		}	
		return result;
	}
	
	private String readFeedBack() throws IOException{
		strbuf.setLength(0);
		int character = -1;
		//byte[] buffer = new byte[1024];
//		String line = null;
		while((character=br.read())!=-1){
			strbuf.append((char)character);
//			strbuf.append(character);
//			System.out.print(line);
		}
		return strbuf.toString().length()>0?strbuf.toString():null;
	}
	
	private void writeCommand(String command) throws IOException{
		bw.write(command);
		bw.write('\r');
		bw.write('\n');
		bw.flush();
//		System.out.println("send "+command);
	}
	
	/**
	 * @author Ender
	 * @since 2015.5.29
	 * @return
	 */
	public String getAnchor(){
		//TODO should tell if the username.length>1
		return username.substring(username.length()/2)+this.prompt;
	}
	
	/**
	 * @author Ender
	 * @since 2015.5.29
	 * @throws IOException
	 */
	public void destroy() throws IOException{
		this.br.close();
		this.bw.close();
		this.in.close();
		this.out.close();
	}
	
	public static void main(String[] args){
		try {
//			String result =new WindowsTelnetHelper().execCommand("172.16.66.110", 23, "administrator", "@WSX3edc", "echo 123456789>1.txt");
			WindowsTelnetHelper wth = new WindowsTelnetHelper("172.16.66.110", "administrator", "@WSX3edc");
			if(wth.connectToHost()){
				wth.initIO(null);
				if(wth.login()){
					String result = wth.execCommand("chcp");
					System.out.println(result);
				}
			}
			wth.destroy();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
