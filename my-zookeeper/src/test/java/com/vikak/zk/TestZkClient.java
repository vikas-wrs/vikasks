package com.vikak.zk;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestZkClient {
	private static final String HOST = "localhost:2181";

	@Test
	public void testCreate() throws IOException, InterruptedException,
			KeeperException {
		ZkClient zk = new ZkClientImpl();
		zk.connect(HOST);
		try {
			String grpName = "testcreate";
			zk.create(grpName, CreateMode.EPHEMERAL);
			List<String> list = zk.list();
			Assert.assertTrue(list.contains(grpName), list.toString());

			zk.delete(grpName);
			list = zk.list();
			Assert.assertFalse(list.contains(grpName), list.toString());
		} finally {
			zk.close();
		}
	}

	@Test
	public void testJoin() throws IOException, InterruptedException, KeeperException {
		ZkClient zk = new ZkClientImpl();
		zk.connect(HOST);
		try {
			String grpName = "testjoin";
			zk.create(grpName, CreateMode.PERSISTENT);
			List<String> list = zk.list();
			Assert.assertTrue(list.contains(grpName), list.toString());
			zk.join(grpName, "member-1");
			Assert.assertTrue(zk.list(grpName).contains("member-1"));

			zk.delete(grpName);
			list = zk.list();
			Assert.assertFalse(list.contains(grpName), list.toString());
		} finally {
			zk.close();
		}
	}
}
