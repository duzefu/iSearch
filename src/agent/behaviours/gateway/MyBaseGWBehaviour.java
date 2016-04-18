package agent.behaviours.gateway;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * 这个Agent的行为类的对象最终被GatewayAgent执行。
 * 执行这个行为时，把消息发送到接口Agent后，行为马上就结束，从Gateway Agent的行为队列的移除，不会再次执行。
 * 使用时，只需要生成这样的一个对象b，然后调用JadeGateway.execute(b)即可。
 * 这个行为要简单，只需要把消息转发出去就好，最好不要做更复杂的事。
 * 即使后续发现目前设计的只有一个Interface Agent会是系统的性能瓶颈，想增加到多个Interface Agent，这个类的修改思路也应该是：
 * 		1) 仍然把生成这个行为对象的过程放在Action里面做；
 * 		2) 确定要用哪个Interface Agent作为消息接收者的过程也应该在Action里面做。
 * 也就是这个行为的功能就仅限于把已经new出来的消息给转发出去，
 * 至于行为对象的生成，行为中要转发的消息对象的生成，确定接收者，都放到Action里面做。
 * @author zhou
 *
 */
public class MyBaseGWBehaviour extends OneShotBehaviour {

	private static final long serialVersionUID = -8055875453680547127L;

	private ACLMessage mMsg;

	/**
	 * 产生行为对象
	 * @param msg 要转发的消息
	 */
	public MyBaseGWBehaviour(ACLMessage msg) {
		super();
		mMsg = msg;
	}
	
	@Override
	public void action() {

		try {
			if (null != mMsg) {
				myAgent.send(mMsg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
