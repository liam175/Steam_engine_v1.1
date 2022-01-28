package steam.engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;


import javax.swing.JTextField;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import org.json.simple.parser.ParseException;

import com.github.sarxos.webcam.Webcam;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import steam.engine.display.window;

import org.json.simple.JSONObject;

public class data {
    public int rounds = 60;
    public String directory = getDir();
    public Boolean GraphMode = false;

    public Object[] getDataTable(int team) {
        Object out[] = {};
        JSONParser parser = new JSONParser();
        Gson gson = new Gson();
        try {
            Object s = parser.parse(new FileReader(
                    directory.replace('\\', '/') + "/t" + team
                            + ".json"));

            out = gson.fromJson(s.toString(), Object[].class);
        } catch (ParseException pe) {

            System.out.println("position: " + pe.getPosition());
            System.out.println(pe);
        } catch (FileNotFoundException e) {
            // e.printStackTrace();
            // System.out.println("team not found");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return out;
    }
    public String getPhoto(){
        String out = null;
        try {
            out = new File(".").getCanonicalPath().toString().replace("C:", "")
        + "/src/main/java/steam/engine/Logogold (1).png";
        } catch (Exception e) {
            //TODO: handle exception
        }
        return out;
    }
    public int[] getLayoutDimensions(int type) {
        int out[] = new int[5];
        JSONParser parser = new JSONParser();
        try {
            String path = new File(".").getCanonicalPath().toString().replace("C:", "")
                    + "/src/main/java/steam/engine/layout_presets.json";
            path = path.replace('\\', '/');
            JSONArray s = (JSONArray) parser.parse(new FileReader(path));
            JSONObject obj = (JSONObject) s.get(type);
            for (int x = 0; x < obj.size(); x++) {
                out[x] = Integer.parseInt(String.valueOf(obj.get("a" + x)));
                // System.out.println(out[x]);
            }
        } catch (ParseException pe) {

            System.out.println("position: " + pe.getPosition());
            System.out.println(pe);
        } catch (FileNotFoundException e) {
            System.out.println("missing layout json");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return out;
    }

    public float[] point(int team, int round, String val) {// round is 1-13 but it will reter round as teh 1-60 if it
                                                           // needs to be used
        float out[] = { -1, 0 };// round val
        if (directory != null) {
            JSONParser parser = new JSONParser();
            // Gson gson = new Gson();
            try {
                JSONArray s = (JSONArray) parser.parse(new FileReader(
                        directory.replace('\\', '/') + "/t" + team
                                + ".json"));
                JSONObject obj = (JSONObject) s.get(round);
                out = new float[] { Integer.parseInt(String.valueOf(obj.get("round"))),
                        Float.parseFloat(String.valueOf(obj.get(val))) };
            } catch (ParseException pe) {
                System.out.println("position: " + pe.getPosition());
                System.out.println(pe);
            } catch (FileNotFoundException e) {
                // e.printStackTrace();
                System.out.println("team not found in point 102");
            } catch (IOException | java.lang.ClassCastException e) {
                e.printStackTrace();
            } catch (IndexOutOfBoundsException e) {

            } catch (NumberFormatException e) {
                // System.out.println("nf exception 126");
            }
        }
        return out;
    }

    public void jsonOut(int team, float[] data) {
        File newjson = new File(directory.replace('\\', '/') + "/t" + team
                + ".json");
        if (newjson.exists()) {
            // main edit
        } else {
            try {
                newjson.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // just make new
        }
    }

    public void saveDir() {
        JSONParser parser = new JSONParser();
        try {
            String path = new File(".").getCanonicalPath().toString().replace("C:", "")
                    + "/src/main/java/steam/engine/saved_data.json";
            path = path.replace('\\', '/');
            JSONObject obj = (JSONObject) parser.parse(new FileReader(path));
            obj.remove("directory");
            obj.put("directory", directory);
            FileWriter write = new FileWriter(path);
            write.write(obj.toJSONString());
            write.flush();
            write.close();
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    String getDir() {
        String out = null;
        JSONParser parser = new JSONParser();
        try {
            String path = new File(".").getCanonicalPath().toString().replace("C:", "")
                    + "/src/main/java/steam/engine/saved_data.json";
            path = path.replace('\\', '/');
            JSONObject obj = (JSONObject) parser.parse(new FileReader(path));
            out = obj.get("directory").toString();
        } catch (ParseException | IOException | NullPointerException e) {
            e.printStackTrace();
        }
        return out;
    }

    public void saveLayout(window wins[], JTextField DataOrder) throws IOException {
        JSONArray arr = new JSONArray();
        JSONObject objs[] = { new JSONObject(), new JSONObject(), new JSONObject(), new JSONObject(), new JSONObject(),
                new JSONObject(), new JSONObject(), new JSONObject(), new JSONObject() };// add to this for as many
                                                                                         // panes as you have
        for (int j = 0; j < wins.length; j++) {
            for (int i = 0; i < 4; i++) {
                objs[j].put("a" + i, wins[j].bounds[i]);
            }
            if (wins[j].isVisible()) {
                objs[j].put("a" + 4, 1);
            } else {
                objs[j].put("a" + 4, 0);
            }
            arr.add(objs[j]);
        }
        try {
            String path = new File(".").getCanonicalPath().toString().replace("C:", "")
                    + "/src/main/java/steam/engine/layout_presets.json";
            path = path.replace('\\', '/');
            FileWriter file = new FileWriter(path);
            file.write(arr.toJSONString());
            file.flush();
            file.close();
        } catch (FileNotFoundException e) {
            System.out.println("layout file not found line:167");
        }
        JSONParser parser = new JSONParser();
        try {
            String path = new File(".").getCanonicalPath().toString().replace("C:", "")
                    + "/src/main/java/steam/engine/saved_data.json";
            path = path.replace('\\', '/');
            JSONObject obj = (JSONObject) parser.parse(new FileReader(path));
            obj.remove("input");
            obj.put("input", DataOrder.getText().toString());
            FileWriter write = new FileWriter(path);
            write.write(obj.toJSONString());
            write.flush();
            write.close();
        } catch (FileNotFoundException e) {
            System.out.println("layout file not found line:167");
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getInputLayoutTxt() {
        String out = null;
        JSONParser parser = new JSONParser();
        try {
            String path = new File(".").getCanonicalPath().toString().replace("C:", "")
                    + "/src/main/java/steam/engine/saved_data.json";
            path = path.replace('\\', '/');
            JSONObject obj = (JSONObject) parser.parse(new FileReader(path));
            out = obj.get("input").toString();
        } catch (ParseException | IOException | NullPointerException e) {
            e.printStackTrace();
        }
        return out;
    }

    public String[] getInputList() {
        String out = null;
        JSONParser parser = new JSONParser();
        try {
            String path = new File(".").getCanonicalPath().toString().replace("C:", "")
                    + "/src/main/java/steam/engine/saved_data.json";
            path = path.replace('\\', '/');
            JSONObject obj = (JSONObject) parser.parse(new FileReader(path));
            out = obj.get("input").toString();
        } catch (ParseException | IOException | NullPointerException e) {
            e.printStackTrace();
        }
        String s[] = out.split("-");
        return s;
    }

    public String chache = "";
    private String old = "";
    public Webcam webcam;

    public void run(window w, data d, JTextField txtArea) {
        do {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Result result = null;
            BufferedImage image = null;

            if (d.webcam.isOpen()) {

                if ((image = webcam.getImage()) == null) {
                    continue;
                }

                LuminanceSource source = new BufferedImageLuminanceSource(image);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                try {
                    result = new MultiFormatReader().decode(bitmap);
                } catch (NotFoundException e) {
                    // fall thru, it means there is no QR code in image
                }
            }

            if (result != null) {
                if (!result.getText().equals(old)) {
                    old = result.getText();
                    if (chache != "") {
                        chache += "-";
                    }
                    chache += result.getText();
                    txtArea.setText(chache);
                }
            }
        } while (true);
    }

    public void saveCalc(JTextField data[][]) {
        JSONArray arr = new JSONArray();
        for (int i = 0; i < data.length; i++) {
            JSONArray arr1 = new JSONArray();
            for (int j = 0; j < data[i].length; j++) {
                Object ob = data[i][j].getText().toString();
                arr1.add(ob);
            }
            arr.add(arr1);
        }
        JSONParser parser = new JSONParser();
        try {
            String path = new File(".").getCanonicalPath().toString().replace("C:", "")
                    + "/src/main/java/steam/engine/saved_data.json";
            path = path.replace('\\', '/');
            JSONObject obj = (JSONObject) parser.parse(new FileReader(path));
            obj.remove("calcs");
            obj.put("calcs", arr);
            FileWriter write = new FileWriter(path);
            write.write(obj.toJSONString());
            write.flush();
            write.close();
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    public JTextField[][] getCalc() {
        JTextField out[][] = { { new JTextField(), new JTextField(), new JTextField() },
                { new JTextField(), new JTextField(), new JTextField() },
                { new JTextField(), new JTextField(), new JTextField() },
                { new JTextField(), new JTextField(), new JTextField() } };
        JSONParser parser = new JSONParser();
        try {
            String path = new File(".").getCanonicalPath().toString().replace("C:", "")
                    + "/src/main/java/steam/engine/saved_data.json";
            path = path.replace('\\', '/');
            JSONObject obj = (JSONObject) parser.parse(new FileReader(path));
            JSONArray arr = (JSONArray) obj.get("calcs");
            for (int i = 0; i < arr.size(); i++) {
                JSONArray arr1 = (JSONArray) arr.get(i);
                for (int j = 0; j < arr1.size(); j++) {
                    out[i][j].setText(arr1.get(j).toString());
                }
            }
        } catch (ParseException | IOException | NullPointerException e) {
            e.printStackTrace();
        }
        return out;
    }

    public void uploadJson(String[][] Calcs, String data) {
        String newData[] = data.split("-");
        String inputLayout[] = getInputList();
        JSONParser parser = new JSONParser();
        for (int i = 0; i < newData.length; i += inputLayout.length) {
            int team = 0;
            try{
                team = Integer.parseInt(newData[i]);
            }catch(NumberFormatException e){
                
            }

            try {
                String path = directory.replace('\\', '/') + "/t" + team + ".json";
                JSONArray arr = (JSONArray) parser.parse(new FileReader(path));
                JSONObject obj = new JSONObject();
                for (int j = 1; j < inputLayout.length; j++) {
                    obj.put(inputLayout[j], newData[i + j]);
                }
                for (int s = 0; s < Calcs.length; s++) {
                    if (Calcs[s][2] != "") {
                        float den = 0;
                        float num = 0;
                        try {
                            String numSplt[] = Calcs[s][1].split("-");
                            for (int h = 0; h < numSplt.length; h++) {
                                for (int g = 0; g < inputLayout.length; g++) {
                                    if (inputLayout[g].equals(numSplt[h])) {
                                        num += Integer.parseInt(newData[i + g]);
                                    }
                                }
                            }
                            String numSplt1[] = Calcs[s][0].split("-");
                            for (int h = 0; h < numSplt1.length; h++) {
                                for (int g = 0; g < inputLayout.length; g++) {
                                    //System.out.println(inputLayout[g] +" "+ numSplt1[h]+ " "+(inputLayout[g] == numSplt1[h]));
                                    if (inputLayout[g].equals(numSplt1[h])) {
                                        den += Integer.parseInt(newData[i + g]);
                                    }
                                }
                            }
                            //System.out.println(den + " " + num);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        if(den!=0){
                            obj.put(Calcs[s][2], (float)(num/den));
                        }
                    }
                }
                arr.add(obj);
                FileWriter write = new FileWriter(path);
                write.write(arr.toJSONString());
                write.flush();
                write.close();
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                System.out.println("team json not found, making file");
                File newJson = new File(directory.replace('\\', '/') + "/t" + team + ".json");
                try {
                    if (newJson.createNewFile()) {
                        System.out.println("file created");
                        FileWriter write = new FileWriter(newJson);
                        write.write("[]");
                        write.flush();
                        write.close();
                        String dataPt2 = newData[i];
                        for (int j = 1; j < inputLayout.length; j++) {
                            dataPt2 += "-" + newData[i + j];
                        }
                        uploadJson(Calcs, dataPt2);
                    } else {
                        System.out.println("oopsie woopsie fucky wucky");
                    }
                } catch (IOException e1) {
                    System.out.println("oopsie woopsie fucky wucky but IOExeption");
                    e1.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
