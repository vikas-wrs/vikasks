package com.vikak.zk;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

public interface ZkClient {

	void close() throws InterruptedException;

	void join(String groupName, String memberName) throws KeeperException,
			InterruptedException;

	void create(String groupName, CreateMode mode) throws KeeperException, InterruptedException;

	void connect(String hosts) throws IOException, InterruptedException;

	List<String> list(String groupName) throws KeeperException, InterruptedException;

	List<String> list() throws KeeperException, InterruptedException;

	void delete(String groupName) throws KeeperException, InterruptedException;

}
