package com.vikak.zk;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

public class ZkClientImpl implements ZkClient {

	private static final int SESSION_TIMEOUT = 5000;
	private ZooKeeper zk;
	private CountDownLatch connectedSignal = new CountDownLatch(1);

	@Override
	public void connect(String hosts) throws IOException, InterruptedException {
		Watcher watcher = new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				if (event.getState() == Event.KeeperState.SyncConnected) {
					System.out.println("Connected...");
					connectedSignal.countDown();
				}
			}
		};
		zk = new ZooKeeper(hosts, SESSION_TIMEOUT, watcher);
		connectedSignal.await();
	}

	@Override
	public void create(String groupName, CreateMode mode)
			throws KeeperException, InterruptedException {
		if (mode == null) {
			throw new IllegalArgumentException(
					"Invalid request to create group with mode " + mode);
		}
		String path = "/" + groupName;
		String createdPath = zk.create(path, null /* data */,
				ZooDefs.Ids.OPEN_ACL_UNSAFE, mode);
		System.out.println("Created " + createdPath);
	}

	@Override
	public void join(String groupName, String memberName)
			throws KeeperException, InterruptedException {
		String path = "/" + groupName + "/" + memberName;
		String createdPath = zk.create(path, null/* data */,
				ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		System.out.println("Created " + createdPath);
	}

	@Override
	public List<String> list(String groupName) throws KeeperException,
	InterruptedException {
		String path = "/" + groupName;
		return zk.getChildren(path, false);
	}

	@Override
	public List<String> list() throws KeeperException, InterruptedException {
		String path = "/";
		return zk.getChildren(path, false);
	}

	@Override
	public void close() throws InterruptedException {
		zk.close();
	}

	@Override
	public void delete(String groupName) throws KeeperException,
	InterruptedException {
		String path = "/" + groupName;
		List<String> children = zk.getChildren(path, false);
		for (String child : children) {
			zk.delete(path + "/" + child, -1);
		}
		zk.delete(path, -1);
	}

	public static void main(String[] args) throws Exception {
		ZkClientImpl createGroup = new ZkClientImpl();
		createGroup.connect("localhost:2181");
		createGroup.create("test-123", CreateMode.EPHEMERAL_SEQUENTIAL);

		createGroup.join("test-group", "m");
		createGroup.close();
	}

}
