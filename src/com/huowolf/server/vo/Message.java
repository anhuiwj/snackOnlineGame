package com.huowolf.server.vo;

import com.huowolf.entities.Food;
import com.huowolf.entities.Ground;
import com.huowolf.entities.Snake;

import java.io.Serializable;
import java.util.List;

/**
 * 交互
 * Created by wangjie on 2018/3/19.
 */
public class Message implements Serializable {
    //用户名
    private String snackName;

    //当前交互类型
    private int handleType;

    //蛇信息(当前用户)
    private Snake snake;

    //食物信息
    private Food food;

    //挑战者
    private String chanllengeName;

    //在线用户
    private List<String> onlineUsers;

    //当前移动步骤
    private int nowStap;

    //所有蛇移动信息
    public List<Snake> stapSnakes;

    private int newDirection;

    private Ground ground;

    private List<Food> foods;

    private int time;

    public String getSnackName() {
        return snackName;
    }

    public void setSnackName(String snackName) {
        this.snackName = snackName;
    }

    public Snake getSnake() {
        return snake;
    }

    public void setSnake(Snake snake) {
        this.snake = snake;
    }

    public int getHandleType() {
        return handleType;
    }

    public void setHandleType(int handleType) {
        this.handleType = handleType;
    }

    public List<String> getOnlineUsers() {
        return onlineUsers;
    }

    public void setOnlineUsers(List<String> onlineUsers) {
        this.onlineUsers = onlineUsers;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public String getChanllengeName() {
        return chanllengeName;
    }

    public void setChanllengeName(String chanllengeName) {
        this.chanllengeName = chanllengeName;
    }

    public int getNowStap() {
        return nowStap;
    }

    public void setNowStap(int nowStap) {
        this.nowStap = nowStap;
    }

    public List<Snake> getStapSnakes() {
        return stapSnakes;
    }

    public void setStapSnakes(List<Snake> stapSnakes) {
        this.stapSnakes = stapSnakes;
    }

    public int getNewDirection() {
        return newDirection;
    }

    public void setNewDirection(int newDirection) {
        this.newDirection = newDirection;
    }

    public Ground getGround() {
        return ground;
    }

    public void setGround(Ground ground) {
        this.ground = ground;
    }

    public List<Food> getFoods() {
        return foods;
    }

    public void setFoods(List<Food> foods) {
        this.foods = foods;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
