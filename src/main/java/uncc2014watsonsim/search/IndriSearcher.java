package uncc2014watsonsim.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import privatedata.UserSpecificConstants;
import uncc2014watsonsim.Passage;
import uncc2014watsonsim.Score;
import uncc2014watsonsim.StringUtils;
import uncc2014watsonsim.scorers.Merge;
import uncc2014watsonsim.Translation;
import lemurproject.indri.ParsedDocument;
import lemurproject.indri.QueryEnvironment;
import lemurproject.indri.ScoredExtentResult;

/**
 *
 * @author Phani Rahul
 */
public class IndriSearcher extends Searcher {
	private QueryEnvironment q;
	private boolean enabled = true;
	/**
	 * Setup the Indri Query Environment.
	 * The "indri_index" property is the Indri index path
	 * @param config  The configuration Properties
	 */
	public IndriSearcher(Properties config) {
		q = new QueryEnvironment();
		try {
			q.addIndex(StringUtils.getOrDie(config, "indri_index"));
		} catch (Exception e) {
			System.out.println("Setting up the Indri index failed."
					+ " Is the index in the correct location?"
					+ " Is indri_jni included?");
			e.printStackTrace();
			enabled=false;
		}
		Score.register("INDRI_ANSWER_SCORE", Double.NaN, Merge.Mean);
		Score.register("INDRI_ANSWER_RANK", Double.NaN, Merge.Mean);
		Score.register("INDRI_ANSWER_PRESENT", 0.0, Merge.Or);
	}
	
	public List<Passage> query(String query) {
		if (!enabled) return new ArrayList<>();
		// Run the query
		query = Translation.getIndriQuery(query);
		
		ScoredExtentResult[] ser;
		// Fetch all titles, texts
		String[] docnos;
		// If they have them, get the titles and full texts
		//ParsedDocument[] full_texts;
		String[] titles;
		try {
			ser = q.runQuery(query, MAX_RESULTS);
			docnos = q.documentMetadata(ser, "docno");
			//full_texts = IndriSearcher.q.documents(ser);
			titles = q.documentMetadata(ser, "title");
		} catch (Exception e) {
			// If any other step fails, give a more general message but don't die.
			System.out.println("Querying Indri failed. Is the index in the correct location? Is indri_jni included?");
			e.printStackTrace();
			return new ArrayList<>();
		}

		// Compile them into a uniform format
		List<Passage> results = new ArrayList<Passage>();
		for (int i=0; i<ser.length; i++) {
	    	results.add(new Passage(
    			"indri",         	// Engine
    			titles[i],	        // Title
    			"", //full_texts[i].text, // Full Text
				docnos[i])          // Reference
			.score("INDRI_ANSWER_RANK", (double) i)
			.score("INDRI_ANSWER_SCORE", ser[i].score)
			.score("INDRI_ANSWER_PRESENT", 1.0));
		}
		// Indri's titles and full texts could be empty. If they are, fill them from sources.db
		return fillFromSources(results);
	}
	
}
