package com.huowolf.server.scoket;

import com.alibaba.fastjson.JSON;
import com.huowolf.entities.Food;
import com.huowolf.entities.Ground;
import com.huowolf.entities.Snake;
import com.huowolf.server.vo.Message;
import com.huowolf.util.Global;
import com.huowolf.util.SnackHandle;
import com.huowolf.util.SnakeTimer;

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


	private Timer gameTimer = null;

	private final long TICK_DELAY = 300;

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
		if(father.snakeMap != null){
			Snake snake = null;
			for (Iterator<Message> iterator = getSnakes().iterator();
				 iterator.hasNext();) {
				Message m = iterator.next();
				for(int i=0;i<m.getStapSnakes().size();i++){
					snake = m.getStapSnakes().get(i);
					if(snake != null
							&& msg.getSnackName().equals(snake.getSnackName())){
						iterator.next().getStapSnakes().get(i).setNewDirection(msg.getNewDirection());
					}
				}
			}
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
//	private void moveNext(Message msg) {
//		move(msg);
//		try {
//			SnackHandle.handLife(msg);
//			msg.setHandleType(Global.HANDLE_TYPE_THIRTEEN);
//			dout.writeUTF(sendMsg(0,null,msg));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * 游戏开始
	 * @param msg
     */
	private synchronized void startGame(Message msg) {
		move(msg);
		msg.setHandleType(Global.HANDLE_TYPE_SIX);

		//初始化蛇信息

		initSnakes(msg);

		//群发用户开始游戏
		Vector tempv=father.onlineList;
		for(int i=0;i<tempv.size();i++) {
			ServerAgentThread satTemp=(ServerAgentThread)tempv.get(i);
			try {
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
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

	}

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
		List<Snake> stapSnakes = new ArrayList<>();

		Ground ground = new Ground();
		ground.generateRocks1();

		msg.setGround(ground);

		Food food = new Food();
		food.newFood(msg.getGround().getPoint());
		msg.setFood(food);



		snake = new Snake();
		snake.init(Global.WIDTH/2,Global.HEIGHT/2);

		snake2 = new Snake();
		snake2.init(10,10);

		snake.setSnackName("snack1");

		snake2.setSnackName("snack2");

		stapSnakes.add(snake);
		stapSnakes.add(snake2);

		msg.setStapSnakes(stapSnakes);

		msg.setSnackName("snack1");
		addSnake(msg);

		msg.setSnackName("snack2");
		addSnake(msg);

	}

	public synchronized void addSnake(Message msg) {
		if (father.snakes.size() == 0) {
			startTimer();
		}
		father.snakes.put(msg.getSnackName(), msg);
	}


	public Collection<Message> getSnakes() {
		//返回集合镜像 若原对象改变 自己也改变
		return Collections.unmodifiableCollection(father.snakes.values());
	}


	public synchronized void removeSnake(Snake snake) {
		father.snakes.remove(snake.getSnackName());
		if (father.snakes.size() == 0) {
			stopTimer();
		}
	}


	public void tick() throws Exception {
		Message message = new Message();

		message.setHandleType(Global.HANDLE_TYPE_THIRTEEN);

		List<Snake> stapSnakes = new ArrayList<>();

		Snake snake = null;
		for (Iterator<Message> iterator = getSnakes().iterator();
			 iterator.hasNext();) {
			Message m = iterator.next();
			for(int i=0;i<m.getStapSnakes().size();i++){
				snake = m.getStapSnakes().get(i);
				//移动
				SnackHandle.move(snake);
				//吃食物
				SnackHandle.handLife(m,snake);
				stapSnakes.add(snake);
			}
			message.setGround(m.getGround());
			message.setFood(m.getFood());
			message.setFoods(m.getFoods());
		}
		message.setStapSnakes(stapSnakes);

		if(father.GAME_TIMES <= 0){
			stopTimer();
			//游戏时间到
			message.setHandleType(Global.HANDLE_TYPE_EIGHT);
		}

		broadcast(JSON.toJSONString(message));
		father.GAME_TIMES -= 1 ;
	}

	public void broadcast(String message) throws Exception {
		sendAllSnack(message);
	}


	public  void startTimer() {
		gameTimer = new Timer(SnakeTimer.class.getSimpleName() + " Timer");
		gameTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				try {
					tick();
				} catch (Throwable e) {
					System.out.println("Caught to prevent timer from shutting down"+e);
				}
			}
		}, TICK_DELAY, TICK_DELAY);
	}


	public  void stopTimer() {
		if (gameTimer != null) {
			gameTimer.cancel();
		}
	}
}