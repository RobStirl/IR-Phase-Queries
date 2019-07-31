
import java.util.ArrayList;
import java.util.List;

// Each instance of a word will keep track of a document and the number of times
// that word appears in that doc, as well as all the bi-words of this word
public class WordInstance
{
	public int docID;					// The doc associated with the frequency counter for the word
	public int wordFreq;				// The frequency of the word in this document
	private List<Integer> biWordIDs;	// A list of all the bi-words of this word in this document
	private List<Integer> numOfBiWords;	// Stores the number of times a bi-word appears (this is a parallel array to biWordIDs)

	WordInstance(Integer d, int biWord)
	{
		docID = d;
		wordFreq = 1;
		biWordIDs = new ArrayList<Integer>();
		numOfBiWords = new ArrayList<Integer>();
		
		if(biWord != -1)
		{
			biWordIDs.add(biWord);
			numOfBiWords.add(1);
		}
	}
	
	// Add a new bi-word to this word
	public void addBiWord(int wordID)
	{
		if(wordID == -1)
			return;
		
		for(int i = 0; i < biWordIDs.size(); i++)
		{
			if(biWordIDs.get(i).equals(wordID))
			{
				numOfBiWords.set(i, numOfBiWords.get(i) + 1);
				return;
			}
		}
		biWordIDs.add(wordID);
		numOfBiWords.add(1);
	}
	
	// Returns true if wordID is a bi-word of this word
	public boolean isABiWord(int wordID)
	{
		return biWordIDs.contains(wordID);
	}
	
	// Returns the number of times wordID is a bi-word of this word
	// Returns 0 if wordID is not a bi-word of this word
	public int getBiWordCountOfAWord(int wordID)
	{
		for(int i = 0; i < biWordIDs.size(); i++)
		{
			if(biWordIDs.get(i).equals(wordID))
			{
				return (int)numOfBiWords.get(i);
			}
		}
		return 0;
	}
	
	
}

