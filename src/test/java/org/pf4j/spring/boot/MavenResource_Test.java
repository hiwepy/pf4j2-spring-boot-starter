package org.pf4j.spring.boot;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.cloud.deployer.resource.maven.MavenProperties;
import org.springframework.cloud.deployer.resource.maven.MavenProperties.RemoteRepository;
import org.springframework.cloud.deployer.resource.maven.MavenResource;

/**
 */
public class MavenResource_Test {

	private static String coordinates = "com.squareup.okhttp3:okhttp:3.13.1";
	private static MavenProperties properties = new MavenProperties();

	static {

		// 当Maven验证构件校验文件失败时该怎么做-ignore（忽略），fail（失败），或者warn（警告）。
		properties.setChecksumPolicy("warn");
		// 连接超时时间
		properties.setConnectTimeout(100000);
		// 本地Maven仓库
		properties.setLocalRepository("E:\\Java\\.m2\\repository2");
		// 是否离线模式
		properties.setOffline(false);
		// 远程仓库地址
		Map<String, RemoteRepository> remoteRepositories = new HashMap<String, RemoteRepository>();

		remoteRepositories.put("maven-local", new RemoteRepository("http://localhost:8081/repository/maven-public/"));

		properties.setRemoteRepositories(remoteRepositories);
		// 请求超时时间
		properties.setRequestTimeout(50000);
		// 除了解析JAR工件之外，如果为true，则解析POM工件。 这与Maven解析工件的方式一致。
		properties.setResolvePom(true);
		// 该参数指定更新发生的频率。Maven会比较本地POM和远程POM的时间戳。这里的选项是：always（一直），daily（默认，每日），interval：X（这里X是以分钟为单位的时间间隔），或者never（从不）。
		properties.setUpdatePolicy("always");
	}

	// @Test
	public void testResource1() {

		MavenResource resource = MavenResource.parse(coordinates);

		System.out.println(resource.toString());
		System.out.println("GroupId:" + resource.getGroupId());
		System.out.println("ArtifactId:" + resource.getArtifactId());
		System.out.println("Classifier:" + resource.getClassifier());
		System.out.println("Version:" + resource.getVersion());

		System.out.println("Description:" + resource.getDescription());
		System.out.println("Extension:" + resource.getExtension());
		System.out.println("Filename:" + resource.getFilename());
		try {

			resource.getFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testResource2() {

		MavenResource resource = new MavenResource.Builder().groupId("com.squareup.okhttp3").artifactId("okhttp")
				.version("3.14.1").build();

		System.out.println(resource.toString());
		System.out.println("GroupId:" + resource.getGroupId());
		System.out.println("ArtifactId:" + resource.getArtifactId());
		System.out.println("Classifier:" + resource.getClassifier());
		System.out.println("Version:" + resource.getVersion());

		System.out.println("Description:" + resource.getDescription());
		System.out.println("Extension:" + resource.getExtension());
		System.out.println("Filename:" + resource.getFilename());
		try {
			resource.getFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testResource3() {

		MavenResource resource = MavenResource.parse(coordinates, properties);

		System.out.println(resource.toString());
		System.out.println("GroupId:" + resource.getGroupId());
		System.out.println("ArtifactId:" + resource.getArtifactId());
		System.out.println("Classifier:" + resource.getClassifier());
		System.out.println("Version:" + resource.getVersion());

		System.out.println("Description:" + resource.getDescription());
		System.out.println("Extension:" + resource.getExtension());
		System.out.println("Filename:" + resource.getFilename());

		try {
			resource.getFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testResource4() {
		
		// 本地Maven仓库
		properties.setLocalRepository("E:\\Java\\.m2\\repository3");
				
		MavenResource resource = new MavenResource.Builder(properties).groupId("com.squareup.okhttp3")
				.artifactId("okhttp").version("3.14.1").build();

		System.out.println(resource.toString());
		System.out.println("GroupId:" + resource.getGroupId());
		System.out.println("ArtifactId:" + resource.getArtifactId());
		System.out.println("Classifier:" + resource.getClassifier());
		System.out.println("Version:" + resource.getVersion());

		System.out.println("Description:" + resource.getDescription());
		System.out.println("Extension:" + resource.getExtension());
		System.out.println("Filename:" + resource.getFilename());

		try {
			resource.getFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
