package server.commonutils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * 使用spring依赖注入时，最顶层类不能通过new的方式构造，那样不会触发注入，生成的类当中相关成员变量没有值
 * 必须用这个类提供的getBean(beanname)获取相应的对象，其中beanname是在beans.xml文件中为相应的类配置的bean的id
 * @author zhou
 *
 */
public class SpringBeanFactoryUtil implements BeanFactoryAware {

	//bean工厂
	private static BeanFactory beanFactory = null;

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		SpringBeanFactoryUtil.beanFactory = beanFactory;
	}

	public static BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public static Object getBean(String name) {
		if(null==name||name.isEmpty()) return null;
		return beanFactory.getBean(name);
	}

}
