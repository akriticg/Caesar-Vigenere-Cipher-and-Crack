import java.util.*;
import edu.duke.*;
import java.io.*;

public class VigenereBreaker {
    public String sliceString(String message, int whichSlice, int totalSlices) {
        String newString = "";
        for(int k = whichSlice; k < message.length(); k += totalSlices)
            {
                newString += message.charAt(k);
            }
        
        return newString;
    }
    
    public char mostCommonCharIn(HashSet<String> dictionary){
        int count = 0;
        int max = 0;
        char maxChar = 'a';
        HashMap<Character,Integer> charCount = new HashMap<Character, Integer>();
        
        for(String s : dictionary){
            for(int k = 0; k<s.length(); k++){
                char c = s.charAt(k);
                if(charCount.containsKey(c)){
                    count = charCount.get(c);
                    charCount.put(s.charAt(k), count+1);
                }
                else{
                charCount.put(s.charAt(k), 1);
                }
            }
        }
        
        for(Character c : charCount.keySet()){
            if(charCount.get(c)>max){
                max = charCount.get(c);
                maxChar = c;
            }
        }
        
        return maxChar;
    }

    public int[] tryKeyLength(String encrypted, int klength, char mostCommon) {
        int[] key = new int[klength];

       for (int i=0; i < key.length; i++){
            String sliced = sliceString(encrypted,i,klength);
            CaesarCracker cc = new CaesarCracker();
            int k = cc.getKey(sliced);
            key[i]= k;
        }
        return key;
    }
   
    public HashSet readDictionary(FileResource fr){
       HashSet<String> words = new HashSet<String>();
       for(String line : fr.lines()){
           line = line.toLowerCase();
           words.add(line);
        }
       return words;
    }
    
   public int countWords(String message, HashSet dictionary){
       String[] words = message.split("\\W+");
       int count = 0;
       for(String word:words){
           word = word.toLowerCase();
           if(dictionary.contains(word)){
               count += 1;
            }
           
        }
        return count;
    }
    
    public String breakForlanguage(String encrypted, HashSet dictionary){
        int max = 0;
        String maxdecrypted = "";
        int keylen = 0;
        for(int k = 1; k<=100; k++){
            int[] num = tryKeyLength(encrypted, k, mostCommonCharIn(dictionary));
            VigenereCipher vc = new VigenereCipher(num);
            String decrypted = vc.decrypt(encrypted);
   
            int count = countWords(decrypted, dictionary);
            if(count>max){
                keylen = num.length;
                max = count;
                maxdecrypted = decrypted;
            }
            
        }
        System.out.println("key length:" + keylen);
        return maxdecrypted;
    }
    
    public String breakForAllLangs(String encrypted, HashMap<String,HashSet<String>> languages){
        int num = 0;
        int max = 0;
        String maxLang = "";
        
        HashMap<String,Integer> newDecrypted = new HashMap<String, Integer>();
        for(String language : languages.keySet()){
            String decrypted = breakForlanguage(encrypted, languages.get(language));
            num = countWords(decrypted, languages.get(language));
            newDecrypted.put(language,num);
        }
        
        for(String s : newDecrypted.keySet()){
            if(newDecrypted.get(s) > max){
                max = newDecrypted.get(s);
                maxLang = s;
            }
        }
        System.out.println(maxLang);
        return breakForlanguage(encrypted,languages.get(maxLang));
    }
    

    public void breakVigenere () {
        HashMap<String,HashSet<String>> dict = new HashMap<String,HashSet<String>>();
        DirectoryResource dr = new DirectoryResource();
        for(File f : dr.selectedFiles()){
            FileResource fr = new FileResource(f);
            dict.put(f.getName(), readDictionary(fr));
            
        }
        
        FileResource fr2 = new FileResource("secretmessage4.txt");
        String encrypted = fr2.asString();
        System.out.println("-----------");
        System.out.println(breakForAllLangs(encrypted,dict));
        //System.out.println(breakForlanguage(message,dict));
        //int[] num1 = tryKeyLength(message, 57, 'e');
        //VigenereCipher vc1 = new VigenereCipher(num1);
        //String decrypted1 = vc1.decrypt(message);
        //System.out.println("valid words actual : " + countWords(decrypted1,dict));
        //int[] num = tryKeyLength(message, 38, 'e');
        //VigenereCipher vc = new VigenereCipher(num);
        //String decrypted = vc.decrypt(message);
        //System.out.println("Valid Words : " + countWords(decrypted,dict));
    }
    
}
