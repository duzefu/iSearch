package common.textprocess.textclassifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import common.textprocess.textsegmentation.*;

public class BayesClassifier {
	
	private static String[] classes = new String[] { "it", "financial",
			"sports", "health", "employment", "military", "education",
			"literature", "tourism" };

	public static String bayes(WordList wl) {
		List<ClassifyResult> cr = new ArrayList<ClassifyResult>();
		double c = 0;
		for (int i = 0; i < 9; i++) {
			c = PriorProbability.getprior(i)
					* ClassConditionalProbability
							.getClassConditionalProbability(wl, i);
			cr.add(new ClassifyResult(classes[i], c));
		}

		java.util.Collections.sort(cr, new Comparator<Object>() {
			@Override
			public int compare(final Object o1, final Object o2) {
				final ClassifyResult m1 = (ClassifyResult) o1;
				final ClassifyResult m2 = (ClassifyResult) o2;
				final double ret = m1.probility - m2.probility;
				if (ret < 0) {
					return 1;
				} else {
					return -1;
				}
			}
		});
		// System.out.println(cr.get(0).classification);
		return cr.get(0).classification;
	}
}
