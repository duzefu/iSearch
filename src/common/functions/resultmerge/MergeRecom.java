package common.functions.resultmerge;
import java.util.Collections;
import java.util.List;

import common.entities.searchresult.Result;

public class MergeRecom {
	/**
	 * 对结果去重，更新相同结果的权重，
	 * @param list
	 */
	public static void resultMerge(List<Result> list)
	{
		for(int i=0;i<list.size();i++)
		{
			String link=list.get(i).getLink();
			for(int j=i+1;j<list.size();j++)
			{
				String xlink=list.get(j).getLink();
				if(link.equals(xlink))
				{
					String source=list.get(i).getSource();
					String xsource=list.get(j).getSource();
					if(!"系统推荐".equals(source)) list.get(i).setSource(source+" "+xsource);
					double value=list.get(i).getValue();
					double xvalue=list.get(j).getValue();
					list.get(i).setValue(value+xvalue);
					list.remove(j);
					j--;
				}
			}
			String source=list.get(i).getSource();
			list.get(i).setArray(source);
		}
		
		MergeSort resultmerge=new MergeSort();
		Collections.sort(list, resultmerge);
	}
}
