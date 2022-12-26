package View;
// 다중 찾기 뷰

import Controller.EditController;

import javax.swing.*;
import java.awt.*;


public class ChangeView extends JFrame
{
    public JTextField tf1;
    public JButton bt1;

    public EditController ed;

    public ChangeView()
    {

        setLayout(null);
        tf1 = new JTextField();//바꾸기 단어를 입력받을 tf1 생성
        bt1 = new JButton();// 단일 바꾸기 버튼 생성

        setTitle("단일 바꾸기");// 타이틀 지정
        setSize(387, 100);// 창의 사이즈 설정
        setLocation(600, 400);// 창이 띄워질 위치 선정
        setResizable(false); //창 크기 조절 불가능하도록 설정

        Container container = getContentPane();//컨테이너를 가질 수 있는 패널 생성
        container.setLayout(null);// 컨테이터 배치관리자 제거 -> 컴포넌트 절대위치와 크기 설정을 위해서
        tf1.setBounds(18, 20, 200, 30);//바꾸기 단어 입력받는 텍스트필드 컴포넌트의 절대 위치 설정
        bt1.setBounds(245, 20, 110, 30);// 버튼 컴포넌트의 절대 위치 설정

        bt1.setText("단일 바꾸기");// 버튼에 "단일바꾸기" 텍스트가 보이도록 설정


        container.add(tf1);// 패널 내에 tf1을 출력
        container.add(bt1);// 패널 내에 button(bt1)을 출력


        bt1.addActionListener(new EditController.MyActionListener());// 에딧컨트롤러와 연결하여 해당 기능을 수행하도록 함
        setVisible(true);// 창을 화면에 나타냄

    }

}

