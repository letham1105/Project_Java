package View;

import Model.Question;
import Model.QuestionAnswer;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class QuestionAndAnswer extends JPanel{
    private JLabel labelQuestionContentView;
    private JPanel panelAnswerGroup;
    private JButton buttonClearChoice;
    private JPanel panelView;

    private ButtonGroup buttonGroup;
    QuestionAndAnswer(Question question, List<QuestionAnswer> questionAnswerList){
        initComponents();
        addEvents();
        showFormContent(question, questionAnswerList);
        this.add(panelView);

    }

    private void showFormContent(Question question, List<QuestionAnswer> questionAnswerList) {
        labelQuestionContentView.setText(question.getContent());
        for (var questionAnswer : questionAnswerList){
            var radioButton = new JRadioButton(questionAnswer.getContent());
            radioButton.setFont(labelQuestionContentView.getFont());
            radioButton.setActionCommand(questionAnswer.getContent());
            buttonGroup.add(radioButton);
            panelView.add(radioButton);
        }
    }

    private void addEvents() {
        buttonClearChoice.addActionListener(event ->{
            buttonGroup.clearSelection();
        });
    }

    private void initComponents() {
        buttonGroup = new ButtonGroup();
        panelView.setLayout(new GridLayout(5,1));
    }
    public ButtonGroup getButtonGroup(){
        return buttonGroup;
    }

}
