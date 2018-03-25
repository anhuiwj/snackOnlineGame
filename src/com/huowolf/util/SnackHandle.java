package com.huowolf.util;

import com.huowolf.entities.Food;
import com.huowolf.entities.Ground;
import com.huowolf.entities.Snake;
import com.huowolf.server.vo.Message;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
    public static void handLife(Message message, Snake snake){
        Food food = message.getFood();
        Ground ground = message.getGround();

        List<Food> foods = message.getFoods();

        if(food.isFoodEated(snake)) {
            snake.eatFood();
            food.newFood(message.getGround().getPoint());
            message.setFood(food);
        }

        //是不是成对
        if(message.getStapSnakes().size()%2 == 0){
            for(int i=0;i<message.getStapSnakes().size()-1;i++){
                if(isEatOtherBody(message.getStapSnakes().get(i),message.getStapSnakes().get(i+1))){
                    initNewFood(message.getStapSnakes().get(i),message.getStapSnakes().get(i+1),message);
                    message.getStapSnakes().get(i).setFoodCount(0);
                    deleteBody(message.getStapSnakes().get(i+1));
                }
                if(isEatOtherBody(message.getStapSnakes().get(i+1),message.getStapSnakes().get(i))){
                    initNewFood(message.getStapSnakes().get(i+1),message.getStapSnakes().get(i),message);
                    message.getStapSnakes().get(i+1).setFoodCount(0);
                    deleteBody(message.getStapSnakes().get(i));
                }

            }
        }

        if(foods != null){
            for(Food f:foods){
                if(f.isFoodEated(snake)) {
                    snake.eatFood();
                    foods.remove(f);
                }
            }
        }

        if(ground.isGroundEated(snake)) {
            initSnack(snake);
            snake.setFoodCount(0);
        }
        if(snake.isEatBody()) {
            initSnack(snake);
            snake.setFoodCount(0);
        }

    }

    //是否吃掉别人的身子
	public static boolean isEatOtherBody(Snake snake,Snake snake2) {
		for(int i=1;i<snake2.body.size();i++) {
			if(snake2.body.get(i).equals(snake.getHead())){
				return true;
			}
		}
		return false;
	}

    /**
     * 蛇死后
     * @param die
     * @param alive
     * @param message
     * @return
     */
    public static void  initNewFood(Snake die,Snake alive,Message message){
        List<Food> foods = new ArrayList<Food>();
        Food food = null;
        //蛇默认长度为3
        for(int i=0;i<die.getBody().size()-3;i++){
            food = new Food();
            food.newFood(message.getGround().getPoint());
            foods.add(food);
        }
        message.setFoods(foods);
    }

    /**
     *死后去吃去的食物
     * @param snake
     */
    public static void deleteBody(Snake snake){
        if(snake != null
                && snake.body != null){
            LinkedList<Point> body = snake.body;
            if(body.size() > 3){
                for(int i=body.size();i>body.size()-2;i++){
                    snake.body.remove(i);
                }
            }
        }
    }

    /**
     * 重生
     * @param snake
     */
    public static void initSnack(Snake snake){
        snake.setBody(new LinkedList<Point>());
        snake.init(Global.WIDTH/2,Global.HEIGHT/2);

    }

    public static void main(String[] args){
        Snake snake = new Snake();
        snake.clear();
        snake.init(Global.WIDTH/2,Global.HEIGHT/2);

//        for(int i=0;i<3;i++){
//            move(snake);
//            System.out.println(JSON.toJSONString(snake));
//        }
        System.out.println(2%2);


    }
}
