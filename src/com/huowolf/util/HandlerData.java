package com.huowolf.util;

import java.io.*;

/**
 * Created by wangjie on 2018/3/25.
 */
public class HandlerData {
    public static boolean isInfoExits(String name,String pwd) throws IOException {
        FileInputStream input=new FileInputStream("./login.txt");
        int length=0;
        String string=null;

        byte[] array=new byte[input.available()+1024];

        while((length=input.read(array))!=-1){
            string=new String(array,0,length);
        }
        String[] users=string.split("&&");//每个人的登录信息称为一组

        String user = name+"#"+pwd;

        for(String s:users){
            if(user.equals(s)){
                return true;
            }
        }

        input.close();
       return false;
    }

    public static boolean isExitsUser(String name){
        FileInputStream input= null;
        try {
            input = new FileInputStream("./login.txt");
            int length=0;
            String string=null;

            byte[] array=new byte[input.available()+1024];

            while((length=input.read(array))!=-1){
                string=new String(array,0,length);
            }
            String[] users=string.split("&&");//每个人的登录信息称为一组

            for(String s:users){
                if(s.contains(name)){
                    return true;
                }
            }
            input.close();
        }catch (Exception e){

        }
        return false;
    }

    public synchronized static boolean registerUser(String name,String pwd){
        BufferedWriter outputStream = null;
        try {
            File f = new File("./login.txt");
            outputStream = new BufferedWriter(new FileWriter(f,true));//true,则追加写入text文本
            outputStream.write("&&"+name+"#"+pwd);
            outputStream.flush();
            outputStream.close();
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }catch (IOException e){
            return false;
        }finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[]args){
        try {
          boolean b =   HandlerData.registerUser("snack1","123456");
            System.out.print(b);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
