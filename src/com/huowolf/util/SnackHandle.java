package com.huowolf.util;

import com.alibaba.fastjson.JSON;
import com.huowolf.entities.Food;
import com.huowolf.entities.Ground;
import com.huowolf.entities.Snake;
import com.huowolf.server.vo.Message;

import java.awt.*;
import java.util.LinkedList;

/**
 * Created by wangjie on 2018/3/19.
 * 蛇移动类
 * {"body":[{"x":15,"y":12},{"x":14,"y":12},{"x":13,"y":12}],"eatBody":false,"foodCount":0,"head":{"x":15,"y":12},"life":true,"listeners":[],"newDirection":-2,"oldDirection":-2,"oldTail":{"x":12,"y":12},"pause":false,"size":3,"sleepTime":300}
 */
public class SnackHandle {

    //代表方向的常
    public static final int UP = 1;
    public static final int DOWN = -1;
    public static final int LEFT = 2;
    public static final int RIGHT = -2;

    //获取蛇移动方向
    public static void move(Snake snake){
        int oldDirection = snake.getOldDirection();

        int newDirection = snake.getNewDirection();

        LinkedList<Point> body = snake.getBody();

        if(!(oldDirection + newDirection==0)) {
            snake.setOldDirection(newDirection);
        }

        //去尾
        snake.setOldTail(body.removeLast());

        int x = body.getFirst().x;
        int y = body.getFirst().y;

        switch(oldDirection) {
            case UP:
                y--;
                if(y<0) {
                    y = Global.HEIGHT -1 ;
                }
                break;
            case DOWN:
                y++;
                if(y >= Global.HEIGHT) {
                    y = 0;
                }
                break;
            case LEFT:
                x--;
                if(x<0) {
                    x = Global.WIDTH-1;
                }
                break;
            case RIGHT:
                x++;
                if(x >= Global.WIDTH) {
                    x = 0;
                }
                break;
        }
        Point newHead = new Point(x, y);
        //加头
        body.addFirst(newHead);
        snake.setBody(body);
    }


    /**
     * 处理蛇游戏规则
     * @param message
     */
    public static void handLife(Message message){
        Food food = message.getFood();
        Snake snake = message.getSnake();
        Ground ground = message.getGround();

        if(food.isFoodEated(snake)) {
            snake.eatFood();
            food.newFood(ground.getPoint());
        }

//		//两人玩
//		if(food.isFoodEated(othersnake)) {
//			othersnake.eatFood();
//			food.newFood(ground.getPoint());
//
//			bottonPanel.repaint();
//			setScore();
//		}

//        if(ground.isGroundEated(snake)) {
//            snake.die();
//        }
//
//        if(snake.isEatBody()) {
//            snake.die();
//        }

//		snake.setSnake2(othersnake);
//		if(isEatOtherBody(snake,othersnake)){
//			snake.die();
//			bottonPanel.getStartButton().setEnabled(true);
//		}
        message.setFood(food);
        message.setGround(ground);
        message.setSnake(snake);
    }

    public static void main(String[] args){
        Snake snake = new Snake();
        snake.clear();
        snake.init(Global.WIDTH/2,Global.HEIGHT/2);

        for(int i=0;i<3;i++){
            move(snake);
            System.out.println(JSON.toJSONString(snake));
        }

    }
}
