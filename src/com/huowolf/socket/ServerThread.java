package com.huowolf.socket;

import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread
{
	Server father; //����Server������
	ServerSocket ss;//����ServerSocket������
	boolean flag=true;
	public ServerThread(Server father)
	{//������
		this.father=father;
		ss=father.ss;
	}
	public void run()
	{
		while(flag)
		{
			try
			{
				Socket sc=ss.accept();//�ȴ�ͻ�������
				ServerAgentThread sat=new ServerAgentThread(father,sc);
				sat.start();//���������������������߳�
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}