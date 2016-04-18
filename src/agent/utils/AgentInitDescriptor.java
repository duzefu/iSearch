package agent.utils;

public class AgentInitDescriptor {

	private String m_strPathofAgent;
	private String[] m_arrNamesofAgent;
	private Object[] m_objArgsofAgent;
	private String[] m_arrDefaultNames;
	private String m_strEleNameInXMLFile;
	
	public void setAgentClassPath(String path){
		m_strPathofAgent=path;
	}
	public String getAgentClassPath(){
		return m_strPathofAgent;
	}
	
	public void setAgentNames(String[] names){
		m_arrNamesofAgent=names;
	}
	public String[] getAgentNames(){
		return m_arrNamesofAgent;
	}
	
	public void setAgentArgs(Object[] args){
		m_objArgsofAgent=args;
	}
	public Object[] getAgentArgs(){
		return m_objArgsofAgent;
	}
	
	public void setAgentDefaultNames(String[] defaultNames){
		m_arrDefaultNames=defaultNames;
	}
	public String[] getAgentDefaultNames(){
		return m_arrDefaultNames;
	}

	public void setElementName(String name){
		m_strEleNameInXMLFile=name;
	}
	public String getElementName(){
		return m_strEleNameInXMLFile;
	}
}
