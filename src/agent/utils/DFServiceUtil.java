package agent.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import server.commonutils.MyStringChecker;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class DFServiceUtil {

	/**
	 * 将这个Agent相关的所有服务注销
	 * @param agent
	 */
	public static void deRegisterAllService(Agent agent){
		
		if(null==agent) return;
		try{
			DFService.deregister(agent);
		}catch(Exception e){
			e.printStackTrace();
		}
		return;
	}
	
	/**
	 * 向DF注册服务
	 * @param registerAgent 提出注册服务的Agent对象
	 * @param serviceType 服务类型（名称）
	 * @return 
	 */
	public static boolean registerService(Agent a,
			String serviceType) {
		return registerService(a, serviceType, serviceType);
	}

	/**
	 * 向DF注册服务
	 * @param registerAgent 提出注册服务的Agent对象
	 * @param serviceType 服务类型（名称）
	 * @param serviceName 服务名（暂时可以不用填）
	 * @return 
	 */
	public static boolean registerService(Agent a,
			String serviceType, String serviceName) {

		boolean ret = false;
		if(MyStringChecker.isBlank(serviceType)||null==a) return ret; 
		if(MyStringChecker.isBlank(serviceName)) serviceName=serviceType;
		try {
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(a.getAID());
			ServiceDescription sd = new ServiceDescription();
			sd.setType(serviceType);
			sd.setName(serviceName);
			dfd.addServices(sd);
			DFService.register(a, dfd);
			ret = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}
	
	public static boolean registerService(Agent a, String serviceType, DFAgentDescription dfd, ServiceDescription sd){
	
		return registerService(a, serviceType, serviceType, dfd, sd);
	}
	
	public static boolean registerService(Agent a,
			String serviceType, String serviceName, DFAgentDescription dfd, ServiceDescription sd){
		
		boolean ret = false;
		if(MyStringChecker.isBlank(serviceType)||null==a) return ret; 
		if(MyStringChecker.isBlank(serviceName)) serviceName=serviceType;
		if(null==dfd||null==sd) return registerService(a, serviceType, serviceName);
		
		try {
			dfd.clearAllServices();
			dfd.setName(a.getAID());
			sd.setType(serviceType);
			sd.setName(serviceName);
			dfd.addServices(sd);
			DFService.register(a, dfd);
			ret = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}
	
	public static DFAgentDescription[] searchService(Agent a, String serviceType){
		
		DFAgentDescription[] ret=null;
		if(null==a||MyStringChecker.isBlank(serviceType)) return ret;
		
		try{
		DFAgentDescription dfd=new DFAgentDescription();
		dfd.setName(null);
		ServiceDescription sd=new ServiceDescription();
		sd.setType(serviceType);
		dfd.addServices(sd);
		ret=DFService.search(a, dfd);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return ret;
	}
	
	/**
	 * 搜索提供了特定类型服务的Agent
	 * @param agent 请求搜索的Agent
	 * @param template 用于搜索时的模板对象
	 * @param sd 用于搜索时的服务描述对象
	 * @param serviceType 要搜索的
	 * @return
	 */
	public static DFAgentDescription[] searchService(Agent a, DFAgentDescription dfd, ServiceDescription sd, String serviceType){
		
		DFAgentDescription[] ret=null;
		if(MyStringChecker.isBlank(serviceType)) return ret;
		if(null==dfd||null==sd) return searchService(a, serviceType);
		
		dfd.clearAllServices();
		dfd.setName(null);
		sd.setType(serviceType);
		sd.setName(null);
		dfd.addServices(sd);
		try{
			ret=DFService.search(a, dfd);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return ret;
		
	}
	
}
