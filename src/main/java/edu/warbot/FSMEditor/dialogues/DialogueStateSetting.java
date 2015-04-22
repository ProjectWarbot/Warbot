package edu.warbot.FSMEditor.dialogues;

import edu.warbot.FSMEditor.models.ModelState;
import edu.warbot.FSMEditor.settings.EditorSettings;
import edu.warbot.FSMEditor.settings.GenericPlanSettings;
import edu.warbot.FSMEditor.views.ViewBrain;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class DialogueStateSetting extends AbstractDialogue {

    private static final long serialVersionUID = 1L;
    JTextField fieldName = new JTextField(DEFAULT_STATE_NAME);
    JComboBox<String> comboxPlan = new JComboBox<>(EditorSettings.getPlansClassSimpleName());

    public DialogueStateSetting(ViewBrain viewBrain, ModelState modelState) {
        this(viewBrain, modelState.getPlanSettings());

        this.fieldName = new JTextField(modelState.getName());
        this.comboxPlan.setSelectedItem(modelState.getPlanName());
    }

    public DialogueStateSetting(ViewBrain viewBrain, GenericPlanSettings planSettings) {
        super(viewBrain, planSettings);

        if (genericSettings == null)
            genericSettings = new GenericPlanSettings();
    }

    @Override
    public void createDialog() {
        super.createDialog();
    }

    protected JPanel getPanelMainSetting() {
        JPanel panelName = new JPanel(new GridLayout(2, 2));
        panelName.setBorder(new TitledBorder("State settings"));

        panelName.add(add(new JLabel("Name")));
        panelName.add(fieldName);

        panelName.add(new JLabel("Plan"));
        panelName.add(comboxPlan);

        return panelName;

    }

    @Override
    public boolean isValide() {
        return !this.fieldName.getText().isEmpty();
    }

    public GenericPlanSettings getPlanSettings() {
        return (GenericPlanSettings) genericSettings;
    }

    public String getStateName() {
        return this.fieldName.getText();
    }

    public String getPlanName() {
        return EditorSettings.getPlanFullName((String) comboxPlan.getSelectedItem());
    }

    public JButton getValidationButton() {
        return this.buttonOk;
    }

}
