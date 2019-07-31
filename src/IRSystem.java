import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Map.Entry;

// Robert Stirling
// COSC 4315
// 8 March 2018
// Reads a directory of text files
// Allows user to search for documents that contain a search word
// Also support up to two word queries, which will display all the docs:
//		That contain the phrase "First Second"
//		That contain "First" and "Second" but not concurrently
//		That contain "First" but not "Second"
//		That contain "Second" but not "First"
public class IRSystem
{
	private MyLibrary lib = new MyLibrary();

	
	public void start() throws FileNotFoundException, UnsupportedEncodingException
	{
		String inDir,  firstWordOfDoc, secWordOfDoc;
		Scanner sc = new Scanner(System.in);
		File folder;
		Scanner scan;
		
		
		// The word that was read it on the last loop. This is used to store bi words
		// If this is the first word to be read in, lastWordID will be -1
	
		int firstWordID = -1;
		int secondWordID = -1;
		int docID;
	
		// User input
		System.out.println("Enter Input Directory: ");
		inDir = sc.nextLine();



		
		// Read in all the documents
		System.out.print("Reading documents...");
		folder = new File(inDir);
		File[] f = folder.listFiles();
		int badFiles = 0;
		for(int i = 0; i < f.length; ++i)
		{
			if(f[i].canRead())
			{
				try
				{
					scan = new Scanner(f[i]);
					scan.useDelimiter("[^A-Za-z0-9']+"); // Ignore all non-letters, numbers, and apostrophes 
					docID = lib.addDocID(f[i].getName());
					
					
					
					// Check to see if if there is at least a single word in the doc
					if(scan.hasNext())
					{
						firstWordOfDoc = scan.next().toLowerCase();
						firstWordID = lib.addWordID(firstWordOfDoc); // Add that word to the library
					}
					// While there are still words to be added
					while(scan.hasNext())
					{
						secWordOfDoc = scan.next().toLowerCase();
						secondWordID = lib.addWordID(secWordOfDoc); // Add the next word to the library
						
						// Connect the first word to the doc, and connect the second word to the first as a bi-word
						lib.addWordFromDoc(docID, firstWordID, secondWordID);
						
						// The second word is now the first, when a new word is brought in it will be "secondWordID"
						// The new firstWordID will be conected to the doc, and the new secondWordID will be linked to this one as a bi-word
						firstWordID = secondWordID;
					}
					// If there was no other words in this doc, then line will add the single firstWordID
					// It doesn't matter if it is a duplicate, since treemaps will ignore duplicates
					// Bi-words also ignore -1 as input
					lib.addWordFromDoc(docID, firstWordID, secondWordID);
					secondWordID = -1;
				}
				catch (FileNotFoundException e)
				{
					System.out.println("Error! Cannot read " + f[i].getName() + "!");
					++badFiles;
				}
			}
			else
			{
				System.out.println("Error! Cannot open " + f[i].getName() + "!");
				++badFiles;
			}
		}
		System.out.println("Done");
		System.out.println("\n" + f.length + " files found.");
		System.out.println("\n" + badFiles + " files could not be read.\n");
		
		while(true)
		{
			search();
			System.out.println("\n\nSearch finished... ");
			System.out.println("Type anything to search again or \'0\' to quit: ");
			docID = sc.nextInt(); //reusing variables 
			if(docID == 0)
				break;
		}
		System.out.println("\n\nDone...");

	}
	

