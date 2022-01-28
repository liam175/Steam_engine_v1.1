package steam.engine.display;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.Color;
import java.awt.Graphics;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.image.BufferedImage;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamException;
import com.github.sarxos.webcam.WebcamPanel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import steam.engine.*;

public class display_main extends JFrame {
    constants c = new constants();
    window wins[] = { new window(), new window(), new window(), new window()};
    int mousepos[] = { 0, 0 };
    boolean sizeChange = true;
    int movePane = 0;
    JCheckBox editMode = new JCheckBox("edit");
    JCheckBox graphStyle = new JCheckBox("gs");
    JComboBox dropMenu = new JComboBox(new String[] { "table", "file system", "graph", "camera" });
    JButton saveLayoutButton = new JButton("save layout");
    JTextField teams[] = { new JTextField(), new JTextField(), new JTextField(), new JTextField(), new JTextField(),
            new JTextField() };
    data data = new data();
    JTextField ShownVar = new JTextField();
    JTextField inputLayout = new JTextField(data.getInputLayoutTxt());
    String intToKeys[] = data.getInputList();
    public Webcam webcam = null;
    String currentVar = "";

    public void init() {
        setSize(1920, 1080);
        setLayout(null);
        setVisible(true);
        setTitle("Team STEAMS SteamEngine v1");
        getContentPane().setBackground(c.steamRedTrue);
        dropMenu.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                movePane = dropMenu.getSelectedIndex();
                if (!wins[movePane].isVisible() && editMode.isSelected()) {
                    wins[movePane].setVisible(true);
                } else if (editMode.isSelected()) {
                    wins[movePane].setVisible(false);
                }
            }
        });
        addMouseMotionListener(new MouseMotionListener() {
            
            @Override
            public void mouseDragged(MouseEvent e) {
                
                //wins[3].mouseDrag(e.getY());
                if (editMode.isSelected()) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        wins[movePane].bounds[0] = e.getX() - 8;
                        wins[movePane].bounds[1] = e.getY() - 30;
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        wins[movePane].bounds[2] = e.getX() - 8
                                - wins[movePane].bounds[0];
                        wins[movePane].bounds[3] = e.getY() - 30
                                - wins[movePane].bounds[1];
                    }
                    wins[movePane].update();
                    //coll(wins[movePane]);
                }

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                //wins[3].coords = new int[]{e.getX(),e.getY()};
            }

        });
        try{
            System.out.println(Webcam.getWebcams());
            webcam = Webcam.getWebcams().get(0);
            data.webcam = webcam;
        }catch(IndexOutOfBoundsException | WebcamException e){
            //e.printStackTrace();
            System.out.println("webcam not found");
    
        }
        for (int x = 0; x < teams.length; x++) {
            add(teams[x]);
            teams[x].setBounds(x * 50 + 260, 0, 50, 20);
            teams[x].setBackground(c.colors[x]);
            teams[x].setForeground(c.colors1[x]);
            if(x==0){
                teams[x].addCaretListener(new CaretListener(){
                    @Override
                    public void caretUpdate(CaretEvent e) {
                        int fin[] = new int[teams.length];
                        for(int y = 0;y<teams.length;y++){
                            String temp = teams[y].getText().toString().replaceAll("[^0-9]", "");
                            try {
                                fin[y]=Integer.parseInt(temp);
                            } catch (NumberFormatException c) {
                                //System.out.println("error with parse int line 92");
                            }
                        }
                        wins[2].reloadGraph(fin, currentVar, data);
                        wins[0].reloadTable(fin[0], data);
                        
                    }});
            }else{
                teams[x].addCaretListener(new CaretListener(){
                    @Override
                    public void caretUpdate(CaretEvent e) {
                        int fin[] = new int[teams.length];
                        for(int y = 0;y<teams.length;y++){
                            String temp = teams[y].getText().toString().replaceAll("[^0-9]", "");
                            try {
                                fin[y]=Integer.parseInt(temp);
                            } catch (NumberFormatException c) {
                                //System.out.println("error with parse int line 93");
                            }
                        }
                        wins[2].reloadGraph(fin, currentVar, data);
                    }});
            }
        }
        add(saveLayoutButton);
        saveLayoutButton.setBounds(160, 0, 100, 20);
        saveLayoutButton.setForeground(c.steamGold);
        saveLayoutButton.setBackground(c.steamRedTrue);
        saveLayoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    data.saveLayout(wins,inputLayout);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        add(inputLayout);
        inputLayout.setBounds(585, 0, 400, 20);
        inputLayout.setForeground(c.steamGold);
        inputLayout.setBackground(c.steamRedTrue);

        add(dropMenu);
        dropMenu.setBounds(0, 0, 100, 20);
        dropMenu.setForeground(c.steamGold);
        dropMenu.setBackground(c.steamRedTrue);

        add(editMode);
        editMode.setBounds(100, 0, 60, 20);
        editMode.setForeground(c.steamGold);
        editMode.setBackground(c.steamRedTrue);
        
        add(ShownVar);
        ShownVar.setBounds(985, 0, 60, 20);
        ShownVar.setForeground(c.steamGold);
        ShownVar.setBackground(c.steamRedDark);
        ShownVar.addCaretListener(new CaretListener(){
            @Override
            public void caretUpdate(CaretEvent e) {
                int fin[] = new int[teams.length];
                for(int y = 0;y<teams.length;y++){
                    String temp = teams[y].getText().toString().replaceAll("[^0-9]", "");
                    try {
                        fin[y]=Integer.parseInt(temp);
                    } catch (NumberFormatException c) {
                        //System.out.println("error with parse int line 144");
                    }
                }
                currentVar = ShownVar.getText().toString();
                wins[2].reloadGraph(fin, currentVar, data);
            }});

        add(graphStyle);
        graphStyle.setBounds(560, 0, 25, 20);
        graphStyle.setForeground(c.steamGold);
        graphStyle.setBackground(c.steamRedTrue);
        graphStyle.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                data.GraphMode = graphStyle.isSelected();
                int fin[] = new int[teams.length];
                for(int y = 0;y<teams.length;y++){
                    String temp = teams[y].getText().toString().replaceAll("[^0-9]", "");
                    try {
                        fin[y]=Integer.parseInt(temp);
                    } catch (NumberFormatException c) {
                        System.out.println("error with parse int line 109");
                    }
                }
                wins[2].reloadGraph(fin, currentVar, data);
            }});
            
        for (int i = 0; i < wins.length; i++) {
            add(wins[i]);
            wins[i].setVisible(false);
            wins[i].init(i, data);
        }
        JLabel pic = new JLabel(new ImageIcon(data.getPhoto()));
        add(pic);
        pic.setBounds(getWidth()/2-396,getHeight()/2-365,791,729);
        dropMenu.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                movePane = dropMenu.getSelectedIndex();
                if (!wins[movePane].isVisible() && editMode.isSelected()) {
                    wins[movePane].setVisible(true);
                } else if (editMode.isSelected()) {
                    wins[movePane].setVisible(false);
                }
            }
        });
        data.run(wins[3], data, wins[3].dataIn);
    }
}
