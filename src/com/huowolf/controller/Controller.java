package com.huowolf.controller;

import com.huowolf.entities.Food;
import com.huowolf.entities.Ground;
import com.huowolf.entities.Snake;
import com.huowolf.listener.SnakeListener;
import com.huowolf.server.vo.Message;
import com.huowolf.server.scoket.ClientAgentThread;
import com.huowolf.util.Global;
import com.huowolf.view.BottonPanel;
import com.huowolf.view.GameMenu;
import com.huowolf.view.GamePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.Socket;
import java.util.Vector;

/**
 * 蛇控制类
 */
public class Controller extends KeyAdapter implements SnakeListener{
	private Snake snake;

	private Food food;
	private Ground ground;
	private GamePanel gamePanel;
	private GameMenu gameMenu;
	private BottonPanel bottonPanel;

	private Component parentComponent;

	//当前蛇移动方向
	int newDirection = Snake.getRIGHT();

	//操作按钮选择
	private int handleButton;

	/**
	 * 贪吃蛇Socket
	 */

	public Socket sc;//连接

	public String myName;//当前人员姓名

	public String changeName;//挑战者名称

	ClientAgentThread clientt;

	Message message;

	public Controller(Snake snake, Food food, Ground ground,GamePanel gamePanel,GameMenu gameMenu,BottonPanel bottonPanel) {
		this.snake = snake;
		this.food = food;
		this.ground = ground;
		this.gamePanel = gamePanel;
		this.gameMenu = gameMenu;
		this.bottonPanel = bottonPanel;
		
		init();
	}
	
	
	//初始化
	public void init() {
		//按钮绑定事件
		bottonPanel.getStartButton().addActionListener(new startHandler());
		bottonPanel.getPauseButton().addActionListener(new pauseHandler());
		bottonPanel.getEndButton().addActionListener(new endHandler());
		bottonPanel.getTwoPButton().addActionListener(new twoPButton());


		//游戏场景绑定事件
		gameMenu.getItem1().addActionListener(new Item1Handler());
		gameMenu.getItem2().addActionListener(new Item2Handler());
		gameMenu.getItem3().addActionListener(new Item3Handler());
		gameMenu.getItem4().addActionListener(new Item4Handler());

		//
		gameMenu.getSpItem1().addActionListener(new spItem1Handler());
		gameMenu.getSpItem2().addActionListener(new spItem2Handler());
		gameMenu.getSpItem3().addActionListener(new spItem3Handler());
		gameMenu.getSpItem4().addActionListener(new spItem4Handler());
		
		gameMenu.getMapItem1().addActionListener(new mapItem1Handler());
		gameMenu.getMapItem2().addActionListener(new mapItem2Handler());
		gameMenu.getMapItem3().addActionListener(new mapItem3Handler());

		//帮助绑定事件
		gameMenu.getAbItem().addActionListener(new abortItemHandler());
		
		bottonPanel.getStartButton().setEnabled(true);


	}

	//获取贪吃蛇
	public Snake getSnake() {
		return snake;
	}
	
	public Ground getGround() {
		return ground;
	}
	
	public GamePanel getGamePanel() {
		return gamePanel;
	}

	public GameMenu getGameMenu() {
		return gameMenu;
	}
	
	public BottonPanel getBottonPanel() {
		return bottonPanel;
	}

	//监听按键
	@Override
	public void keyPressed(KeyEvent e) {
//		if(Global.HANDER_BUTTON_ONE == handleButton){
//
//		}else{
//			switch (e.getKeyCode()) {
//
//				default:
//					break;
//			}
//			//改变方向
//			clientt.changeDirection(newDirection,message);
//		}
		switch (e.getKeyCode()) {
			case KeyEvent.VK_UP:
				newDirection = Snake.UP;
				break;
			case KeyEvent.VK_DOWN:
				newDirection = Snake.DOWN;
				break;
			case KeyEvent.VK_LEFT:
				newDirection = Snake.LEFT;
				break;
			case KeyEvent.VK_RIGHT:
				newDirection = Snake.RIGHT;
				break;
			case KeyEvent.VK_W:
				newDirection = Snake.UP;
				break;
			case KeyEvent.VK_S:
				newDirection = Snake.DOWN;
				break;
			case KeyEvent.VK_A:
				newDirection = Snake.LEFT;
				break;
			case KeyEvent.VK_D:
				newDirection = Snake.RIGHT;
				break;
		}
		//改变方向
		snake.setNewDirection(newDirection);
		if(clientt != null){
			clientt.changeDirection(newDirection,message);
		}
	}

	/**
	 * 此处为蛇移动的方法
	 *
	 * 在线对战关键点
	 *
	 *
	 * 思路:
	 * 	每条蛇都有自己移动方法 ,是不是可以从服务端计算好转发给客户端
	 * @param snake
     */
	//贪吃蛇移动
	@Override
	public void snakeMoved(Snake snake) {
		gamePanel.display(snake,null,food, ground);

		if(food.isFoodEated(snake)) {
			snake.eatFood();
			food.newFood(ground.getPoint());
			
			bottonPanel.repaint();
			setScore();		
		}
		
		if(ground.isGroundEated(snake)) {
			snake.die();
			bottonPanel.getStartButton().setEnabled(true);
		}
		
		if(snake.isEatBody()) {
			snake.die();
			bottonPanel.getStartButton().setEnabled(true);
		}

	}

	public void setScore() {
		int score = snake.getFoodCount() ;
		bottonPanel.setScore(score);
	}




	public void setOnline(){
		//连接服务端
		//jbConnect_event();
		connect_event();
	}

