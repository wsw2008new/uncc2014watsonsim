package uncc2014watsonsim.research;

import uncc2014watsonsim.Answer;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Question;

/**
 * Returns 1.0 of the question text is found in the passage and 0.0 otherwise
 * @author Ken Overholt
 *
 */
public class QuestionInPassageScorer extends PassageScorer {
	
	@Override
	public double passage(Question q, Answer a, Passage p) {
		String rawText = q.getRaw_text().toLowerCase();
		String passageText = p.text;
		
		if (passageText.contains(rawText.toLowerCase()))
			return 1.0;
		else
			return 0.0;		
	}
	
}