	// Ask for one or two words. If they are incorrect words, provide similar words
	// and allow the user to choose. Provide all the documents
	private void search()
	{
		boolean noS1 = false, noS2 = false; // if no words were close to s1 or s2 (but might have matched the other), 
											// then quit after printing the appropriate list
		String s1 = "", s2 = "";
		int i1, i2;
		Scanner scan  = new Scanner(System.in);
		scan.useDelimiter("[^A-Za-z0-9' ]+");
		System.out.print("Enter word or phrase (two words): ");
		
		s1 = scan.next().toLowerCase();
		if(s1.contains(" "))
		{
			s2 = s1;
			s1 = s1.substring(0, s1.indexOf(" "));
			s2 = s2.substring(s2.indexOf(" ") + 1);
			if(s2.contains(" "))
			{
				System.out.print("Error: Please enter at most two words: ");
				return;
			}
			//s1 = s1.replaceAll(" ", "");
			//s2 = s2.replaceAll(" ", "");
		}
		System.out.println("");
			


		if(!lib.containsWord(s1))
		{
			List<String> words = new ArrayList<String>();
			int temp = -1;
			System.out.println("Couldn't find \"" + s1 + "\". ");
			words = lib.findSimularWords(s1);
			if(words.size()==0)
			{
				System.out.println("No words were close to " + s1 + ". Check spelling and restart...");
				noS1 = true;
				//return;
			}
			if(!noS1)
			{
				System.out.println("Showing simular words...");
				for(int i = 0; i < words.size(); ++i)
				{
					
					System.out.println((i+1) + ")\t"+ words.get(i));
				}
				while(temp==-1)
				{
					System.out.print("Enter number of the word you will like: ");
					if(scan.hasNextInt() )
					{
						temp = scan.nextInt();
						
						if(temp < 1 || temp > words.size())
						{
							System.out.println("Number out of range!");
							temp = -1;
						}
					}
					else
					{
						temp = -1;
						scan.next();
						System.out.println("Incorrect Input!");
					}
				}
				
				s1 = words.get(temp - 1);
			}
		}
		i1 = lib.getWordID(s1);
		
		
		// Is s2 empty? If so display s1 and return
		if(s2.equals(""))
		{
			System.out.print("Document(s) that contain the word \"" + s1 + "\": ");
			List<Integer> bi = lib.getDocsOfWord(i1);
			System.out.println(bi.size());
			for(int i = 0; i < bi.size(); i++)
			{
				System.out.println(lib.getDocFromID(bi.get(i)));
			}
			return;
		}
		System.out.println("");
		
		
		
		// More than one word
		
		
		
		
		
		if(!lib.containsWord(s2))
		{
			List<String> words = new ArrayList<String>();
			int temp = -1;
			System.out.println("Couldn't find \"" + s2 + "\". ");
			words = lib.findSimularWords(s2);
			if(words.size()==0)
			{
				System.out.println("No words were close to " + s2 + ". Check spelling and restart...");
				noS2 = true;
				//return;
			}
			if(!noS2)
			{
				System.out.println("Showing simular words...");
				for(int i = 0; i < words.size(); ++i)
				{
					System.out.println((i+1) + ")\t"+ words.get(i));
				}
				while(temp==-1)
				{
					System.out.print("Enter number of the word you will like: ");
					if(scan.hasNextInt() )
					{
						temp = scan.nextInt();
						
						if(temp < 1 || temp > words.size())
						{
							System.out.println("Number out of range!");
							temp = -1;
						}
					}
					else
					{
						temp = -1;
						scan.next();
						System.out.println("Incorrect Input!");
					}
				}
				
				s2 = words.get(temp - 1);
			}
		}
		
		
		i2 = lib.getWordID(s2);
		
		if(!noS1 && !noS2)
		{
			System.out.print("Document(s) that contain the phrase \"" + s1 + " " + s2 + "\": ");
			List<Integer> bi = lib.getDocIDsOfBiWords(i1, i2);
			System.out.println(bi.size());
			for(int i = 0; i < bi.size(); i++)
			{
				System.out.println(lib.getDocFromID(bi.get(i)));
			}
		}
		
		if(!noS1 && !noS2)
		{
			System.out.print("Document(s) that contain the word \"" + s1 + "\" and the word \"" + s2 + "\": ");
			List<Integer> nbi = lib.getDocsIDsOfNotBiWord(i1, i2);
			System.out.println(nbi.size());
			for(int i = 0; i < nbi.size(); i++)
			{
				System.out.println(lib.getDocFromID(nbi.get(i)));
			}
		}
		
		if((!noS1 && noS2) || (!noS1 && !noS2))
		{
			System.out.print("Document(s) that contain the word \"" + s1 + "\" but not the word \"" + s2 + "\": ");
			List<Integer> s1only = lib.getDocsIDsWithOneButNotAnother(i1, 0);
			System.out.println(s1only.size());
			for(int i = 0; i < s1only.size(); i++)
			{
				System.out.println(lib.getDocFromID(s1only.get(i)));
			}
		}
		
		if((noS1 && !noS2) || (!noS1 && !noS2))
		{
			System.out.print("Document(s) that contain the word \"" + s2 + "\" but not the word \"" + s1 + "\": ");
			List<Integer> s2only = lib.getDocsIDsWithOneButNotAnother(i2, 0);
			System.out.println(s2only.size());
			for(int i = 0; i < s2only.size(); i++)
			{
				System.out.println(lib.getDocFromID(s2only.get(i)));
			}
		}
		
		
	}
	
}
