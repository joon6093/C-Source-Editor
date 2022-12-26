package View;

import Controller.EditController;
import Controller.FileController;
import Controller.InputController;

import javax.swing.*;
import java.awt.*;
public class MainView extends JFrame {
    public static JTextPane textPane = new JTextPane();
    private final InputController song = new InputController(textPane);

    private final JLabel jLabel1 = new JLabel("");
    private final JLabel jLabel2 = new JLabel("");

    public MainView() {
        setLayout(null);
        JFrame jFrame = new JFrame();

        jFrame.setTitle("C 소스 편집기");
        jFrame.setSize(1080, 720);
        jFrame.setLocation(100, 50);
        jFrame.setDefaultCloseOperation(jFrame.EXIT_ON_CLOSE);
        Container c = jFrame.getContentPane();
        c.setLayout(null);
        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setSize(1010, 600);
        scrollPane.setLocation(10, 10);
        c.add(scrollPane);
        jLabel1.setSize(860, 20);
        jLabel1.setLocation(10, 610);
        c.add(jLabel1);

        jLabel2.setSize(860, 20);
        jLabel2.setLocation(10, 630);
        c.add(jLabel2);

        JMenuBar menuBar = new JMenuBar();
        jFrame.setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        JMenu editMenu = new JMenu("Edit");
        menuBar.add(editMenu);

        jFrame.setVisible(true);

        JMenuItem newItem = new JMenuItem("NewFile    Ctrl+N");
        fileMenu.add(newItem);

        JMenuItem loadItem = new JMenuItem("LoadFile   Ctrl+O");
        fileMenu.add(loadItem);

        JMenuItem saveItem = new JMenuItem("SaveFile   Ctrl+S");
        fileMenu.add(saveItem);

        JMenuItem exitItem = new JMenuItem("ExitFile     Ctrl+Q");
        fileMenu.add(exitItem);

        //파일 메뉴 액션 리스너 등록
        FileController fc = new FileController();
        newItem.addActionListener(fc);
        loadItem.addActionListener(fc);
        saveItem.addActionListener(fc);
        exitItem.addActionListener(fc);

        //파일 메뉴 단축키 리스너 등록
        textPane.addKeyListener(fc.new FileHotkeyListener());

        //에딧메뉴 단축키 리스너 등록
        textPane.addKeyListener(new EditController.MykeyListener());
        textPane.addMouseListener(new EditController.MyMouseEvent());
        EditController ed = new EditController();

        JMenuItem Edit_Menu_search_Item = new JMenuItem("Search          Ctrl+F");
        editMenu.add(Edit_Menu_search_Item);

        JMenuItem Edit_Menu_ALLsearch_Item = new JMenuItem("searchALL   Ctrl+G");
        editMenu.add(Edit_Menu_ALLsearch_Item);

        JMenuItem Edit_Menu_change_Item = new JMenuItem("Change         Ctrl+R");
        editMenu.add(Edit_Menu_change_Item);

        JMenuItem Edit_Menu_ALLchange_Item = new JMenuItem("ChangeALl   Ctrl+T");
        editMenu.add(Edit_Menu_ALLchange_Item);

        JMenuItem Edit_Menu_Delete_highlight = new JMenuItem("remove highlight");
        editMenu.add(Edit_Menu_Delete_highlight);

        Edit_Menu_Delete_highlight.addActionListener(ed);
        Edit_Menu_search_Item.addActionListener(ed);
        Edit_Menu_ALLsearch_Item.addActionListener(ed);
        Edit_Menu_change_Item.addActionListener(ed);
        Edit_Menu_ALLchange_Item.addActionListener(ed);

        StatusThread proThread = new StatusThread();
        proThread.start();

    }

    class StatusThread extends Thread {
        public void run() {
            while (true) {
                char[] UserText = textPane.getText().replace(System.getProperty("line.separator"), "\n").toCharArray();
                int Rowindex = 1;
                int Colindex = 1;
                for (int Textindex = 0; Textindex < textPane.getCaretPosition(); Textindex++) {
                    if (UserText[Textindex] == '\n') {
                        Rowindex++;
                        Colindex = 0;
                    }
                    Colindex++;
                }
                jLabel1.setText("행 :" + Rowindex );
                jLabel2.setText("열 :" + Colindex);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }
}


