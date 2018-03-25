package com.huowolf.game;


import com.huowolf.controller.Controller;
import com.huowolf.entities.Food;
import com.huowolf.entities.Ground;
import com.huowolf.entities.Snake;
import com.huowolf.util.Global;
import com.huowolf.view.BottonPanel;
import com.huowolf.view.GameMenu;
import com.huowolf.view.GamePanel;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame implements Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		new GameFrame(new Controller(new Snake(), new Food(), new Ground(),
				new GamePanel(), new GameMenu(),new BottonPanel()),"snack1").run();

	}

	
	private GamePanel gamePanel;
	private GameMenu gameMenu;

	private Snake snake;

	private Controller controller;
	private JPanel buttonPanel;

	public GameFrame(Controller c,String myName) {
		this.controller = c;

		snake = controller.getSnake();

		gameMenu = controller.getGameMenu();
		gamePanel = controller.getGamePanel();
		buttonPanel = controller.getBottonPanel();

		setTitle("我的贪吃蛇");
		setBounds(300,100,Global.WIDTH*Global.CELL_SIZE+250,Global.HEIGHT*Global.CELL_SIZE+60);
		setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Container contentPane = this.getContentPane();
		this.setJMenuBar(gameMenu);

		contentPane.add(gamePanel);
		contentPane.add(buttonPanel);

		setResizable(false);
		setVisible(true);


		this.setLocation(this.getToolkit().getScreenSize().width / 2
				- this.getWidth() / 2, this.getToolkit().getScreenSize().height
				/ 2 - this.getHeight() / 2);


		gamePanel.addKeyListener(controller);

		snake.addSnakeListener(controller);


		controller.setMyName(myName);
		controller.setParentComponent(this);
		controller.setOnline();
	}

	@Override
	public void run() {
		controller.newGame();
	}
}
