package com.huowolf.socket;

import com.huowolf.entities.Snake;
import com.huowolf.server.vo.Message;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server extends JFrame implements ActionListener
{
	JLabel jlPort=new JLabel("端口");

	JTextField jtfPort=new JTextField("9999");

	JButton jbStart=new JButton("开启");

	JButton jbStop=new JButton("关闭");

	JPanel jps=new JPanel();

	JList jlUserOnline=new JList();

	JScrollPane jspx=new JScrollPane(jlUserOnline);

	JSplitPane jspz=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,jspx,jps);

	ServerSocket ss;

	ServerThread st;

	//在线人员列表
	Vector onlineList=new Vector();

	//父节点存放唯一信息
	Map<String,Vector<Message>> snakeMap = new HashMap<String, Vector<Message>>();

	Map<String,Integer> dirations = new HashMap<String, Integer>();

	Map<String,Message> snakes = new HashMap<String,Message>();

//	//蛇一移动信息
//	Vector<Message> snake1 = new Vector<Message>();
//
//	//蛇二移动信息
//	Vector<Message> snake2 = new Vector<Message>();

	Lock lock = new ReentrantLock();

	public Server()
	{
		this.initialComponent();
		this.addListener();
		this.initialFrame();
	}
	public void initialComponent()
	{
		jps.setLayout(null);
		jlPort.setBounds(20,20,50,20);
		jps.add(jlPort);
		this.jtfPort.setBounds(85,20,60,20);
		jps.add(this.jtfPort);
		this.jbStart.setBounds(18,50,60,20);
		jps.add(this.jbStart);
		this.jbStop.setBounds(85,50,60,20);
		jps.add(this.jbStop);
		this.jbStop.setEnabled(false);
	}
	public void addListener()
	{
		this.jbStart.addActionListener(this);
		this.jbStop.addActionListener(this);
	}
	public void initialFrame()
	{
		this.setTitle("贪吃蛇服务端");
		Image image=new ImageIcon("ico.gif").getImage();
		this.setIconImage(image);
		this.add(jspz);
		jspz.setDividerLocation(250);
		jspz.setDividerSize(4);
		this.setBounds(20,20,420,320);
		this.setVisible(true);
		this.addWindowListener(
			new WindowAdapter()
			{
				public void windowClosing(WindowEvent e)
				{
					if(st==null)
					{
						System.exit(0);
						return;
					}
					try
					{
						//在线用户组
						Vector v = onlineList;
						int size=v.size();
						for(int i=0;i<size;i++)
						{
							ServerAgentThread tempSat=(ServerAgentThread)v.get(i);
							tempSat.dout.writeUTF("<#SERVER_DOWN#>");
							tempSat.flag=false;
						}
						st.flag=false;
						st=null;
						ss.close();
						v.clear();
						refreshList();
					}
					catch(Exception ee)
					{
						ee.printStackTrace();
					}
					System.exit(0);
				}
			}
			);
	}
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource()==this.jbStart)
		{
			this.jbStart_event();
		}
		else if(e.getSource()==this.jbStop)
		{
			this.jbStop_event();
		}
	}
	public void jbStart_event()
	{
		int port=0;
		try
		{
			port=Integer.parseInt(this.jtfPort.getText().trim());
		}
		catch(Exception ee)
		{
			JOptionPane.showMessageDialog(this,"端口开启异常","",
			                               JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(port>65535||port<0)
		{
			JOptionPane.showMessageDialog(this,"端口请在0-65535之间","",
			                               JOptionPane.ERROR_MESSAGE);
			return;
		}
		try
		{
			this.jbStart.setEnabled(false);
			this.jtfPort.setEnabled(false);
			this.jbStop.setEnabled(true);
			ss=new ServerSocket(port);
			st=new ServerThread(this);
			st.start();

			JOptionPane.showMessageDialog(this,"开启成功,等待连接","ʾ",
			                               JOptionPane.INFORMATION_MESSAGE);
		}
		catch(Exception ee)
		{
			JOptionPane.showMessageDialog(this,"开启异常","",
			                               JOptionPane.ERROR_MESSAGE);
			this.jbStart.setEnabled(true);
			this.jtfPort.setEnabled(true);
			this.jbStop.setEnabled(false);
		}
	}
	public void jbStop_event()
	{
		try
		{
			Vector v=onlineList;
			int size=v.size();
			for(int i=0;i<size;i++)
			{
				ServerAgentThread tempSat=(ServerAgentThread)v.get(i);
				tempSat.dout.writeUTF("<#SERVER_DOWN#>");
				tempSat.flag=false;
			}
			st.flag=false;
			st=null;
			ss.close();
			v.clear();
			refreshList();
			this.jbStart.setEnabled(true);
	    	this.jtfPort.setEnabled(true);
		    this.jbStop.setEnabled(false);
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
	}
	public void refreshList()
	{
		Vector v=new Vector();
		int size=this.onlineList.size();
		for(int i=0;i<size;i++)
		{
			ServerAgentThread tempSat=(ServerAgentThread)this.onlineList.get(i);
			String temps=tempSat.sc.getInetAddress().toString();
			temps=temps+"|"+tempSat.getName();
			v.add(temps);
		}
		this.jlUserOnline.setListData(v);
	}
	public static void main(String args[])
	{
		new Server();
	}
}