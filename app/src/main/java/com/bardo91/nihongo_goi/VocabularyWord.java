package com.bardo91.nihongo_goi;

public class VocabularyWord {
    // fields
    private int wordId;

    private String spanish;
    private String japanese;

    // constructors
    public VocabularyWord() {}

    public VocabularyWord(int id, String spanish, String japanese) {
        this.wordId= id;
        this.spanish= spanish;
        this.japanese= japanese;

    }

    // properties
    public void id(int id) {
        this.wordId = id;
    }
    public int id() {
        return this.wordId;
    }
    public void setSpanish(String spanish) {
        this.spanish= spanish;
    }
    public String getSpanish() {
        return this.spanish;
    }

    public void setJapanese(String japanese) {
        this.japanese= japanese;
    }
    public String getJapanese() {
        return this.japanese;
    }

}