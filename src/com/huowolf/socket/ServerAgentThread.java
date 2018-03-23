package com.huowolf.socket;

import com.alibaba.fastjson.JSON;
import com.huowolf.entities.Food;
import com.huowolf.entities.Ground;
import com.huowolf.entities.Snake;
import com.huowolf.server.vo.Message;
import com.huowolf.util.Global;
import com.huowolf.util.SnackHandle;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

/**
 * 服务端处理类
 */
public class ServerAgentThread extends Thread
{
	Server father;

	Socket sc;

	DataInputStream din;

	DataOutputStream dout;

	private Snake snake;//用户1

	private Snake snake2;//用户2


	boolean flag=true;

	Map<String,Vector<Message>> snakeMap = new HashMap<String, Vector<Message>>();

	public ServerAgentThread(Server father, Socket sc)
	{
		this.father=father;
		this.sc=sc;
		try
		{
			din=new DataInputStream(sc.getInputStream());
			dout=new DataOutputStream(sc.getOutputStream());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void run()
	{
		while(flag)
		{
			try
			{
				//介绍客户端信息
				String readInfo = din.readUTF().trim();
				if(readInfo != null
						&& readInfo != ""){
					Message msg = JSON.parseObject(readInfo, Message.class);
					switch (msg.getHandleType()){
						//上线
						case Global.HANDLE_TYPE_ONE:
							this.nick_name(msg);
							break;
						//下线
						case Global.HANDLE_TYPE_TWO:
							this.nick_name(msg);
							break;
						//发起挑战
						case Global.HANDLE_TYPE_THREE:
							this.sendChanllenge(msg);
							break;
						//接受挑战
						case Global.HANDLE_TYPE_FOUR:
							this.startGame(msg);
							break;
						//拒绝挑战
						case Global.HANDLE_TYPE_FIVE:
							this.nick_name(msg);
							break;
//						//上传移动信息
//						case Global.HANDLE_TYPE_FOURTEEN:
//							moveNext(msg);
//							break;
						//上次移动完成信息
						case Global.HANDLE_TYPE_SIXTEEN:
							receiveSucessMove(msg);
							break;
						//改变方向
						case Global.HANDLE_TYPE_EIGHTTEEN:
							changeDiration(msg);
							break;
					}
				}
			} catch(EOFException e) {

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	//改变计算方向
	private void changeDiration(Message msg) {
		father.dirations.put(msg.getSnackName(),msg.getSnake().getNewDirection());
	}

	/**
	 * 接收移动完成信息
	 * @param msg
     */
	private void receiveSucessMove(Message msg) throws IOException {
		father.lock.lock();
		try {
			//当前用户
			Vector<Message> vectors = null;
			//挑战者
			Vector<Message> chanllengeVectors = null;

			Message target = null;

			vectors = father.snakeMap.get(msg.getSnackName());
			if(vectors == null){
				vectors = new Vector<>();
			}
			vectors.add(msg);
			father.snakeMap.put(msg.getSnackName(),vectors);


			if(father.snakeMap.get(msg.getSnackName()) != null){
				//查到当前用户所有移动信息
				vectors = father.snakeMap.get(msg.getSnackName());
				if(vectors == null){
					vectors = new Vector<>();
					father.snakeMap.put(msg.getSnackName(),vectors);
				}

				chanllengeVectors = father.snakeMap.get(msg.getChanllengeName());
				if(vectors != null
						&& chanllengeVectors != null){

					for(Message snake2:chanllengeVectors){
						if(msg.getNowStap() == snake2.getNowStap()){
							target = snake2;
						}
					}
				}
			}

			//不为空则下发两者最新移动信息
			if(target != null){
				msg.setHandleType(Global.HANDLE_TYPE_THIRTEEN);
				target.setHandleType(Global.HANDLE_TYPE_THIRTEEN);

				Vector tempv=father.onlineList;
				//向挑战双方发生移动信息
				String chanllenge = msg.getChanllengeName();


				msg.getSnake().setNewDirection(father.dirations.get(msg.getSnackName()));

				//移动当前用户信息
				move(msg);
				SnackHandle.handLife(msg);


				target.getSnake().setNewDirection(father.dirations.get(target.getSnackName()));
				//移动对方信息
				move(target);
				SnackHandle.handLife(target);


				//交换双方信息
				msg.setSnake2(target.getSnake());
				target.setSnake2(msg.getSnake());


				for(int i=0;i<tempv.size();i++) {
					ServerAgentThread satTemp=(ServerAgentThread)tempv.get(i);
					try {
						//发给对应双方
						if(satTemp.getName().equals(msg.getSnackName())){
							System.out.println(msg.getSnackName()+"发送最新移动信息");
							msg.setChanllengeName(target.getSnackName());
							satTemp.dout.writeUTF(JSON.toJSONString(msg));
						}
						if(satTemp.getName().equals(target.getSnackName())){
							System.out.println(target.getSnackName()+"发送最新移动信息");
							target.setChanllengeName(msg.getSnackName());
							satTemp.dout.writeUTF(JSON.toJSONString(target));
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}else {
				//等待对方完成
				msg.setHandleType(Global.HANDLE_TYPE_SEVENTEEN);
				dout.writeUTF(sendMsg(0,null,msg));
			}
		}finally {
			father.lock.unlock();
		}

	}

	/**
	 * 转发挑战信息
	 * @param msg
     */
	private void sendChanllenge(Message msg) {
		Vector tempv=father.onlineList;
		msg.setHandleType(Global.HANDLE_TYPE_FIVETEEN);
		//挑战者
		String chanllenge = msg.getChanllengeName();
		for(int i=0;i<tempv.size();i++) {
			ServerAgentThread satTemp=(ServerAgentThread)tempv.get(i);
			try {
				if(satTemp.getName().equals(chanllenge)){
					msg.setChanllengeName(msg.getSnackName());
					satTemp.dout.writeUTF(JSON.toJSONString(msg));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("向发送信息:"+satTemp.getName());
		}
	}

	/**
	 * 移动下一步信息
	 * @param msg
     */
	private void moveNext(Message msg) {
		move(msg);
		try {
			SnackHandle.handLife(msg);
			msg.setHandleType(Global.HANDLE_TYPE_THIRTEEN);
			dout.writeUTF(sendMsg(0,null,msg));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 游戏开始
	 * @param msg
     */
	private void startGame(Message msg) {
		move(msg);
		msg.setHandleType(Global.HANDLE_TYPE_SIX);

		//初始化蛇信息

		initSnakes(msg);

		//群发用户开始游戏
		Vector tempv=father.onlineList;
		for(int i=0;i<tempv.size();i++) {
			ServerAgentThread satTemp=(ServerAgentThread)tempv.get(i);
			try {
				father.dirations.put(satTemp.getName(),Snake.getRIGHT());
				msg = father.snakes.get(msg.getSnackName());
				satTemp.dout.writeUTF(JSON.toJSONString(msg));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 处理客户端上线操作
	 *
	 * @param msg
     */
	public void nick_name(Message msg) {
		try {

			this.setName(msg.getSnackName());
			Vector v=father.onlineList;

			boolean isChongMing=false;

			for(int i=0;i<v.size();i++) {
				ServerAgentThread tempSat=(ServerAgentThread)v.get(i);
				if(tempSat.getName().equals(msg.getSnackName())) {
					isChongMing=true;
					break;
				}
			}
			if(isChongMing==true) {
				//有重名用户
				dout.writeUTF(sendMsg(Global.HANDLE_TYPE_ELEVEN,null,null));
				din.close();
				dout.close();
				sc.close();
				flag=false;
			} else {
				//添加用户
				v.add(this);
				father.refreshList();

				List<String> onlineUsers = new ArrayList<String>();

				for(int i=0;i<v.size();i++) {
					ServerAgentThread tempSat=(ServerAgentThread)v.get(i);
					onlineUsers.add(tempSat.getName());
				}

				//下发列表
				sendAllSnack(sendMsg(Global.HANDLE_TYPE_TWELVE,onlineUsers,null));
//				Vector tempv=father.onlineList;
//				size=tempv.size();
//				for(int i=0;i<size;i++) {
//					ServerAgentThread satTemp=(ServerAgentThread)tempv.get(i);
//					//下发列表
//					satTemp.dout.writeUTF(sendMsg(Global.HANDLE_TYPE_TWELVE,onlineUsers,null));
//
//					if(satTemp != this) {
//						satTemp.dout.writeUTF("<#MSG#>"+this.getName()+"...");
//					}
//				}
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

	}
//	public void client_leave(String msg){
//		try{
//			Vector tempv=father.onlineList;
//			tempv.remove(this);
//			int size=tempv.size();
//			String nl="<#NICK_LIST#>";
//			for(int i=0;i<size;i++){
//				ServerAgentThread satTemp=(ServerAgentThread)tempv.get(i);
//				satTemp.dout.writeUTF("<#MSG#>"+this.getName()+"...");
//				nl=nl+"|"+satTemp.getName();
//			}
//			for(int i=0;i<size;i++){
//				ServerAgentThread satTemp=(ServerAgentThread)tempv.get(i);
//				satTemp.dout.writeUTF(nl);
//			}
//			this.flag=false;
//			father.refreshList();
//		}
//		catch(IOException e){e.printStackTrace();}
//	}
//	public void tiao_zhan(String msg)
//	{
//		try
//		{
//			String name1=this.getName();
//			String name2=msg.substring(13);
//			Vector v=father.onlineList;
//			int size=v.size();
//			for(int i=0;i<size;i++)
//			{
//				ServerAgentThread satTemp=(ServerAgentThread)v.get(i);
//				if(satTemp.getName().equals(name2))
//				{
//					satTemp.dout.writeUTF("<#TIAO_ZHAN#>"+name1);
//					break;
//				}
//			}
//		}
//		catch(IOException e)
//		{
//			e.printStackTrace();
//		}
//	}
//	public void tong_yi(String msg){
//		try{
//			String name=msg.substring(11);
//			Vector v=father.onlineList;
//			int size=v.size();
//			for(int i=0;i<size;i++){
//				ServerAgentThread satTemp=(ServerAgentThread)v.get(i);
//				if(satTemp.getName().equals(name)){
//					satTemp.dout.writeUTF("<#TONG_YI#>");
//					break;
//				}
//			}
//		}
//		catch(Exception e){e.printStackTrace();}
//	}
//	public void butong_yi(String msg){
//		try{
//			String name=msg.substring(13);
//			Vector v=father.onlineList;
//			int size=v.size();
//			for(int i=0;i<size;i++)
//			{
//				ServerAgentThread satTemp=(ServerAgentThread)v.get(i);
//				if(satTemp.getName().equals(name)){
//					satTemp.dout.writeUTF("<#BUTONG_YI#>");
//					break;
//				}
//			}
//		}
//		catch(IOException e){e.printStackTrace();}
//	}
//	public void busy(String msg){
//		try{
//			String name=msg.substring(8);//սû
//			Vector v=father.onlineList;//ûб
//			int size=v.size();//ûбĴС
//			for(int i=0;i<size;i++){//б?սû
//				ServerAgentThread satTemp=(ServerAgentThread)v.get(i);
//				if(satTemp.getName().equals(name)){//ûͶԷæϢ
//					satTemp.dout.writeUTF("<#BUSY#>");
//					break;
//				}
//			}
//		}
//		catch(IOException e){e.printStackTrace();}
//	}
//	public void move(String msg){
//		try{
//			String name=msg.substring(8,msg.length()-4);//ýշ
//			Vector v=father.onlineList;//ûб
//			int size=v.size();//ûбĴС
//			for(int i=0;i<size;i++){//б?շ
//				ServerAgentThread satTemp=(ServerAgentThread)v.get(i);
//				if(satTemp.getName().equals(name)){//Ϣתշ
//					satTemp.dout.writeUTF(msg);
//					break;
//				}
//			}
//		}
//		catch(IOException e){e.printStackTrace();}
//	}
//	public void renshu(String msg){
//		try{
//			String name=msg.substring(10);//ýշ
//			Vector v=father.onlineList;//ûб
//			int size=v.size();//ûбĴС
//			for(int i=0;i<size;i++){//б?շ
//				ServerAgentThread satTemp=(ServerAgentThread)v.get(i);
//				if(satTemp.getName().equals(name)){//Ϣתշ
//					satTemp.dout.writeUTF(msg);
//					break;
//				}
//			}
//		}
//		catch(IOException e){e.printStackTrace();}
//	}

	/**
	 * 发送信息
	 * @return
     */
	public String sendMsg(int handleType,List<String> onlineUsers,Message newMsg){
		if(newMsg == null){
			Message msg = new Message();
			//信息类型
			msg.setHandleType(handleType);
			//设置在线用户信息
			msg.setOnlineUsers(onlineUsers);
			return JSON.toJSONString(msg);
		}else {
			return JSON.toJSONString(newMsg);
		}
	}

	/**
	 * 蛇移动信息
	 * @param msg
     */
	public void move(Message msg){
		//蛇一移动信息
		if(msg != null){
			if(msg.getSnake() != null){
				SnackHandle.move(msg.getSnake());
			}

//			//蛇二移动信息
//			if(msg.getSnake2() != null){
//				SnackHandle.move(msg.getSnake2());
//			}
		}
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 向所有用户发送信息
	 */
	public void sendAllSnack(String msg){
		Vector tempv=father.onlineList;
		for(int i=0;i<tempv.size();i++) {
			ServerAgentThread satTemp=(ServerAgentThread)tempv.get(i);
			try {
				satTemp.dout.writeUTF(msg);
//				if(satTemp != this) {
//					satTemp.dout.writeUTF("<#MSG#>"+this.getName()+"...");
//				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 初始化游戏信息
	 * @param msg
     */
	public void initSnakes(Message msg){
		snake = new Snake();
		snake2 = new Snake();
		snake.init(Global.WIDTH/2,Global.HEIGHT/2);
		snake2.init(10,10);
		msg.setSnake(snake);
		msg.setSnake2(snake2);
		msg.setGround(new Ground());
		msg.setFood(new Food());
		//当前用户
		Vector<Message> vectors = null;
		//挑战者
		Vector<Message> chanllengeVectors = null;

		Message target = null;

		father.snakes.put("snack1",msg);

		vectors = new Vector<>();
		snake = new Snake();
		snake2 = new Snake();
		snake2.init(Global.WIDTH/2,Global.HEIGHT/2);
		snake.init(10,10);
		msg.setSnake(snake);
		msg.setSnake2(snake2);
		msg.setGround(new Ground());
		msg.setFood(new Food());
		vectors.add(msg);
		father.snakes.put("snack2",msg);
	}
}