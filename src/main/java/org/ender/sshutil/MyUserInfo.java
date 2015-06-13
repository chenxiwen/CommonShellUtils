package org.ender.sshutil;

import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

public class MyUserInfo implements UserInfo, UIKeyboardInteractive{

	public String[] promptKeyboardInteractive(String arg0, String arg1,
			String arg2, String[] arg3, boolean[] arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPassphrase() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean promptPassphrase(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean promptPassword(String str) {
		// TODO Auto-generated method stub
		System.out.println("promptPassword: "+str);
		return true;
	}

	public boolean promptYesNo(String str) {
		// TODO Auto-generated method stub
		//System.out.println("promptYesNo: "+str);
		return true;
	}

	public void showMessage(String message) {
		// TODO Auto-generated method stub
		System.out.println("message: "+message);
	}

}
