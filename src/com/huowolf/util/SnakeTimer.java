/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.huowolf.util;

import com.huowolf.entities.Snake;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Sets up the timer for the multi-player snake game WebSocket example.
 */
public class SnakeTimer {


    private static Timer gameTimer = null;

    private static final long TICK_DELAY = 100;

    private static final ConcurrentHashMap<String, Snake> snakes =
            new ConcurrentHashMap<String, Snake>();

    public static synchronized void addSnake(Snake snake) {
        if (snakes.size() == 0) {
            startTimer();
        }
        snakes.put(snake.getSnackName(), snake);
    }


    public static Collection<Snake> getSnakes() {
        //返回集合镜像 若原对象改变 自己也改变
        return Collections.unmodifiableCollection(snakes.values());
    }


    public static synchronized void removeSnake(Snake snake) {
        snakes.remove(snake.getSnackName());
        if (snakes.size() == 0) {
            stopTimer();
        }
    }


    public static void tick() throws Exception {
        StringBuilder sb = new StringBuilder();
        for (Iterator<Snake> iterator = SnakeTimer.getSnakes().iterator();
                iterator.hasNext();) {
            Snake snake = iterator.next();
            SnackHandle.move(snake);
//            snake.update(SnakeTimer.getSnakes());
//            sb.append(snake.getLocationsJson());
//            if (iterator.hasNext()) {
//                sb.append(',');
//            }
        }
        broadcast(String.format("{'type': 'update', 'data' : [%s]}",
                sb.toString()));
    }

    public static void broadcast(String message) throws Exception {
        for (Snake snake : SnakeTimer.getSnakes()) {
            //snake.sendMessage(message);
        }
    }


    public static void startTimer() {
        gameTimer = new Timer(SnakeTimer.class.getSimpleName() + " Timer");
        gameTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    tick();
                } catch (Throwable e) {
                    System.out.println("Caught to prevent timer from shutting down"+e);
                }
            }
        }, TICK_DELAY, TICK_DELAY);
    }


    public static void stopTimer() {
        if (gameTimer != null) {
            gameTimer.cancel();
        }
    }
}
