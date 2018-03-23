package com.huowolf.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import com.huowolf.entities.Food;
import com.huowolf.entities.Ground;
import com.huowolf.entities.Snake;
import com.huowolf.util.Global;

public class GamePanel extends JPanel{

	private static final long serialVersionUID = 1L;
	
	private Snake snake;
	private Snake snake2;
	private Food food;
	private Ground ground;	
	public Color backgroundColor;
	
	public GamePanel() {
		setLocation(0, 0);		
		this.setSize(Global.WIDTH * Global.CELL_SIZE, Global.HEIGHT
				* Global.CELL_SIZE);
		this.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		this.setFocusable(true);
		
	}


	public void display(Snake snake,Snake snake2,Food food,Ground ground) {
		this.snake = snake;
		this.snake2 = snake2;
		this.food = food;
		this.ground = ground;
		
		repaint();
	}

	
	public void clearDraw(Graphics g) {
			if(backgroundColor==null) {
				g.setColor(new Color(0x00FFFF));
			}
			else {
				g.setColor(backgroundColor);
			}
			g.fillRect(0, 0, Global.WIDTH*Global.CELL_SIZE, Global.HEIGHT*Global.CELL_SIZE);
	}
	
	
	@Override
	public void paint(Graphics g) {
			clearDraw(g);
			if(ground != null && snake != null && food != null
					) {
					ground.drawMe(g);
					food.drawMe(g);
					snake.drawMe(g);
					if(snake2 != null){
						snake2.drawMe(g);
					}
			}
			if(snake!=null && snake.isLife()==false)  {
				recover(g);
			}
	
		}

	
	public void recover(Graphics g) {
		clearDraw(g);
		
		g.setColor(Color.GREEN);
		g.setFont(new Font("Serif",Font.BOLD,50));
		g.drawString("Game Over", 130, 210);
		
	}
	
	
}
