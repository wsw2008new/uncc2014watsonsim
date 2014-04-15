package uncc2014watsonsim.research;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;
import uncc2014watsonsim.StringUtils;

public class SkipBigram extends Scorer {
	
	public double scorePassage(Question q, Answer a, Passage p) {
		
		// Jane Austen
		Set<String> a_set = generateBigrams(a.candidate_text);
		
		// Romantic novelist Jane Austen once wrote -the- book Emma.
		Set<String> p_set = generateBigrams(p.text);
		
		a_set.retainAll(p_set);
		
		return a_set.size();
	}
	
	private Set<String> generateBigrams(String text) {
		List<String> terms = StringUtils.tokenize(text);
		Set<String> bigrams = new HashSet<>();
		for (int ti=0; ti<terms.size()-1; ti++) {
			// First the bigram
			bigrams.add(terms.get(ti) + terms.get(ti+1));
			if (ti < terms.size()-2) {
				// Maybe the skip bigram, if we are more than one word from end
				bigrams.add(terms.get(ti) + terms.get(ti+1));
			}
		}
		return bigrams;
	}

}
