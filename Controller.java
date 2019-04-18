package com.javarush.task.task32.task3209;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.io.*;

public class Controller {
    private View view;
    private HTMLDocument document;
    private File currentFile;

    public Controller(View view) {
        this.view = view;
    }

    public static void main(String[] args) {
        View view = new View();
        Controller controller = new Controller(view);
        view.setController(controller);
        view.init();
        controller.init();

    }

    public HTMLDocument getDocument() {
        return document;
    }

    public void resetDocument(){
        if (document != null) {
            //Удалять у текущего документа document слушателя правок которые можно отменить/вернуть
            document.removeUndoableEditListener(view.getUndoListener());
        }
        //Создавать новый документ по умолчанию и присваивать его полю document
        document = (HTMLDocument) new HTMLEditorKit().createDefaultDocument();
        document.putProperty("IgnoreCharsetDirective", Boolean.TRUE);
        //Добавлять новому документу слушателя правок
        document.addUndoableEditListener(view.getUndoListener());
        //Вызывать у представления метод update()
        view.update();
    }
    public void setPlainText(String text){
        resetDocument();
        StringReader reader = new StringReader(text);
        HTMLEditorKit kit = new HTMLEditorKit();
        try {
            kit.read(reader, document, 0);
        } catch (Exception e) {
            ExceptionHandler.log(e);
        }

    }
    public String getPlainText(){
        StringWriter writer = new StringWriter();
        HTMLEditorKit kit = new HTMLEditorKit();
        try {
            kit.write(writer,document,0, document.getLength());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BadLocationException e) {
            ExceptionHandler.log(e);
        }
        return writer.toString();
    }

    public void createNewDocument(){
        view.selectHtmlTab();
        resetDocument();
        view.setTitle("HTML редактор");
        view.resetUndo();
        currentFile = null;
    }
//    public void openDocument() {
// Метод должен работать аналогично методу saveDocumentAs(), в той части, которая отвечает за выбор файла
//
//        //Переключать представление на html вкладку
//        view.selectHtmlTab();
//        //Создавать новый объект для выбора файла JFileChooser
//        JFileChooser jFileChooser = new JFileChooser();
//        //Устанавливать ему в качестве фильтра объект HTMLFileFilter
//        jFileChooser.setFileFilter(new HTMLFileFilter());
//        //Показывать диалоговое окно "Save File" для выбора файла
//        int n = jFileChooser.showOpenDialog(view);
//
//        //Когда файл выбран, необходимо
//        if (n == JFileChooser.APPROVE_OPTION) {
//            //Установить новое значение currentFile
//            currentFile = jFileChooser.getSelectedFile();
//            //Сбросить документ
//            resetDocument();
//            //Установить имя файла в заголовок у представления
//            view.setTitle(currentFile.getName());
//
//            //Создать FileReader, используя currentFile
//            try (FileReader fileReader = new FileReader(currentFile)) {
//                //Вычитать данные из FileReader-а в документ document с помощью объекта класса
//                new HTMLEditorKit().read(fileReader, document, 0);
//                //Сбросить правки
//                view.resetUndo();
//            } catch (Exception e) {
//                ExceptionHandler.log(e);
//            }
//        }
//    }
    public void openDocument(){
        view.selectHtmlTab();
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileFilter(new HTMLFileFilter());
        int index = jFileChooser.showOpenDialog(view);
        if (index == JFileChooser.APPROVE_OPTION) {
            currentFile = jFileChooser.getSelectedFile();
            resetDocument();
            view.setTitle(currentFile.getName());
            try {
                FileReader reader = new FileReader(currentFile);
                HTMLEditorKit kit = new HTMLEditorKit();

                kit.read(reader,document,0);
                reader.close();
                view.resetUndo();
            } catch (Exception e) {
                ExceptionHandler.log(e);
            }
        }
    }

public void saveDocument() {
    view.selectHtmlTab();
    if (currentFile == null)
        saveDocumentAs();
    else {
        try {
            view.setTitle(currentFile.getName());
            FileWriter writer = new FileWriter(currentFile);
            new HTMLEditorKit().write(writer, document, 0, document.getLength());
        } catch (Exception e) {
            ExceptionHandler.log(e);
        }
    }
}

    public void saveDocumentAs(){
        view.selectHtmlTab();
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileFilter(new HTMLFileFilter());
        int index = jFileChooser.showSaveDialog(view);
        if (index == JFileChooser.APPROVE_OPTION) {
            currentFile = jFileChooser.getSelectedFile();
            view.setTitle(currentFile.getName());
            try {
                FileWriter writer = new FileWriter(currentFile);
                HTMLEditorKit kit = new HTMLEditorKit();
                kit.write(writer,document,0, document.getLength());
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (BadLocationException e) {
                ExceptionHandler.log(e);
            }
        }
    }

    public void showAbout(){}

    public void init(){
        createNewDocument();
    }

    public void exit(){
        System.exit(0);
    }

}
