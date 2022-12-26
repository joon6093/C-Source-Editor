package Controller;

import View.MainView;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Objects;
import java.util.StringTokenizer;

public class FileController extends JFrame implements ActionListener {
    private final JTextPane pane = MainView.textPane;
    private String SavePath = null;

    public void fileLoad(String path) throws BadLocationException {
        InputStreamReader reader = null;
        FileInputStream inputStream = null;
        BufferedReader bufferedReader = null;

        try {
            inputStream = new FileInputStream(path);
            reader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(reader);
            pane.setText("");
            String str = null;
            String data = "";

            while ((str = bufferedReader.readLine()) != null)
                data = data.concat(str + "\n");
            MainView.textPane.setText(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert inputStream != null;
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void fileSave(String path) {
        FileOutputStream OutputFile = null;
        try {
            OutputFile = new FileOutputStream(path);
            String UserText = pane.getText();
            StringTokenizer userTextTokenizer = new StringTokenizer(UserText, "\n");
            while (userTextTokenizer.hasMoreTokens()) {
                OutputFile.write(userTextTokenizer.nextToken().getBytes());
                OutputFile.write(("\r\n").getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert OutputFile != null;
                OutputFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (Objects.equals(e.getActionCommand(), "ExitFile     Ctrl+Q")) {
            System.out.println("exit");
            System.exit(0);
        }
        if (Objects.equals(e.getActionCommand(), "NewFile    Ctrl+N") && !pane.getText().equals("")) {
            int option = JOptionPane.showConfirmDialog(null, "현재 내용을 저장 하시겠습니까?", "New", JOptionPane.YES_NO_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                ActionEvent ae = new ActionEvent(new Object(), 0, "SaveFile   Ctrl+S");
                this.actionPerformed(ae);
                pane.setText("");
            }
            else if (option == JOptionPane.NO_OPTION)
                pane.setText("");
        }
        if (Objects.equals(e.getActionCommand(), "LoadFile   Ctrl+O")) {
            if (!pane.getText().equals("")) {
                int option = JOptionPane.showConfirmDialog(null, "현재 내용을 저장 하시겠습니까?", "fileLoad", JOptionPane.YES_NO_CANCEL_OPTION);
                //OK누르면 SaveFile실행
                if (option == JOptionPane.OK_OPTION) {
                    ActionEvent ae = new ActionEvent(new Object(), 0, "SaveFile   Ctrl+S");
                    this.actionPerformed(ae);
                } else if (option == JOptionPane.CANCEL_OPTION) {
                    return;
                }
            }
            FileDialog fileDialog = new FileDialog(this, "파일 불러오기", FileDialog.LOAD);
            fileDialog.setVisible(true);

            String LoadPath = fileDialog.getDirectory() +fileDialog.getFile();
            try {
                fileLoad(LoadPath);
            } catch (BadLocationException e1) {
                e1.printStackTrace();
            }
        }
        if (Objects.equals(e.getActionCommand(), "SaveFile   Ctrl+S")) {
            FileDialog fileDialog = new FileDialog(this, "파일 저장하기", FileDialog.SAVE);
            fileDialog.setVisible(true);

            SavePath = fileDialog.getDirectory() +fileDialog.getFile();
            fileSave(SavePath);
            new AutoSave(SavePath);
        }
    }

    class AutoSave implements Runnable {
        String Autopath;
        AutoSave(String path) {
            Autopath = path;
            new Thread(this).start();
        }

        public void run() {
            while (true) {
                try {
                    if (SavePath == null) {
                        continue;
                    }
                    Thread.sleep(20000);
                    fileSave(Autopath);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }

    public class FileHotkeyListener extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S) {
                if (SavePath == null) {
                    ActionEvent ae = new ActionEvent(new Object(), 0, "SaveFile   Ctrl+S");
                    actionPerformed(ae);
                } else
                    fileSave(SavePath);
            }
            if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_N) {
                ActionEvent ae = new ActionEvent(new Object(), 0, "NewFile    Ctrl+N");
                actionPerformed(ae);
            }
            if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_O) {
                ActionEvent ae = new ActionEvent(new Object(), 0, "LoadFile   Ctrl+O");
                actionPerformed(ae);
            }
            if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Q) {
                System.exit(0);
            }
        }
    }
}