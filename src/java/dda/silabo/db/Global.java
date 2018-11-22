/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dda.silabo.db;

import redis.clients.jedis.Jedis;

/**
 *
 * @author
 */
public class Global {

    public static String ipRedis = "172.17.102.143";
    public static Integer portRedis = 6379;

    public Jedis conexion() {
        Jedis jedis = null;
        try {
            jedis = new Jedis(ipRedis, portRedis);
            jedis.auth("redisespoch2018");
        } catch (Exception e) {
        }
        return jedis;
    }

}
