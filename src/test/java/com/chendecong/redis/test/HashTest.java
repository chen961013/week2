package com.chendecong.redis.test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.chendecong.redis.entity.User;
import com.cong.common.utils.DateUtil;
import com.cong.common.utils.RandomUtil;
import com.cong.common.utils.StringUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-hashredis.xml")
public class HashTest {
	@Resource
	private RedisTemplate<String, User> redisTemplate;
	
	@Test
	public void hashTest() {
		HashMap<String, User> map = new HashMap<String, User>();
		for (int i = 1; i <= 50000; i++) {
			User user = new User();

			// (1) ID使用1-5万的顺序号
			user.setId(i);

			String name = "";
			// 取到姓名长度为3时
			while (name.length() != 3) {
				name = StringUtil.generateChineseName();
			}
			user.setName(name);

			// (3) 性别在女和男两个值中随机
			String[] sex = { "男", "女" };
			user.setGender(sex[RandomUtil.random(0, sex.length - 1)]);

			// (4) 手机以13开头+9位随机数模拟
			user.setPhone("13" + RandomUtil.randomNumber(9));

			// (5) 邮箱以3-20个随机字母 + @qq.com | @163.com | @sian.com | @gmail.com | @sohu.com |
			// @hotmail.com | @foxmail.com模拟
			String email = "@qq.com  | @163.com | @sian.com | @gmail.com | @sohu.com | @hotmail.com | @foxmail.com";
			String[] split = email.replaceAll(" ", "").split("\\|");
			user.setEmail(
					RandomUtil.randomString(RandomUtil.random(3, 20)) + split[RandomUtil.random(0, split.length - 1)]);

			// (6) 生日要模拟18-70岁之间，即日期从1949年到2001年之间
			Calendar d1 = Calendar.getInstance();
			d1.set(d1.YEAR, 1949);
			Calendar d2 = Calendar.getInstance();
			d2.set(d2.YEAR, 2001);
			Date randomDate = DateUtil.randomDate(d1.getTime(), d2.getTime());
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

			user.setBirthday(format.format(randomDate));
			map.put(i+"", user);
			//System.out.println(user);
		}

		HashOperations opsForHash = redisTemplate.opsForHash();
		long star = System.currentTimeMillis();
		opsForHash.putAll("user_hash", map);
		long end = System.currentTimeMillis();
		System.out.println("用时" + (end - star) + "ms");
		System.out.println("数量" + map.size() + "条");
	}

}
