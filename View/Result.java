package View;

import Model.Question;
import Model.User;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.util.List;

public class Result extends JFrame{
    private final User loginUser;
    private final List<Question> questionList;
    private final List<String> chosenAnswerList;
    private final List<String> correctAnswerList;
    private final List<String> resultList;
    private final double score;
    private final int totalCorrect;
    private JTextField textFieldSearch;
    private JButton backButton;
    private JTable tableView;
    private JPanel panelView;
    private JLabel labelSearch;
    private JLabel labelResult;
    private DefaultTableModel columnModel;
    private DefaultTableModel rowModel;
    private TableRowSorter<TableModel> rowSorter;

    public Result(User loginUser,
                      List<Question> questionList,
                      List<String> chosenAnswerList,
                      List<String> correctAnswerList,
                      List<String> resultList,
                      int totalCorrect,
                      double score) {
        this.loginUser = loginUser;
        this.questionList = questionList;
        this.chosenAnswerList = chosenAnswerList;
        this.correctAnswerList = correctAnswerList;
        this.resultList = resultList;
        this.totalCorrect = totalCorrect;
        this.score = score;
        this.setTitle("Result");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setContentPane(panelView);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        initComponents();
        addActionEvent();
        fillData();
        makeTableSearchable();
    }

    public static void main(String[] args) {

    }
    private void makeTableSearchable() {
        rowSorter = new TableRowSorter<>(rowModel);
        var i = 0;
        while (i < columnModel.getColumnCount()) {
            rowSorter.setSortable(i, false);
            ++i;
        }
        tableView.setRowSorter(rowSorter);
        textFieldSearch
                .getDocument()
                .addDocumentListener(new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        var text = textFieldSearch.getText().strip();
                        if (text.length() != 0){
                            rowSorter.setRowFilter(RowFilter.regexFilter(text));
                        }else{
                            rowSorter.setRowFilter(null);
                        }
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        var text = textFieldSearch.getText().strip();
                        if (text.length() != 0){
                            rowSorter.setRowFilter(RowFilter.regexFilter(text));
                        }else{
                            rowSorter.setRowFilter(null);
                        }

                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {

                    }
                });
    }

    private void fillData() {
        rowModel.setRowCount(0);
        var index = 0;
        for ( var question : questionList){
            var chosenAnswer = chosenAnswerList.get(index);
            var correctAnswer = correctAnswerList.get(index);
            var result = resultList.get(index);
            index++;
            rowModel.addRow(new Object[]{
                    question.getContent(),
                    chosenAnswer,
                    correctAnswer,
                    result
            });
        }
        var resultText = "Result : "
                +totalCorrect
                +" / "
                +questionList.size()
                +"Question - Score:"
                +score;
        labelResult.setText(resultText);
    }

    private void addActionEvent() {
        backButton.addActionListener(event ->{
            this.dispose();
            new MenuExaminee(loginUser);
        });
    }

    private void initComponents() {
        tableView.setDefaultEditor(Object.class, null);
        tableView.getTableHeader().setReorderingAllowed(false);
        columnModel = new DefaultTableModel(
                new Object[][]{},
                new String[] {"Question", "Your Answer", "Correct Answer", "Result"}
        );
        tableView.setModel(columnModel);
        rowModel = (DefaultTableModel) tableView.getModel();
    }
}
