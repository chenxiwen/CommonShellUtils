package org.ender.sshutil;

import java.io.IOException;
import java.util.Map;

import com.jcraft.jsch.JSchException;



public class Test {

	public static void main(String[] args) {
//		Vsim7ModeSSHHelper v7h = new Vsim7ModeSSHHelper("172.16.65.224", 22, "root", "netapp1!");
//		String anchor = v7h.getAnchor();
//		String result;
//		try {
//			v7h.initChannel();
//			v7h.connectToHost();
//			v7h.setCommand("ls");
//			v7h.shellExec(anchor);
//			result = v7h.getFeedBack();
//			System.out.println(result);
//			
//			v7h.setCommand("?");
//			v7h.shellExec(anchor);
//			result = v7h.getFeedBack();
//			System.out.println(result);
//			
//			v7h.setCommand("vol offline vol_test1");
//			v7h.shellExec(anchor);
//			result = v7h.getFeedBack();
//			if(result.contains("is now offline")){
//				System.out.println("detected the key words, continue!");
//				v7h.setCommand("vol destroy vol_test1");
//				v7h.shellExec("Are you sure you want to destroy volume 'vol_test1'?", "y", anchor);
//				result = v7h.getFeedBack();
//				System.out.println(result);
//			}
//			
//			v7h.destroy();
//		} catch (JSchException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		try {
			Map map = SSHHelper.execCommandWithExitCode("172.16.64.215", "root", "@WSX3edc", "?");
			System.out.println(map.get("ExitCode"));
			System.out.println(map.get("Result"));
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
