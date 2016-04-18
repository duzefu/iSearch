package common.functions.resultanalysis;

import java.util.HashSet;
import java.util.Set;

import server.engine.api.EngineFactory.EngineName;

public class CoincidenceRateKey {

	private Set<EngineName> m_setEngine;
	
	public CoincidenceRateKey(){
		m_setEngine=new HashSet<EngineName>();
	}
	
	public CoincidenceRateKey(CoincidenceRateKey other){
		this();
		m_setEngine.addAll(other.m_setEngine);
	}
	
	public boolean addEngine(EngineName name){
		
		if(null==m_setEngine||m_setEngine.contains(name)) return false;
		m_setEngine.add(name);
		return true;
	}
	
	public void removeEngine(EngineName name){
		if(null!=m_setEngine) m_setEngine.remove(name);
	}
	
	public boolean equals(CoincidenceRateKey o){
		
		if(null==o) return false;
		return o.m_setEngine.equals(m_setEngine);
		
	}
	
	public int hashCode(){
		
		return m_setEngine.hashCode();
	}
}
