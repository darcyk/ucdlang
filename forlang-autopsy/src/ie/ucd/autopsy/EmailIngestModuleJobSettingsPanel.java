package ie.ucd.autopsy;

import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;
import org.sleuthkit.autopsy.ingest.IngestModuleIngestJobSettings;
import org.sleuthkit.autopsy.ingest.IngestModuleIngestJobSettingsPanel;

/** UI component used to make per ingest job settings for Email ingest module */
public final class EmailIngestModuleJobSettingsPanel extends IngestModuleIngestJobSettingsPanel {

    private static final long serialVersionUID = 1L;
    private JCheckBox addToGraphDatabaseCheckBox = null;
    private JTextField neo4jPasswordTextField = null;
    private JTextField neo4jUriTextField = null;
    private JTextField neo4jUsernameTextField = null;

    /** Creates new form EmailIngestModuleIngestJobSettingsPanel */
    public EmailIngestModuleJobSettingsPanel(EmailIngestModuleJobSettings settings) {
        initComponents();
        customizeComponents(settings);
    }

    /**
     * Gets the ingest job settings for an ingest module.
     *
     * @return The ingest settings.
     */
    @Override
    public final IngestModuleIngestJobSettings getSettings() {
        return new EmailIngestModuleJobSettings(addToGraphDatabaseCheckBox.isSelected(), neo4jUriTextField.getText(),
            neo4jUsernameTextField.getText(), neo4jPasswordTextField.getText());
    }

    private final void customizeComponents(EmailIngestModuleJobSettings settings) {
        addToGraphDatabaseCheckBox.setSelected(settings.addToGraphDatabase());
        neo4jUriTextField.setText(settings.getNeo4jUriStr());
        neo4jUsernameTextField.setText(settings.getNeo4jUsername());
        neo4jPasswordTextField.setText(settings.getNeo4jPassword());
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    private void initComponents() {
        addToGraphDatabaseCheckBox = new JCheckBox();
        JLabel neo4jUriLabel = new JLabel();
        neo4jUriTextField = new JTextField();
        JLabel neo4jUsernameLabel = new JLabel();
        neo4jUsernameTextField = new JTextField();
        JLabel neo4jPasswordLabel = new JLabel();
        neo4jPasswordTextField = new JPasswordField();
        Mnemonics.setLocalizedText(addToGraphDatabaseCheckBox, NbBundle.getMessage(
                EmailIngestModuleJobSettingsPanel.class,
                "EmailIngestModuleIngestJobSettingsPanel.addToGraphDatabaseCheckBox.text"));
        Mnemonics.setLocalizedText(neo4jPasswordLabel, NbBundle.getMessage(
                EmailIngestModuleJobSettingsPanel.class,
                "EmailIngestModuleIngestJobSettingsPanel.neo4jPasswordLabel.text"));
        Mnemonics.setLocalizedText(neo4jUriLabel, NbBundle.getMessage(
                EmailIngestModuleJobSettingsPanel.class,
                "EmailIngestModuleIngestJobSettingsPanel.neo4jUriLabel.text"));
        Mnemonics.setLocalizedText(neo4jUsernameLabel, NbBundle.getMessage(
                EmailIngestModuleJobSettingsPanel.class,
                "EmailIngestModuleIngestJobSettingsPanel.neo4jUsernameLabel.text"));
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(neo4jUriLabel)
                .addComponent(neo4jUsernameLabel)
                .addComponent(neo4jPasswordLabel))
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(addToGraphDatabaseCheckBox)
                .addComponent(neo4jUriTextField)
                .addComponent(neo4jUsernameTextField)
                .addComponent(neo4jPasswordTextField))
            );
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addComponent(addToGraphDatabaseCheckBox)
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(neo4jUriLabel)
                .addComponent(neo4jUriTextField))
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(neo4jUsernameLabel)
                .addComponent(neo4jUsernameTextField))
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(neo4jPasswordLabel)
                .addComponent(neo4jPasswordTextField))
            
	);
        
        
//        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
//        hGroup.addGroup(layout.createParallelGroup().addComponent(addToGraphDatabaseCheckBox));
//        hGroup.addGroup(layout.createParallelGroup().addComponent(neo4jUriLabel).addComponent(neo4jUsernameLabel).addComponent(neo4jPasswordLabel));
//        hGroup.addGroup(layout.createParallelGroup().addComponent(neo4jUriTextField).addComponent(neo4jUsernameTextField).addComponent(neo4jPasswordTextField));
//        layout.setHorizontalGroup(hGroup); 
//        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
//                layout.createSequentialGroup().addContainerGap().addComponent(addToGraphDatabaseCheckBox)
//                .addContainerGap(255, Short.MAX_VALUE)));
//        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
//                layout.createSequentialGroup().addContainerGap().addComponent(addToGraphDatabaseCheckBox)
//                .addContainerGap(270, Short.MAX_VALUE)));
    }
}