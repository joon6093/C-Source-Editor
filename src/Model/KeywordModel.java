package Model;
public class KeywordModel {
    private final String[] KeyWord = {"#include","#define","struct","union", "enum","for","while","return","false","true",
                                      "sizeof","typedef","#pragma","do","static","default","const","switch","case","break",
                                       "continue","if","else","goto","register","extern","volatile"};

    public String[] getKeyword()
    {
        return KeyWord;
    }
}