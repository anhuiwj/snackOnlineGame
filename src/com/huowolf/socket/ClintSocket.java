package com.huowolf.socket;

import com.alibaba.fastjson.JSON;
import com.huowolf.entities.Snake;
import com.huowolf.server.vo.Message;
import com.huowolf.util.Global;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by wangjie on 2018/3/19.
 */
public class ClintSocket {
    public static void main(String[] args){
        connect_event();
    }
    public static void connect_event(){
        Socket sc = null;

        DataInputStream din = null;
        DataOutputStream dout = null;

        try
        {
            sc = new Socket("127.0.0.1",9999);
            din = new DataInputStream(sc.getInputStream());
            dout = new DataOutputStream(sc.getOutputStream());

            Message msg = new Message();
            Snake snake = new Snake();
            Snake snake2 = new Snake();
            snake2.init(20,20);
            snake.init(10,10);
            msg.setHandleType(Global.HANDLE_TYPE_FOUR);
            msg.setSnake(snake);
            msg.setSnackName("123");
            msg.setSnake2(snake2);
//			Snake user1 = JSON.parseObject(JSON.toJSONString(snake), Snake.class);
//			System.out.print(JSON.toJSONString(snake));
            String str =  JSON.toJSONString(msg);
            dout.writeUTF(str);

            for(;;){
                String readInfo = din.readUTF().trim();
                Message mssage =  JSON.parseObject(readInfo, Message.class);
                mssage.setHandleType(Global.HANDLE_TYPE_FOURTEEN);
                dout.writeUTF(readInfo);
                System.out.println(readInfo);
            }
//            din.close();
//            dout.close();
//            sc.close();
        }
        catch(Exception ee)
        {
            ee.printStackTrace();
        }finally {
            try {
                din.close();
                dout.close();
                sc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
