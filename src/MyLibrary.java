import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;


// Stores all the words, documents, the bitset mapping a word to a doc, the wordInDocInfo for each word
// and the list of soundexes for each word
class MyLibrary
{
	// 			   < key, value >
	private TreeMap<String, Integer> wTree;			// Stores the words in alphabetical order with value = wordID
	private TreeMap<String, Integer> dTree; 		// Stores the documents in alphabetical order with value = docID
	private List<WordData> wordDataArray;			// A list of all WordData about each word

	private List<WordSoundexData> soundexDataList;	// A list of all WordSoundexData about each word
	
	public List<BitSet> truthArray;					// Each word will have a BitSet. Each bit represents a document and whether that word 
													// is in that document. The first doc loaded in will be pos(0), the next pos(1), etc
	
	private int m_wordID = 0;	// Used to generate a unique word id when a word is added
	private int m_docID = 0;	// Used to generate a unique doc id when a doc is added

	
	MyLibrary()
	{
		wTree = new TreeMap<String, Integer>();
		dTree = new TreeMap<String, Integer>();
		wordDataArray = new ArrayList<WordData>();
		soundexDataList = new ArrayList<WordSoundexData>();
		truthArray = new ArrayList<BitSet>();
	}
	
	// Get the id of a word in wTree, if it doesn't exist in
	// the tree yet, add it to the tree and return the new id
	public Integer addWordID(String w)
	{
		String temp = "";
		Integer id = wTree.get(w);
		if(id == null)
		{
			id = m_wordID;
			wTree.put(w, m_wordID);
			//soundexTree.put(createSoundex(w), m_soundexID);
			soundexDataList.add(new WordSoundexData(m_wordID, createSoundex(w)));
			
			m_wordID++;
		}
		return id;
	}
	
	
	// Get the id of a doc in dTree, if it doesn't exist in
	// the tree yet, add it to the tree and return the new id
	public Integer addDocID(String d)
	{
		Integer id = dTree.get(d);	
		if(id == null)				
		{
			id = m_docID;
			dTree.put(d, m_docID++);
		}	
		return id;
	}
	
	// Gets the id of a word, returns -1 if that word is not in the libary
	public Integer getWordID(String w)
	{
		Integer id = wTree.get(w);
		if(id == null)
		{
			id = -1;
		}
		return id;
	}

	
	// Map a new word to a document, pass in the ids by using getWordID(String w)
	// and getDocID(String w). If the word or doc doesn't exist in the trees,
	// getWordID and getDocID will add them
	// Also add that word previous to this word. This will be this word's bi word.
	// Other appearances of this word in this doc may have different bi words (which will also be added)
	public void addWordFromDoc(int _docID, int _wordID, int _lastWordID)
	{
		// Bad id check (would never happen if getWordID and getDocID are used)
		if(_docID >= m_docID || _wordID >= m_wordID || _lastWordID >= m_wordID)
		{
			//System.out.println("ERROR_BAD_ID");
			return;
		}
		BitSet d = getDocTruthArray(_wordID);
		d.set(m_docID);	
		WordData data = getAddWordData(_wordID);
		data.addDoc(_docID, _lastWordID);
	}
	
	// Gets the BitSet of a word.
	// Each word will have a BitSet. Each bit represents a document and whether that word 
	// is in that document. The first doc loaded in will be pos(0), the next pos(1), etc
	public BitSet getDocTruthArray(int wordID)
	{
		if(wordID >= truthArray.size())
		{
			int numDocs = wordID - truthArray.size() + 1;
			for(int i = 0; i < numDocs; ++i)
				truthArray.add(new BitSet());
		}

		return truthArray.get(wordID);
	}
	
	// Get the WordData of a word
	public WordData getAddWordData(Integer wID)
	{
		if(wID >= wordDataArray.size())	// If the word id is greater than the size of infoArray
		{								// then it doesn't exist yet
			
			int numDocs = wID - wordDataArray.size() + 1;
			for(int i = 0; i < numDocs; ++i)
				wordDataArray.add(new WordData());
		}
		
		return wordDataArray.get(wID);
	}
	
	// Adds a bi-word to a word
	public void addBiWord(int docID, int firstWordID, int secWordID)
	{
		getAddWordData(firstWordID).addBiWord(docID, secWordID);
	}

	// Returns the number of words in library
	public int getWordCount()
	{
		return m_wordID;
	}
	
	// Returns the number of documents in library
	public int getDocCount()
	{
		return m_docID;
	}
	
	// Returns true if a word is in the library
	public boolean containsWord(String w)
	{
		return wTree.containsKey(w);
	}
	// Returns true if a doc is in the library
	public boolean containsDoc(String d)
	{
		return dTree.containsKey(d);
	}
	// Returns true if a word is in the library
	public boolean containsWord(int wID)
	{
		return wTree.containsValue(wID);
	}
	// Returns true if a doc is in the library
	public boolean containsDoc(int dID)
	{
		return dTree.containsValue(dID);
	}
	
