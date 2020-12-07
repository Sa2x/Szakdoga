import jdk.nashorn.internal.parser.JSONParser;

import javax.swing.table.TableModel;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DTController {
    private ArrayList<Device> devices;

    private DeviceTableModel model;

    public DTController() {
        devices = new ArrayList<>();
        //devices.add(new Device("mock","10.42.0.25","pi","raspberry"));
        try {
            loadDevices();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        model = new DeviceTableModel(devices);

    }

    public void loadDevices() throws ClassNotFoundException {
        try {
            FileInputStream devicefile = new FileInputStream("devices");
            ObjectInputStream deviceinput = new ObjectInputStream(devicefile);

            devices = (ArrayList<Device>) deviceinput.readObject();

            deviceinput.close();
            devicefile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveDevices() {
        try {
            FileOutputStream devicefile = new FileOutputStream("devices");
            ObjectOutputStream deviceoutput = new ObjectOutputStream(devicefile);

            deviceoutput.writeObject(devices);

            deviceoutput.close();
            devicefile.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printAllDevice() {
        if (devices.size() == 0) {
            System.out.print("There is no device");
        } else {
            for (Device d : devices) {
                d.print();
            }
        }
    }

    public String checkConnection(int index) {
        String path = "/home/sasa/data1/TDKCaseStudy/DigitalTwinController/checksshv2.py";
        Device d = devices.get(index);
        boolean failed = false;
        try {
            ProcessBuilder pb = new ProcessBuilder("python3", path, d.getIpaddress(), d.getHostname(), d.getPassword());
            Process p = pb.start();

            BufferedReader bfr = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line = bfr.readLine()) != null) {
                System.out.println(line);
            }
            int code = p.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Map<String,String>> preprocess(String filepath, int selected) {
        String path = "/home/sasa/data1/TDKCaseStudy/DigitalTwinController/genFromDSL-preprocess.py";
        Device d = devices.get(selected);

        Map<String,String> datas = new HashMap<>();
        Map<String,String> functiontodata = new HashMap<>();
        try {
            ProcessBuilder pb = new ProcessBuilder("python3", path, filepath);
            Process p = pb.start();

            BufferedReader bfr = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            boolean data = false;
            boolean operations = false;
            boolean namespace = false;
            boolean infomodel = false;
            String ns = "";
            String imodel = "";
            //String[] functionblocks = new String[];
            String functionblock = "";
            while ((line = bfr.readLine()) != null) {
                //System.out.println(line);
                if(line.contains("[NAMESPACE]")){
                    namespace = true;
                    continue;
                }
                if(line.contains("[IMODEL]")){
                    namespace = false;
                    infomodel = true;
                    continue;
                }
                if (line.contains("[DATA]")) {
                    infomodel = false;
                    data = true;
                    continue;
                }
                if (line.contains("[OPERATIONS]")) {
                    data = false;
                    operations = true;
                    continue;
                }
                if(namespace){
                    ns = line;
                }
                if(infomodel){
                    imodel=line;
                }
                if (data) {

                    if (line.contains("Functionblock")) {
                        functionblock = line.split(" ")[1];
                        continue;
                    }
                    functiontodata.put(line.split(" ")[0],functionblock);
                    datas.put(line.split(" ")[0],line.split(" ")[1]);

                }
                if (operations) {
                }

            }
            d.setDtwin(new DigitalTwin(ns,imodel,datas));
            int code = p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<Map<String, String>> ret= new ArrayList<>();
        ret.add(datas);
        ret.add(functiontodata);
        return ret;
    }

    public void generate(ArrayList<String> sendables,int selected){
        String path = "/home/sasa/data1/TDKCaseStudy/DigitalTwinController/genFromDSL-generate.py";
        String[] commandlist = {"python3",path};
        Device d = devices.get(selected);
        ArrayList<String> sending = new ArrayList<>();
        sending.add("python3");
        sending.add(path);
        sending.add(d.getDtwin().getThingID());
        sending.add(d.getDtwin().getName());
        sending.add(d.getDtwin().getNamespace());
        for(String s:sendables){
            sending.add(s);
        }
        ProcessBuilder pb = new ProcessBuilder( sending.toArray(new String[sendables.size()+2]));
        try {
            Process p = pb.inheritIO().start();
            int code = p.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


    }

    public Map<String, String> gethttp(String command, Map<String,String> data){
        String[] properties = data.keySet().toArray(new String[data.size()]);

        Map<String, String> returndata = new HashMap<>();
        ProcessBuilder pb = new ProcessBuilder(command.split(" "));
        try {
            Process p = pb.start();
            BufferedReader bfr = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            String[] sliced = new String[0];
            ArrayList<String> rows = new ArrayList<>();
            while ((line = bfr.readLine()) != null) {
                System.out.println(line);
                sliced= line.split(",");
                for(String s:sliced){
                    rows.add(s);
                }
            }
            rows.remove(0);
            rows.remove(0);
            rows.remove(0);
            rows.remove(0);

            for(String s:rows){
                String[] splitted = s.split("\"");
                for(int i = 0;i<splitted.length;i++){
                    if(data.containsKey(splitted[i])){
                        returndata.put(splitted[i],splitted[++i].replace(":","").replace("}",""));
                    }
                }
            }
            int code = p.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return returndata;
    }
    public ArrayList<Device> getDevices() {
        return devices;
    }

    public boolean addDevice(Device d) {
        return model.addDevice(d);
    }

    public void removeDevice(int index) {
        model.deleteDevice(index);
    }

    public TableModel getTableModel() {
        return model;
    }
}
