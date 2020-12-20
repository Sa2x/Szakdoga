import javax.naming.directory.DirContext;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DtControllerGUI extends JFrame implements ActionListener {
    protected DTController controller = new DTController();

    private int selected;

    private JPanel devicePanel;

    private JPanel controlPanel;

    private JPanel addPanel;

    private JPanel generatePanel;

    private JPanel viewDataPanel;

    private JPanel deployPanel;

    private JButton addButton;

    private JButton removeButton;

    private JButton viewDataButton;

    private JButton generateButton;

    private JButton stopButton;

    private JButton checkSSHCOnnection;

    private JButton deployButton;

    private JTable table;

    private JFrame frame = this;
    //private DeviceTableModel model;

    private String filepath = "";

    public DtControllerGUI() {
        //model = new DeviceTableModel(controller.getDevices());
        setSize(1000, 500);
        setResizable(false);
        setVisible(true);
        setTitle("Digital Twin Controller");
        //this.add(new DeviceTablePanel(this));
        this.setLayout(new BorderLayout());
        initDevicePanel();
        initControlPanel();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                controller.saveDevices();
                System.exit(0);
            }
        });
    }

    public void initControlPanel() {
        controlPanel = new JPanel();
        addButton = new JButton("Add device");
        removeButton = new JButton("Remove device");
        viewDataButton = new JButton("View data of device");
        generateButton = new JButton("Generate DT");
        checkSSHCOnnection = new JButton("Check SSH connection");
        deployButton = new JButton("Deploy DT ");
        stopButton = new JButton("Stop running");

        removeButton.setEnabled(false);
        viewDataButton.setEnabled(false);
        generateButton.setEnabled(false);
        checkSSHCOnnection.setEnabled(false);
        //deployButton.setEnabled(false);

        addButton.addActionListener(this);
        addButton.setActionCommand("addbutton");
        removeButton.addActionListener(this);
        removeButton.setActionCommand("remove");
        viewDataButton.addActionListener(this);
        viewDataButton.setActionCommand("view");
        generateButton.addActionListener(this);
        generateButton.setActionCommand("generate");
        checkSSHCOnnection.addActionListener(this);
        checkSSHCOnnection.setActionCommand("ssh");
        deployButton.addActionListener(this);
        deployButton.setActionCommand("deploy");
        stopButton.addActionListener(this);
        stopButton.setActionCommand("stoprunning");

        controlPanel.setLayout(new GridLayout(0, 1, 0, 5));
        controlPanel.add(addButton);
        controlPanel.add(removeButton);
        controlPanel.add(viewDataButton);
        controlPanel.add(generateButton);
        controlPanel.add(checkSSHCOnnection);
        controlPanel.add(deployButton);
        controlPanel.add(stopButton);

        this.add(controlPanel, BorderLayout.CENTER);
        validate();
        repaint();
    }

    public void initDevicePanel() {
        devicePanel = new JPanel();
        table = new JTable(controller.getTableModel());
        JScrollPane tablepane = new JScrollPane(table);

/*        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                    removeButton.setEnabled(true);
                    viewDataButton.setEnabled(true);
                    generateButton.setEnabled(true);
                    checkSSHCOnnection.setEnabled(true);
            }
        });*/
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());
                if (row >= 0) {
                    removeButton.setEnabled(true);
                    //viewDataButton.setEnabled(true);
                    generateButton.setEnabled(true);
                    checkSSHCOnnection.setEnabled(true);
                    selected = row;
                    System.out.println(selected);
                }
            }
        });
        tablepane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());
                System.out.println(row);
                if (row < 0) {
                    table.clearSelection();
                    removeButton.setEnabled(false);
                    viewDataButton.setEnabled(false);
                    generateButton.setEnabled(false);
                    checkSSHCOnnection.setEnabled(false);
                    deployButton.setEnabled(false);
                    selected = -1;
                    System.out.println(selected);
                }

            }
        });
        devicePanel.add(tablepane);

        this.add(devicePanel, BorderLayout.WEST);
        validate();
        repaint();
    }

    public void initAddPanel() {
        addPanel = new JPanel(new GridLayout(0, 2, 0, 5));

        JLabel devicename = new JLabel("Device name:");
        JLabel deviceip = new JLabel("Device ip address:");
        JLabel devicehost = new JLabel("Device host name:");
        JLabel devicepass = new JLabel("Device password:");

        JTextField nametf = new JTextField(20);
        JTextField iptf = new JTextField(20);
        JTextField hosttf = new JTextField(20);
        JTextField passtf = new JTextField(20);

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(this);
        cancel.setActionCommand("cancelfromAdd");
        JButton add = new JButton("Add");

        add.addActionListener(new ActionListener() {
            private final String IPV4_REGEX =
                    "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                            "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                            "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                            "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

            private final Pattern IPv4_PATTERN = Pattern.compile(IPV4_REGEX);

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (nametf.getText().length() != 0 && iptf.getText().length() != 0 && hosttf.getText().length() != 0 && passtf.getText().length() != 0) {
                    Matcher matcher = IPv4_PATTERN.matcher(iptf.getText());
                    if (matcher.matches()) {
                        //model.addDevice(new Device(nametf.getText(),iptf.getText(),hosttf.getText(),passtf.getText()));
                        controller.addDevice(new Device(nametf.getText(), iptf.getText(), hosttf.getText(), passtf.getText()));
                        frame.remove(addPanel);
                        frame.add(controlPanel);
                        validate();
                        repaint();
                    } else {
                        System.out.println("Ip address is not good");
                        JOptionPane.showMessageDialog(frame,
                                "Ip address is not valid",
                                "Error",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame,
                            "Device already exists",
                            "Error",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }

        });

        addPanel.add(devicename);
        addPanel.add(nametf);
        addPanel.add(deviceip);
        addPanel.add(iptf);
        addPanel.add(devicehost);
        addPanel.add(hosttf);
        addPanel.add(devicepass);
        addPanel.add(passtf);
        addPanel.add(cancel);
        addPanel.add(add);
    }


    public void initgeneratePanel() {
        generatePanel = new JPanel(new GridBagLayout());
        JPanel defaultpanel = new JPanel();
        defaultpanel.setLayout(new BoxLayout(defaultpanel,BoxLayout.Y_AXIS));
        ArrayList<JComboBox> comboBoxes = new ArrayList<>();
        JLabel addzip = new JLabel("Add an information model in zip format");
        JButton startgenerate = new JButton("Start preprocessing");
        startgenerate.setEnabled(false);
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                add(controlPanel, BorderLayout.CENTER);
                remove(generatePanel);
                validate();
                repaint();

            }
        });
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST ;
        generatePanel.add(cancel,gbc);
        JButton choosefile = new JButton("Choose zip");
        JLabel chosenfile = new JLabel("No data has been choosen");
        choosefile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "Zip files", "zip");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(generatePanel);
                if (returnVal == JFileChooser.APPROVE_OPTION) {

                    filepath = chooser.getSelectedFile().getAbsolutePath();
                    chosenfile.setText(chooser.getSelectedFile().toString());
                    System.out.println(filepath);
                    //filepath="/home/sasa/data1/TDKCaseStudy/com.incquery_MeArm_1.0.0.zip";
                    startgenerate.setEnabled(true);
                }

            }
        });
