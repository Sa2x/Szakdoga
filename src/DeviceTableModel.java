import javax.swing.table.AbstractTableModel;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class DeviceTableModel extends AbstractTableModel {

    public ArrayList<Device> devices ;

    public DeviceTableModel(ArrayList<Device> _devices){
        devices = new ArrayList<>();
        devices = _devices;
    }

    @Override
    public int getRowCount() {
        return devices.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public String getColumnName(int column){
        switch(column){
            case 0:
                return "Device name";
            case 1:
                return "Device IP";
            case 2:
                return "Device hostname";
            default:
                return "Device password";
        }
        }
    @Override
    public Object getValueAt(int i, int i1) {
        Device d = devices.get(i);
        switch(i1){
            case 0:
                return d.getDevname();
            case 1:
                return d.getIpaddress();
            case 2:
                return d.getHostname();
            default:
                return d.getPassword();

        }
    }

    public boolean addDevice(Device d){
        System.out.println(devices.size());
        if(devices.contains(d)){

            System.out.println("tartalmaz????");
            return false;
        }
        else{
            devices.add(d);
            System.out.println("jeee im herre");
            fireTableRowsInserted(devices.size()-1, devices.size()-1);
            return true;
        }
    }
    @Override
    public Class<?> getColumnClass(int columnIndex) {//TODO Megkrédezni a sleepeltetést,a specifikaciot,a tábla szebbé tevését,osztálydiagramm és Javadoc hogy a g-hez mit kell irni.
        switch (columnIndex) {
            default:
                return String.class;
        }
    }

    public void deleteDevice(int index) {
        devices.remove(index);
        System.out.println(devices);
        fireTableRowsDeleted(index,index);
    }
}
