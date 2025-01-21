package com.example.countingandroid;


import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

public class Main extends AppCompatActivity {
    //functions on the display
    private TextView answer;
    private TextInputEditText fileName;
    private Button getWordCountButton, getSentenceCountButton, getUniqueWordsButton, getHighestFreqButton, paragraphGeneratorButton, nextButton, backButton, exportButton;
    private static ArrayList<String> commonWords = new ArrayList<>();
    private static ArrayList<String> words = new ArrayList<>();

    //properties within code
    private static int wordCount = 0;
    private static int sentenceCount = 0;
    private static ArrayList<Integer> counterArray;
    private static ArrayList<String> uniqueWords;
    private static String paragraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
//connecting properties with functions on the display
        fileName = findViewById(R.id.textInputEditText);
        getWordCountButton = findViewById(R.id.buttonGetWordCount);
        getUniqueWordsButton = findViewById(R.id.getUniqueWordsButton);
        getSentenceCountButton = findViewById(R.id.getSentenceCountButton);
        getHighestFreqButton = findViewById(R.id.getHighestFreqButton);
        paragraphGeneratorButton = findViewById(R.id.paragraphGeneratorButton);
        nextButton = findViewById(R.id.nextButton);
        backButton = findViewById(R.id.backButton);
        exportButton = findViewById(R.id.buttonExport);
        readTextFile(fileName);
        removeCommonWords();
        try {
            loadCommonWords();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        getWordCountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answer.setText("The total word count is " + getWordCount() + ".");
            }
        });

        getSentenceCountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answer.setText("The total sentence count is " + getSentenceCount() + ".");
            }
        });

        makeCounterArraylist();
        sortDescending();

        getUniqueWordsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answer.setText("There are " + uniqueWords.size() + " unique words, which are:" + getUniqueWords());
            }
        });

        getHighestFreqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answer.setText(getHighestFreq());
            }
        });

    }

    //load common words from assets folder
    public void loadCommonWords() throws IOException {
        AssetManager assetManager = this.getResources().getAssets(); //access folder, this is an InputStream type
        InputStream inputStream = assetManager.open("commonWords.txt");
        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNext()){
            commonWords.add(scanner.next());
        }
        scanner.close();
        inputStream.close();
    }

    public void readTextFile(TextInputEditText fileName){

        if (!fileName.getText().equals("Animal Farm")){
            System.out.println("Invalid file Name. Please use the given options.");
        }

        AssetManager assetManager = this.getResources().getAssets(); //access folder, this is an InputStream type
        try {
            InputStream inputStream = assetManager.open(fileName + ".txt");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder stringBuilder = new StringBuilder();//nice way of concatenating strings without using alot of "+"
            String line;
            while ((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }
            String output = stringBuilder.toString();
            answer.setText(output);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void removeCommonWords(){
        words.removeAll(commonWords);
    }

    public int getSentenceCount(){
        for (int i = 0; i < words.size(); i++){
            String temp = words.get(i);
            for (int j = 0; j < temp.length(); j++){
                if (temp.substring(temp.length()-1).equalsIgnoreCase(",")){
                    temp = temp.substring(0,temp.length()-1);
                    words.set(i,temp);
                }
                else if(temp.substring(temp.length()-1).equalsIgnoreCase(".") ||
                        temp.substring(temp.length()-1).equalsIgnoreCase("!") ||
                        temp.substring(temp.length()-1).equalsIgnoreCase("?")){
                    sentenceCount++;
                    temp = temp.substring(0,temp.length()-1);
                    words.set(i,temp);
                }
            }
        }
        return sentenceCount;
    }

    public int getWordCount() {
        wordCount = words.size();
        return wordCount;
    }

    public void makeCounterArraylist(){
        //parallel arraylist that will have the corresponding frequency of the word's appearances
        for (int i = 0; i < words.size(); i++){
            int counter = 1;
            for (int j = i + 1; j < words.size(); j++){
                if (words.get(i).equalsIgnoreCase(words.get(j))) {
                    counter++;
                    //remove repeated words in words arraylist
                    words.remove(j);
                }
            }
            //Setting up parallel array
            counterArray.add(counter);
        }
    }

    public void sortDescending(){
        int temp;
        String temp2;
        for (int i = 0; i < counterArray.size(); i++){
            for (int j = i + 1; j < counterArray.size(); j++){
                if (counterArray.get(j) > counterArray.get(i)){
                    temp = counterArray.get(i);
                    temp2 = words.get(i);

                    counterArray.set(i, counterArray.get(j));
                    words.set(i, words.get(j));

                    counterArray.set(j, temp);
                    words.set(j, temp2);
                }
            }
        }
    }

    public static ArrayList<String> getUniqueWords() {
        for (int i = 0; i < counterArray.size(); i++){
            if (counterArray.get(i) == 1){
                uniqueWords.add(words.get(i));
            }
        }
        return uniqueWords;
    }

    public static String getHighestFreq() {
        return "The word with the highest frequency is " + words.get(0) + ", with a frequency of " + counterArray.get(0) + " words."

                + "\n\nThe top five words with the highest frequency:"
                + "\n1. \"" + words.get(0) + "\" with " + counterArray.get(0) + " appearances."
                + "\n2. \"" + words.get(1) + "\" with " + counterArray.get(1) + " appearances."
                + "\n3. \"" + words.get(2) + "\" with " + counterArray.get(2) + " appearances."
                + "\n4. \"" + words.get(3) + "\" with " + counterArray.get(3) + " appearances."
                + "\n5. \"" + words.get(4) + "\" with " + counterArray.get(4) + " appearances.";
    }
}