/*        generatePanel.add(addzip);
        generatePanel.add(choosefile);
        generatePanel.add(startgenerate);*/

        startgenerate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ArrayList<Map<String,String>> datamaps= controller.preprocess(filepath,selected);
                Map<String,String> datas = datamaps.get(0);
                Map<String, String> functiontodata = datamaps.get(1);

                final int[] i = {6};
                JPanel ThingPanel = new JPanel();
                ThingPanel.setLayout(new BoxLayout(ThingPanel,BoxLayout.X_AXIS));
                JLabel thinglabel = new JLabel("thingID: ");
                JTextField thingidtf = new JTextField(20);
                ThingPanel.add(thinglabel);
                ThingPanel.add(thingidtf);
                GridBagConstraints gbc2 = new GridBagConstraints();
                gbc2.gridx = 0;
                gbc2.gridy = 4;
                gbc2.anchor = GridBagConstraints.NORTHWEST;
                generatePanel.add(ThingPanel,gbc2);
                JPanel DirPanel = new JPanel();
                DirPanel.setLayout(new BoxLayout(DirPanel,BoxLayout.X_AXIS));
                JLabel dirlabel = new JLabel("Target dir: ");
                JTextField dirtf = new JTextField(20);
                DirPanel.add(dirlabel);
                DirPanel.add(dirtf);
                gbc2.gridx = 0;
                gbc2.gridy = 5;
                generatePanel.add(DirPanel,gbc2);
                datas.forEach((key, value)->{

                    System.out.println(key + " "+value);
                    JPanel datachooserpanel = new JPanel();
                    datachooserpanel.setLayout(new BoxLayout(datachooserpanel,BoxLayout.X_AXIS));
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.gridx = 0;
                    gbc.gridy = i[0];
                    gbc.anchor = GridBagConstraints.NORTHWEST;
                    System.out.println(i[0]);
                    JLabel dataname = new JLabel(key);
                    datachooserpanel.add(dataname);

                    String[] boards = new String[]{"","SenseHat"};

                    JComboBox boardsbox = new JComboBox(boards);
                    boardsbox.setBorder(BorderFactory.createTitledBorder("Board Type"));
                    comboBoxes.add(boardsbox);
                    datachooserpanel.add(boardsbox);

                    String[] sensors = new String[]{"","Temperature","Pressure","Humidity"};
                    JComboBox sensorbox = new JComboBox(sensors);
                    sensorbox.setBorder(BorderFactory.createTitledBorder("Sensor Type"));
                    comboBoxes.add(sensorbox);
                    datachooserpanel.add(sensorbox);

/*                    generatePanel.add(dataname,gbc);
                    gbc.gridx = 1;
                    generatePanel.add(new JComboBox<>(),gbc);*/
                    generatePanel.add(datachooserpanel,gbc);
                    generatePanel.revalidate();
                    generatePanel.repaint();
                    i[0] = i[0] +1;

                });
                startgenerate.setEnabled(false);
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = i[0];
                gbc.anchor = GridBagConstraints.NORTHWEST;
                JButton generate = new JButton("Generate");

                generate.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        final int[] index ={0};
                        ArrayList<String> sendables = new ArrayList<>();
                        for(String s:functiontodata.keySet()){
                            sendables.add(s+"->"+functiontodata.get(s));
                        }
                        datas.forEach((key,value)->{
                            sendables.add(key);
                            sendables.add(value);
                            sendables.add(comboBoxes.get(2*index[0]+0).getSelectedItem().toString());
                            sendables.add(comboBoxes.get(2*index[0]+1).getSelectedItem().toString());
                            index[0]++;
                        });
                        for(String s:sendables){
                            System.out.println(s);
                        }

                        controller.getDevices().get(selected).getDtwin().setThingID(thingidtf.getText());
                        controller.getDevices().get(selected).getDtwin().setRunning(false);
                        controller.generate(sendables,selected,dirtf.getText());
                        add(controlPanel, BorderLayout.CENTER);
                        remove(generatePanel);
                        validate();
                        repaint();
                        deployButton.setEnabled(true);
                    }

                });
                generatePanel.add(generate,gbc);
                generatePanel.revalidate();
                generatePanel.repaint();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx =0.5;
        gbc.weighty = 0.5;
