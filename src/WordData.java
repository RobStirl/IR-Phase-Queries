import java.util.ArrayList;
import java.util.List;

// Each word will have a WordInDocsInfo. This contains a list of wordFreqData, each one containing a
// document the word is in and the frequency the word appears in said document. The list will have
// a wordFreqData for every document that contains the word

// Note: The Incidence Matrix uses the bitset found in myLibrary to determining if a word is a doc
// WordInDocsInfo is used by the Inverted Matrix, since it can quickly look up a word and find every doc associated with it
public class WordData
{
	private List<WordInstance> freqList;	// Stores the frequency of each word corresponding to the document
	private int freq;						// The number of docs containing this word
	
	WordData()
	{
		freqList = new ArrayList<WordInstance>();
		freq = 0;
	}
	
	// Get the list containing all the word's WordInDocsInfo
	public List<WordInstance> getWordData()
	{
		return freqList;
	}
	
	// Get the global frequency of the word appearing across all documents
	// If 5 documents contain this word, it will return 5
	public int getNumberOfDocsContainingWord()
	{
		return freq;
	}
	
	// Get the total number of times this word is used
	// If 5 documents contain this word, and each doc has the word
	// twice, it will return 10
	public int getTotalAccurancesOfWord()
	{
		int tot = 0;
		for(int i = 0; i < freqList.size(); ++i)
		{
			tot += freqList.get(i).wordFreq;
		}
		return tot;
	}
	
	
	// Adds a new bi-word to this word, with the corresponding doc id where
	// these two words were found
	public void addBiWord(int doc, int biWord)
	{
		for(WordInstance f : freqList)	// Search for the document in wordFreq
		{
			if(f.docID == doc)		// When found, add 1 count to the word frequency
			{
				f.addBiWord(biWord);
				return;
			}
		}
		System.out.println("ERROR_CANNOT_ADD_BIWORDID :" + biWord +": TO_NON-EXISTENT_DOCID :" + doc+ ":");
	}
	
	// Add a new document to the list of documents that have this word
	public void addDoc(int doc, int biWord)
	{
		for(WordInstance f : freqList)	// Search for the document in wordFreq
		{
			if(f.docID == doc)		// When found, add 1 count to the word frequency
			{
				f.wordFreq++;
				f.addBiWord(biWord);
				return;
			}
		}
		freq++;	// Increment global frequency
		// If it was not found in wordFreq, it doesn't exist yet so add a new one
		freqList.add(new WordInstance(doc, biWord));
	}
	
	// Returns a list of all the docs that have this word and biWordID as bi-words
	public List<Integer> getDocsThatContainBiWord(int biWordID)
	{
		List<Integer> temp = new ArrayList<Integer>();
		
		for(int i = 0; i < freqList.size(); i++)
		{
			if(freqList.get(i).isABiWord(biWordID))
				temp.add(freqList.get(i).docID);
		}
		
		return temp;
	}
}
