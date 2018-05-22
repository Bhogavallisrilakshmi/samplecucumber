package com.optum.bdd.rwe.test;

import org.junit.After;
import org.junit.Before;

public class TestJunit {
	
	@Before
	public void TestBefore() {
		System.out.println("TestBefore");
	}
	
	@After
	public void TestAfter() {
		System.out.println("AfterTests");
	}

}
