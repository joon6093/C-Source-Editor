package Controller;

import View.*;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.event.*;


public class EditController extends JFrame implements ActionListener {
    private static SearchView SearchView;
    private static ChangeView ChangeView;
    private static ChangeAllView ChangeAllView;
    private static SearchAllView SearchAllView;

    public void actionPerformed(ActionEvent e)
    {
        if (e.getActionCommand().equals("Search          Ctrl+F"))
        {
            SearchView = new SearchView();
        }
        else if (e.getActionCommand().equals("searchALL   Ctrl+G"))
        {
            SearchAllView =new SearchAllView();
        }
        else if (e.getActionCommand().equals("Change         Ctrl+R"))
        {
            ChangeView = new ChangeView();
        }
        else if (e.getActionCommand().equals("ChangeALl   Ctrl+T"))
        {
            ChangeAllView = new ChangeAllView();
        }
        else if(e.getActionCommand().equals("remove highlight"))
        {
            if(MyActionListener.text_highlight!= null)
            {
                MyActionListener.text_highlight.removeAllHighlights();
            }
        }
    }

    public static class MyMouseEvent implements MouseListener
    {
        //마우스가 해당 컴포넌트를 클릭했을때.
        public void mouseClicked(MouseEvent e) throws NullPointerException
        {
            if(MyActionListener.text_highlight!= null)
            {
                MyActionListener.text_highlight.removeAllHighlights();
            }
        }
        public void mouseEntered(MouseEvent e) { }
        public void mouseExited(MouseEvent e) { }
        public void mousePressed(MouseEvent e) { }
        public void mouseReleased(MouseEvent e) { }
    }


    public static class MykeyListener extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_F) {
                SearchView = new SearchView();
            } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_G) {
                SearchAllView = new SearchAllView();
            } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_R) {
                ChangeView = new ChangeView();
            } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_T) {
                ChangeAllView = new ChangeAllView();
            }
        }
    }

    public static class MyActionListener implements ActionListener
    {
        private final JTextPane textPane;
        public static Highlighter text_highlight;
        private static int offset = 0;
        private static int last_offset = 0;
        private static String find_text;
        private static int count = 0;
        private static int word_count = 0;
        private static String conversion_word = "";
        private static String viewText;

        public MyActionListener()
        {
            this.textPane = MainView.textPane;
            this.text_highlight = this.textPane.getHighlighter();
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            int fi = 0;
            int max;
            JButton currentButton = (JButton)e.getSource();  //액션된 버튼의 정보

            if(currentButton.getText().equals("단일 찾기"))
            {
                text_highlight.removeAllHighlights();   // 먼저 존재하는 하이라이트 제거
                viewText = textPane.getText().replace("\r\n", "\n"); ///텍스트펜의 전체 문자열, \r\n 처리된 개행을 \n으로 변경
                find_text = SearchView.tf1.getText(); //찾으려는 단어

                if(viewText.contains(find_text))     //단어가 존재한다면
                {
                    word_count = 1;      //단어 갯수
                    offset = viewText.indexOf(find_text, offset);   //뷰텍스트의 단어 위치
                    count = 0;   //단어 전체를 안 돌았을 때
                    try { text_highlight.addHighlight(offset, offset + find_text.length(), DefaultHighlighter.DefaultPainter); }   //단어 하이라인팅
                    catch (BadLocationException CanNotSearch) { CanNotSearch.printStackTrace(); }
                    offset = offset + find_text.length();    //현재 작업 위치
                    if(offset > viewText.lastIndexOf(find_text)) {last_offset = offset; count = 1; offset = 0;}  // 단어가 마지막이라면 맨 위쪽으로 초기화
                }
                else   //단어가 존재하지 않는다면
                {
                    word_count=0;
                    JOptionPane.showMessageDialog(null, "더 이상  찾는 단어가 없습니다.", "message", JOptionPane.WARNING_MESSAGE);  //에러메세지 출력
                }
            }
            else if(currentButton.getText().equals("다중 찾기"))
            {
                text_highlight.removeAllHighlights();   //하이라이팅 제거
                viewText = textPane.getText().replace("\r\n", "\n"); //텍스트펜의 전체 문자열, \r\n 처리된 개행을 \n으로 변경
                find_text = SearchAllView.tf1.getText();        //찾으려는 단어

                if(viewText.contains(find_text))   //존재한다면
                {
                    max = viewText.lastIndexOf(find_text); //해당 단어의 마지막으로 있는 위치
                    for(int k=0; k < max; k++)
                    {
                        fi = viewText.indexOf(find_text, fi); //뷰텍스트의 위치
                        try
                        {
                            text_highlight.addHighlight(fi, fi+find_text.length(), DefaultHighlighter.DefaultPainter);  //하이라이팅
                        }
                        catch (BadLocationException CanNotSearch)
                        {
                            CanNotSearch.printStackTrace();
                        }
                        fi = fi + find_text.length();    // 다음 단어 위치 변경
                        if(fi >= max + 1) break;        //마지막을 넘었으면 반복문 종료
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "더 이상  찾는 단어가 없습니다.", "message", JOptionPane.WARNING_MESSAGE);  //단어가 없으면 에러메세지 출력
                }
            }
            else if(currentButton.getText().equals("단일 바꾸기"))
            {
                if(word_count != 0)   //단어가 존재할 때(하이라이팅)
                {
                    conversion_word = ChangeView.tf1.getText(); //바꾸려는 단어
                    if(count == 1) //마지막에서 첫번째로 다시 돌아온 경우가 아니라면
                    {
                        textPane.select(last_offset-find_text.length(), last_offset);   //바꿀 단어 선택
                        textPane.replaceSelection(conversion_word); //변경
                        try {
                            text_highlight.addHighlight(last_offset - find_text.length(), last_offset - find_text.length() + conversion_word.length(), DefaultHighlighter.DefaultPainter);
                            //변경된 단어 하이라이팅
                        } catch (BadLocationException e1) {
                            e1.printStackTrace();
                        }
                        count=0;
                    }
                    else //마지막에서 첫번째로 다시 돌아온 경우(첫번째 단어)
                    {
                        textPane.select(offset-find_text.length(), offset);  //바꿀 단어 선택
                        textPane.replaceSelection(conversion_word);     //변경
                        try {
                            text_highlight.addHighlight(offset-find_text.length(), offset-find_text.length()+ conversion_word.length(), DefaultHighlighter.DefaultPainter);
                            //변경된 단어 하이라이팅
                        } catch (BadLocationException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                word_count=0; //변경할 단어 없음
            }
            else if(currentButton.getText().equals("다중 바꾸기"))
            {
                int cpos=0;
                String cur, after;
                conversion_word = ChangeAllView.tf1.getText();
                cur = textPane.getText(); //현재 텍스트팬 적힌 문자열
                after = cur.replaceAll(find_text, conversion_word); // 변환할 문자열
                textPane.selectAll(); //텍스트팬 전체 선택
                textPane.replaceSelection(after); // 바뀐 문자열로 교체
                int max2;
                max2 = after.lastIndexOf(conversion_word);
                for(int k=0; k<after.length(); k++) //바뀐 단어 하나씩 하이라이팅.
                {
                    cpos = after.indexOf(conversion_word, cpos);
                    try {
                        text_highlight.addHighlight(cpos, cpos+ conversion_word.length(), DefaultHighlighter.DefaultPainter);
                    } catch (BadLocationException ble) {
                    }
                    cpos = cpos+ conversion_word.length();
                    if(cpos >= max2 + 1)
                        break;
                }
            }
        }
    }




}
