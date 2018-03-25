package com.huowolf.view;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.*;


public class BottonPanel extends JPanel{


	private static final long serialVersionUID = 1L;
	
	private JButton startButton;
	private JButton pauseButton;
	private JButton endButton;
	private JButton twoPButton;

	private JLabel scoreLabel;

	private JLabel timeeLabel;

	private int score;

	private int time;

	private JComboBox jComboBox;
	
	public BottonPanel() {

		//setFocusable(false);
		setLayout(null);
		setBounds(455, 0, 235, 450);
		
		startButton = new JButton("开始游戏");
		startButton.setBounds(10,20, 100, 25);
		add(startButton);
		
		pauseButton = new JButton("暂停游戏");
		pauseButton.setBounds(10, 60, 100, 25);
		add(pauseButton);	
		
		endButton = new JButton("结束游戏");
		endButton.setBounds(10,100, 100, 25);
		add(endButton);

		jComboBox = new JComboBox();
		jComboBox.setBounds(10,140,100,25);
		add(jComboBox);

		twoPButton = new JButton("发起挑战");
		twoPButton.setBounds(10,180, 100, 25);
		add(twoPButton);


		scoreLabel = new JLabel("Score:");
		scoreLabel.setFont(new Font("Serif",Font.BOLD,18));
		scoreLabel.setBounds(30, 220, 100, 30);
		add(scoreLabel);

		timeeLabel = new JLabel("Time:");
		timeeLabel.setFont(new Font("Serif",Font.BOLD,18));
		timeeLabel.setBounds(40, 300, 100, 30);
		add(timeeLabel);

		Color c= new Color(0, 250,154);
		this.setBackground(c);
		
		this.setFocusable(true);
		
	}
	
	
	public JButton getStartButton() {
		return startButton;
	}


	public JButton getPauseButton() {
		return pauseButton;
	}


	public JButton getEndButton() {
		return endButton;
	}

	public JButton getTwoPButton() {
		return twoPButton;
	}

	public int getScore() {
		return score;
	}


	public void setScore(int score) {
		this.score = score;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public JComboBox getjComboBox() {
		return jComboBox;
	}

	public void setjComboBox(JComboBox jComboBox) {
		this.jComboBox = jComboBox;
	}

	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		g.setColor(Color.PINK);
		g.setFont(new Font("Serif",Font.BOLD,50));
		g.drawString(score+"", 40, 290);
		g.drawString(time+"", 40, 360);

	}
	

}
