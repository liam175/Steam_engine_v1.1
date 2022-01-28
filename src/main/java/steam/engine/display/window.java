package steam.engine.display;

import java.awt.*;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.net.CookiePolicy;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import com.github.sarxos.webcam.WebcamPanel;

import steam.engine.constants;
import steam.engine.data;

public class window extends JPanel {
    constants c = new constants();
    public int bounds[] = new int[5];// bounds bassed on type
    int teams1[] = new int[6];
    String vals1 = null; 
    JPanel graph = null;
    JTable table = null;
    JFileChooser fc = null;
    data newdata = null;

    WebcamPanel webcamP = null;
    JTextField dataIn = null;
    JTextField autoFields[][] ={{null,null,null},{null,null,null},{null,null,null},{null,null,null}};
    JButton push = null;

    Boolean percent = false;
    public float vm = 10;
    public int coords[] = {0, 0};
    JTextArea coordsButTxt = null;
    public int thisType = 0;

    public void init(int type, data data) {
        newdata = data;
        thisType = type;
        bounds = data.getLayoutDimensions(type);
        setBounds(bounds[0], bounds[1], bounds[2], bounds[3]);
        setBackground(c.steamRedDark);
        setForeground(c.steamGold);
        if (bounds[4] == 1) {
            setVisible(true);
        }
        switch (type) {
            case 0:
                setLayout(new BorderLayout());
                table = new JTable(13, 1);
                add(table);
                table.setBounds(0, 0, bounds[2], bounds[3]);
                table.setForeground(c.steamGold);
                table.setBackground(c.steamRedDark);
                table.setGridColor(c.steamGold);
                break;
            case 1:
                fc = new JFileChooser();
                add(fc);
                fc.setBounds(bounds[0], bounds[1], bounds[2], bounds[3]);
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fc.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
                            changeDir();
                        }
                    }

                });
                break;
            case 2:
            setLayout(null);
            graph = new JPanel(null);
            add(graph);
            graph.setBounds(0,0,bounds[2],bounds[3]);
            graph.setBackground(c.steamRedDark);
                coordsButTxt = new JTextArea();
                add(coordsButTxt);
                coordsButTxt.setBounds(0,0,50,20);
                coordsButTxt.setBackground(c.steamRedDark);
                coordsButTxt.setForeground(c.steamGold);
                addMouseMotionListener(new MouseMotionListener(){
                    @Override
                    public void mouseDragged(MouseEvent e) {
                        if(e.getY() - coords[1]>1 && vm>1){
                            vm -= .5;
                        }else {
                            vm += .5;
                        }
                        reloadGraph(teams1, vals1, newdata);
                        //System.out.println(vm);
                    }

                    @Override
                    public void mouseMoved(MouseEvent e) { 
                        coords = new int[]{e.getX(),e.getY()};
                        changeCoords();
                    }
                    
                });
                break;
            case 3:
            setLayout(null);
            dataIn = new JTextField();
            dataIn.setBackground(c.steamRedLight);
            dataIn.setForeground(c.steamGold);
            dataIn.setBounds(0, bounds[3]-80, bounds[2], 20);
            add(dataIn);
            autoFields = newdata.getCalc();
            int x = bounds[2]/(autoFields.length+1);
            for(int i = 0;i<autoFields.length;i++){
                for(int j = 0;j<autoFields[i].length;j++){
                    add(autoFields[i][j]);
                    if(j!=1){
                        autoFields[i][j].setBackground(c.steamRedDark);
                    }else{
                        autoFields[i][j].setBackground(c.steamRedLight);
                    }
                    autoFields[i][j].setForeground(c.steamGold);
                    autoFields[i][j].setBounds(x*i,bounds[3]-(j+1)*20,x,20);
                    autoFields[i][j].addCaretListener(new CaretListener(){

                        @Override
                        public void caretUpdate(CaretEvent e) {
                            saveCalcs();
                        }});
                }
            }
            push = new JButton("push");
            add(push);
            push.setBackground(c.steamRedDark);
            push.setForeground(c.steamGold);
            push.setBounds(x*4, bounds[3]-60, x, 60);
            push.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                    String s[][] = new String[4][3];
                    for(int i = 0;i<autoFields.length;i++){
                        for(int j =0;j<autoFields[i].length;j++){
                            s[i][j] = autoFields[i][j].getText().toString();
                        }
                    }
                    newdata.uploadJson(s, dataIn.getText().toString());
                }});
            try{
            webcamP = new WebcamPanel(data.webcam);
            add(webcamP);
            webcamP.setFPSDisplayed(true);
            webcamP.setBounds(0,0,bounds[2],bounds[3]-80);
            }catch(IllegalArgumentException e){
                System.out.println("failed to open webcam");
                //e.printStackTrace();
            }
            break;
        }

    }

    void changeDir() {
        newdata.directory = fc.getSelectedFile().getAbsolutePath().toString().replace("C:", "");
        setVisible(false);
        newdata.saveDir();
    }

    public void reloadTable(int team, data data) {
        Object vals[] = data.getDataTable(team);
        for (int i = 0; i < 13; i++) {
            try {
                table.setValueAt(vals[i], i, 0);
            } catch (ArrayIndexOutOfBoundsException e) {
                //System.out.println("out of bounds eception on table");
                table.setValueAt("null", i, 0);
            }
        }
    }
    public void update() {// updtate only does movement
        if (bounds[0] < 0) {
            bounds[0] = 0;
        }
        if (bounds[1] < 20) {
            bounds[1] = 20;
        }

        if (bounds[0] + bounds[2] > 1920) {
            bounds[0] = 1920 - bounds[2];
        }
        if (bounds[1] + bounds[3] > 1080) {
            bounds[1] = 1080 - bounds[3];
        }

        if (bounds[2] < 10) {
            bounds[2] = 10;
        }
        if (bounds[3] < 10) {
            bounds[3] = 10;
        }
        setBounds(bounds[0], bounds[1], bounds[2], bounds[3]);
        switch (thisType) {
            case 0:
                setLayout(null);
                table.setBounds(0, 0, bounds[2], bounds[3]);
                setVisible(false);
                setLayout(new BorderLayout());
                setVisible(true);
                break;
            case 1:
                fc.setBounds(0, 0, bounds[2], bounds[3]);
                break;
            case 2:
            coordsButTxt.setBounds(0, 0, 50, 20);
            break;
            case 3:
            dataIn.setBounds(0, bounds[3]-80, bounds[2], 20);
            int x = bounds[2]/(autoFields.length+1);
            for(int i = 0;i<autoFields.length;i++){
                for(int j = 0;j<autoFields[i].length;j++){
                    autoFields[i][j].setForeground(c.steamGold);
                    autoFields[i][j].setBounds(x*i,bounds[3]-(j+1)*20,x,20);
                }
            }
            push.setBounds(x*4, bounds[3]-60, x, 60);
            if(webcamP!=null){
                webcamP.setBounds(0,0,bounds[2],bounds[3]-80);
            }
            break;
        }

    }

    void reloadGraph(int[] team, String val, data d) {
        teams1 = team;
        vals1 = val;
        Graphics g = getGraphics();
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        //g2.drawLine(bounds[0], bounds[1], bounds[2], bounds[3]);
        for (int x = 0; x < team.length; x++) {
            g2.setColor(c.colors[x]);
            if (d.GraphMode && team[x]!=0) {// if you wanna see 1-60 rather than 1-13
                float last[] = d.point(team[x], 0, val);
                for (int i = 1; i < 13; i++) {
                    float temp[] = d.point(team[x], i, val);
                    if (temp[0] != -1&&last[0]!=-1) {
                        if (temp[1] <= 1 && temp[1] >= 0){
                            percent = true;
                            float nvm = vm*60;
                            g2.drawLine((int) last[0] * (bounds[2] / d.rounds), (int) (bounds[3]-last[1] * nvm),
                                (int) temp[0] * (bounds[2] / d.rounds), (int) (bounds[3]-temp[1] * nvm));
                        last = new float[] { temp[0], temp[1] };
                        }else{
                            percent = false;
            g2.drawLine((int) last[0] * (bounds[2] / d.rounds), (int) ((bounds[3]-last[1]) * vm),
                                (int) temp[0] * (bounds[2] / d.rounds), (int) ((bounds[3]-temp[1]) * vm));
                        last = new float[] { temp[0], temp[1] };
                        }
                        
                    }
                }
            } else if(team[x]!=0){
                float last[] = {0f,0f};
                for (int i = 0; i < 13; i++) {
                    float temp[] = d.point(team[x], i, val);
                        if (temp[0] != -1) {
                            if (temp[1] < 1 && temp[1] > 0){
                                percent = true;
                                float nvm = vm*60;
                                //System.out.println(last[0]+" "+(bounds[3]-last[1]*nvm)+" "+i*(bounds[2] / 13)+" "+(bounds[3]-temp[1]*nvm));
                                g2.drawLine((int) last[0]-2, (int) (bounds[3]-last[1] * nvm), i * (bounds[2] / 13)-2,
                                    (int) (bounds[3]-temp[1]* nvm));
                                last = new float[] { i *(bounds[2] / 13), temp[1]};
                            }else{
                                percent = false;
                                 g2.drawLine((int) last[0]-2, (int) (bounds[3]-last[1] * vm), i * (bounds[2] / 13)-2,
                                    (int) (bounds[3]-temp[1]* vm));
                                last = new float[] { i *(bounds[2] / 13), temp[1]};
                            }
                        }
                }
            }
            changeCoords();
        }
    }
    void changeCoords(){
        int x = 0;
        if(newdata.GraphMode){
            x = (coords[0]+(bounds[2]/13)/2)/(bounds[2]/60);
            if(percent){
                coordsButTxt.setText(x+" "+(Float)((bounds[3] - coords[1])/(vm*60)));
            }else{
                coordsButTxt.setText(x+" "+(int)((bounds[3] - coords[1])/vm));
            }
        }else{
            x = (coords[0]+(bounds[2]/13)/2)/(bounds[2]/13);
            if(percent){
                coordsButTxt.setText(x+" "+(Float)((bounds[3] - coords[1])/(vm*60)));
            }else{
                coordsButTxt.setText(x+" "+(int)((bounds[3] - coords[1])/vm));
            }
        }
    }
    void saveCalcs(){
        newdata.saveCalc(autoFields);
    }
    public void mouseDrag(int y){
        if(y - coords[1]>1 && vm>1){
            vm -= .5;
        }else {
            vm += .5;
        }
        reloadGraph(teams1, vals1, newdata);
    }
}
