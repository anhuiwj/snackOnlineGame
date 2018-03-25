package com.huowolf.server.scoket;

import com.alibaba.fastjson.JSON;
import com.huowolf.controller.Controller;
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

public class ClientAgentThread extends Thread
{
	Controller father;

	boolean flag=true;

	public DataInputStream din;

	public DataOutputStream dout;

	public ClientAgentThread(Controller father)
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
						//接收到重名信息
						case Global.HANDLE_TYPE_ELEVEN:
							break;
						//接收到一方死亡信息
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
							gameOver(msg);
							break;
						//接收到挑战信息
						case Global.HANDLE_TYPE_FIVETEEN:
							reciveChanllengeMsg(msg);
							break;
						//等待
						case Global.HANDLE_TYPE_SEVENTEEN:
							//等待后继续请求
							stapSuccess(msg);
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
	 * 结束游戏
	 * @param msg
     */
	private void gameOver(Message msg) {
		List<Snake> stapSnakes = msg.stapSnakes;
		String winner = "";
		int score = 0;
		for(Snake s:stapSnakes){
			if(s.getFoodCount() > score){
				score = s.getFoodCount();
				winner = s.getSnackName();
			}
		}
		JOptionPane.showMessageDialog(null, winner+"胜利了", "提示", 1);
	}

	/**
	 * 接受到挑战信息
	 * @param msg
     */
	private void reciveChanllengeMsg(Message msg) {
		//设置自己的姓名
		msg.setSnackName(father.getMyName());
		//赋值挑战者
		father.setChangeName(msg.getChanllengeName());
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
	public void stapSuccess(Message msg){
		try {
			msg.setHandleType(Global.HANDLE_TYPE_SIXTEEN);
			msg.setSnackName(father.myName);
			msg.setChanllengeName(father.changeName);
			dout.writeUTF(JSON.toJSONString(msg));
			System.out.print(msg.getSnackName()+"向服务端发送移动完成");
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


	/**
	 * 改变方向
	 * @param direction
     */
	public void changeDirection(int direction,Message msg){
		msg.setHandleType(Global.HANDLE_TYPE_EIGHTTEEN);
		msg.setSnackName(father.myName);
		msg.setNewDirection(direction);
		sendMsg(msg);
	}

	public void sendMsg(Message msg){
		try {
			dout.writeUTF(JSON.toJSONString(msg));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}