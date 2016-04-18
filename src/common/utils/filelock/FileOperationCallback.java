package common.utils.filelock;

import java.io.File;

/**
 * 文件读写锁的回调接口，进一步派生为读写回调接口
 * @author zhou
 *
 */
public interface FileOperationCallback {

	public boolean doOperation(File file, Object data);
	
}
