package agent.utils;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.HashSet;
import java.util.Set;

import server.commonutils.MyStringChecker;

public class AIDPoolUpdateUtil {
	
	public static void updatePool(String serviceType, AgentIDPool pool, Agent a, DFAgentDescription dfd, ServiceDescription sd){
		
		if(null==dfd||null==sd) updatePool(serviceType, pool, a);
		if(MyStringChecker.isBlank(serviceType)||null==pool) return;
		
		DFAgentDescription[] result=DFServiceUtil.searchService(a, dfd, sd, serviceType);
		if(null==result||result.length==0) return;
		Set<AID> aidSet=new HashSet<AID>();
		for(int i=0;i<result.length;++i){
			DFAgentDescription dad=result[i];
			if(null==dad) continue;
			aidSet.add(dad.getName());
		}
		
		pool.update(aidSet);
	}
	
	public static void updatePool(String serviceType, AgentIDPool pool, Agent a){
		
		if(MyStringChecker.isBlank(serviceType)||null==pool) return;
		
		DFAgentDescription dfd=new DFAgentDescription();
		ServiceDescription sd=new ServiceDescription();
		DFAgentDescription[] result=DFServiceUtil.searchService(a, dfd, sd, serviceType);
		if(null==result||result.length==0) return;
		Set<AID> aidSet=new HashSet<AID>();
		for(int i=0;i<result.length;++i){
			DFAgentDescription dad=result[i];
			if(null==dad) continue;
			aidSet.add(dad.getName());
		}
		
		pool.update(aidSet);
	}
}
