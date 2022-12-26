package Controller;

import Model.DatatypeModel;
import Model.HeaderModel;
import Model.KeywordModel;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.StringTokenizer;

public class InputController implements Runnable {  //변수들에 대한 주석은 효율적인 이해를 위해 변수 저장 시점에 기술
    private final JTextPane UserText;
    private String UserTextString;
    private char[] UserTextCharacter;
    private String[] UserTextCharacterColor;
    private ArrayList<String> UserTextWord;

    private final String[] getKeyword;
    private final String[] getHeader;
    private final String[] getDatatype;

    private final AttributeSet CyanColor;
    private final AttributeSet BlackColor;
    private final AttributeSet PinkColor;
    private final AttributeSet RedColor;
    private final AttributeSet ErrorColor;
    private final AttributeSet MagentaColor;
    private final AttributeSet BlueColor;
    private final AttributeSet GreenColor;
    private final AttributeSet OrangeColor;
    private final AttributeSet GrayColor;
    public InputController(JTextPane text) {
        HeaderModel Header = new HeaderModel();  //각각의 Model에 접근하기 위해 선언
        DatatypeModel Datatype = new DatatypeModel();
        KeywordModel KeyWord = new KeywordModel();
        getKeyword = KeyWord.getKeyword();  //각각의 Model에서 데이터들을 들고와 String 배열에 저장
        getHeader = Header.getHeader();
        getDatatype = Datatype.getDatatype();
        this.UserText = text; //사용자가 입력한 텍스트값을 받기
        new Thread(this).start();  //색깔 지정을 위한 쓰레드 실행
        StyleContext context = new StyleContext();
        PinkColor = context.addAttribute(SimpleAttributeSet.EMPTY,StyleConstants.Foreground, Color.PINK);  //헤더파일명의 색
        CyanColor = context.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.CYAN); //키워드의 색
        BlackColor = context.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLACK); //일반텍스트의 색
        MagentaColor = context.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.MAGENTA); //식별자 및 함수 및 상수 및 괄호의 색
        BlueColor = context.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLUE); //데이터 타입의 색
        RedColor = context.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.RED); //오류를 위한 색
        GreenColor = context.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.GREEN); //주석의 색
        OrangeColor = context.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.ORANGE); //따옴표의 색
        GrayColor=context.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.GRAY); //세미콜론의 색
        AttributeSet as_underline = context.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Underline, Boolean.TRUE); //오류 컬러를 위한 밑줄
        ErrorColor = context.addAttributes(RedColor, as_underline); //오류 컬러 = 빨간색 + 밑줄

    }

    @Override
    public void run() {
        while (true) {
            UserTextString = UserText.getText().replaceAll(System.getProperty("line.separator"), "\n"); //운영체제마다 개행의 문자가 다름 이를 공통적으로 처리해주기 위해 (System.getProperty("line.separator") 사용
            StyledDocument userTextDocument = UserText.getStyledDocument(); //색 지정을 위한 자바 스윙 인터페이스
            UserTextCharacter = UserTextString.toCharArray(); //주석, 괄호, 따음표, 세미콜론을 위한 문자형배열으로 분리
            UserTextCharacterColor = new String[UserTextCharacter.length]; //각각의 색정보들을 저장할 문자열배열
            Arrays.fill(UserTextCharacterColor, ""); //초기화
            UserTextWord = new ArrayList<String>(); //토큰화한 데이터를 담기위한 String형 가변배열
            StringTokenizer userTextTokenizer = new StringTokenizer(UserText.getText()); //공백을 기준으로 토큰화
            while (userTextTokenizer.hasMoreTokens()) {
                UserTextWord.add(userTextTokenizer.nextToken()); //키워드. 헤더파일명, 매크로, 식별자, 함수를 위한 공백을 기준으로 분리한 문자열을 가변배열에 저장
            }
            //System.out.println(UserText.getText());
            //System.out.println(UserTextWord);
            KeywordEdit(); //키워드 색 지정
            IncludeEdit(); //헤더파일명 색지정
            DefineEdit(); //#Define 상수 색 지정
            DateTypeEdit();//데이터 타입 색 지정 및 식별자, 함수 색 지정
            MarkEdit(); //주석, 괄호, 따옴표, 세미콜론 색 지정
            for (int ColorSetindex = 0; ColorSetindex < UserTextCharacter.length; ColorSetindex++) { //색 정보를 저장하고있는 문자열배열을 이용하여 색 지정하기
                if (UserTextCharacterColor[ColorSetindex].equals("CYAN"))
                    userTextDocument.setCharacterAttributes(ColorSetindex, 1, CyanColor, true);
                else if (UserTextCharacterColor[ColorSetindex].equals("GREEN"))
                    userTextDocument.setCharacterAttributes(ColorSetindex, 1, GreenColor, true);
                else if (UserTextCharacterColor[ColorSetindex].equals("ORANGE"))
                    userTextDocument.setCharacterAttributes(ColorSetindex, 1, OrangeColor, true);
                else if (UserTextCharacterColor[ColorSetindex].equals("MAGENTA"))
                    userTextDocument.setCharacterAttributes(ColorSetindex, 1, MagentaColor, true);
                else if (UserTextCharacterColor[ColorSetindex].equals("BLUE"))
                    userTextDocument.setCharacterAttributes(ColorSetindex, 1, BlueColor, true);
                else if (UserTextCharacterColor[ColorSetindex].equals("PINK"))
                    userTextDocument.setCharacterAttributes(ColorSetindex, 1, PinkColor, true);
                else if (UserTextCharacterColor[ColorSetindex].equals("RED"))
                    userTextDocument.setCharacterAttributes(ColorSetindex, 1, RedColor, true);
                else if (UserTextCharacterColor[ColorSetindex].equals("GRAY"))
                    userTextDocument.setCharacterAttributes(ColorSetindex, 1, GrayColor, true);
                else if (UserTextCharacterColor[ColorSetindex].equals("ERROR"))
                    userTextDocument.setCharacterAttributes(ColorSetindex, 1, ErrorColor, true);
                else
                    userTextDocument.setCharacterAttributes(ColorSetindex, 1, BlackColor, true);
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    public void KeywordEdit() {
        int KeyWordindex = 0;   //검색한 키워드의 위치를 저장하는 int형 변수
        String KeywordValue = null; //색 지정을 해줄 키워드의 이름을 저장하는 String형 변수(사용자가 입력함)
        String ModelKeywordValue = null; //색 지정을 해줄 키워드의 이름을 저장하는 String형 변수(모델에서 들고옴)
        for (int Wordindex = 0; Wordindex < UserTextWord.size(); Wordindex++) { //사용자가 입력한 텍스트를 공백단위로 분리한 데이터들
            for (int ModelKeywordindex = 0; ModelKeywordindex < getKeyword.length; ModelKeywordindex++) { //키워드 모델의 데이터들
                KeywordValue = UserTextWord.get(Wordindex);
                ModelKeywordValue = getKeyword[ModelKeywordindex];  //둘의 차이는 KeywordValue는 ModelKeywordValue 담고있을수있으나 역은 성립하지 않는다.
                if (KeywordValue.equals(ModelKeywordValue)) {  //KeywordValue와 ModelKeywordValue가 같다면
                    KeyWordindex = UserTextString.indexOf(ModelKeywordValue, KeyWordindex);  //키워드명으로 검색 
                    for (int SeachKeyWordColor = KeyWordindex; SeachKeyWordColor < KeyWordindex + ModelKeywordValue.length(); SeachKeyWordColor++) {
                        UserTextCharacterColor[SeachKeyWordColor] = "CYAN";  //키워드명의 검색위치 ~ 키워드명의 문자열길이까지 색 지정
                    }
                    KeyWordindex = KeyWordindex + KeywordValue.length(); //키워드명의 검색위치 업데이트 이 작업을 생략할 경우 계속해서 같은 위치만 검색함.
                } else if (KeywordValue.equals("(" + ModelKeywordValue) || KeywordValue.equals("{" + ModelKeywordValue) || KeywordValue.equals("[" + ModelKeywordValue)) { //키워드의 경우 if( 와 같이 괼호를 붙여쓰는 경우가 많음. 사용자의 편의성을 위해 이 경우를 처리
                    KeyWordindex = UserTextString.indexOf(KeywordValue, KeyWordindex);
                    for (int SeachKeyWordColor = KeyWordindex + 1; SeachKeyWordColor < KeyWordindex + ModelKeywordValue.length() + 1; SeachKeyWordColor++) { //괄호때문에 색지정은 키워드의 위치 +1부터 지정
                        UserTextCharacterColor[SeachKeyWordColor] = "CYAN";
                    }
                    KeyWordindex = KeyWordindex + KeywordValue.length();
                } else if (KeywordValue.equals(ModelKeywordValue + ")") || KeywordValue.equals(ModelKeywordValue + "}") || KeywordValue.equals(ModelKeywordValue + "]")) {
                    KeyWordindex = UserTextString.indexOf(KeywordValue, KeyWordindex);
                    for (int SeachKeyWordColor = KeyWordindex; SeachKeyWordColor < KeyWordindex + ModelKeywordValue.length(); SeachKeyWordColor++) {
                        UserTextCharacterColor[SeachKeyWordColor] = "CYAN";
                    }
                    KeyWordindex = KeyWordindex + KeywordValue.length();
                } else if (KeywordValue.equals("(" + ModelKeywordValue + ")") || KeywordValue.equals("{" + ModelKeywordValue + "}") || KeywordValue.equals("[" + ModelKeywordValue + "]")) {
                    KeyWordindex = UserTextString.indexOf(KeywordValue, KeyWordindex);
                    for (int SeachKeyWordColor = KeyWordindex + 1; SeachKeyWordColor < KeyWordindex + ModelKeywordValue.length() + 1; SeachKeyWordColor++) {
                        UserTextCharacterColor[SeachKeyWordColor] = "CYAN";
                    }
                    KeyWordindex = KeyWordindex + KeywordValue.length();
                } else if (KeywordValue.contains(ModelKeywordValue)) {  //만약 위에 모든 경우가 아니고 사용자가 키워드의 명을 포함하는 문자열을 작성하였을 경우 색을 지정해주면 안됨.
                    KeyWordindex = UserTextString.indexOf(KeywordValue, KeyWordindex); //그렇다고 해서 검색위치를 업데이트해주지않으면 후에 키워드명을 포함하는 문자열의 색을 지정해주는 버그가 발생.
                    KeyWordindex = KeyWordindex + KeywordValue.length();
                }
            }
        }
    }
    public void IncludeEdit() {
        int Headerindex = 0; //헤더파일명의 위치지정을 위한 검색한 단어의 위치를 저장하는 int형 변수
        for (int WordIndex = 0; WordIndex < UserTextWord.size(); WordIndex++) {
            for (int HeaderIndex = 0; HeaderIndex < getHeader.length; HeaderIndex++) { //헤더파일명 모델의 데이터들
                if (UserTextWord.get(WordIndex).equals("#include" + getHeader[HeaderIndex])) { //#inclde<stdio.h>와 같이 붙여쓰는 경우를 처리
                    Headerindex = UserTextString.indexOf(UserTextWord.get(WordIndex), Headerindex); //붙여쓴 경우 위에서 키워드명으로 인식을 못해주어서 여기서 색을 지정해주어야함.
                    for (int IncludeColor = Headerindex; IncludeColor < Headerindex + "#include".length(); IncludeColor++) {
                        //System.out.println(k);
                        UserTextCharacterColor[IncludeColor] = "CYAN";
                    }
                    Headerindex = Headerindex + "#include".length(); //검색 위치 업데이트
                    for (int HeaderColor = Headerindex; HeaderColor < Headerindex + getHeader[HeaderIndex].length(); HeaderColor++) { //헤더파일명의 색 지정
                        //System.out.println(k);
                        UserTextCharacterColor[HeaderColor] = "PINK";
                    }
                    Headerindex = Headerindex + getHeader[HeaderIndex].length(); //검색 위치 업데이트
                    //System.out.println("test1");
                } else if (UserTextWord.get(WordIndex).contains("#include" + getHeader[HeaderIndex])) { //여기도 마찬가지로 포함하는 문자열을 처리해주어함.
                    Headerindex = UserTextString.indexOf(UserTextWord.get(WordIndex), Headerindex);
                    Headerindex = Headerindex + UserTextWord.get(WordIndex).length();  //검색 위치 업데이트
                    //System.out.println("test2");
                } else if (WordIndex != 0 && UserTextWord.get(WordIndex - 1).equals("#include")) {  //#include <stdio.h> 와 같이 띄어쓴 경우 처리 이 경우 #include뒤에 나오니 0에서는 처리해주면 오류 발생
                    if (UserTextWord.get(WordIndex).equals(getHeader[HeaderIndex])) {  //헤더파일명과 일치하는 경우
                        Headerindex = UserTextString.indexOf("#include", Headerindex);  //정밀도를 위한 #include 부터 검색
                        Headerindex = Headerindex + "#include".length();
                        Headerindex = UserTextString.indexOf(getHeader[HeaderIndex], Headerindex); //헤더파일명을 검색한다.
                        //System.out.println("test2"+Headerindex);
                        for (int HeaderColor = Headerindex; HeaderColor < Headerindex + getHeader[HeaderIndex].length(); HeaderColor++) { //해더파일 색 지정
                            //System.out.println(k);
                            UserTextCharacterColor[HeaderColor] = "PINK";
                        }
                        Headerindex = Headerindex + getHeader[HeaderIndex].length();
                        //System.out.println("test3");
                    } else if (UserTextWord.get(WordIndex).contains(getHeader[HeaderIndex])) { //여기도 마찬가지로 포함하는 문자열을 처리해주어함.
                        Headerindex = UserTextString.indexOf(UserTextWord.get(WordIndex), Headerindex);
                        Headerindex = Headerindex + UserTextWord.get(WordIndex).length();
                        //System.out.println("test4");
                    }
                }
            }
        }
    }
    public void DefineEdit() {
        char DefinecheckFirst = ' ';  //Define 상수의 이름 검사를 위해 첫번째 문자를 담을 char형 변수
        int DefinecheckFirsttint = 0; //Define 상수의 이름 검사를 위한 첫번째 문자를 int 형으로 변경한 값을 담을 int 형 변수  시작이 숫자로 하면 안되기 때문
        char DefinecheckLast = ' '; //Define 상수의 이름 검사를 위해 마지막 문자를 담을 char형 변수
        int Defineindex = 0;  //Define 상수의 초기선언할때 위치지정을 위한 검색한 단어의 위치를 저장하는 int형 변수
        String DefineValue = null;  //색 지정을 해줄 상수의 이름을 저장하는 String형 변수(사용자가 입력함)
        int DefineValueindex = 0; //위에 선언한 Defineindex를 이용하여 초기선언 이후에 나오는 상수의 위치를 저장하는 int형 변수
        String SearchConstantValue = null;  //초기 선언이후에 나오는 상수의 색 지정을 위한 String형 변수
        for (int Wordindex = 1; Wordindex < UserTextWord.size(); Wordindex++) { //무조건 #define 뒤에 나오니 1부터 시작
            if (UserTextWord.get(Wordindex - 1).equals("#define")) { //만약 앞에 단어가 #define이라면 
                DefinecheckFirst = UserTextWord.get(Wordindex).charAt(0);  //첫번째 문자저장
                DefinecheckFirsttint = Character.getNumericValue(DefinecheckFirst); //첫번째 문자를 int형으로 변환
                DefinecheckLast= UserTextWord.get(Wordindex).charAt(UserTextWord.get(Wordindex).length() -1); //마지막 문자 저장
                DefineValue = UserTextWord.get(Wordindex); //해당 단어 저장
                Defineindex = UserTextString.indexOf("#define", Defineindex); //정밀도를 위한 #define 부터 검색
                Defineindex = Defineindex + "#define".length(); //검색위치 업데이트
                Defineindex = UserTextString.indexOf(DefineValue, Defineindex); //해당 단어 검색
                if (DefinecheckLast != ';' &&  !(0<DefinecheckFirsttint && 10>DefinecheckFirsttint) && DefinecheckFirst != '!' && DefinecheckFirst != '@' && DefinecheckFirst != '#' && DefinecheckFirst != '%' && DefinecheckFirst != '^' && DefinecheckFirst != '&' && DefinecheckFirst != '*' && DefinecheckFirst != '(' && DefinecheckFirst != ')') {
                    //C언어 규칙에따라 검사
                    for (int ConstantColor = Defineindex; ConstantColor < Defineindex + DefineValue.length(); ConstantColor++) { //색 지정
                        UserTextCharacterColor[ConstantColor] = "MAGENTA";
                        //System.out.println("test1:     "+ConstantColor);
                    }
                    Defineindex = Defineindex + DefineValue.length();
                    DefineValueindex = Defineindex; //해당 위치부터 후에 나오는 동일한 이름을 가진 상수를 처리해주어야함. 이를 안해줄시 선언 전에 나오는 동일한 명을 가진 상수도 색 지정해주는 버그가 발생 (교수님이 피드백 해주신 부분)
                    for (int SearchConstant = Wordindex + 1; SearchConstant < UserTextWord.size(); SearchConstant++) { //선언 위치 이후부터 검색
                        SearchConstantValue = UserTextWord.get(SearchConstant);
                        if (SearchConstantValue.equals(DefineValue)) { //후에 나오는 단어가 위에 선언할떄의 상수명과 같다면 
                            DefineValueindex = UserTextString.indexOf(SearchConstantValue, DefineValueindex); //검색
                            for (int SeachConstantColor = DefineValueindex; SeachConstantColor < DefineValueindex + DefineValue.length(); SeachConstantColor++) { //색지정
                                UserTextCharacterColor[SeachConstantColor] = "MAGENTA";
                                //System.out.println("test2:     "+h);
                            }
                            DefineValueindex = DefineValueindex + SearchConstantValue.length(); //위치업데이트
                        } else if (SearchConstantValue.equals("(" + DefineValue) || SearchConstantValue.equals("{" + DefineValue) || SearchConstantValue.equals("[" + DefineValue)) {
                            //마찬가지로 상수를 후에 사용할때에는 괄호를 함께 사용해도 사용자의 편의성을 위해 처리
                            DefineValueindex = UserTextString.indexOf(SearchConstantValue, DefineValueindex);
                            for (int SeachConstantColor = DefineValueindex + 1; SeachConstantColor < DefineValueindex + DefineValue.length() + 1; SeachConstantColor++) {
                                UserTextCharacterColor[SeachConstantColor] = "MAGENTA";
                            }
                            DefineValueindex = DefineValueindex + SearchConstantValue.length();
                        } else if (SearchConstantValue.equals(DefineValue + ")") || SearchConstantValue.equals(DefineValue + "}") || SearchConstantValue.equals(DefineValue + "]")) {
                            DefineValueindex = UserTextString.indexOf(SearchConstantValue, DefineValueindex);
                            for (int SeachConstantColor = DefineValueindex; SeachConstantColor < DefineValueindex + DefineValue.length(); SeachConstantColor++) {
                                UserTextCharacterColor[SeachConstantColor] = "MAGENTA";
                            }
                            DefineValueindex = DefineValueindex + SearchConstantValue.length();
                        } else if (SearchConstantValue.equals("(" + DefineValue + ")") || SearchConstantValue.equals("{" + DefineValue + "}") || SearchConstantValue.equals("[" + DefineValue + "]")) {
                            DefineValueindex = UserTextString.indexOf(SearchConstantValue, DefineValueindex);
                            for (int SeachConstantColor = DefineValueindex + 1; SeachConstantColor < DefineValueindex + DefineValue.length() + 1; SeachConstantColor++) {
                                UserTextCharacterColor[SeachConstantColor] = "MAGENTA";
                            }
                            DefineValueindex = DefineValueindex + SearchConstantValue.length();
                        } else if (SearchConstantValue.contains(DefineValue)) { //여기도 마찬가지로 포함하는 문자열을 처리해주어함.
                            DefineValueindex = UserTextString.indexOf(SearchConstantValue, DefineValueindex);
                            DefineValueindex = DefineValueindex + SearchConstantValue.length();
                        }
                    }
                } else { //C언어 규칙에 맞지않는 상수선언은 오류처리
                    for (int j = Defineindex; j < UserTextCharacter.length; j++) {
                        if (UserTextCharacter[j] == '\n') { //개행까지 에러로 색 지정
                            break;
                        }
                        UserTextCharacterColor[j] = "ERROR";
                    }
                    Defineindex = Defineindex + DefineValue.length(); //오류더라도 검색위치 업데이트를 해주어야함. 이를 안해줄씨 마찬가지로 한칸씩 밀리는 오류 발생 가능성 있음.
                }
            }
        }
    }
    public void DateTypeEdit() {
        String DataValue = null;  //색 지정을 해줄 데이터타입의 이름을 저장하는 String형 변수(사용자가 입력함)
        String ModelDataValue = null; //색 지정을 해줄 데이터타입의 이름을 저장하는 String형 변수(모델에서 들고옴)
        int Dataindex = 0;  //위치지정을 위한 검색한 단어의 위치를 저장하는 int형 변수
        for (int Wordindex = 0; Wordindex < UserTextWord.size(); Wordindex++) { //여기서는 이해하기 쉽게 설명하자면 int, char 과 같은 데이터타입의 색 지정임.
            for (int ModelDataindex = 0; ModelDataindex < getDatatype.length; ModelDataindex++) {
                DataValue = UserTextWord.get(Wordindex);
                ModelDataValue = getDatatype[ModelDataindex];
                if (DataValue.equals(ModelDataValue)) { //사용자가 입력한 단어와 모델에서 들고온 단어가 일치한다면 
                    Dataindex = UserTextString.indexOf(ModelDataValue, Dataindex);
                    for (int SeachKeyWordColor = Dataindex; SeachKeyWordColor < Dataindex + ModelDataValue.length(); SeachKeyWordColor++) {
                        UserTextCharacterColor[SeachKeyWordColor] = "BLUE";
                    }
                    Dataindex = Dataindex + DataValue.length();
                } else if (DataValue.equals("(" + ModelDataValue) || DataValue.equals("{" + ModelDataValue) || DataValue.equals("[" + ModelDataValue)) {
                    //마찬가지로 사용자의 편의성을 위한 처리
                    Dataindex = UserTextString.indexOf(DataValue, Dataindex);
                    for (int SeachKeyWordColor = Dataindex + 1; SeachKeyWordColor < Dataindex + ModelDataValue.length() + 1; SeachKeyWordColor++) {
                        UserTextCharacterColor[SeachKeyWordColor] = "BLUE";
                    }
                    Dataindex = Dataindex + DataValue.length();
                } else if (DataValue.equals(ModelDataValue + ")") || DataValue.equals(ModelDataValue + "}") || DataValue.equals(ModelDataValue + "]")) {
                    Dataindex = UserTextString.indexOf(DataValue, Dataindex);
                    for (int SeachKeyWordColor = Dataindex; SeachKeyWordColor < Dataindex + ModelDataValue.length(); SeachKeyWordColor++) {
                        UserTextCharacterColor[SeachKeyWordColor] = "BLUE";
                    }
                    Dataindex = Dataindex + DataValue.length();
                } else if (DataValue.equals("(" + ModelDataValue + ")") || DataValue.equals("{" + ModelDataValue + "}") || DataValue.equals("[" + ModelDataValue + "]")) {
                    Dataindex = UserTextString.indexOf(DataValue, Dataindex);
                    for (int SeachKeyWordColor = Dataindex + 1; SeachKeyWordColor < Dataindex + ModelDataValue.length() + 1; SeachKeyWordColor++) {
                        UserTextCharacterColor[SeachKeyWordColor] = "BLUE";
                    }
                    Dataindex = Dataindex + DataValue.length();
                } else if (DataValue.contains(ModelDataValue)) {   //여기도 마찬가지로 포함하는 문자열을 처리해주어함.
                    Dataindex = UserTextString.indexOf(DataValue, Dataindex);
                    Dataindex = Dataindex + DataValue.length();
                }
            }
        }

        char DataTypecheckFirst = ' '; //식별자와 함수의 이름 검사를 위한 첫번째 문자를 담을 변수
        int DataTypecheckFirstint =0 ; //식별자와 함수의 이름 검사를 위한 첫번째 문자를 int 형으로 변경한 값을 담을 int 형 변수  시작이 숫자로 하면 안되기 때문
        char DataTypecheckLast = ' ';  //식별자와 함수의 이름 검사를 위한 마지막 문자를 담을 변수
        int DataTypeindex = 0;  //위치지정을 위한 검색한 단어의 위치를 저장하는 int형 변수
        String DataTypeValue = null;  //색 지정을 해줄 식별자와 함수의 이름을 저장하는 String형 변수(사용자가 입력함)
        String ModellDataTypeValue = null; //DataTypeValue의 데이터타입을 담을 String형 변수
        int DataTypeValueindex = 0;  //위에 선언한 DataTypeindex 이용하여 초기선언 이후에 나오는 상수의 위치를 저장하는 int형 변수
        String SearchDataValue = null;  //초기 선언이후에 나오는 식별자와 함수의 색 지정을 위한 String형 변수 
        for (int Wordindex = 1; Wordindex < UserTextWord.size(); Wordindex++) {  //여기에서는 식별자 와 함수의 색 지정임.
            if (!(Arrays.asList(getDatatype).contains(UserTextWord.get(Wordindex)))) {  //double int와 같이 데이터타입명이 연속으로 쓰일 경우를 방지하여 현재 단어가 데이터타입이 아닐경우
                if ((Arrays.asList(getDatatype).contains(UserTextWord.get(Wordindex - 1)))) { //현재 단어의 전 단어가 데이터 타입일 경우
                    ModellDataTypeValue = UserTextWord.get(Wordindex - 1); //현재 단어의 데이터타입 저장
                    DataTypecheckFirst = UserTextWord.get(Wordindex).charAt(0); //첫번째 문자 저장
                    DataTypecheckFirstint = Character.getNumericValue(DataTypecheckFirst); //첫번째 문자를 int형으로 변환
                    DataTypecheckLast= UserTextWord.get(Wordindex).charAt(UserTextWord.get(Wordindex).length() -1); //마지막 문자 저장
                    DataTypeValue = UserTextWord.get(Wordindex); //현재 단어 저장
                    DataTypeindex = UserTextString.indexOf(ModellDataTypeValue, DataTypeindex); //정밀도를 위해 데이터타입부터 검색
                    DataTypeindex = DataTypeindex + ModellDataTypeValue.length();
                    DataTypeindex = UserTextString.indexOf(DataTypeValue, DataTypeindex); //현재 단어 검색 
                    //System.out.println("test2:     " + DataTypeindex);
                    if (!(Arrays.asList(getKeyword).contains(DataTypeValue)) && DataTypecheckLast != ';' && DataTypecheckLast != '(' && DataTypecheckLast != ')' &&  !(0<DataTypecheckFirstint && 10>DataTypecheckFirstint) && DataTypecheckFirst != '!' && DataTypecheckFirst != '@' && DataTypecheckFirst != '#' && DataTypecheckFirst != '%' && DataTypecheckFirst != '^' && DataTypecheckFirst != '&' && DataTypecheckFirst != '*' && DataTypecheckFirst != '(' && DataTypecheckFirst != ')') {
                        //C언어 규칙에 맞게 검사. 추가로 현재는 띄어쓰기를 기준으로 식별자와 함수의 명들을 구분함. 그래서 사용자가 실수로 띄어쓰기를 하지 않고 ; ) ( 를 붙였을 경우 알려주기 위한 처리
                        for (int ConstantColor = DataTypeindex; ConstantColor < DataTypeindex + DataTypeValue.length(); ConstantColor++) {
                            UserTextCharacterColor[ConstantColor] = "MAGENTA";
                            //System.out.println("test1:     "+ConstantColor);
                        }
                        DataTypeindex = DataTypeindex + DataTypeValue.length();
                        DataTypeValueindex = DataTypeindex; //해당 위치부터 후에 나오는 동일한 이름을 가진 식별자와 함수를 처리해주어야함. 이를 안해줄시 선언 전에 나오는 동일한 명을 가진 상수도 색 지정해주는 버그가 발생 (교수님이 피드백 해주신 부분)
                        //System.out.println("test3:     " + DataTypeindex);
                        for (int SearchConstant = Wordindex + 1; SearchConstant < UserTextWord.size(); SearchConstant++) { //위에 나오는 #define부분과 동일 생략하겠음
                            SearchDataValue = UserTextWord.get(SearchConstant);
                            if (SearchDataValue.equals(DataTypeValue)) {
                                DataTypeValueindex = UserTextString.indexOf(SearchDataValue, DataTypeValueindex);
                                for (int SeachDatatypeColor = DataTypeValueindex; SeachDatatypeColor < DataTypeValueindex + DataTypeValue.length(); SeachDatatypeColor++) {
                                    UserTextCharacterColor[SeachDatatypeColor] = "MAGENTA";
                                    //System.out.println("test2:     "+h);
                                }
                                DataTypeValueindex = DataTypeValueindex + SearchDataValue.length();
                            } else if (SearchDataValue.equals("(" + DataTypeValue) || SearchDataValue.equals("{" + DataTypeValue) || SearchDataValue.equals("[" + DataTypeValue)) {
                                DataTypeValueindex = UserTextString.indexOf(SearchDataValue, DataTypeValueindex);
                                for (int SeachDatatypeColor = DataTypeValueindex + 1; SeachDatatypeColor < DataTypeValueindex + DataTypeValue.length() + 1; SeachDatatypeColor++) {
                                    UserTextCharacterColor[SeachDatatypeColor] = "MAGENTA";
                                }
                                DataTypeValueindex = DataTypeValueindex + SearchDataValue.length();
                            } else if (SearchDataValue.equals(DataTypeValue + ")") || SearchDataValue.equals(DataTypeValue + "}") || SearchDataValue.equals(DataTypeValue + "]")) {
                                DataTypeValueindex = UserTextString.indexOf(SearchDataValue, DataTypeValueindex);
                                for (int SeachDatatypeColor = DataTypeValueindex; SeachDatatypeColor < DataTypeValueindex + DataTypeValue.length(); SeachDatatypeColor++) {
                                    UserTextCharacterColor[SeachDatatypeColor] = "MAGENTA";
                                }
                                DataTypeValueindex = DataTypeValueindex + SearchDataValue.length();
                            } else if (SearchDataValue.equals("(" + DataTypeValue + ")") || SearchDataValue.equals("{" + DataTypeValue + "}") || SearchDataValue.equals("[" + DataTypeValue + "]")) {
                                DataTypeValueindex = UserTextString.indexOf(SearchDataValue, DataTypeValueindex);
                                for (int SeachDatatypeColor = DataTypeValueindex + 1; SeachDatatypeColor < DataTypeValueindex + DataTypeValue.length() + 1; SeachDatatypeColor++) {
                                    UserTextCharacterColor[SeachDatatypeColor] = "MAGENTA";
                                }
                                DataTypeValueindex = DataTypeValueindex + SearchDataValue.length();
                            } else if (SearchDataValue.contains(DataTypeValue)) {
                                DataTypeValueindex = UserTextString.indexOf(SearchDataValue, DataTypeValueindex);
                                DataTypeValueindex = DataTypeValueindex + SearchDataValue.length();
                            }
                        }
                    }
                    else {
                        for (int j = DataTypeindex; j < UserTextCharacter.length; j++) {
                            if (UserTextCharacter[j] == '\n') {
                                break;
                            }
                            UserTextCharacterColor[j] = "ERROR";
                        }
                        DataTypeindex = DataTypeindex + DataTypeValue.length();
                        //System.out.println("test4:     " + DataTypeindex);
                    }
                }
            }
        }
    }
    public void MarkEdit() {
        Stack<Character> MarkEditStack = new Stack<Character>(); //주석, 따옴표, 괄호의 색 지정을 위한 Stack 위 3가지는 누가 먼저나오냐에따라서 처리방식이 달라짐 그래서 하나의 스택에 일괄적으로 처리해야함 .
        Stack<Integer> brackePosStack = new Stack<Integer>(); //괄호의 색지정과 오류색 지정을 위해서는 열린괄호의 위치를 알고있어야함. 열린괄호의 위치를 저장하기 위해 선언한 int형 Stack
        int OpenRemarkPos = 0; //열린 주석 위치를 저장하는 int형 변수
        int OpenQuotmarkPos = 0; //열린 따옴표의 위치를 저장하는 int형 변수
        int OpenBracketPos = 0; //열린 괄호의 위치를 저장하는 int형 변수
        char BracketItem = ' '; //괄호는 소괄호 중괄호 대괄호로 구분됨. 괄호의 종류를 담을 char형 변수
        for (int characterIndex = 0; characterIndex < UserTextCharacter.length; characterIndex++) {
            if (characterIndex != UserTextCharacter.length - 1) { //주석은 //, /* 과같이 문자형 데이터로 변환시에 두자리의 공간을 차지함. 그래서 오류방지를 위한 접근에 제한이 필요함
                if (MarkEditStack.size() != 0) { //스택의 크기가 0이 아닐때에만 peek와 pop이 가능함. 또한 스택의 크기가 0이 아니라는 점은 push할 때 주석, 괄호, 따옴표의 순서를 고려해야한다는 사실을 의미한다.
                    if ((MarkEditStack.peek() != '"') && (MarkEditStack.peek() != '*') && (MarkEditStack.peek() != '+')) { //만약 스택의 제일 위에있는 값이 따옴표, 한줄주석, 열린스택이라면 스택에 푸쉬하면 안됨
                        if ((characterIndex + 1 < UserTextCharacter.length) && ((UserTextCharacter[characterIndex] == '/') && (UserTextCharacter[characterIndex + 1] == '/'))) { //한줄주석이라면
                            MarkEditStack.push('+'); //한줄주석은 편의상 +라고 치환하여 스택에 푸쉬  //=+
                            for (int characterColor = characterIndex; characterColor < UserTextCharacter.length; characterColor++) {
                                if (UserTextCharacter[characterColor] == '\n') { //한줄주석이므로 개행이 나올때까지 계속해서 색을 지정
                                    break;
                                }
                                UserTextCharacterColor[characterColor] = "GREEN";
                            }
                        }
                        if ((UserTextCharacter[characterIndex] == '/') && (UserTextCharacter[characterIndex + 1] == '*')) { //만약 열린주석이라면 
                            MarkEditStack.push('*');  //열린주석은 편의상 *라고 치환하여 스택에 푸쉬  /*=*
                            OpenRemarkPos = characterIndex; //닫힌주석이 없을 경우 오류표시를 위한 열린 주석의 위치값 저장
                            for (int characterColor = characterIndex; characterColor < UserTextCharacter.length - 1; characterColor++) {
                                if (UserTextCharacter[characterColor] == '*' && UserTextCharacter[characterColor + 1] == '/') {  //열린주석이므로 닫힌주석이 나올때까지 색을 지정
                                    break;
                                } else {
                                    UserTextCharacterColor[characterColor] = "ERROR";  //닫힌주석이 나올때까지 에러로 색을 지정해야함
                                    UserTextCharacterColor[characterColor + 1] = "ERROR"; //열린주석은 2칸을 차지하니 for문을 돌릴때 범위의 제한을 걸어두었음. 그래서 +1범위까지 에러로 색을 지정
                                }
                            }
                        }
                        if (((UserTextCharacter[characterIndex] == '*') && (UserTextCharacter[characterIndex + 1] == '/'))) { //닫힌주석만 있을경우 에러 처리
                            UserTextCharacterColor[characterIndex] = UserTextCharacterColor[characterIndex + 1] = "ERROR";
                        }
                    } //주석, 괄호, 따옴표의 순서를 고려한 주석의 push는 마무리
                    if ((UserTextCharacter[characterIndex] == '\n') && (MarkEditStack.peek()) == '+') { //개행일 경우와 스택의 제일 위에 있는 값이 + 즉 한줄 주석일 경우
                        MarkEditStack.pop(); //pop
                        continue; //continue를 생략할시 뒤에 코드에서 peek했는데 스택의 크기가 0이라면 오류가 발생함.
                    }
                    if ((MarkEditStack.peek() == '*')) { //스택의 제일 위에있는 값이 * 즉 열린주석일 경우
                        if (UserTextCharacter[characterIndex] == '*' && UserTextCharacter[characterIndex + 1] == '/') { //단힌 주석이라면
                            MarkEditStack.pop(); //pop
                            for (int characterColor = OpenRemarkPos; characterColor < characterIndex + 2; characterColor++) //위에서는 에러처리를 했으나 닫힌주석을 확인한 후이니 위에서 저장한 열린괄호의 위치값을 이용하여 초록색으로 지정
                                UserTextCharacterColor[characterColor] = "GREEN";
                            continue;
                        }
                    }
                }
                else { //반대로 스택의 크기가 0이라면 push만 해주면 되고 주석, 괄호, 따옴표의 순서를 고려할 필요가 없다.
                    if (((UserTextCharacter[characterIndex] == '/') && (UserTextCharacter[characterIndex + 1] == '/'))) {  //한줄주석처리
                        MarkEditStack.push('+');
                        for (int characterColor = characterIndex; characterColor < UserTextCharacter.length; characterColor++) {
                            if (UserTextCharacter[characterColor] == '\n') {
                                break;
                            }
                            UserTextCharacterColor[characterColor] = "GREEN";
                        }
                    }
                    if (((UserTextCharacter[characterIndex] == '/') && (UserTextCharacter[characterIndex + 1] == '*'))) { //열린주석처리
                        MarkEditStack.push('*');
                        OpenRemarkPos = characterIndex;
                        for (int characterColor = characterIndex; characterColor < UserTextCharacter.length - 1; characterColor++) {
                            if (UserTextCharacter[characterColor] == '*' && UserTextCharacter[characterColor + 1] == '/') {
                                break;
                            } else {
                                UserTextCharacterColor[characterColor] = "ERROR";
                                UserTextCharacterColor[characterColor + 1] = "ERROR";
                            }
                        }
                    }
                    if (UserTextCharacter[characterIndex] == '*' && UserTextCharacter[characterIndex + 1] == '/') { //닫힌주석만 있을경우 처리
                        UserTextCharacterColor[characterIndex] = UserTextCharacterColor[characterIndex + 1] = "ERROR";
                    }
                }
                //작은 따옴표를 처리하는 부분이다. 작은따옴표같은 경우에는 크기가 1로 고정되어있어 굳이 스택을 사용하지않았다.
                if (UserTextCharacter[characterIndex] == '\'' && UserTextCharacterColor[characterIndex] != "ORANGE" && UserTextCharacterColor[characterIndex] != "GREEN" && UserTextCharacterColor[characterIndex] != "ERROR" ) { //열린작은따옴표이고 해당 값이 따옴표가 아니고 주석이 아니고 또한 열린주석에서의 에러가 아니라면
                    UserTextCharacterColor[characterIndex] = "ERROR"; //일단 닫힌 작은 따옴표가 나올때까지는 에러이다.
                    if ((UserTextCharacter[characterIndex + 1] == '\'')) { //+1의 위치에있는 값이 닫힌 작은 따옴표라면 이경우는 '' 아무값도 입력하지않고 닫아주었을때를 의미한다.
                        UserTextCharacterColor[characterIndex + 1] = UserTextCharacterColor[characterIndex] = "ORANGE"; //오렌지로 색을 지정하여준다.
                    } else if ((characterIndex != UserTextCharacter.length - 2) && UserTextCharacter[characterIndex + 2] == '\'') { //+2의 위치에있는 값이 닫힌 작은 따옴표라면 이경우는 'a' 와 같이 한문자를 입력한 경우에 해당한다.
                        UserTextCharacterColor[characterIndex + 2] = UserTextCharacterColor[characterIndex + 1] = UserTextCharacterColor[characterIndex] = "ORANGE"; //오렌지로 색을 지정하여준다.
                    }
                } //작은 따옴표 마무리
            }
            //큰따옴표를 처리하는 부분이다.
            if (MarkEditStack.size() != 0) { //스택의 크기가 0이 아닐때에만 peek와 pop이 가능함. 또한 스택의 크기가 0이 아니라는 점은 push할 때 주석, 괄호, 따옴표의 순서를 고려해야한다는 사실을 의미한다.
                if (UserTextCharacter[characterIndex] == '"' && !(MarkEditStack.contains('"')) && (MarkEditStack.peek() != '+') && (MarkEditStack.peek() != '*')) { //큰따옴표이나 스택에 따옴표값이 없어야하고(닫힌따옴표 구분을 위함) 한줄주석과 열린주석이 최근값이 아니여야한다.
                    MarkEditStack.push('"'); //push
                    OpenQuotmarkPos = characterIndex; //후에 색 지정을 위해 열린 큰따옴표의 위치를 저장한다.
                    UserTextCharacterColor[characterIndex] = "ERROR"; //일단 열린 큰 따옴표 하나만으로는 에러이다.
                    for (int characterColor = characterIndex + 1; characterColor < UserTextCharacter.length; characterColor++) {
                        if (UserTextCharacter[characterColor] == '"' || UserTextCharacter[characterColor] == '\n') { //닫힌 따옴표가 나오거나 개행전까지에는
                            break;
                        }
                        UserTextCharacterColor[characterColor] = "ERROR"; //에러로 색 지정
                    }
                }
                if (MarkEditStack.peek() == '"') { //스택의 최근값이 따옴표이고
                    //System.out.println(i);
                    //System.out.println(OpenQuotmarkPos);
                    if (UserTextCharacter[characterIndex] == '"' && characterIndex != OpenQuotmarkPos) { //현재 값이 따옴표라면
                        MarkEditStack.pop(); //pop
                        for (int characterColor = OpenQuotmarkPos; characterColor < characterIndex + 1; characterColor++) { //push할때 저장한 열린 따옴표의 위치를 이용하여 색 지정
                            UserTextCharacterColor[characterColor] = "ORANGE";
                            //System.out.println("test4");
                        }
                        continue;
                    } else if (UserTextCharacter[characterIndex] == '\n') { //개행일 경우에는 pop만 해주고 색지정은 해주지 않는다.
                        //System.out.println("test4");
                        MarkEditStack.pop();
                        continue;
                    }
                }
            }
            else { //반대로 스택의 크기가 0이라면 push만 해주면 되고 주석, 괄호, 따옴표의 순서를 고려할 필요가 없다.
                if (UserTextCharacter[characterIndex] == '"') {
                    MarkEditStack.push('"');
                    OpenQuotmarkPos = characterIndex;
                    UserTextCharacterColor[characterIndex] = "ERROR";
                    for (int characterColor = characterIndex + 1; characterColor < UserTextCharacter.length; characterColor++) {
                        if (UserTextCharacter[characterColor] == '"' || UserTextCharacter[characterColor] == '\n') {
                            break;
                        }
                        UserTextCharacterColor[characterColor] = "ERROR";
                    }
                }
            }
            if (MarkEditStack.size() != 0) { //스택의 크기가 0이 아닐때에만 peek와 pop이 가능함. 또한 스택의 크기가 0이 아니라는 점은 push할 때 주석, 괄호, 따옴표의 순서를 고려해야한다는 사실을 의미한다.
                if ((MarkEditStack.peek() != '"') && (MarkEditStack.peek() != '*') && (MarkEditStack.peek() != '+')) { //만약 스택의 제일 위에있는 값이 따옴표, 한줄주석, 열린스택이라면 스택에 푸쉬하면 안됨 또한 에러처리도 하면 안됨.
                    if (UserTextCharacter[characterIndex] == '{') { //열린 대괄호인경우
                        BracketItem = UserTextCharacter[characterIndex];
                        OpenBracketPos = characterIndex;
                        MarkEditStack.push(BracketItem);  //대괄호를 푸쉬
                        brackePosStack.push(OpenBracketPos); //대괄호의 위치를 푸쉬
                        UserTextCharacterColor[characterIndex] = "ERROR"; //닫힌괄호가없으니 에러
                    }
                    if (UserTextCharacter[characterIndex] == '[') { //열린 소괄호일경우
                        BracketItem = UserTextCharacter[characterIndex];
                        OpenBracketPos = characterIndex;
                        MarkEditStack.push(BracketItem); //소괄호를 푸쉬
                        brackePosStack.push(OpenBracketPos); //대괄호를 푸쉬
                        UserTextCharacterColor[characterIndex] = "ERROR"; //닫힌괄호가없으니 에러
                    }
                    if (UserTextCharacter[characterIndex] == '(') { //열린 중괄호인경우
                        BracketItem = UserTextCharacter[characterIndex];
                        OpenBracketPos = characterIndex;
                        MarkEditStack.push(BracketItem); //중괄호를 푸쉬
                        brackePosStack.push(OpenBracketPos); //중괄호를 푸쉬
                        UserTextCharacterColor[characterIndex] = "ERROR"; //닫힌괄호가없으니 에러
                    }
                    if (MarkEditStack.contains('{')) { //일단 스택에 괄호가 있는지부터 검사
                        if ((MarkEditStack.peek() == '{') && (UserTextCharacter[characterIndex] == '}')) { //스택에 괄호가있고 제일 최상위값이 내가 찾는 괄호이고 현재값이 해당 괄호의 닫는 괄호일경우
                            MarkEditStack.pop(); //pop
                            UserTextCharacterColor[brackePosStack.peek()] = UserTextCharacterColor[characterIndex] = "MAGENTA"; //push해줄때 저장해놓은 위치값을 이용하여 여는 괄호를 에러에서 마젠타색으로 변경
                            brackePosStack.pop(); //pop
                            continue; //버그발생 방지 
                        } else if (UserTextCharacter[characterIndex] == '}') { //스택에 괄호가 있으나 내가 찾는 괄호가 최상위값이 아닐경우
                             UserTextCharacterColor[characterIndex] = "ERROR"; //짝이안맞는다는 뜻이므로 에러처리
                        }
                    } else if (UserTextCharacter[characterIndex] == '}') { //닫는 괄호만있을경우 에러처리
                        UserTextCharacterColor[characterIndex] = "ERROR";
                    }
                    if (MarkEditStack.contains('[')) { //이하동일
                        if ((MarkEditStack.peek() == '[') && (UserTextCharacter[characterIndex] == ']')) {
                            MarkEditStack.pop();
                            UserTextCharacterColor[brackePosStack.peek()] = UserTextCharacterColor[characterIndex] = "MAGENTA";
                            brackePosStack.pop();
                            continue;
                        } else if (UserTextCharacter[characterIndex] == ']') {
                            UserTextCharacterColor[characterIndex] = "ERROR";
                        }
                    } else if (UserTextCharacter[characterIndex] == ']') {
                        UserTextCharacterColor[characterIndex] = "ERROR";
                    }

                    if (MarkEditStack.contains('(')) {
                        if ((MarkEditStack.peek() == '(') && (UserTextCharacter[characterIndex] == ')')) {
                            MarkEditStack.pop();
                            UserTextCharacterColor[brackePosStack.peek()] = UserTextCharacterColor[characterIndex] = "MAGENTA";
                            brackePosStack.pop();
                            continue;
                        } else if (UserTextCharacter[characterIndex] == ')') {
                            UserTextCharacterColor[characterIndex] = "ERROR";
                        }
                    } else if (UserTextCharacter[characterIndex] == ')') {
                        UserTextCharacterColor[characterIndex] = "ERROR";
                    }
                }
            }
            else{  //반대로 스택의 크기가 0이라면 push만 해주면 되고 주석, 괄호, 따옴표의 순서를 고려할 필요가 없다.
                if (UserTextCharacter[characterIndex] == '{') { //위에 push와 동일
                    BracketItem = UserTextCharacter[characterIndex];
                    OpenBracketPos = characterIndex;
                    MarkEditStack.push(BracketItem);
                    brackePosStack.push(OpenBracketPos);
                    UserTextCharacterColor[characterIndex] = "ERROR";
                }
                if (UserTextCharacter[characterIndex] == '[') {
                    BracketItem = UserTextCharacter[characterIndex];
                    OpenBracketPos = characterIndex;
                    MarkEditStack.push(BracketItem);
                    brackePosStack.push(OpenBracketPos);
                    UserTextCharacterColor[characterIndex] = "ERROR";
                }
                if (UserTextCharacter[characterIndex] == '(') {
                    BracketItem = UserTextCharacter[characterIndex];
                    OpenBracketPos = characterIndex;
                    MarkEditStack.push(BracketItem);
                    brackePosStack.push(OpenBracketPos);
                    UserTextCharacterColor[characterIndex] = "ERROR";
                }
                //닫힌괄호만있을경우 에러처리
                if (UserTextCharacter[characterIndex] == '}') {
                    UserTextCharacterColor[characterIndex] = "ERROR";
                }
                if (UserTextCharacter[characterIndex] == ']') {
                    UserTextCharacterColor[characterIndex] = "ERROR";
                }
                if (UserTextCharacter[characterIndex] == ')') {
                    UserTextCharacterColor[characterIndex] = "ERROR";
                }
            }

        }
        //세미콜론 처리를 위한 부분
        int SemicolonIndex=0; //개행을 기준으로 +1 즉 한 라인이 새로 시작되는 위치값을 담을 int형 변수
        int ErrorException=0; //사용자가 세미콜론 처리를 안해줘도 될때 예외처리를 도와줄 int 형 변수  ex) #키워드는 세미콜론이 필요없다. 
        int SemicolonCheckindex=0; //한라인에 세미콜론이 2개 나왔을 경우 처리를 위한 int형 변수 ex) int a; int b 의 경우에는 뒤에 int b는 오류 발생해야함
        int OpenBracesindex= 0; //정밀도를 위한 여는 중괄호의 위치를 저장하는 int형 변수
        int OpenBracescheck=0; //{ 의 경우 2가지가 나누어지는데 해당 예외처리를 도와줄 int형 변수  후에 해당 코드가 나오면 자세히 기술하겠음.
        int CloseBracesindex= 0; //정밀도를 위한 닫는 중괄호의 위치를 저장하는 변수
        for (int characterIndex =0 ; characterIndex < UserTextCharacter.length; characterIndex++) {
            ErrorException=0;
            OpenBracesindex= 0;
            CloseBracesindex= 0;
            OpenBracescheck=0;
            //System.out.println("test1    "+UserTextCharacter[characterIndex]);
            if (UserTextCharacter[characterIndex] == '\n') { //개행이라면
                //System.out.println("test1    "+SemicolonIndex);
                //System.out.println("test2    "+characterIndex);
                for (int characterColor = SemicolonIndex; characterColor < characterIndex; characterColor++) { //전 줄의 개행+1 ~ 해당 줄의 개행  즉 범위는 한 라인이다.
                    if (UserTextCharacterColor[characterColor].equals("GREEN") || UserTextCharacterColor[characterColor].equals("PINK") || UserTextCharacterColor[characterColor].equals("CYAN")) { //키워드거나 헤더파일명이거나 주석일경우는 세미콜론이 필요없다.
                        ErrorException = 1;
                        break;
                    }
                    //함수의 선언부분 세미콜론 예외처리를 위한 부분
                    if (UserTextCharacterColor[characterColor].equals("BLUE")) { //만약 데이터타입이고
                        for (int Bracescheck = characterColor; Bracescheck < characterIndex; Bracescheck++) { //해당 데이터 타입 이후에 나온 문자가 범위는 한 라인
                            if (UserTextCharacter[Bracescheck] == '(') { //여는괄호라면 위치저장
                                OpenBracesindex=Bracescheck;
                            }
                            if (UserTextCharacter[Bracescheck] == ')') { //닫는괄호라면 위치저장
                                CloseBracesindex=Bracescheck;
                            }
                        }
                        if(OpenBracesindex < CloseBracesindex){  //int song( int a ) 와 같이 함수의 정의부분에서는 여는 괄호가 닫는괄호보다 먼저 나와야함
                            for (int Bracescheck = CloseBracesindex+1; Bracescheck < UserTextCharacter.length; Bracescheck++) { //함수를 정의할때 해당라인에 여는 대괄호가 없을 경우에도 처리를 위해 닫는 괄호 ~ 사용자가 입력한 텍스트 전체
                                if (UserTextCharacter[Bracescheck] == '{') { //여는 대괄호를 찾았을 경우에는 
                                    ErrorException = 1; //마찬가지로 세미콜론이 없어도 됨
                                    OpenBracescheck = 1; //또한 뒤에 나오는 정의부 밑에 대괄호가 나올 경우를 처리해줄 필요가 없음.
                                    //System.out.println("test1");
                                    break;
                                }
                                else if (UserTextCharacter[Bracescheck] == ' ' || UserTextCharacter[Bracescheck] == '\n' || UserTextCharacter[Bracescheck] == '\t') { //여는 대괄호가 아닌 공백과 개행의 경우에는 넘어감
                                    //System.out.println("test2");
                                }
                                else { //하지만 여는괄호,공백,개행의 경우가 아닌경우에는 이상한 함수정의이거나 함수 선언임 그래서 세미콜론이 필요함 for문 break
                                    break;
                                }
                            }
                        }
                    }
                    if (OpenBracescheck == 0) { //위에 함수 정의에서의 해당 라인에 대한 세미콜론 예외가 없었을 경우에만 실행하면 됨
                        if (UserTextCharacter[characterColor] == '{') { //여는 대괄호일경우
                            //System.out.println("test1");
                            ErrorException = 1; //일단 세미콜론예외
                            for (int Bracescheck = SemicolonIndex; Bracescheck < characterIndex; Bracescheck++) { //해당 여는 대괄호가 있는 라인 전부 검사
                                //System.out.println(UserTextCharacter[Bracescheck]);
                                if (UserTextCharacter[Bracescheck] != '{' && UserTextCharacter[Bracescheck] != ' ' && UserTextCharacter[Bracescheck] != '\t') { //여는 대괄호, 공백을 제외하고는 어떤 값도 나오면 안됨. 
                                    ErrorException = 0; //해당 경우에는 세미콜론 예외처리를 해주면 안됨
                                    break;
                                }
                            }
                        }
                    }
                    if (UserTextCharacter[characterColor] == '}'){ //이하 동일
                        //System.out.println("test2");
                        ErrorException = 1;
                        for (int Bracescheck = SemicolonIndex; Bracescheck < characterIndex; Bracescheck++) {
                            //System.out.println(UserTextCharacter[Bracescheck]);
                            if (UserTextCharacter[Bracescheck] != '}' && UserTextCharacter[Bracescheck] != ' ' && UserTextCharacter[Bracescheck] != '\t' ) {
                                ErrorException = 0;
                                break;
                            }
                        }
                    }
                }
                if(ErrorException==0) //만약 위에 세미콜론 예외처리를 못받은 라인의 경우에는 세미콜론이 필요함.
                {
                    SemicolonCheckindex=SemicolonIndex; //일단 해당라인만 처리해주기 위해서 라인의 시작인덱스 저장
                    for (int characterColor = SemicolonIndex; characterColor < characterIndex; characterColor++) { //해당라인 전체 검사
                        if(UserTextCharacter[characterColor] ==';'){ //세미콜론을 만났다면 
                            UserTextCharacterColor[characterColor] = "GRAY";
                            SemicolonCheckindex=characterColor+1; //해당 세미콜론의 위치 +1로 에러처리 해줄 시작 위치 변경
                        }
                    }
                    for (int characterColor = SemicolonCheckindex; characterColor < characterIndex; characterColor++) { //세미콜론 위치 +1부터 해당라인 에러처리
                        if(UserTextCharacter[characterColor] != ' ' && UserTextCharacter[characterColor] != '\t') {  //공백에는 오류처리 x
                            UserTextCharacterColor[characterColor] = "ERROR";
                        }
                    }
                }
                SemicolonIndex=characterIndex+1; //해당 라인이 끝났으니 다음라인으로 시작위치 업데이트
            }
        }
    }
}


