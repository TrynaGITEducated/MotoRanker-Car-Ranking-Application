
/**
 *
 * @author
 * 221447628 Hadley Booysen
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import za.ac.cput.votingserver.domain.Car;

public class VotingClientWindow extends JFrame {

    //Swing Components
    private JLabel head_lbl, filter_lbl, search_lbl;
    private JPanel mainpanel, navigation_panel;
    private JComboBox filter_cbo;
    private JButton vote_btn, view_btn, search_btn, addVehicleBtn;
    private static JTable dataTable;
    private static JTextArea nominationLog;
    private DefaultTableModel dModel = new DefaultTableModel();
    private JScrollPane scrollPane;
    private JMenu menu, info;
    private JMenuBar nav_bar;
    private JMenuItem exit_item, result_view_item, home_item;
    private Font font1 = new Font("Segoe UI", Font.TRUETYPE_FONT, 15);
    private Font font2 = new Font("Segoe UI", Font.BOLD, 15);

    //I/O object(s)
    private ObjectInputStream data_in;
    private ObjectOutputStream data_out;
    //Socket Object(s)
    private Socket clientSocket;

    public VotingClientWindow() {
        super("Client - Vehicle Voting System");
        createClient();
        System.out.println("connected..client");
        voterUI();

    }

    public void voterUI() {
//PANELS
        mainpanel = new JPanel();
        navigation_panel = new JPanel();
//LABELS
        head_lbl = new JLabel("MotoRanKing");
        head_lbl.setIcon(new ImageIcon("small-logo.png"));
        head_lbl.setFont(font1);
        filter_lbl = new JLabel("Candidates");
        filter_lbl.setFont(font2);
        filter_lbl.setForeground(Color.WHITE);
//NAVIGATION BAR
        nav_bar = new JMenuBar();
        menu = new JMenu();
        menu.setIcon(new ImageIcon("menu-bar.png"));
        menu.setFocusable(false);
        info = new JMenu();
        info.setIcon(new ImageIcon("info.png"));
        info.setFocusable(false);
        home_item = new JMenuItem("home");
        home_item.setIcon(new ImageIcon("home.png"));
        home_item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        exit_item = new JMenuItem("exit");
        exit_item.setIcon(new ImageIcon("exit.png"));
        exit_item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String exit_request = "EXIT";
                    data_out.writeObject(exit_request);
                    data_out.flush();

                    String response = (String) data_in.readObject();
                    JOptionPane.showMessageDialog(null, response);
                    closeIOChannels();
                    System.exit(0);

                } catch (IOException ex) {
                    System.out.println(ex);
                } catch (ClassNotFoundException ex) {
                    System.out.println(ex);
                }
            }
        });

        menu.add(home_item);
        menu.add(exit_item);

        nav_bar.add(menu);
        nav_bar.add(info);
//COMBOBOX
        filter_cbo = new JComboBox();
//BUTTONS

        vote_btn = new JButton(new ImageIcon("vote.png"));
        vote_btn = new JButton("VOTE");
        vote_btn.setBackground(Color.green);
        vote_btn.setForeground(Color.WHITE);
        vote_btn.setFocusable(false);
        vote_btn.setFont(font2);
        vote_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String nominee = (String) filter_cbo.getSelectedItem();
                castVote(nominee);

            }
        });

        view_btn = new JButton("VIEW");
        view_btn.setBackground(Color.green);
        view_btn.setForeground(Color.WHITE);
        view_btn.setFocusable(false);
        view_btn.setFont(font2);
        view_btn.setText("VIEW");
        view_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String request = "VIEW";
                    //WRITE A REQUEST TO THE SERVER TO RETURN INFORMATION STORED IN THE DATABASE
                    data_out.writeObject(request);
                    data_out.flush();
                    //READ IN AN ARRAYLIST OF DATA FROM SERVER 
                    ArrayList<Car> car_Statistics = (ArrayList<Car>) data_in.readObject();
                    //DISPLAY REQUESTED DATA FROM SERVER...
                    dModel.setRowCount(0);
                    for (int i = 0; i < car_Statistics.size(); i++) {
                        String carname = car_Statistics.get(i).getVehicle_name();
                        String category = car_Statistics.get(i).getCategory();
                        int votes = car_Statistics.get(i).getVotes();

                        Object[] data = {carname, category, votes};
                        dModel.addRow(data);

                        filter_cbo.addItem(carname);
                        filter_cbo.setSelectedIndex(-1);
                    }
                } catch (IOException ex) {
                    System.out.println(ex);
                } catch (ClassNotFoundException ex) {
                    System.out.println(ex);
                }
            }
        });

//TABLE
        dataTable = new JTable(dModel);
        dataTable.setShowGrid(false);
        dataTable.setGridColor(dataTable.getBackground());
        dModel.addColumn("CANDIDATE");
        dModel.addColumn("CATEGORY");
        dModel.addColumn("VOTES RECIEVED");
        dataTable.setFont(font2);
        scrollPane = new JScrollPane(dataTable);

        //POSITION COMPONENTS
        head_lbl.setBounds(120, 0, 800, 50);
        nav_bar.setBounds(0, 0, 100, 50);
        filter_lbl.setBounds(200, 50, 100, 30);
        filter_cbo.setBounds(300, 50, 200, 30);
        view_btn.setBounds(300, 100, 100, 30);
        scrollPane.setBounds(70, 230, 650, 200);
        vote_btn.setBounds(300, 450, 200, 50);
        navigation_panel.setPreferredSize(new Dimension(800, 50));
        navigation_panel.setBackground(Color.WHITE);
        mainpanel.setBackground(Color.BLACK);
        mainpanel.setPreferredSize(new Dimension(800, 550));
        navigation_panel.setLayout(null);
        mainpanel.setLayout(null);
//ADD COMPONENTS
        navigation_panel.add(head_lbl);
        navigation_panel.add(nav_bar);
        mainpanel.add(filter_cbo);
        mainpanel.add(filter_lbl);
        mainpanel.add(filter_cbo);
        mainpanel.add(view_btn);
        mainpanel.add(scrollPane);
        mainpanel.add(vote_btn);

//CREATE GUI
        this.add(navigation_panel, BorderLayout.NORTH);
        this.add(mainpanel, BorderLayout.CENTER);

        this.setVisible(true);
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public void createClient() {//start
        /**
         * THIS METHOD MAKES A CLIENT CONNECT WITH THE LOCAL HOST/SERVER ON PORT
         * 8008_METHOD ALSO INITIALIZES THE STREAMS FOR COMMINUCATION ALT. WE
         * COULD CREATE A createStreams() method and plce it here
         */
        try {
            clientSocket = new Socket("localhost", 8008);
            data_out = new ObjectOutputStream(clientSocket.getOutputStream());
            data_out.flush();
            data_in = new ObjectInputStream(clientSocket.getInputStream());
            System.out.println("STATUS>>" + clientSocket.isBound());
        } catch (IOException ex) {
            System.out.println(ex);
        }

    }//end

    private void castVote(String vote) {//start
        try {
            data_out.writeObject("VOTE");
            data_out.flush();
            data_out.writeObject(vote);
            data_out.flush();

            String response = (String) data_in.readObject();
            if (response.equals("VOTE_SUCCESS")) {
                JOptionPane.showMessageDialog(this, "Your vote for " + vote + " was successfully cast!", "Vote Cast", JOptionPane.INFORMATION_MESSAGE);
            } else {
                System.out.println("error");
            }
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println(ex);
        }
    }//end

    private void closeIOChannels() {
        if (clientSocket != null && !clientSocket.isClosed()) {
            try {
                clientSocket.close();
                data_out.close();
                data_in.close();
            } catch (IOException ex) {
                System.out.println("Error Closing");
            }
        }
    }

}