	// Returns the string name associated with the word id
	public String getWordFromID(int id)
	{
		
		for (Entry<String, Integer> entry : wTree.entrySet())
		{
			if(entry.getValue() == id)
			{
				return entry.getKey();
			}
		}
		return "ERROR_WORD_NOT_FOUND_BAD_ID"; // An invalid id was passed
	}
	
	// Returns the string name associated with the doc id
	public String getDocFromID(int id)
	{
		
		for (Entry<String, Integer> entry : dTree.entrySet())
		{
			if(entry.getValue() == id)
			{
				return entry.getKey();
			}
		}
		return "ERROR_DOCUMENT_NOT_FOUND_BAD_ID"; // An invalid id was passed
	}
	
	// Gets wTree, which contains all the words in the library
	public TreeMap<String, Integer> getWordTree()
	{
		return wTree;
	}
	
	// Returns a list of all the docs that contain firstWordID and SecondWordID as bi-words
	public List<Integer> getDocIDsOfBiWords(int firstWordID, int SecondWordID)
	{
		return wordDataArray.get(firstWordID).getDocsThatContainBiWord(SecondWordID);	
	}
	
	// Returns a list of all the docs that contain word
	public List<Integer> getDocsOfWord(int word)
	{
		List<Integer> docs = new ArrayList<Integer>();
		BitSet truth = truthArray.get(word);
		for(int i = 0; i <= m_docID; i++)
		{
			if(truth.get(i))
			{
				docs.add(i - 1);
			}
		}
		
		return docs;
	}
	
	// Returns a list of all the docs that contain firstWordID and SecondWordID excluding docs that
	// contain those words as bi-words
	public List<Integer> getDocsIDsOfNotBiWord(int firstWordID, int secWordID)
	{
		List<Integer> docs = new ArrayList<Integer>();
		//List<Integer> biWordDocs = getDocIDsOfBiWords( firstWordID, secWordID);
		BitSet tempTruthArray1 = new BitSet();
		tempTruthArray1.or(truthArray.get(firstWordID));
		BitSet tempTruthArray2 = new BitSet();
		tempTruthArray2.or(truthArray.get(secWordID));

		tempTruthArray1.and(tempTruthArray2);
		
		for(int i = 0; i <= m_docID; i++)
		{
			if(tempTruthArray1.get(i) )//&& !biWordDocs.contains(i - 1) )
			{
				docs.add(i - 1);
			}
		}
		
		return docs;
	}
	
	// Returns a list of all the docs that contain allowedWord and do not contain notAllowedWord
	public List<Integer> getDocsIDsWithOneButNotAnother(int allowedWord, int notAllowedWord)
	{

		List<Integer> docs = new ArrayList<Integer>();
		BitSet tempTruthArray1 = new BitSet();
		tempTruthArray1.or(truthArray.get(allowedWord));
		BitSet tempTruthArray2 = new BitSet();
		tempTruthArray2.or(truthArray.get(notAllowedWord));
		tempTruthArray1.andNot(tempTruthArray2);
		
		for(int i = 0; i <= m_docID; i++)
		{
			if(tempTruthArray1.get(i) )
			{
				docs.add(i - 1);
			}
		}
		return docs;
	}
	
	// Returns a 4 char string soundex of word
	public String createSoundex(String word)
	{
		if(word.isEmpty())
			return "0000";

		char [] sem = word.toCharArray();
		
		for(int i = 1; i < word.length(); ++i)
		{
			switch(sem[i])
			{
			case 'a': case 'e':case 'i':case 'o':case 'u':case 'h':case 'w':case 'y':
				sem[i] = '0';
				break;
			case 'b':case 'f':case 'p':case 'v':
				sem[i] = '1';
				break;
			case 'c':case 'g':case 'j':case 'k':case 'q':case 's':case 'x':case 'z':
				sem[i] ='2';
				break;
			case 'd':case 't':
				sem[i] = '3';
				break;
			case 'l':
				sem[i] = '4';
				break;
			case 'm':
			case 'n':
				sem[i] = '5';
				break;
			case 'r':
				sem[i] = '6';
				break;
			default:
				sem[i] = '0';
			}
		}
		
		// remove duplicates
        String output = "" + sem[0];
        for (int i = 1; i < sem.length; i++)
            if (sem[i] != sem[i-1] && sem[i] != '0')
                output += sem[i];

        // pad with 0's or truncate
        output = output + "0000";
        return output.substring(0, 4);
	}
	
	
	// Returns a list of words that have a similar soundex to w
	public List<String> findSimularWords(String w)
	{
		
		String badWord = createSoundex(w);
		List<String> out = new ArrayList<String>();
		//System.out.println(badWord);
		for(int i = 0; i < soundexDataList.size(); ++i  )
		{
			if(soundexDataList.get(i).soundex.matches(badWord))
				out.add(getWordFromID(soundexDataList.get(i).wordID));
		}
		
		return out;
	}
	
}