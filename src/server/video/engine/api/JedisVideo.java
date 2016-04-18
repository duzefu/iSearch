package server.video.engine.api;
import server.video.engine.api.SerializeUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;



public class JedisVideo {
	private static String host = "localhost";
	private static int port = 6379;
	private static JedisPoolConfig jedisConfig = new JedisPoolConfig();
	private static JedisPool jedisPool = null;
	static {
	    jedisConfig.setMaxTotal(100);
	    jedisConfig.setMaxIdle(20);
	    jedisConfig.setMaxWaitMillis(10000);
	
	    jedisPool = new JedisPool(jedisConfig, host, port);
	}
	
	public boolean setVideoInfoList(String key, List<VideoInfo> lists) throws Exception{
		try {
			Jedis jedis = jedisPool.getResource();
			for(VideoInfo videoInfo : lists) {
				jedis.rpush(key.getBytes(), SerializeUtil.serialize(videoInfo));
			}
			return true;
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public List<VideoInfo> getVideoInfoList(String key) throws Exception {
		try {
			Jedis jedis = jedisPool.getResource();
			List<byte[]> bytes = jedis.lrange(key.getBytes(), 0, -1);
			if(bytes == null) {
				return null;
			}
			List<VideoInfo> res = new ArrayList<>();
			for(byte[] object:bytes){
				res.add((VideoInfo)SerializeUtil.unserialize(object));
			}
			return res;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
