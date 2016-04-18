package agent.utils;

import java.io.IOException;

import server.info.config.agent.CommonAgentNames;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import agent.behaviours.gateway.MyBaseGWBehaviour;
import agent.data.inmsg.DataToInterfaceAgent;

/**
 * 各个Action用这个类的函数来创建Gateway Agent的行为对象（MyBaseGatewayBehaviour类或其子类的对象）
 * @author zhou
 *
 */
public class GatewayBehaviourUtil {

	//Gateway Agent的交互对象只有一个：接口Agent
	static private AID receiver = new AID(CommonAgentNames.INTERFACE_AGENT,
			AID.ISLOCALNAME);
	
	/**
	 * 生成Gateway Agent要执行的行为，用这个函数的返回值执行JadeGateway.execute(b)之后，参数中的data对象是可以重用的。
	 * @param data 要转发的数据，必须实现序列化接口
	 * @return 生成的行为对象
	 * @throws IOException 由于data未序列化，试图把参数中的数据设置在ACL消息中时抛出的异常
	 */
	public static MyBaseGWBehaviour getBaseBehaviour(DataToInterfaceAgent data) throws IOException{

		if(null==data) return null;
		
		ACLMessage msg=prepareBaseACLMessage(data);
		MyBaseGWBehaviour ret=new MyBaseGWBehaviour(msg);
		
		return ret;
	}
	
	/**
	 * 建立Gateway Agent的行为要发送的消息
	 * @param data
	 * @return
	 * @throws IOException
	 */
	private static ACLMessage prepareBaseACLMessage(DataToInterfaceAgent data) throws IOException{
		
		ACLMessage ret=new ACLMessage(ACLMessage.INFORM);
		ret.addReceiver(receiver);
		ret.setContentObject(data);
		return ret;
	}
	
}
