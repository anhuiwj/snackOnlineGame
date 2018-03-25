package com.huowolf.server.scoket;

import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread
{
	Server father;
	ServerSocket ss;
	boolean flag=true;
	public ServerThread(Server father)
	{
		this.father=father;
		ss=father.ss;
	}
	public void run()
	{
		while(flag)
		{
			try
			{
				Socket sc=ss.accept();
				ServerAgentThread sat=new ServerAgentThread(father,sc);
				sat.start();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}