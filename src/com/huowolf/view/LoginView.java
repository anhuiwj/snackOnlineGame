package com.huowolf.view;

import com.huowolf.controller.Controller;
import com.huowolf.entities.Food;
import com.huowolf.entities.Ground;
import com.huowolf.entities.Snake;
import com.huowolf.game.GameFrame;
import com.huowolf.util.HandlerData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by wangjie on 2018/3/24.
 */
public class LoginView{
    private static int count = 0;
    private static JButton bt1;//登陆按钮
    private static JButton bt2;//注册
    public JLabel jl_1;//登录的版面
    private static JFrame jf_1;//登陆的框架
    private static JTextField jtext1;//用户名
    private static JPasswordField jtext2;//密码
    private static JLabel jl_admin;
    private static JLabel jl_password;

    public LoginView() {
        //初始化登陆界面
        Font font = new Font("黑体", Font.PLAIN, 20);//设置字体
        jf_1 = new JFrame("登陆界面");
        jf_1.setSize(450, 400);
        //给登陆界面添加背景图片
        ImageIcon bgim = new ImageIcon(LoginView.class.getResource("login.jpg"));//背景图案
        bgim.setImage(bgim.getImage().
                getScaledInstance(bgim.getIconWidth(),
                        bgim.getIconHeight(),
                        Image.SCALE_DEFAULT));
        jl_1 = new JLabel();
        jl_1.setIcon(bgim);

        jl_admin = new JLabel("用户名");
        jl_admin.setBounds(20, 50, 60, 50);
        jl_admin.setFont(font);

        jl_password = new JLabel("密码");
        jl_password.setBounds(20, 120, 60, 50);
        jl_password.setFont(font);

        bt1 = new JButton("登陆");         //更改成loginButton
        bt1.setBounds(90, 250, 100, 50);
        bt1.setFont(font);

        bt2 = new JButton("注册");
        bt2.setBounds(250, 250, 100, 50);
        bt2.setFont(font);

        //加入文本框
        jtext1 = new JTextField("");
        jtext1.setBounds(150, 50, 250, 50);
        jtext1.setFont(font);

        jtext2 = new JPasswordField("");//密码输入框
        jtext2.setBounds(150, 120, 250, 50);
        jtext2.setFont(font);

        jl_1.add(jtext1);
        jl_1.add(jtext2);

        jl_1.add(jl_admin);
        jl_1.add(jl_password);
        jl_1.add(bt1);
        jl_1.add(bt2);

        jf_1.add(jl_1);
        jf_1.setVisible(true);
        jf_1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf_1.setLocation(300, 400);

        bt1.addActionListener(new LoginView.LoginListener());
        bt2.addActionListener(new LoginView.RegiterListener());

    }

    /**
     * 登录
     *  * 处理点击事件
     * 1.登陆按钮点击事件，判断账号密码是否正确，若正确，弹出监测信息界面
     * 否则，无响应（暂时无响应）
     * ：后可在登陆界面添加一个logLabel提示用户是否用户密码正确
     * 2.退出按钮，直接退出程序
     */
    class LoginListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String name=jtext1.getText();
            char[] password=jtext2.getPassword();

            try {
                if(HandlerData.isInfoExits(name,String.valueOf(password)))
                {
                    new GameFrame(new Controller(new Snake(), new Food(), new Ground(),
                            new GamePanel(), new GameMenu(),new BottonPanel()),name).run();
                }
                else {
                    JOptionPane.showMessageDialog(null, "用户名或密码错误", "提示", 0);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }


    /**
     * 注册
     */
    class RegiterListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            String name=jtext1.getText();
            char[] password=jtext2.getPassword();

            if(name.length() >0
                    && password.length >0){
                if(!HandlerData.isExitsUser(name)){
                    HandlerData.registerUser(name,new java.lang.String(password));
                    JOptionPane.showMessageDialog(null, "注册成功", "提示", 1);
                }else {
                    JOptionPane.showMessageDialog(null, "用户已存在", "提示", 0);
                }
            }else {
                JOptionPane.showMessageDialog(null, "用户名或密码不可为空", "提示", 0);
            }
        }
    }
}
