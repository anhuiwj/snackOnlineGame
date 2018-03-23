package com.huowolf.socket;

import com.alibaba.fastjson.JSON;
import com.huowolf.controller.ControllerTwo;
import com.huowolf.entities.Snake;
import com.huowolf.server.vo.Message;
import com.huowolf.util.Global;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

public class ClientAgentThreadTwo extends Thread
{
	ControllerTwo father;

	boolean flag=true;

	public DataInputStream din;

	public DataOutputStream dout;

	String tiaoZhanZhe=null;//挑战者
	public ClientAgentThreadTwo(ControllerTwo father)
	{
		this.father = father;
		try
		{
			din = new DataInputStream(father.sc.getInputStream());//
			dout = new DataOutputStream(father.sc.getOutputStream());

			Message msg = new Message();
			msg.setHandleType(Global.HANDLE_TYPE_ONE);
			msg.setSnackName(father.myName);
			sendMsg(msg);

//			if(din.available() >= 0){
//				String name=father.myName.trim();//当前用户姓名
//				this.dout.writeUTF("<#NICK_NAME#>"+name);//发送
//			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	@Override
	public void run(){
		while(flag){
			try{
				/**
				 * /服务端返回
				 */
				String readInfo=din.readUTF().trim();
				System.out.println("接受到服务端信息"+readInfo);
				if(readInfo != null
						&& readInfo != ""){
					Message msg = JSON.parseObject(readInfo, Message.class);
					switch (msg.getHandleType()){
						//接受到下发列表
						case Global.HANDLE_TYPE_TWELVE:
							setChanllenges(msg);
							break;
						//下发移动步骤
						case Global.HANDLE_TYPE_THIRTEEN:
							moveStep(msg);
							break;
						//接受到重名信息
						case Global.HANDLE_TYPE_ELEVEN:
							break;
						//接受到一方死亡信息
						case Global.HANDLE_TYPE_NINE:
							break;
						//挑战开始
						case Global.HANDLE_TYPE_SIX:
							strtGame(msg);
							break;
						//挑战结束
						case Global.HANDLE_TYPE_SEVEN:
							break;
						//时间到
						case Global.HANDLE_TYPE_EIGHT:
							break;
						//接受到挑战信息
						case Global.HANDLE_TYPE_FIVETEEN:
							reciveChanllengeMsg(msg);
							break;
					}
				}
			}catch(EOFException e) {

			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	/**
	 * 接受到挑战信息
	 * @param msg
     */
	private void reciveChanllengeMsg(Message msg) {
		if(msg != null){
			int response =
					JOptionPane.showConfirmDialog(this.father.getParentComponent(), "接收到"+msg.getChanllengeName()+"的挑战", "提示",JOptionPane.YES_NO_CANCEL_OPTION);
			//接受挑战信息
			if(response == 0){
				msg.setHandleType(Global.HANDLE_TYPE_FOUR);
			}else if (response == 1){
				msg.setHandleType(Global.HANDLE_TYPE_FIVE);
			}
			sendMsg(msg);
		}

	}

	private void strtGame(Message msg) {
		father.snakeMoved(msg);
		father.newGame(msg);
	}

	/**
	 * 移动信息
	 * @param msg
     */
	private void moveStep(Message msg) {
		//接受服务端移动信息
		father.snakeMoved(msg);
	}
//	public void name_chongming(){
//		try{
////			JOptionPane.showMessageDialog(this.father,"Ѿռãд",
////			            "",JOptionPane.ERROR_MESSAGE);//ʾϢ
//			din.close();//ر
//			dout.close();//ر
////			this.father.jtfHost.setEnabled(!false);//ıΪ
////			this.father.jtfPort.setEnabled(!false);//˿ںŵıΪ
////			this.father.jtfNickName.setEnabled(!false);//ǳƵıΪ
////			this.father.jbConnect.setEnabled(!false);//""ťΪ
////			this.father.jbDisconnect.setEnabled(!true);//"Ͽ"ťΪ
////			this.father.jbChallenge.setEnabled(!true);//"ս"ťΪ
////			this.father.jbYChallenge.setEnabled(false);//"ս"ťΪ
////			this.father.jbNChallenge.setEnabled(false);//"ܾս"ťΪ
////			this.father.jbFail.setEnabled(false);//""ťΪ
//			father.sc.close();//رSocket
//			father.sc=null;
//			//father.cat=null;
//			flag=false;//ֹÿͻ˴߳
//		}
//		catch(IOException e){e.printStackTrace();}
//	}

	/**
	 * 设置挑战者
	 * @param message
     */
	public void setChanllenges(Message message){
		if(message != null){
			Vector v = new Vector();
			List<String> names = message.getOnlineUsers();
			if(names != null){
				for(String s:names){
					if(!father.myName.equals(s)){
						v.add(s);//挑战者
					}
				}
			}
			//将在线用户加入下拉框
			father.setComplexValue(v);
		}
//		if(v.size() > 0){
//			father.changeName = v.get(0)+"";
//		}
	}


	/**
	 * 开始游戏
	 */
	public void agreeChallenge(Snake snake){
		try {
			Message msg = new Message();
			msg.setHandleType(Global.HANDLE_TYPE_FOUR);
			msg.setSnackName("snack1");
			msg.setSnake(snake);
			dout.writeUTF(JSON.toJSONString(msg));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取下一步
	 * @param
     */
	public void getNextStap(Message msg){
		try {
			msg.setHandleType(Global.HANDLE_TYPE_FOURTEEN);
//			Message msg = new Message();
//
//			msg.setSnackName("snack1");
//			msg.setSnake(snake);
//			msg.setSnake2(snake.getSnake2());
			dout.writeUTF(JSON.toJSONString(msg));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 发起挑战
	 * @param
     */
	public void chanllengeSb(Message msg){
		try {
			msg.setHandleType(Global.HANDLE_TYPE_THREE);
			dout.writeUTF(JSON.toJSONString(msg));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
//	public void server_down(){
//		//this.father.jtfHost.setEnabled(!false);//ıΪ
////		this.father.jtfPort.setEnabled(!false);;//˿ںŵıΪ
////		this.father.jtfNickName.setEnabled(!false);//ǳƵıΪ
////		this.father.jbConnect.setEnabled(!false);//""ťΪ
////		this.father.jbDisconnect.setEnabled(!true);//"Ͽ"ťΪ
////		this.father.jbChallenge.setEnabled(!true);//"ս"ťΪ
////		this.father.jbYChallenge.setEnabled(false);//"ս"ťΪ
////		this.father.jbNChallenge.setEnabled(false);//"ܾս"ťΪ
////		this.father.jbFail.setEnabled(false);//""ťΪ
////		this.flag=false;//ֹÿͻ˴߳
////		father.cat=null;
////		JOptionPane.showMessageDialog(this.father,"ֹͣ","ʾ",
////		           JOptionPane.INFORMATION_MESSAGE);//뿪ʾϢ
//	}
//	public void tiao_zhan(String msg){
//		try{
//			String name=msg.substring(13);//սߵǳ
//			if(name != null
//				&& name.trim() != ""){
////				father.newGame();
//				father.changeName = name;
//
//				int response=JOptionPane.showConfirmDialog(this.father.getParentComponent(), "接收到"+name+"的挑战", "提示",JOptionPane.YES_NO_CANCEL_OPTION);
//				System.out.print(response);
//				if(response == 0){
//					this.dout.writeUTF("<#TONG_YI#>"+name);
//				}else if (response == 1){
//					this.dout.writeUTF("<#BUTONG_YI#>"+name);
//				}
//
//
//			}else{
//				this.dout.writeUTF("<#BUSY#>"+name);
//			}
//
//
//
//		}
//		catch(IOException e){
//			System.out.print("接受挑战失败"+e);
//			e.printStackTrace();
//		}
//	}
//	public void tong_yi(){
////		father.startGame();
////		this.father.jtfHost.setEnabled(false);//ıΪ
////		this.father.jtfPort.setEnabled(false);//˿ںŵıΪ
////		this.father.jtfNickName.setEnabled(false);//ǳƵıΪ
////		this.father.jbConnect.setEnabled(false);//""ťΪ
////		this.father.jbDisconnect.setEnabled(!true);//"Ͽ"ťΪ
////		this.father.jbChallenge.setEnabled(!true);//"ս"ťΪ
////		this.father.jbYChallenge.setEnabled(false);//"ս"ťΪ
////		this.father.jbNChallenge.setEnabled(false);//"ܾս"ťΪ
////		this.father.jbFail.setEnabled(!false);//""ťΪ
////		JOptionPane.showMessageDialog(this.father,"Էս!()",
////		                           "ʾ",JOptionPane.INFORMATION_MESSAGE);
//	}
//	public void butong_yi(){
////		this.father.caiPan=false;//caiPanΪfalse
////		this.father.color=0;//colorΪ0
////		this.father.jtfHost.setEnabled(false);//ıΪ
////		this.father.jtfPort.setEnabled(false);//˿ںŵıΪ
////		this.father.jtfNickName.setEnabled(false);//ǳƵıΪ
////		this.father.jbConnect.setEnabled(false);//""ťΪ
////		this.father.jbDisconnect.setEnabled(true);//"Ͽ"ťΪ
////		this.father.jbChallenge.setEnabled(true);//"ս"ťΪ
////		this.father.jbYChallenge.setEnabled(false);//"ս"ťΪ
////		this.father.jbNChallenge.setEnabled(false);//"ܾս"ťΪ
////		this.father.jbFail.setEnabled(false);//""ťΪ
////		JOptionPane.showMessageDialog(this.father,"Էܾս!","ʾ",
////		            JOptionPane.INFORMATION_MESSAGE);//ԷܾսʾϢ
////		this.tiaoZhanZhe=null;
//	}
//	public void busy(){
////		this.father.caiPan=false;//caiPanΪfalse
////		this.father.color=0;//colorΪ0
////		this.father.jtfHost.setEnabled(false);//ıΪ
////		this.father.jtfPort.setEnabled(false);//˿ںŵıΪ
////		this.father.jtfNickName.setEnabled(false);//ǳƵıΪ
////		this.father.jbConnect.setEnabled(false);//""ťΪ
////		this.father.jbDisconnect.setEnabled(true);//"Ͽ"ťΪ
////		this.father.jbChallenge.setEnabled(true);//"ս"ťΪ
////		this.father.jbYChallenge.setEnabled(false);//"ս"ťΪ
////		this.father.jbNChallenge.setEnabled(false);//"ܾս"ťΪ
////		this.father.jbFail.setEnabled(false);//""ťΪ
////		JOptionPane.showMessageDialog(this.father,"ԷæµУ","ʾ",
////		            JOptionPane.INFORMATION_MESSAGE);//ԷæµʾϢ
////		this.tiaoZhanZhe=null;
//	}
//	public void move(String msg){
////		int length=msg.length();
////		int startI=Integer.parseInt(msg.substring(length-4,length-3));//ӵԭʼλ
////		int startJ=Integer.parseInt(msg.substring(length-3,length-2));
////		int endI=Integer.parseInt(msg.substring(length-2,length-1));//ߺλ
////		int endJ=Integer.parseInt(msg.substring(length-1));
////		this.father.jpz.move(startI,startJ,endI,endJ);//÷
////		this.father.caiPan=true;//canPanΪtrue
//	}
//	public void renshu(){
////		JOptionPane.showMessageDialog(this.father,"ϲ,ʤ,Է","ʾ",
////		             JOptionPane.INFORMATION_MESSAGE);//ʤϢ
////		this.tiaoZhanZhe=null;//սΪ
////		this.father.color=0;//colorΪ0
////		this.father.caiPan=false;//caiPanΪfalse
////		this.father.next();//һ
////		this.father.jtfHost.setEnabled(false);//ıΪ
////		this.father.jtfPort.setEnabled(false);//˿ںŵıΪ
////		this.father.jtfNickName.setEnabled(false);//ǳƵıΪ
////		this.father.jbConnect.setEnabled(false);//""ťΪ
////		this.father.jbDisconnect.setEnabled(true);//"Ͽ"ťΪ
////		this.father.jbChallenge.setEnabled(true);//"ս"ťΪ
////		this.father.jbYChallenge.setEnabled(false);//"ս"ťΪ
////		this.father.jbNChallenge.setEnabled(false);//"ܾս"ťΪ
////		this.father.jbFail.setEnabled(false);//""ťΪ
//	}

	public void sendMsg(Message msg){
		try {
			dout.writeUTF(JSON.toJSONString(msg));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}