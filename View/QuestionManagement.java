package View;

import DAO.ExamDAO;
import DAO.QuestionDAO;
import Model.Question;
import Model.User;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class QuestionManagement extends JFrame {
    private final User loginUser;
    private JTextField textFieldExamID;
    private JTextField textFieldContent;
    private JComboBox comboBoxLevel;
    private JTextField textFieldQuestionID;
    private JButton addButton;
    private JButton updatedButton;
    private JButton deleteButton;
    private JButton answerManagementButton;
    private JButton refreshButton;
    private JButton backButton;
    private JTextField textFieldSearch;
    private JTable tableViewQuestion;
    private JPanel panelViewQM;
    private JLabel ExamIDLabel;
    private DefaultTableModel columnModel;
    private DefaultTableModel rowModel;
    private TableRowSorter<TableModel> rowSorter = null;
    private List<Question> list;
    private Question chosenQuestion = null;

    public QuestionManagement(User user) {
        this.loginUser = user;
        initComponents();
        addActionEvent();
        this.setTitle("Question Management");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(panelViewQM);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        fillDataToTable();
        makeTableSearchable();

    }

    public static void main(String[] args) {
        User admin = new User("admin", "admin", "admin", true);
        EventQueue.invokeLater(() -> new QuestionManagement(admin));

    }

    private void makeTableSearchable() {
        rowSorter = new TableRowSorter<>(rowModel);
        var i = 0;
        while ( i < columnModel.getColumnCount()){
            rowSorter.setSortable(i, false);
            ++i;
        }
        tableViewQuestion.setRowSorter(rowSorter);
        textFieldSearch
                .getDocument()
                .addDocumentListener(new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                      var text = textFieldSearch.getText().strip();
                      if ( text.length() != 0){
                          rowSorter.setRowFilter(RowFilter.regexFilter(text));
                      }else{
                          rowSorter.setRowFilter(null);
                      }
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        var text = textFieldSearch.getText().strip();
                        if ( text.length() != 0){
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

    private void fillDataToTable() {
        list = QuestionDAO.selectAll();
        rowModel.setRowCount(0);
        for ( var question : list){
            rowModel.addRow(new Object[]{
                    question.getQuestion_id(),
                    question.getExam_id(),
                    question.getLevel(),
                    question.getContent()
            });
        }
    }

    private void addActionEvent() {
        tableViewQuestion.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tableViewQuestionMouseClicked();
            }

            private void tableViewQuestionMouseClicked() {
                resetInputField();
                textFieldQuestionID.setEnabled(false);
                var index = tableViewQuestion.getSelectedRow();
                chosenQuestion = list.get(index);
                textFieldQuestionID.setText(String.valueOf(chosenQuestion.getQuestion_id()));
                textFieldExamID.setText(String.valueOf(chosenQuestion.getExam_id()));
                textFieldContent.setText(chosenQuestion.getContent());
                comboBoxLevel.setSelectedIndex(chosenQuestion.getLevel()-1);
            }
        });
        addButton.addActionListener(e ->{
            if (textFieldExamID.getText().isEmpty()
            || textFieldContent.getText().isEmpty()){
                JOptionPane.showMessageDialog(
                        this,
                        "Information can not be blank !",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            var exam_id = Long.parseLong(textFieldExamID.getText().strip());
            var checkValidExamID = ExamDAO.selectByID(exam_id);
            if ( checkValidExamID == null ){
                JOptionPane.showMessageDialog(
                        this,
                        "ExamID is not exits, Please try again!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
          var level = comboBoxLevel.getSelectedIndex() +1;
            var content = textFieldContent.getText().strip();
            var question = new Question(exam_id,level,content);
            var isSuccess = QuestionDAO.insert(question);
            if ( isSuccess ){
                JOptionPane.showMessageDialog(
                        this,
                        "Add Success",
                        "Notice",
                        JOptionPane.INFORMATION_MESSAGE
                );
                fillDataToTable();
            }else{
                JOptionPane.showMessageDialog(
                        this,
                        "Add fail. Please try again !",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
          resetInputField();
        });
    updatedButton.addActionListener(event ->{
        if ( textFieldQuestionID.getText().isEmpty()
        || textFieldExamID.getText().isEmpty()
        || textFieldContent.getText().isEmpty()
        ){
            JOptionPane.showMessageDialog(
                    this,
                    "Information can not be blank !",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        var question_id = Long.parseLong(textFieldQuestionID.getText().strip());
        var exam_id = Long.parseLong(textFieldExamID.getText().strip());
        var checkValidExamID = ExamDAO.selectByID(exam_id);
        if (checkValidExamID == null){
            JOptionPane.showMessageDialog(
                    this,
                    "ExamID is not exits, Please try again!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        var level = comboBoxLevel.getSelectedIndex() + 1;
        var content = textFieldContent.getText().strip();
        var question = new Question(question_id,exam_id, level, content);
        var isSuccess = QuestionDAO.update(question);
        if ( isSuccess ){
            JOptionPane.showMessageDialog(
                    this,
                    "Update Success",
                    "Notice",
                    JOptionPane.INFORMATION_MESSAGE
            );
            fillDataToTable();
        }else{
            JOptionPane.showMessageDialog(
                    this,
                    "Update failure. Please try again",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
        resetInputField();
    });
    deleteButton.addActionListener(e ->{
        var questionID = textFieldQuestionID.getText().strip();
        if (!questionID.isEmpty()){
            var isSuccess = QuestionDAO.delete(Long.parseLong(questionID));
            if (isSuccess){
                    JOptionPane.showMessageDialog(
                            this,
                            "Delete Success",
                            "Notice",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    fillDataToTable();
                }else{
                    JOptionPane.showMessageDialog(
                            this,
                            "Delete failure. Please try again",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
                resetInputField();
            }else{
            JOptionPane.showMessageDialog(
                    this,
                    "Please select the question you want to delete !",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    });
        answerManagementButton.addActionListener(e ->{
            new AnswerManagement(loginUser);
        });
        refreshButton.addActionListener(e -> {
            resetInputField();
            textFieldSearch.setText("");
        });

    backButton.addActionListener(e -> {
        if (loginUser.getUser_id().equals("admin")){
            this.dispose();
            new MenuAdmin(loginUser);
        }else{
            this.dispose();
            new MenuExaminationSetter(loginUser);
        }
    });
    }

    private void resetInputField() {
        textFieldQuestionID.setText("");
        textFieldExamID.setText("");
        textFieldContent.setText("");
        comboBoxLevel.setSelectedIndex(0);
    }

    private void initComponents() {
        textFieldQuestionID.setEnabled(false);
        tableViewQuestion.setDefaultEditor(Object.class,null);
        tableViewQuestion.getTableHeader().setReorderingAllowed(false);
        columnModel = new DefaultTableModel(
                new Object[][]{},
                new String[] {"QuestionID","ExamID","Level","Content"}
        );
        tableViewQuestion.setModel(columnModel);
        rowModel = (DefaultTableModel) tableViewQuestion.getModel();
    }
}