	public void connect_event(){

		try {
			sc = new Socket(Global.IP,Global.PORT);
			clientt = new  ClientAgentThread(this);
			clientt.start();
		}catch(Exception ee) {
			ee.printStackTrace();
		}
	}
	
	//重新开始
	public void newGame() {
		ground.clear();
		//战斗场地
		switch (ground.getMapType()) {
			case 1:
				ground.generateRocks1();
				break;
			case 2:
				ground.generateRocks2();
				break;
			case 3:
				ground.generateRocks3();
				break;
		}

		food.newFood(ground.getPoint());
		bottonPanel.setScore(0);
	}


	//开始按钮 单人游戏
	class startHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {	
			gamePanel.requestFocus(true);
			snake.clear();
			snake.init(Global.WIDTH/2,Global.HEIGHT/2);
			snake.begin();
			newGame();
			bottonPanel.getStartButton().setEnabled(false);
		}
		
	}
	
	//下来操作只针对单人游戏
	//暂停
	class pauseHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			gamePanel.requestFocus(true);
			snake.changePause();

			if(e.getActionCommand() == "暂停游戏") {
				bottonPanel.getPauseButton().setText("继续游戏");
			}else {
				bottonPanel.getPauseButton().setText("暂停游戏");
			}
		}
		
	}
	

	//结束
	class endHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			snake.die();
			bottonPanel.getStartButton().setEnabled(true);
		}
		
	}


	
	//颜色
	class Item1Handler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Color color = JColorChooser.showDialog(gamePanel, "请选择颜色", Color.BLACK);
			gamePanel.backgroundColor = color;
			
		}
		
	}
	
	
	class Item2Handler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Color color = JColorChooser.showDialog(gamePanel, "请选择颜色", Color.BLACK);
			food.setFoodColor(color);
		}
		
	}
	
	
	class Item3Handler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Color color = JColorChooser.showDialog(gamePanel, "请选择颜色", Color.BLACK);
			snake.setHeadColor(color);
		}
		
	}
	
	class Item4Handler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Color color = JColorChooser.showDialog(gamePanel, "请选择颜色", Color.BLACK);
			snake.setBodyColor(color);
		}
		
	}
	
	class spItem1Handler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			snake.setSleepTime(600);
			
		}
		
	}
	
	class spItem2Handler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			snake.setSleepTime(350);
			
		}	
	}
	
	
	class spItem3Handler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			snake.setSleepTime(150);
		}	
	}
	
	class spItem4Handler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			snake.setSleepTime(80);
			
		}	
	}
	
	
	class mapItem1Handler	implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			ground.setMapType(1);
			
		}
		
	}
	
	class mapItem2Handler	implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			ground.setMapType(2);
			
		}
		
	}
	
	class mapItem3Handler	implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			ground.setMapType(3);
			
		}
		
	}
	
	class abortItemHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			StringBuffer sb= new StringBuffer();
			sb.append("方向键\n");
			sb.append("WASD控制 都可控制其方向\n");
			sb.append("在规定120s内,谁的食物多者胜\n");
			String message = sb.toString();
			JOptionPane.showMessageDialog(null, message, "使用说明",JOptionPane.INFORMATION_MESSAGE);
			
		}
		
	}
	//-----------------------------------------------------------------------------------------

	//以下为在线对战
	//挑战
	class twoPButton implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			gamePanel.requestFocus(true);
			Object o = bottonPanel.getjComboBox().getSelectedItem();
				if(o != null ){
					changeName = o.toString();
					//发起挑战
					Message message = new Message();
					message.setChanllengeName(changeName);
					message.setSnackName(myName);
					clientt.chanllengeSb(message);
				}
		}
	}

	/**
	 * 服务端传输移动信息
	 * @param msg
	 */
	public void snakeMoved(Message msg){
		message = msg;
		if(msg != null){
			gamePanel.display(msg.getStapSnakes(),msg.getFood(),msg.getGround(),msg.getFoods());
		}
		setScore(msg);
	}

	//设置分数
	public void setScore(Message message) {
		if(message != null){
			for(Snake snake:message.getStapSnakes()){
				//给自己设置分数
				if(myName.equals(snake.getSnackName())){
					int score = snake.getFoodCount() ;
					bottonPanel.repaint();
					bottonPanel.setScore(score);
					bottonPanel.setTime(message.getTime());
				}
			}
		}
	}



	//重新开始
	public void newGame(Message message) {
		message.getGround().clear();
		//战斗场地
		switch (message.getGround().getMapType()) {
			case 1:
				message.getGround().generateRocks1();
				break;
			case 2:
				message.getGround().generateRocks2();
				break;
			case 3:
				message.getGround().generateRocks3();
				break;
		}

		message.getFood().newFood(message.getGround().getPoint());
		bottonPanel.setScore(0);
	}

	public String getMyName() {
		return myName;
	}

	public void setMyName(String myName) {
		this.myName = myName;
	}

	public String getChangeName() {
		return changeName;
	}

	public void setChangeName(String changeName) {
		this.changeName = changeName;
	}

	public void setComplexValue(Vector v){
		bottonPanel.getjComboBox().setModel(new DefaultComboBoxModel(v));
	}

	public Component getParentComponent() {
		return parentComponent;
	}

	public void setParentComponent(Component parentComponent) {
		this.parentComponent = parentComponent;
	}

	public void setSnake(Snake snake) {
		this.snake = snake;
	}

	public int getHandleButton() {
		return handleButton;
	}

	public void setHandleButton(int handleButton) {
		this.handleButton = handleButton;
	}
}
