import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MainPanel extends JPanel implements ActionListener {
    DtControllerGUI frame;
    JButton listdevices;
    JButton checkconnection;

    public MainPanel(DtControllerGUI _frame){
        this.frame = _frame;

        listdevices = new JButton("List devices");
        listdevices.addActionListener(this);
        listdevices.setActionCommand("listdevices");

        checkconnection = new JButton ("Check connection on devices");
        checkconnection.addActionListener(this);
        checkconnection.setActionCommand("checkconnection");

        this.add(listdevices);
        this.add(checkconnection);

    }
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        switch (actionEvent.getActionCommand()){
            case "listdevices":
/*                DeviceTableModel model = new DeviceTableModel(frame.controller.getDevices());
                JTable table  = new JTable();
                table.setFillsViewportHeight(true);
                table.setModel(model);
                JScrollPane pane = new JScrollPane(table);
                frame.add(pane);*/
                DeviceTablePanel panel = new DeviceTablePanel(frame);
                frame.add(panel);
                frame.remove(this);
                frame.pack();
                break;
            case "checkconnection":
                /*boolean failed =frame.controller.checkConnection(selected);
                if(unreachables.size() != 0){
                    String message = "";
                    for(Device d:unreachables){
                        message+=d.getDevname()+" is unreachle on "+d.getIpaddress()+"\n";
                    }
                    JOptionPane.showMessageDialog(frame,
                            message,
                            "Error",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                else{
                    JOptionPane.showMessageDialog(frame,
                            "All host are reachable",
                            "OK",
                            JOptionPane.INFORMATION_MESSAGE);
                }*/
                break;
        }
    }
}
