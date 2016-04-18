package agent.containers;

import com.sun.org.apache.bcel.internal.util.ClassPath;

import server.info.config.agent.ContainerConstInfo;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;

public class MainContainer {

	private static MainContainer instance;
	private AgentContainer mainContainer;
	
	private MainContainer(){}
	
	private static MainContainer getInstance(){
		
		if(null==instance){
			synchronized (MainContainer.class) {
				if(null==instance) instance=new MainContainer();
			}
		}
		return instance;
	}
	
	/**
	 * 获取当前平台（每一个JVM上唯一的）的唯一主容器，如果主容器还没有初始化，将初始化主容器
	 * 主容器的配置信息由ContainerConstInfo类的相关字符串常量控制
	 * @return 主容器对象
	 */
	public static AgentContainer get(){
		return getInstance().getMainContainer();
	}
	
	private AgentContainer getMainContainer(){
		
		if(null==mainContainer) createMainContainerInstance();
		return mainContainer;
	}
	
	synchronized void createMainContainerInstance() {

		if(null!=mainContainer) return;
		Runtime rt = Runtime.instance();
		rt.setCloseVM(true);
		Profile profile = new ProfileImpl(
				ContainerConstInfo.MAIN_CONTAINER_HOST,
				ContainerConstInfo.MAIN_CONTAINER_PORT,
				ContainerConstInfo.PLATFORM_NAME);
		if(null==mainContainer) mainContainer = rt.createMainContainer(profile);

	}
}
