package za.ac.cput.votingserver.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import za.ac.cput.votingserver.dao.VehicleDao;
import za.ac.cput.votingserver.domain.Car;

public class VotingServiceWindow extends JFrame {
    
    private JLabel make_lbl, category_lbl;
    private JPanel head_pnl, stats_pnl;
    private JTable view_Table;
    private DefaultTableModel dModel;
    private JComboBox cbo_make, cbo_category;
    private JScrollPane scrollPane;
    private JButton btnViewVehicles, btnAddCar, btnExit, btnDeleteCar;
    private Font font1 = new Font("Segoe UI", Font.TRUETYPE_FONT, 15);
    private Font font2 = new Font("Segoe UI", Font.BOLD, 15);
    
    private ServerSocket service;
    private Socket client;
    private ObjectOutputStream DATA_OUT;
    private ObjectInputStream DATA_IN;
    
    public VotingServiceWindow() {
        super("Server - Vehicle Voting System");
        startServer();
        VehicleDao.createVehiclesTable();
    }

    // Initialize the GUI
    private void setUpGUI() {
        //PANELS
        head_pnl = new JPanel();
        stats_pnl = new JPanel();
        stats_pnl.setBackground(Color.black);
//LABELS
        make_lbl = new JLabel("Vehicle Model");
        make_lbl.setForeground(Color.white);
        category_lbl = new JLabel("Vehicle Cat.");
        category_lbl.setForeground(Color.white);
//COMBOBOXES
        String makes[] = {"Toyota Camry", "BMW 3 Series", "Ford F-150", "Mercedes-Benz S-Class", "Tesla Model 3", "VW Citi Golf", "Honda Civic", "Honda Accord", "Porsche 911", "Toyota Tazz", "Dodge Caliber", "Ford Mustang"};
        cbo_make = new JComboBox(makes);
        cbo_make.setBounds(300, 90, 200, 30);
        String categories[] = {"Sedan", "Luxury", "SUV", "EV", "Sports", "Bakkie", "Military"};
        cbo_category = new JComboBox(categories);
//TABLE
        dModel = new DefaultTableModel();
        view_Table = new JTable(dModel);
        view_Table.setShowGrid(false);
        view_Table.setGridColor(view_Table.getBackground());
        dModel.addColumn("CANDIDATE");
        dModel.addColumn("CATEGORY");
        dModel.addColumn("VOTES");
        view_Table.setFont(font2);
        scrollPane = new JScrollPane(view_Table);
//BUTTONS
        btnViewVehicles = new JButton("SEE VEHICLES");
        btnViewVehicles.setBackground(Color.GREEN);
        btnViewVehicles.setForeground(Color.WHITE);
        btnViewVehicles.setFocusable(false);
        btnViewVehicles.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Car> statistics = VehicleDao.getAllVehicles();
                dModel.setRowCount(0);
                for (int i = 0; i < statistics.size(); i++) {
                    String carname = statistics.get(i).getVehicle_name();
                    String category = statistics.get(i).getCategory();
                    int votes = statistics.get(i).getVotes();
                    
                    Object[] data = {carname, category, votes};
                    dModel.addRow(data);
                }
            }
        });
        
        btnAddCar = new JButton("NEW");
        btnAddCar.setFocusable(false);
        btnAddCar.setBackground(Color.GREEN);
        btnAddCar.setForeground(Color.WHITE);
        btnAddCar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String vehicle_name = (String) cbo_make.getSelectedItem();
                String category = (String) cbo_category.getSelectedItem();
                Car car = new Car(vehicle_name, category);
                int insert_status = VehicleDao.insertVehiclesintoTable(car);
                if (insert_status > 0) {
                    JOptionPane.showMessageDialog(null, vehicle_name + " added as candidate", "OPERATION STATUS", JOptionPane.INFORMATION_MESSAGE);
                    cbo_make.setSelectedIndex(-1);
                    cbo_category.setSelectedIndex(-1);
                } else {
                    JOptionPane.showMessageDialog(null, vehicle_name + " FAILED TO ADD", "OPERATION STATUS", JOptionPane.INFORMATION_MESSAGE);
                    cbo_make.setSelectedIndex(-1);
                    cbo_category.setSelectedIndex(-1);
                }
            }
        });
        
        btnDeleteCar = new JButton("REMOVE");
        btnDeleteCar.setBackground(Color.RED);
        btnDeleteCar.setForeground(Color.WHITE);
        btnDeleteCar.setFocusable(false);
        btnDeleteCar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String carToDelete = (String) cbo_make.getSelectedItem();
                int query_status = VehicleDao.deleteCarByName(carToDelete);
                if (query_status > 0) {
                    JOptionPane.showMessageDialog(null, "vehicle: " + carToDelete + " removed from database");
                }
                cbo_make.setSelectedItem(-1);
            }
        });
        
        btnExit = new JButton(new ImageIcon("exit.png"));
        btnExit.setBackground(Color.BLACK);
        btnExit.setFocusable(false);
        btnExit.setBorderPainted(false);
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    DATA_OUT.writeObject("EXIT");
                    DATA_OUT.flush();
                    closeIOChannels();
                    System.exit(0);
                } catch (IOException ex) {
                    System.out.println(ex);
                }
            }
        });

        //POSITION COMPONENTS
        btnExit.setBounds(0, 0, 50, 50);
        make_lbl.setBounds(100, 90, 100, 30);
        cbo_make.setBounds(200, 90, 200, 30);
        category_lbl.setBounds(100, 130, 100, 30);
        cbo_category.setBounds(200, 130, 200, 30);
        scrollPane.setBounds(0, 200, 700, 300);
        btnAddCar.setBounds(420, 130, 100, 30);
        btnDeleteCar.setBounds(550, 130, 100, 30);
        btnViewVehicles.setBounds(300, 500, 150, 50);
        
        stats_pnl.setLayout(null);
        stats_pnl.setPreferredSize(new Dimension(700, 600));
        stats_pnl.add(btnExit);
        stats_pnl.add(make_lbl);
        stats_pnl.add(cbo_make);
        stats_pnl.add(category_lbl);
        stats_pnl.add(cbo_category);
        stats_pnl.add(btnAddCar);
        stats_pnl.add(btnDeleteCar);
        stats_pnl.add(scrollPane);
        stats_pnl.add(btnViewVehicles);
        
        this.add(stats_pnl, BorderLayout.NORTH);
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(700, 600);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    // Start the server to listen for client connections
    public void startServer() {
        try {
            System.out.println("Starting up server...");
            while (true) {
                service = new ServerSocket(8008);
                client = service.accept();
                if (client.isBound()) {
                    setUpGUI();
                }
                requestHandler(client);
                System.out.println(" CONNECTED TO>> " + service.isBound());
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public void requestHandler(Socket clientSocket) {
        try {
            DATA_OUT = new ObjectOutputStream(clientSocket.getOutputStream());
            DATA_IN = new ObjectInputStream(clientSocket.getInputStream());
            System.out.println("reading in");
            String incoming_request;
            while ((incoming_request = (String) DATA_IN.readObject()) != null) {
                if (incoming_request.equals("VIEW")) {
                    ArrayList<Car> statistics = VehicleDao.getAllVehicles();
                    DATA_OUT.writeObject(statistics);
                    DATA_OUT.flush();
                    System.out.println("request fulfilled");
                } else if (incoming_request.equals("VOTE")) {
                    String nomination = (String) DATA_IN.readObject();
                    System.out.println("request recieved");
                    int update_status = VehicleDao.updateVotes(nomination);
                    System.out.println("preparing");
                    if (update_status > 0) {
                        DATA_OUT.writeObject("VOTE_SUCCESS");
                        DATA_OUT.flush();
                    }
                } else if (incoming_request.equals("EXIT")) {
                    DATA_OUT.writeObject("APPLICATION TERMINATED");
                    DATA_OUT.flush();
                    closeIOChannels();
                    System.exit(0);
                }
            }
            
        } catch (ClassNotFoundException ex) {
            System.out.println("ERROR OCCURRED" + ex);
        } catch (EOFException ex) {
            System.out.println("ERROR OCCURRED" + ex);
        } catch (IOException ex) {
            
        }
    }
    
    private void closeIOChannels() {
        
        try {
            if (client != null && !client.isClosed()) {
                client.close();
            }
            
        } catch (IOException ex) {
            System.out.println("Error Closing");
        }
    }
    
}
