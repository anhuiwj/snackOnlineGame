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

    //蛇二信息
    private Snake snake2;

    //食物信息
    private Food food;

    //石头
    private Ground ground;

    //挑战者
    private String chanllengeName;

    //在线用户
    private List<String> onlineUsers;

    //当前移动步骤
    private int nowStap;

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

    public Snake getSnake2() {
        return snake2;
    }

    public void setSnake2(Snake snake2) {
        this.snake2 = snake2;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public Ground getGround() {
        return ground;
    }

    public void setGround(Ground ground) {
        this.ground = ground;
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
}
