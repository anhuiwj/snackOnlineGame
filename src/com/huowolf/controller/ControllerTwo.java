package com.huowolf.controller;

import com.huowolf.entities.Food;
import com.huowolf.entities.Ground;
import com.huowolf.entities.Snake;
import com.huowolf.listener.SnakeListener;
import com.huowolf.server.vo.Message;
import com.huowolf.socket.ClientAgentThreadTwo;
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
public class ControllerTwo extends KeyAdapter implements SnakeListener{
	private Snake snake;

	private Snake snake2;

	private Food food;
	private Ground ground;
	private GamePanel gamePanel;
	private GameMenu gameMenu;
	private BottonPanel bottonPanel;

	private Component parentComponent;

	//当前蛇移动方向
	int newDirection = Snake.getRIGHT();


	/**
	 * 贪吃蛇Socket
	 */

	public Socket sc;//连接

	public String myName = "snack2";//当前人员姓名

	public String changeName;//挑战者名称

	ClientAgentThreadTwo clientt;


	public ControllerTwo(Snake snake, Snake snake2, Food food, Ground ground, GamePanel gamePanel, GameMenu gameMenu, BottonPanel bottonPanel) {
		this.snake = snake;
		this.snake2 = snake2;
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

	public Snake getSnake2() {
		return snake2;
	}

	//监听按键
	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
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
//			case KeyEvent.VK_N:
//				msg.getSnake().speedUp();
//				break;
//			case KeyEvent.VK_M:
//				msg.getSnake().speedDown();
//				break;
//			case KeyEvent.VK_W:
//				snake2.changeDirection(Snake.UP);
//				break;
//			case KeyEvent.VK_S:
//				snake2.changeDirection(Snake.DOWN);
//				break;
//			case KeyEvent.VK_A:
//				snake2.changeDirection(Snake.LEFT);
//				break;
//			case KeyEvent.VK_D:
//				snake2.changeDirection(Snake.RIGHT);
//				break;
			default:
				break;
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

//		//两人玩
//		if(food.isFoodEated(othersnake)) {
//			othersnake.eatFood();
//			food.newFood(ground.getPoint());
//
//			bottonPanel.repaint();
//			setScore();
//		}
		
		if(ground.isGroundEated(snake)) {
			snake.die();
			bottonPanel.getStartButton().setEnabled(true);
		}
		
		if(snake.isEatBody()) {
			snake.die();
			bottonPanel.getStartButton().setEnabled(true);
		}

//		snake.setSnake2(othersnake);
//		if(isEatOtherBody(snake,othersnake)){
//			snake.die();
//			bottonPanel.getStartButton().setEnabled(true);
//		}
	}


//	//是否吃掉别人的身子
//	public boolean isEatOtherBody(Snake snake,Snake snake2) {
//		for(int i=1;i<snake2.body.size();i++) {
//			if(snake2.body.get(i).equals(snake.getHead())){
//				return true;
//			}
//
//		}
//		System.out.println(snake.getHead());
//
//		return false;
//	}
//
//
	//
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
			clientt = new ClientAgentThreadTwo(this);
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

	//开始游戏
	public void startGame() {
		gamePanel.requestFocus(true);
//		snake.clear();
//		snake.init(Global.WIDTH/2,Global.HEIGHT/2);
//		snake2.clear();
//		snake2.init(15,15);
//		snake.setSnake2(snake2);
//		clientt.agreeChallenge(snake);
		bottonPanel.getStartButton().setEnabled(false);
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
			sb.append("方向键控制方向\n");
			sb.append("w键 s键分表控制其加速减速\n");
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
			Object o = bottonPanel.getjComboBox().getSelectedItem();
				if(o != null ){
					changeName = o.toString();
					//发起挑战
					Message message = new Message();
					message.setChanllengeName(changeName);
					message.setSnackName("snack2");
					clientt.chanllengeSb(message);
				}
//			//开始游戏
//			startGame();
		}
	}

	/**
	 * 服务端传输移动信息
	 * @param msg
	 */
	public void snakeMoved(Message msg){
		if(msg != null){
			gamePanel.display(msg.getSnake2(),msg.getSnake(),msg.getFood(), msg.getGround());
			msg.getSnake2().changeDirection(newDirection);
			msg.setSnackName(myName);
			clientt.getNextStap(msg);
			setScore(msg);
		}
	}

	//设置分数
	public void setScore(Message message) {
		int score = message.getSnake().getFoodCount() ;
		bottonPanel.repaint();
		bottonPanel.setScore(score);
	}

	//重新开始
	public void newGame(Message message) {
		if(message != null){
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
		}
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

	public void setSnake2(Snake snake2) {
		this.snake2 = snake2;
	}
}