/*        defaultpanel.add(addzip);
        defaultpanel.add(choosefile);
        defaultpanel.add(startgenerate);*/
        generatePanel.setBorder(BorderFactory.createTitledBorder("jee"));
        addzip.setBorder(BorderFactory.createTitledBorder("jee"));
        generatePanel.add(addzip,gbc);
       gbc.gridx = 0;
        gbc.gridy = 2;
        JPanel chooserPanel = new JPanel();
        chooserPanel.setLayout(new BoxLayout(chooserPanel,BoxLayout.X_AXIS));
        choosefile.setBorder(BorderFactory.createTitledBorder("jee"));
        chooserPanel.add(choosefile);
        chooserPanel.add(chosenfile);
        generatePanel.add(chooserPanel,gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        generatePanel.add(startgenerate,gbc);
        //generatePanel.add(defaultpanel,gbc);


    }

    public void initViewDataPanel(){
        viewDataPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0 ;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JButton cancelBtn = new JButton("Cancel");
        viewDataPanel.add(cancelBtn,gbc);

        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                remove(viewDataPanel);
                add(controlPanel,BorderLayout.CENTER);

                validate();
                repaint();
            }
        });

        JPanel dataPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcdata = new GridBagConstraints();
        int y = 0;
        for(String s:controller.getDevices().get(selected).getDtwin().getDatas().keySet()){
            gbcdata.gridx = 0;
            gbcdata.gridy = y++;
            gbcdata.anchor = GridBagConstraints.NORTHWEST;
            dataPanel.add(new JLabel(s),gbcdata);
        }
        String command ="curl -X GET -u ditto:ditto http://localhost:8080/api/1/things/"+controller.getDevices().get(selected).getDtwin().getNamespace()+":"+controller.getDevices().get(selected).getDtwin().getThingID();
        System.out.println(command);
        Map<String,String> displaymap = controller.gethttp(command,controller.getDevices().get(selected).getDtwin().getDatas());
        y = 0;

        for(String key:displaymap.keySet()){
            gbcdata.gridx=1;
            gbcdata.gridy=y++;
            gbcdata.anchor = GridBagConstraints.NORTHEAST;
            JLabel value = new JLabel(displaymap.get(key));
            dataPanel.add(value,gbcdata);
        }

        gbc.gridx=0;
        gbc.gridy=1;
        gbc.anchor=GridBagConstraints.CENTER;
        viewDataPanel.add(dataPanel,gbc);

    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        switch (actionEvent.getActionCommand()) {
            case "addbutton":
                initAddPanel();
                add(addPanel, BorderLayout.CENTER);
                remove(controlPanel);
                validate();
                repaint();
                break;
            case "generate":
                initgeneratePanel();
                add(generatePanel, BorderLayout.CENTER);
                remove(controlPanel);
                validate();
                repaint();
                break;
            case "ssh":
                String message = controller.checkConnection(selected);

                Device d = controller.getDevices().get(selected);
                JOptionPane.showMessageDialog(this,
                        message,
                        "Notification",
                        JOptionPane.INFORMATION_MESSAGE);
                break;
            case "cancelfromAdd":
                add(controlPanel, BorderLayout.CENTER);
                remove(addPanel);
                validate();
                repaint();
                break;
            case "view":

                add(new DataPanel(controller,this,controlPanel),BorderLayout.CENTER);
                remove(controlPanel);
                validate();
                repaint();
                break;
            case "remove":
                controller.removeDevice(selected);
                validate();
                repaint();
                break;
            case "deploy":
                initDeployPanel();
                add(deployPanel, BorderLayout.CENTER);
                remove(controlPanel);
                validate();
                repaint();
                break;
            case "stoprunning":
                controller.stopprocess();
        }
    }

    private void initDeployPanel() {
        deployPanel = new JPanel(new GridBagLayout());

        JLabel targetdirectory = new JLabel("Target Directory on device");
        JTextField targetdirtf = new JTextField(20);

        JButton deployBtn = new JButton("Deploy");

        JPanel filesettingsPanel = new JPanel();

        filesettingsPanel.setLayout(new BoxLayout(filesettingsPanel,BoxLayout.X_AXIS));

        filesettingsPanel.add(targetdirectory);
        filesettingsPanel.add(targetdirtf);
        filesettingsPanel.add(deployBtn);

        deployBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                controller.deploy(controller.getDevices().get(getSelected()).getDevname(),targetdirtf.getText());
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx=0;
                gbc.gridy=2;
                JButton runBtn = new JButton("Run");
                deployPanel.add(runBtn,gbc);
                runBtn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        controller.run();
                        frame.remove(deployPanel);
                        frame.add(controlPanel);
                        repaint();
                        revalidate();
                    }
                });
                deployPanel.revalidate();
                deployPanel.repaint();
            }
        });
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                frame.remove(deployPanel);
                frame.add(controlPanel);
                repaint();
                revalidate();
            }
        });
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx=0;
        gbc.gridy=0;
        deployPanel.add(cancelBtn,gbc);
        gbc.gridx=0;
        gbc.gridy=1;
        deployPanel.add(filesettingsPanel,gbc);

    }

    public int getSelected() {
        return selected;
    }
}
