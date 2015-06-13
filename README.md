# CommonShellUtils
A esay lib to use SSH2 and Telnet(Windows)

If you just want to execute one command:
  	try {
			Map map = SSHHelper.execCommandWithExitCode("172.16.64.215", "root", "@WSX3edc", "?");
			System.out.println(map.get("ExitCode"));
			System.out.println(map.get("Result"));
			String resultStr = SSHHelper.execCommand("172.16.64.215", "root", "@WSX3edc", "?");
			System.out.println(resultStr);
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


If you need to execute a list of commands within one session, you can try this reference:
		Vsim7ModeSSHHelper v7h = new Vsim7ModeSSHHelper("172.16.65.224", 22, "root", "netapp1!");
		String anchor = v7h.getAnchor();
		String result;
		try {
			v7h.initChannel();
			v7h.connectToHost();
			v7h.setCommand("ls");
			v7h.shellExec(anchor);
			result = v7h.getFeedBack();
			System.out.println(result);
			
			v7h.setCommand("?");
			v7h.shellExec(anchor);
			result = v7h.getFeedBack();
			System.out.println(result);
			
			v7h.setCommand("vol offline vol_test1");
			v7h.shellExec(anchor);
			result = v7h.getFeedBack();
			if(result.contains("is now offline")){
				System.out.println("detected the key words, continue!");
				v7h.setCommand("vol destroy vol_test1");
				v7h.shellExec("Are you sure you want to destroy volume 'vol_test1'?", "y", anchor);
				result = v7h.getFeedBack();
				System.out.println(result);
			}
			
			v7h.destroy();
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	And for Windows Telnet:
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
