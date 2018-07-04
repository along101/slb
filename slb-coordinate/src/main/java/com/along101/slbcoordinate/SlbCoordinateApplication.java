package com.along101.slbcoordinate;

import com.along101.slbcoordinate.app.SlbSynTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SlbCoordinateApplication {

	@Autowired
	private SlbSynTest test;
	//@GetMapping("/test")
	public String getTest() {
		return test.test();
	}

}
