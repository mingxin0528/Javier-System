package com.hwer.admin;

import cn.hutool.core.lang.UUID;
import com.hwer.admin.util.UserUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

//@SpringBootTest
class HwerApplicationTests {

	@Test
	void contextLoads() {

	}
	@Test
	void test(){
		System.out.println(UserUtil.decode("57,30,27,33,-53,-42,-115,74,-124,91,-51,-7,6,32,-72,-9,-93,-114,-102,-99,57,45,99,-44,74,-115,-74,-62,52,53,17,-70"));
		System.out.println(UUID.fastUUID());
	}

}
