package common.entities.blackboard;

public  class GetBlackboardThread implements Runnable{
		
				@Override
				public void run() {
					//获取黑板实例（由系统服务器端的一个单独的线程中执行，生成一个这样的黑板实例）
					Blackboard.getInstance();
				}
}
