package GUI;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

// Backend Sınıflarının Importları
import Flight.*;
import Management.*;
import Reservation.*;
import Simulation.ReportSimulation;
import Simulation.SeatSimulation;
import User.*;

public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    
    // --- BACKEND MANAGERS ---
    private UserManager userManager;
    private FlightManager flightManager;
    private ReservationManager reservationManager;
    private SeatManager seatManager;
    private PlaneManager planeManager;
    private FlightSearchEngine searchEngine;
    private SeatSimulation simulationManager;
    private ReportSimulation reportManager;
    private java.util.Map<String, JButton> simulationButtonsMap = new java.util.HashMap<>();
    
    // Oturum Açan Kullanıcı
    private User currentUser; 
    
    // GUI Components
    private JPanel mainPanel, sideMenuPanel, customerParentPanel, contentPanel;
    private JPanel mainContent, searchPanel, flightResultsPanel, seatChoosePanel, baggageChoosePanel, paymentPanel, welcomePanel;
    private JPanel staffParentPanel, staffSideMenu, staffContentPanel, addFlightPanel, deleteFlightPanel, updateFlightPanel, seeFlightsPanel, seeTicketsPanel, staffReportPanel;
    private JPanel myTicketsPanel;
    private JPanel adminParentPanel, adminSideMenu, adminContentPanel;
    private JPanel adminSeeFlightsPanel, adminSeeTicketsPanel, adminAddFlightPanel, adminDeleteFlightPanel, adminUpdateFlightPanel, seeAllStaffsPanel;
    private JPanel addStaffPanel, deleteStaffPanel, adminReportPanel;
    
    private JTextField textField, txtFrom, txtTo, txtBagWeight, txtTotalPrice;
    private JTextField textField_1, textField_2, textField_3; 
    private JPasswordField passwordField, passwordField_1;
    private JButton btnbooking, btnmyTickets, btnlogOut, btnSimulation;
    private JButton btnSeeFlights, btnSeeTickets, btnAddFlight, btnDeleteFlight, btnUpdateFlight, btnStaffLogout;
    private JButton btnAdminSeeFlights, btnAdminSeeTickets, btnAdminAddFlight, btnAdminDeleteFlight, btnAdminUpdateFlight, btnAdminReport;;
    private JButton btnSeeAllStaffs, btnAddStaff, btnDeleteStaff, btnAdminLogout, btnStaffReport;;
    private JButton btnUpdateStaff;
    private JPanel updateStaffPanel;
    private JButton btnAddPlane;
    private JButton btnAdminAddPlane;
    private JPanel addPlanePanel;
    private JPanel adminAddPlanePanel;

    private static class PlaneItem {
        final String planeId;
        final String label;
        PlaneItem(String planeId, String label) { this.planeId = planeId; this.label = label; }
        @Override public String toString() { return label; }
    }
    private final List<JComboBox<PlaneItem>> planeCombos = new ArrayList<>();
    private final List<String> planeIdRegistry = new ArrayList<>();

    private void registerPlaneCombo(JComboBox<PlaneItem> combo) {
        if (combo != null) {
            planeCombos.add(combo);
        }
    }

    @SuppressWarnings("unchecked")
    private java.util.List<Plane> safeGetAllPlanes() {
        if (planeManager == null) return java.util.Collections.emptyList();

        String[] methodNames = new String[] {
                "getAllPlanes", "getPlanes", "listPlanes", "getPlaneList", "getAll", "getAllPlane"
        };
        boolean foundMethod = false;
        java.util.List<Plane> resultList = java.util.Collections.emptyList();

        for (int i = 0; i < methodNames.length && !foundMethod; i++) {
            try {
                java.lang.reflect.Method m = planeManager.getClass().getMethod(methodNames[i]);
                Object res = m.invoke(planeManager);
                if (res instanceof java.util.List) {
                    resultList = (java.util.List<Plane>) res;
                    foundMethod = true;
                }
            } catch (Exception ignore) { }
        }

        if (!foundMethod) {
            String[] fieldNames = new String[] { "planes", "planeList", "allPlanes", "planeArrayList" };
            for (int i = 0; i < fieldNames.length && !foundMethod; i++) {
                try {
                    java.lang.reflect.Field f = planeManager.getClass().getDeclaredField(fieldNames[i]);
                    f.setAccessible(true);
                    Object res = f.get(planeManager);
                    if (res instanceof java.util.List) {
                        resultList = (java.util.List<Plane>) res;
                        foundMethod = true;
                    }
                } catch (Exception ignore) { }
            }
        }

        return resultList;
    }

    private void rebuildPlaneRegistryAndRefreshCombos() {
        java.util.LinkedHashSet<String> ids = new java.util.LinkedHashSet<>();
        for (Plane p : safeGetAllPlanes()) {
            if (p != null) {
                String id = p.getPlaneID();
                if (id != null && !id.isEmpty()) ids.add(id);
            }
        }

        planeIdRegistry.clear();
        planeIdRegistry.addAll(ids);

        refreshAllPlaneCombos();
    }

    private void refreshAllPlaneCombos() {
        for (JComboBox<PlaneItem> cb : planeCombos) {
            if (cb != null) {
                PlaneItem placeholder = null;
                if (cb.getItemCount() > 0) {
                    PlaneItem first = cb.getItemAt(0);
                    if (first != null && (first.planeId == null || first.planeId.isEmpty())) {
                        placeholder = first;
                    }
                }

                cb.removeAllItems();
                if (placeholder != null) cb.addItem(placeholder);

                for (String id : planeIdRegistry) {
                    Plane p = planeManager.getPlaneByID(id);
                    if (p != null) {
                        cb.addItem(new PlaneItem(id, p.getPlaneModel() + " (" + p.getPlaneID() + ")"));
                    }
                }
            }
        }
    }

    private void addPlaneToAllCombos(String planeId) {
        if (planeId != null && !planeId.isEmpty()) {

            if (!planeIdRegistry.contains(planeId)) {
                planeIdRegistry.add(planeId);
            }

            Plane p = planeManager.getPlaneByID(planeId);
            if (p == null) {
                refreshAllPlaneCombos();
            } else {
                PlaneItem item = new PlaneItem(planeId, p.getPlaneModel() + " (" + p.getPlaneID() + ")");

                for (JComboBox<PlaneItem> cb : planeCombos) {
                    if (cb != null) {
                        boolean exists = false;
                        for (int i = 0; i < cb.getItemCount() && !exists; i++) {
                            PlaneItem it = cb.getItemAt(i);
                            if (it != null && planeId.equals(it.planeId)) {
                                exists = true;
                            }
                        }
                        if (!exists) cb.addItem(item);
                    }
                }
            }
        }
    }

    
    //Helper metod
    private boolean isPlaneAvailableForSlot(String planeId, java.time.LocalDateTime start, java.time.LocalDateTime end, String excludeFlightNum) {
        boolean conflictFound = false;
        
        List<Flight> flights = flightManager.getAllFlights();
        for (int i = 0; i < flights.size() && !conflictFound; i++) {
            Flight f = flights.get(i);
            
            if (f != null && f.getPlane() != null) {
                boolean shouldCheck = true;
                
                if (excludeFlightNum != null && excludeFlightNum.equalsIgnoreCase(f.getFlightNum())) {
                    shouldCheck = false;
                }

                if (shouldCheck) {
                    String usedPlaneId = f.getPlane().getPlaneID();
                    if (usedPlaneId != null && usedPlaneId.equalsIgnoreCase(planeId)) {
                        java.time.LocalDateTime otherStart = java.time.LocalDateTime.of(f.getDate(), f.getHour());
                        java.time.LocalDateTime otherEnd = otherStart.plusMinutes(f.getDurationMinutes());

                        if (start.isBefore(otherEnd) && otherStart.isBefore(end)) {
                            conflictFound = true;
                        }
                    }
                }
            }
        }
        return !conflictFound;
    }

    private void refreshPlaneComboForSlot(JComboBox<PlaneItem> combo,
                                          java.time.LocalDate date,
                                          java.time.LocalTime time,
                                          int durationMin,
                                          String excludeFlightNum,
                                          boolean includeKeepCurrent) {
        combo.removeAllItems();
        if (includeKeepCurrent) {
            combo.addItem(new PlaneItem("", "-- Keep Current --"));
        }

        for (String planeId : planeIdRegistry) {
            if (planeId != null && !planeId.isEmpty()) {
                Plane p = planeManager.getPlaneByID(planeId);
                if (p != null) {
                    combo.addItem(new PlaneItem(planeId, p.getPlaneModel() + " (" + p.getPlaneID() + ")"));
                }
            }
        }
    }

    // Seçilen uçuşu tutmak için geçici değişken
    private Flight selectedFlightForBooking;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public MainFrame() {
        seatManager = new SeatManager();
        planeManager = new PlaneManager();
        flightManager = new FlightManager();
        reservationManager = new ReservationManager(seatManager);
        userManager = UserManager.getInstance();
        searchEngine = new FlightSearchEngine(flightManager, seatManager);
        simulationManager = new SeatSimulation();
        reportManager = new ReportSimulation();
        
        if (userManager.getAllUsers().isEmpty()) {
             Admin admin = new Admin("admin", "123", "System", "Admin");
             userManager.addUser(admin);
             System.out.println("Default Admin created: admin / 123");
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1260, 900);
        setTitle("Airline System");
        
        mainPanel = new JPanel(new CardLayout());
        setContentPane(mainPanel);

        createLoginPanel();
        createSignUpPanel();
        createCustomerPanel();
        createStaffPanel();
        createAdminPanel();

        rebuildPlaneRegistryAndRefreshCombos();
    }

    
    //Login Panel
    private void createLoginPanel() {
        JPanel loginPanel = new JPanel(null);
        mainPanel.add(loginPanel, "login");
        
        JLabel lblTitle = new JLabel("Airline System");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 32));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBounds(420, 230, 420, 40);
        loginPanel.add(lblTitle);

        JComboBox<String> roleCombo = new JComboBox<>(new String[] {"Customer", "Staff"});
        roleCombo.setBounds(555, 290, 150, 30);
        roleCombo.setFont(new Font("Arial", Font.PLAIN, 15));
        loginPanel.add(roleCombo);
        
        textField = new JTextField(); 
        textField.setBounds(505, 365, 250, 35);
        loginPanel.add(textField);
        
        passwordField = new JPasswordField(); 
        passwordField.setBounds(505, 440, 250, 35);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 15));
        loginPanel.add(passwordField);
        
        JLabel lblUser = new JLabel("Username:"); 
        lblUser.setBounds(505, 340, 150, 20);
        lblUser.setFont(new Font("Arial", Font.PLAIN, 15));
        loginPanel.add(lblUser);
        
        JLabel lblPass = new JLabel("Password:"); 
        lblPass.setBounds(505, 415, 150, 20);
        lblPass.setFont(new Font("Arial", Font.PLAIN, 15));
        loginPanel.add(lblPass);
        
        JButton btnLogin = new JButton("Log In"); 
        btnLogin.setBounds(560, 500, 140, 40);
        btnLogin.setFont(new Font("Arial", Font.PLAIN, 15));
        loginPanel.add(btnLogin);
        
        btnLogin.addActionListener(e -> {
            String username = textField.getText();
            String password = new String(passwordField.getPassword());
            String selectedRole = roleCombo.getSelectedItem().toString();
            
            User foundUser = userManager.authenticate(username, password);
            
            if (foundUser != null) {
                currentUser = foundUser; 
                textField.setText("");
                passwordField.setText("");

                if (selectedRole.equals("Customer") && foundUser instanceof Customer) {
                    ((CardLayout) mainPanel.getLayout()).show(mainPanel, "customer");
                    ((CardLayout) contentPanel.getLayout()).show(contentPanel, "welcome");
                    
                } else if (selectedRole.equals("Staff")) {
                    if (foundUser instanceof Admin) {
                        ((CardLayout) mainPanel.getLayout()).show(mainPanel, "admin");
                        ((CardLayout) adminContentPanel.getLayout()).show(adminContentPanel, "adminWelcome");
                    } else if (foundUser instanceof Staff) {
                        ((CardLayout) mainPanel.getLayout()).show(mainPanel, "staff");
                        ((CardLayout) staffContentPanel.getLayout()).show(staffContentPanel, "staffWelcome");
                    } else {
                        JOptionPane.showMessageDialog(this, "Unauthorized Access (Role Mismatch)!");
                    }
                } else {
                     JOptionPane.showMessageDialog(this, "Please select the correct role.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Incorrect username or password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton btnSignupRedirect = new JButton("Sign Up");
        btnSignupRedirect.setBounds(560, 550, 140, 35);
        btnSignupRedirect.setFont(new Font("Arial", Font.PLAIN, 15));
        
        btnSignupRedirect.addActionListener(e -> {
            textField.setText("");
            passwordField.setText("");
            ((CardLayout) mainPanel.getLayout()).show(mainPanel, "signup");
        });
        loginPanel.add(btnSignupRedirect);
    }
    
    // Sign Up Panel
    private void createSignUpPanel() {
        JPanel signupPanel = new JPanel(null);
        mainPanel.add(signupPanel, "signup");
        
        JLabel lblTitle = new JLabel("Create New Account");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBounds(430, 80, 400, 40);
        signupPanel.add(lblTitle);
        
        JLabel nl = new JLabel("Name"); nl.setBounds(505, 150, 100, 20); signupPanel.add(nl);
        textField_1 = new JTextField(); textField_1.setBounds(505, 175, 250, 35); signupPanel.add(textField_1);
        
        JLabel sl = new JLabel("Surname"); sl.setBounds(505, 220, 100, 20); signupPanel.add(sl);
        textField_2 = new JTextField(); textField_2.setBounds(505, 245, 250, 35); signupPanel.add(textField_2);
        
        JLabel ul = new JLabel("Username"); ul.setBounds(505, 290, 100, 20); signupPanel.add(ul);
        textField_3 = new JTextField(); textField_3.setBounds(505, 315, 250, 35); signupPanel.add(textField_3);
        
        JLabel pl = new JLabel("Password");pl.setBounds(505, 360, 100, 20); signupPanel.add(pl);
        passwordField_1 = new JPasswordField(); passwordField_1.setBounds(505, 385, 250, 35); signupPanel.add(passwordField_1);
        
        JLabel telLbl = new JLabel("Phone"); telLbl.setBounds(505, 430, 100, 20); signupPanel.add(telLbl);
        JTextField txtPhone = new JTextField(); txtPhone.setBounds(505, 455, 250, 35); signupPanel.add(txtPhone);

        JButton btnRealSignup = new JButton("Sign Up"); btnRealSignup.setBounds(560, 520, 140, 40); signupPanel.add(btnRealSignup);
        
        btnRealSignup.addActionListener(e -> {
            try {
                String name = textField_1.getText().trim();
                String surname = textField_2.getText().trim();
                String uname = textField_3.getText().trim();
                String pass = new String(passwordField_1.getPassword()).trim();
                String phone = txtPhone.getText().trim(); 
                
                // Boş alan kontrolü
                if (name.isEmpty() || surname.isEmpty() || uname.isEmpty() || pass.isEmpty() || phone.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill in all fields!", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Telefon kontrolü
                if (phone.length() < 5) {
                    JOptionPane.showMessageDialog(this, "Invalid contact info (At least 5 characters).", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return; 
                }
                
                Customer newCust = new Customer(uname, pass, name, surname, phone);
                boolean success = userManager.addUser(newCust);

                if(success) {
                    JOptionPane.showMessageDialog(this, "Registration Successful! You can log in.");
                    textField_1.setText("");
                    textField_2.setText("");
                    textField_3.setText("");
                    passwordField_1.setText("");
                    txtPhone.setText("");
                    ((CardLayout) mainPanel.getLayout()).show(mainPanel, "login");
                } else {
                    JOptionPane.showMessageDialog(this, "Registration Failed!\nThis username or phone number is already registered.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch(Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        
        JButton btnBack = new JButton("Back"); btnBack.setBounds(560, 570, 140, 35);
        btnBack.addActionListener(ev -> {
            textField_1.setText("");
            textField_2.setText("");
            textField_3.setText("");
            passwordField_1.setText("");
            txtPhone.setText("");
            
            ((CardLayout) mainPanel.getLayout()).show(mainPanel, "login");
        });
        signupPanel.add(btnBack);
    }

    // Customer Panel
    private void createCustomerPanel() {
        customerParentPanel = new JPanel(null);
        mainPanel.add(customerParentPanel, "customer");
        
        sideMenuPanel = new JPanel();
        sideMenuPanel.setBounds(0, 0, 252, 900);
        sideMenuPanel.setBackground(new Color(44, 62, 80));
        sideMenuPanel.setLayout(new BoxLayout(sideMenuPanel, BoxLayout.Y_AXIS));
        sideMenuPanel.add(Box.createVerticalStrut(50));
        
        btnbooking = new JButton("Booking");
        btnmyTickets = new JButton("My Tickets");
        btnSimulation = new JButton("Simulation");
        btnlogOut = new JButton("Log Out");
        
        JButton[] customerButtons = {btnbooking, btnmyTickets, btnSimulation, btnlogOut};
        
        for (JButton btn : customerButtons) {
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(150, 40));
            btn.setPreferredSize(new Dimension(150, 40));
            btn.setBackground(new Color(52, 73, 94));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setFont(new Font("Arial", Font.PLAIN, 14));
            btn.setOpaque(true); 
            sideMenuPanel.add(btn);
            sideMenuPanel.add(Box.createVerticalStrut(20));
        }
        sideMenuPanel.add(Box.createVerticalGlue());
        
        contentPanel = new JPanel(new CardLayout());
        contentPanel.setBounds(252, 0, 1008, 860);
        
        customerParentPanel.add(sideMenuPanel);
        customerParentPanel.add(contentPanel);
        
        welcomePanel = new JPanel(new GridBagLayout());
        welcomePanel.add(new JLabel("Welcome Customer"));
        contentPanel.add(welcomePanel, "welcome");

     // --- MY TICKETS PANEL ---
        myTicketsPanel = new JPanel(new BorderLayout());
        myTicketsPanel.setBorder(BorderFactory.createTitledBorder("My Tickets"));
        
        String[] columnNames = {
            "Ticket ID", "Flight Num", "Passenger", "Seat", "Date", "Hour", "Price", "Baggage (kg)", "Dep -> Arr", "Status"
        };
        
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable ticketsTable = new JTable(tableModel);
        ticketsTable.setRowHeight(25);
        ticketsTable.getColumnModel().getColumn(0).setPreferredWidth(80); 
        ticketsTable.getColumnModel().getColumn(1).setPreferredWidth(70); 
        ticketsTable.getColumnModel().getColumn(2).setPreferredWidth(120); 
        ticketsTable.getColumnModel().getColumn(8).setPreferredWidth(150); 
        
        myTicketsPanel.add(new JScrollPane(ticketsTable), BorderLayout.CENTER);
        
        // --- İPTAL BUTONU ---
        JPanel ticketActionPanel = new JPanel();
        JButton btnCancelReservation = new JButton("Cancel Selected Reservation");
        
        btnCancelReservation.setOpaque(true);
        btnCancelReservation.setBorderPainted(false);
        
        btnCancelReservation.setBackground(new Color(192, 57, 43));
        btnCancelReservation.setForeground(Color.WHITE);
        ticketActionPanel.add(btnCancelReservation);
        myTicketsPanel.add(ticketActionPanel, BorderLayout.SOUTH);

        contentPanel.add(myTicketsPanel, "myTickets");

        btnmyTickets.addActionListener(e -> {
            if(currentUser instanceof Customer) {
                tableModel.setRowCount(0); 
                List<Ticket> allTickets = TicketFileManager.loadAllTickets();
                java.time.LocalDateTime now = java.time.LocalDateTime.now();

                for(Ticket t : allTickets) {
                    if (t.getReservation() != null && t.getReservation().getPassenger() != null) {
                        
                        if (t.getReservation().getPassenger().getPassengerID() == currentUser.getUserID()) {
                            Flight f = t.getReservation().getFlight();
                            Passenger p = t.getReservation().getPassenger();
                            
                            String status = "ACTIVE";
                            java.time.LocalDateTime flightTime = java.time.LocalDateTime.of(f.getDate(), f.getHour());
                            
                            if (t.isCancelled()) {
                                status = "CANCELLED";
                            } else if (flightTime.isBefore(now)) {
                                status = "COMPLETED";
                            }

                            tableModel.addRow(new Object[]{
                                t.getTicketID(), f.getFlightNum(), p.getName() + " " + p.getSurname(),
                                t.getReservation().getSeat().getSeatNum(), f.getDate(), f.getHour(),
                                t.getPrice() + " TL", (t.getBaggage() != null ? t.getBaggage().getWeight() : 0.0),
                                f.getDeparturePlace() + " -> " + f.getArrivalPlace(), status
                            });
                        }
                    }
                }
            }
            ((CardLayout) contentPanel.getLayout()).show(contentPanel, "myTickets");
        });
        
        // --- İPTAL İŞLEMİ ---
        btnCancelReservation.addActionListener(e -> {
            int selectedRow = ticketsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select the ticket you want to cancel.");
                return;
            }

            String status = (String) tableModel.getValueAt(selectedRow, 9); 
            if (!"ACTIVE".equals(status)) {
                JOptionPane.showMessageDialog(this, "Only 'ACTIVE' tickets can be cancelled.");
                return;
            }

            String ticketId = (String) tableModel.getValueAt(selectedRow, 0); 
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Ticket ID: " + ticketId + "\nAre you sure you want to cancel this reservation?", 
                "Cancellation Confirmation", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                List<Ticket> allTix = TicketFileManager.loadAllTickets();
                boolean changed = false;
                for (int i = 0; i < allTix.size() && !changed; i++) {
                    Ticket t = allTix.get(i);
                    if (t.getTicketID().equals(ticketId)) {
                        t.cancelTicket();
                        changed = true;
                    }
                }
                
                if (changed) {
                    TicketFileManager.saveAllTickets(allTix); 
                    JOptionPane.showMessageDialog(this, "Reservation successfully cancelled.");
                    btnmyTickets.doClick();
                } else {
                    JOptionPane.showMessageDialog(this, "Ticket not found.");
                }
            }
        });
        

        // Booking Flow
        JPanel bookingRoot = new JPanel(new BorderLayout());
        contentPanel.add(bookingRoot, "booking");
        JScrollPane sp = new JScrollPane();
        bookingRoot.add(sp);
        mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        sp.setViewportView(mainContent);
        createBookingFlow(sp);
        
        btnbooking.addActionListener(e -> {
             flightResultsPanel.setVisible(false);
             seatChoosePanel.setVisible(false);
             baggageChoosePanel.setVisible(false);
             paymentPanel.setVisible(false);
             ((CardLayout) contentPanel.getLayout()).show(contentPanel, "booking");
        });
        
        btnlogOut.addActionListener(e -> {
            currentUser = null;
            ((CardLayout) mainPanel.getLayout()).show(mainPanel, "login");
        });

        
        // --- SİMÜLASYON EKRANI ---
        JPanel simulationPanel = new JPanel(new BorderLayout());
        
        JPanel topPanel = new JPanel();
        JCheckBox chkSync = new JCheckBox("Synchronized Mode");
        JButton btnStart = new JButton("Start Simulation");
        JLabel lblStatus = new JLabel("Status: Ready");
        
        topPanel.add(chkSync);
        topPanel.add(btnStart);
        topPanel.add(lblStatus);
        simulationPanel.add(topPanel, BorderLayout.NORTH);
        
        JPanel gridPanel = new JPanel(new GridLayout(30, 6, 5, 5));
        
        if (simulationButtonsMap == null) simulationButtonsMap = new java.util.HashMap<>();
        simulationButtonsMap.clear(); 
        
        char[] cols = {'A', 'B', 'C', 'D', 'E', 'F'};
        for (int i = 1; i <= 30; i++) {
            for (char c : cols) {
                String seatNum = i + String.valueOf(c); 
                JButton btn = new JButton(seatNum);
                
                btn.setOpaque(true);            
                btn.setBorderPainted(false); 
                btn.setBackground(new Color(46, 204, 113)); // Yeşil
                btn.setForeground(Color.BLACK);  // Yazı rengi
                
                btn.setEnabled(false);
                btn.setPreferredSize(new Dimension(45, 30));
                
                gridPanel.add(btn);
                simulationButtonsMap.put(seatNum, btn); 
            }
        }
        simulationPanel.add(new JScrollPane(gridPanel), BorderLayout.CENTER);
        
        btnStart.addActionListener(e -> {
            for (JButton btn : simulationButtonsMap.values()) {
                btn.setBackground(new Color(46, 204, 113)); // Yeşil
            }
            lblStatus.setText("Starting...");

            simulationManager.runSimulation(chkSync.isSelected(), new SeatSimulation.SimulationCallback() {
                @Override
                public void onSeatReserved(String seatNum) {
                    SwingUtilities.invokeLater(() -> {
                        JButton btn = simulationButtonsMap.get(seatNum);
                        if (btn != null) {
                            // --- DOLU KOLTUK RENGİ ---
                            btn.setBackground(new Color(231, 76, 60)); // Kırmızı
                        }
                    });
                }

                @Override
                public void onStatusUpdated(int occupied, int target) {
                    SwingUtilities.invokeLater(() -> {
                        lblStatus.setText("Occupied: " + occupied + " / " + target);
                    });
                }
            });
        });
        
        contentPanel.add(simulationPanel, "simulation");
        
        btnSimulation.addActionListener(e -> 
            ((CardLayout) contentPanel.getLayout()).show(contentPanel, "simulation")
        );
        
        btnbooking.addActionListener(e -> {
             flightResultsPanel.setVisible(false);
             seatChoosePanel.setVisible(false);
             baggageChoosePanel.setVisible(false);
             paymentPanel.setVisible(false);
             ((CardLayout) contentPanel.getLayout()).show(contentPanel, "booking");
        });

        btnlogOut.addActionListener(e -> {
            currentUser = null;
            ((CardLayout) mainPanel.getLayout()).show(mainPanel, "login");
        });
    }

    private void createBookingFlow(JScrollPane sp) {
        // 1. SEARCH PANEL
        searchPanel = new JPanel(null);
        searchPanel.setPreferredSize(new Dimension(1000, 160));
        searchPanel.setMaximumSize(new Dimension(2000, 160));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Flight"));
        searchPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblF = new JLabel("From:"); lblF.setBounds(150, 50, 50, 25); searchPanel.add(lblF);
        txtFrom = new JTextField(); txtFrom.setBounds(200, 50, 150, 25); searchPanel.add(txtFrom);
        
        JLabel lblT = new JLabel("To:"); lblT.setBounds(370, 50, 30, 25); searchPanel.add(lblT);
        txtTo = new JTextField(); txtTo.setBounds(400, 50, 150, 25); searchPanel.add(txtTo);
        
        JLabel lblD = new JLabel("Date:"); lblD.setBounds(580, 50, 40, 25); searchPanel.add(lblD);
        JSpinner dSpin = new JSpinner(new SpinnerDateModel());
        dSpin.setBounds(625, 50, 130, 25);
        dSpin.setEditor(new JSpinner.DateEditor(dSpin, "dd.MM.yyyy"));
        searchPanel.add(dSpin);
        
        JButton btnS = new JButton("Find Flights");
        btnS.setBounds(425, 100, 150, 35);
        searchPanel.add(btnS);
        mainContent.add(searchPanel);
        mainContent.add(Box.createVerticalStrut(10));

        // 2. RESULTS PANEL
        flightResultsPanel = new JPanel();
        flightResultsPanel.setLayout(new BoxLayout(flightResultsPanel, BoxLayout.Y_AXIS));
        flightResultsPanel.setBorder(BorderFactory.createTitledBorder("Search Results"));
        flightResultsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        flightResultsPanel.setVisible(false);
        mainContent.add(flightResultsPanel);

        // 3. SEAT PANEL
        seatChoosePanel = new JPanel(new GridLayout(0, 6, 10, 10));
        seatChoosePanel.setBorder(BorderFactory.createTitledBorder("Select Seat (Green: Selected)"));
        seatChoosePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        seatChoosePanel.setVisible(false);
        mainContent.add(seatChoosePanel);
        
        JButton btnConfirmSeat = new JButton("Confirm Seat & Continue");
        JPanel seatFooter = new JPanel();
        seatFooter.add(btnConfirmSeat);
        seatFooter.setVisible(false);

        // 4. BAGGAGE
        baggageChoosePanel = new JPanel(null);
        baggageChoosePanel.setPreferredSize(new Dimension(800, 120));
        baggageChoosePanel.setMaximumSize(new Dimension(1000, 120));
        baggageChoosePanel.setBorder(BorderFactory.createTitledBorder("Baggage"));
        baggageChoosePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        baggageChoosePanel.setVisible(false);
        
        JLabel lblW = new JLabel("Weight (kg):"); lblW.setBounds(250, 40, 100, 25); baggageChoosePanel.add(lblW);
        txtBagWeight = new JTextField("0"); txtBagWeight.setBounds(350, 40, 100, 25); baggageChoosePanel.add(txtBagWeight);
        
        JButton btnToP = new JButton("Calculate Price");
        btnToP.setBounds(470, 40, 150, 25);
        baggageChoosePanel.add(btnToP);
        mainContent.add(baggageChoosePanel);

        // 5. PAYMENT
        paymentPanel = new JPanel(null);
        paymentPanel.setPreferredSize(new Dimension(800, 150));
        paymentPanel.setMaximumSize(new Dimension(1000, 150));
        paymentPanel.setBorder(BorderFactory.createTitledBorder("Payment"));
        paymentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        paymentPanel.setVisible(false);
        
        JLabel lblPrice = new JLabel("Total Price:"); lblPrice.setBounds(250, 40, 100, 25); paymentPanel.add(lblPrice);
        txtTotalPrice = new JTextField("0.00"); txtTotalPrice.setBounds(350, 40, 120, 25); txtTotalPrice.setEditable(false); paymentPanel.add(txtTotalPrice);
        
        JButton btnPay = new JButton("Pay & Book");
        btnPay.setBounds(500, 40, 150, 30);
        paymentPanel.add(btnPay);
        mainContent.add(paymentPanel);

        final Seat[] selectedSeat = {null}; 


        btnS.addActionListener(e -> {
            flightResultsPanel.removeAll();
            flightResultsPanel.setVisible(true);
            seatChoosePanel.removeAll();
            seatChoosePanel.setLayout(new GridLayout(0, 7, 5, 10)); 
            seatFooter.setVisible(false);
            
            java.time.LocalDate dateD = ((Date)dSpin.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            List<Flight> results = searchEngine.searchFlights(txtFrom.getText(), txtTo.getText(), dateD);
            
            if(results.isEmpty()) {
                flightResultsPanel.add(new JLabel("No flights found."));
            } else {
                for(Flight f : results) {
                    JButton btnFlight = new JButton(f.toString());
                    btnFlight.addActionListener(ev -> {
                        selectedFlightForBooking = f;
                        selectedSeat[0] = null; 
                        
                        seatChoosePanel.removeAll();
                        
                        
                        double currentFlightBasePrice = 0.0;
                        if (f.getPrice() > 0) {
                            currentFlightBasePrice = f.getPrice(); 
                        } else if (f.getRoute() != null) {
                            currentFlightBasePrice = f.getRoute().getDistance() * 1.5; 
                        }

                        CalculatePrice cp = new CalculatePrice();

                        Set<String> takenSeats = new HashSet<>();
                        List<Ticket> allTix = TicketFileManager.loadAllTickets();
                        for(Ticket t : allTix) {
                            if(t != null && t.getReservation() != null && 
                               t.getReservation().getFlight() != null && 
                               t.getReservation().getSeat() != null) {
                                String ticketFlightNum = t.getReservation().getFlight().getFlightNum();
                                if(ticketFlightNum != null && ticketFlightNum.equals(f.getFlightNum()) && !t.isCancelled()) {
                                    takenSeats.add(t.getReservation().getSeat().getSeatNum());
                                }
                            }
                        }


                        for(Seat s : f.getPlane().getSeatMatrix().values()) {
                            s.setReserved(false); 
                            
                            String seatNumStr = s.getSeatNum();
                            JButton seatBtn = new JButton(seatNumStr);
                            seatBtn.setFont(new Font("Arial", Font.PLAIN, 10)); 
                            
                            seatBtn.setOpaque(true);
                            seatBtn.setBorderPainted(false);
                            
                            String rowStr = seatNumStr.substring(0, seatNumStr.length() - 1);
                            int rowNum = Integer.parseInt(rowStr);

                            // Koltuk Tipini Belirle
                            SeatType seatType;
                            if (rowNum <= 3) {
                                seatType = SeatType.BUSINESS;
                            } else {
                                seatType = SeatType.ECONOMY;
                            }
                            s.setType(seatType);

                            cp.calculatePrice(currentFlightBasePrice, s, 0, 0); 
                            
                            
                         // --- RENK TANIMLAMALARI ---
                            Color businessColor = new Color(255, 200, 80); // SARI (Orange-Yellow)
                            Color economyColor = Color.LIGHT_GRAY;         // GRİ
                            Color takenColor = new Color(231, 76, 60);     // KIRMIZI
                            Color selectedColor = new Color(46, 204, 113); // YEŞİL
                            Color defaultColor = (rowNum <= 3) ? businessColor : economyColor;
                            
                            boolean isActuallyTaken = takenSeats.contains(seatNumStr);

                            if (isActuallyTaken) {
                                seatBtn.setBackground(takenColor);
                                seatBtn.setForeground(Color.WHITE);
                                seatBtn.setEnabled(false);
                            } else {
                                seatBtn.setBackground(defaultColor);
                                seatBtn.setForeground(Color.BLACK);
                                seatBtn.setEnabled(true);
                            }
                            
                            seatBtn.addActionListener(es -> {
                                selectedSeat[0] = s;
                                for(Component c : seatChoosePanel.getComponents()) {
                                    if(c instanceof JButton && c.isEnabled()) {
                                        String btnTxt = ((JButton)c).getText();
                                        String rStr = btnTxt.substring(0, btnTxt.length() - 1);
                                        int rNum = Integer.parseInt(rStr);
                                        c.setBackground((rNum <= 3) ? businessColor : economyColor);
                                    }
                                }
                                seatBtn.setBackground(selectedColor);
                            });
                            
                            seatChoosePanel.add(seatBtn);
                            
                            char seatLetter = seatNumStr.charAt(seatNumStr.length() - 1);
                            if (seatLetter == 'C') {
                                seatChoosePanel.add(Box.createHorizontalStrut(20)); 
                            }
                        }
                        seatChoosePanel.setVisible(true);
                        seatFooter.setVisible(true);
                        updateScroll(sp);
                    });
                    flightResultsPanel.add(btnFlight);
                    flightResultsPanel.add(Box.createVerticalStrut(5));
                }
            }
            updateScroll(sp);
        });
        
        mainContent.add(seatFooter);
        
        btnConfirmSeat.addActionListener(e -> {
            if(selectedSeat[0] == null) {
                JOptionPane.showMessageDialog(this, "Please select a seat.");
                return;
            }
            baggageChoosePanel.setVisible(true);
            updateScroll(sp);
        });

        
        btnToP.addActionListener(e -> {
             try {
                 double weight = Double.parseDouble(txtBagWeight.getText());
              // --- Eksi Ağırlık Kontrolü ---
                 if (weight < 0) {
                     JOptionPane.showMessageDialog(this, "Baggage weight cannot be negative!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                     return;
                 }
                 
                 CalculatePrice cp = new CalculatePrice();
                 
                 double baseFlightPrice = 1500.0;
                 if(selectedFlightForBooking != null) {
                     // 1. Uçuşun kendi fiyatı varsa onu kullan
                     if (selectedFlightForBooking.getPrice() > 0) {
                         baseFlightPrice = selectedFlightForBooking.getPrice();
                     } 
                     // 2. Yoksa mesafeden hesapla
                     else if (selectedFlightForBooking.getRoute() != null) {
                         double dist = selectedFlightForBooking.getRoute().getDistance();
                         if(dist > 0) baseFlightPrice = dist * 1.5; 
                     }
                 }
                 
                 SeatType type = selectedSeat[0].getType();
                 if(type == null) type = SeatType.ECONOMY;

                 double price = cp.calculatePrice(baseFlightPrice, selectedSeat[0], weight, 15);
                 
                 selectedSeat[0].setPrice(price);

                 txtTotalPrice.setText(String.format("%.2f", price)); 
                 paymentPanel.setVisible(true);
                 updateScroll(sp);
             } catch (NumberFormatException ex) {
                 JOptionPane.showMessageDialog(this, "Invalid baggage weight.");
             }
        });

        btnPay.addActionListener(e -> {
            try {
                if(selectedFlightForBooking != null && selectedSeat[0] != null && currentUser instanceof Customer) {
                    double weight = Double.parseDouble(txtBagWeight.getText());
                    
                    String priceStr = txtTotalPrice.getText().replace(",", ".");
                    double finalPrice = Double.parseDouble(priceStr);

                    Customer c = (Customer) currentUser;

                    // Eğer kullanıcının iletişim bilgisi 5 karakterden kısa mı kontrolü
                    String contactInfo = c.getContactInfo();
                    if (contactInfo == null || contactInfo.length() < 5) {
                        contactInfo = "NoContactInfo"; 
                    }

                    Passenger p = new Passenger(c.getName(), c.getSurname(), contactInfo);
                    p.setPassengerID(c.getUserID()); 

                    Ticket ticket = reservationManager.makeReservation(selectedFlightForBooking, p, selectedSeat[0], weight, 15);
                    
                    if(ticket != null) {
                        // Bileti güncelle ve kaydet
                        ticket.setPrice(finalPrice);
                        List<Ticket> allTix = TicketFileManager.loadAllTickets();
                        boolean ticketUpdated = false;
                        for(int i = 0; i < allTix.size() && !ticketUpdated; i++) {
                            Ticket t = allTix.get(i);
                            if(t.getTicketID().equals(ticket.getTicketID())) {
                                t.setPrice(finalPrice);
                                ticketUpdated = true;
                            }
                        }
                        TicketFileManager.saveAllTickets(allTix);

                        JOptionPane.showMessageDialog(this, "Booking Successful!\nTicket ID: " + ticket.getTicketID() + 
                                                            "\nPrice: " + finalPrice + " TL");
                        
                        flightManager.updateFlight(selectedFlightForBooking);
                        ((CardLayout) contentPanel.getLayout()).show(contentPanel, "welcome");
                    } else {
                        JOptionPane.showMessageDialog(this, "Booking Failed (Seat might be taken).");
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
    }

    // Staff Panel
    private void createStaffPanel() {
        staffParentPanel = new JPanel(null);
        mainPanel.add(staffParentPanel, "staff");
        
        staffSideMenu = new JPanel();
        staffSideMenu.setBounds(0, 0, 252, 900);
        staffSideMenu.setBackground(new Color(44, 62, 80));
        staffSideMenu.setLayout(new BoxLayout(staffSideMenu, BoxLayout.Y_AXIS));
        staffSideMenu.add(Box.createVerticalStrut(30));
        
        btnSeeFlights = new JButton("See Flights");
        btnSeeTickets = new JButton("See Tickets");
        btnAddPlane = new JButton("Add Plane");
        btnAddFlight = new JButton("Add Flight");
        btnDeleteFlight = new JButton("Delete Flight");
        btnUpdateFlight = new JButton("Update Flight");
        btnStaffReport = new JButton("Occupancy Report");
        btnStaffLogout = new JButton("Log Out");
        
        JButton[] staffButtons = {btnSeeFlights, btnSeeTickets, btnAddPlane, btnAddFlight, 
                                 btnDeleteFlight, btnUpdateFlight, btnStaffReport, btnStaffLogout};
        
        for (JButton btn : staffButtons) {
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(150, 40));
            btn.setPreferredSize(new Dimension(150, 40));
            btn.setBackground(new Color(52, 73, 94));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setFont(new Font("Arial", Font.PLAIN, 14));
            staffSideMenu.add(btn);
            staffSideMenu.add(Box.createVerticalStrut(15));
        }
        staffSideMenu.add(Box.createVerticalGlue());
        
        staffParentPanel.add(staffSideMenu);

        staffContentPanel = new JPanel(new CardLayout());
        staffContentPanel.setBounds(252, 0, 1008, 860);
        staffParentPanel.add(staffContentPanel);

        // 1. SEE FLIGHTS
        seeFlightsPanel = new JPanel(new BorderLayout());
        seeFlightsPanel.setBorder(BorderFactory.createTitledBorder("All Flights"));
        
        String[] colsF = {"Num", "Route", "Date", "Hour", "Duration","Plane Info", "Capacity", "Status"};
        DefaultTableModel modelF = new DefaultTableModel(colsF, 0);
        JTable tableF = new JTable(modelF);
        seeFlightsPanel.add(new JScrollPane(tableF), BorderLayout.CENTER);
        staffContentPanel.add(seeFlightsPanel, "seeFlightsPanel");
        
        btnSeeFlights.addActionListener(e -> {
            modelF.setRowCount(0);
            
            // Şimdiki zaman alınır
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            
            for(Flight f : flightManager.getAllFlights()) {
                Plane p = f.getPlane();
                String planeInfo = p.getPlaneModel() + " (" + p.getPlaneID() + ")";
                
                String status = "ACTIVE";
                java.time.LocalDateTime flightTime = java.time.LocalDateTime.of(f.getDate(), f.getHour());
                
                if (flightTime.isBefore(now)) {
                    status = "COMPLETED";
                }

                modelF.addRow(new Object[]{
                    f.getFlightNum(),
                    f.getDeparturePlace() + "->" + f.getArrivalPlace(),
                    f.getDate(), 
                    f.getHour(),
                    f.getDurationMinutes() + " min",
                    planeInfo,
                    p.getCapacity(),
                    status 
                });
            }
            ((CardLayout) staffContentPanel.getLayout()).show(staffContentPanel, "seeFlightsPanel");
        });

        
        // 2. SEE TICKETS
        seeTicketsPanel = new JPanel(new BorderLayout());
        seeTicketsPanel.setBorder(BorderFactory.createTitledBorder("All Tickets System-Wide"));
        
        String[] colsT = {
            "Ticket ID", "Flight Num", "Passenger Info", "Seat", "Date", "Hour", "Duration", "Price", "Baggage", "Route", "Status"
        };
        
        DefaultTableModel modelT = new DefaultTableModel(colsT, 0);
        JTable tableT = new JTable(modelT);
        
        tableT.getColumnModel().getColumn(2).setPreferredWidth(150); 
        tableT.getColumnModel().getColumn(9).setPreferredWidth(120); 
        
        seeTicketsPanel.add(new JScrollPane(tableT), BorderLayout.CENTER);
        staffContentPanel.add(seeTicketsPanel, "seeTicketsPanel");
        
        btnSeeTickets.addActionListener(e -> {
             modelT.setRowCount(0);
             
             List<Ticket> allTickets = TicketFileManager.loadAllTickets();
             java.time.LocalDateTime now = java.time.LocalDateTime.now();
             
             for(Ticket t : allTickets) {
                 if (t.getReservation() != null) {
                 
                     Flight f = t.getReservation().getFlight();
                     Passenger p = t.getReservation().getPassenger();
                     
                     String passInfo = "N/A";
                     if(p != null) {
                         passInfo = String.format("%s %s (ID:%d) Tel:%s", 
                             p.getName(), p.getSurname(), p.getPassengerID(), p.getContactInfo());
                     }
                     
                     String status = "ACTIVE";
                     if(f != null) {
                         java.time.LocalDateTime fTime = java.time.LocalDateTime.of(f.getDate(), f.getHour());
                         if (t.isCancelled()) status = "CANCELLED";
                         else if (fTime.isBefore(now)) status = "COMPLETED";
                     }

                     modelT.addRow(new Object[]{
                         t.getTicketID(),                                
                         (f != null ? f.getFlightNum() : "?"),           
                         passInfo,                                       
                         t.getReservation().getSeat().getSeatNum(),      
                         (f != null ? f.getDate() : "?"),                
                         (f != null ? f.getHour() : "?"),                
                         (f != null ? f.getDurationMinutes() + " min" : "?"), 
                         t.getPrice() + " TL",                           
                         (t.getBaggage() != null ? t.getBaggage().getWeight() : 0.0) + " kg", 
                         (f != null ? f.getDeparturePlace() + "->" + f.getArrivalPlace() : "?"), 
                         status                                          
                     });
                 }
             }
             ((CardLayout) staffContentPanel.getLayout()).show(staffContentPanel, "seeTicketsPanel");
        });

        // 2.5 ADD PLANE
        addPlanePanel = new JPanel(null);
        addPlanePanel.setBorder(BorderFactory.createTitledBorder("Add New Plane"));

        JLabel pl1 = new JLabel("Plane ID:"); pl1.setBounds(250, 200, 160, 40); addPlanePanel.add(pl1);
        JTextField pId = new JTextField(); pId.setBounds(420, 200, 320, 40); addPlanePanel.add(pId);

        JLabel pl2 = new JLabel("Plane Model:"); pl2.setBounds(250, 260, 160, 40); addPlanePanel.add(pl2);
        JTextField pModel = new JTextField(); pModel.setBounds(420, 260, 320, 40); addPlanePanel.add(pModel);

        JLabel pl3 = new JLabel("Capacity:"); pl3.setBounds(250, 320, 160, 40); addPlanePanel.add(pl3);
        JTextField pCap = new JTextField(); pCap.setBounds(420, 320, 320, 40); addPlanePanel.add(pCap);

        JButton btnPlaneSave = new JButton("Save Plane");
        btnPlaneSave.setBounds(420, 390, 320, 45);
        addPlanePanel.add(btnPlaneSave);

        btnPlaneSave.addActionListener(ev -> {
            String id = pId.getText().trim();
            String model = pModel.getText().trim();
            String capStr = pCap.getText().trim();

            if (id.isEmpty() || model.isEmpty() || capStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.");
                return;
            }
            int cap;
            try {
                cap = Integer.parseInt(capStr);
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Capacity must be numeric.");
                return;
            }
            if (cap <= 0) {
                JOptionPane.showMessageDialog(this, "Capacity must be greater than 0.");
                return;
            }

            boolean ok = planeManager.addPlane(id, model, cap, 0.0);
            if (!ok) {
                JOptionPane.showMessageDialog(this, "This Plane ID is already in use!");
                return;
            }

            addPlaneToAllCombos(id);
            JOptionPane.showMessageDialog(this, "Plane added!");
            pId.setText(""); pModel.setText(""); pCap.setText("");
        });

        staffContentPanel.add(addPlanePanel, "addPlanePanel");
        btnAddPlane.addActionListener(ev -> ((CardLayout) staffContentPanel.getLayout()).show(staffContentPanel, "addPlanePanel"));

        // 3. ADD FLIGHT (STAFF)
        addFlightPanel = new JPanel(null);
        addFlightPanel.setBorder(BorderFactory.createTitledBorder("Add New Flight"));

        JLabel af0 = new JLabel("Plane:"); af0.setBounds(250, 30, 160, 40); addFlightPanel.add(af0);
        JComboBox<PlaneItem> cbPlane = new JComboBox<>();
        cbPlane.setBounds(420, 30, 320, 40);
        addFlightPanel.add(cbPlane);
        registerPlaneCombo(cbPlane);

        JLabel l3 = new JLabel("Flight Num:"); l3.setBounds(250, 85, 160, 40); addFlightPanel.add(l3);
        JTextField tNum = new JTextField(); tNum.setBounds(420, 85, 320, 40); addFlightPanel.add(tNum);

        JLabel l4 = new JLabel("From:"); l4.setBounds(250, 140, 160, 40); addFlightPanel.add(l4);
        JTextField tFrom = new JTextField(); tFrom.setBounds(420, 140, 320, 40); addFlightPanel.add(tFrom);

        JLabel l5 = new JLabel("To:"); l5.setBounds(250, 195, 160, 40); addFlightPanel.add(l5);
        JTextField tTo = new JTextField(); tTo.setBounds(420, 195, 320, 40); addFlightPanel.add(tTo);
        
        JLabel lDist = new JLabel("Distance (km):"); 
        lDist.setBounds(250, 250, 160, 40); 
        addFlightPanel.add(lDist);
        JTextField tDist = new JTextField();
        tDist.setBounds(420, 250, 320, 40); 
        addFlightPanel.add(tDist);

        JLabel l6 = new JLabel("Date:"); l6.setBounds(250, 305, 160, 40); addFlightPanel.add(l6);
        JSpinner spinDate = new JSpinner(new SpinnerDateModel()); spinDate.setBounds(420, 305, 320, 40);
        spinDate.setEditor(new JSpinner.DateEditor(spinDate, "dd.MM.yyyy")); addFlightPanel.add(spinDate);

        JLabel l7 = new JLabel("Time:"); l7.setBounds(250, 360, 160, 40);addFlightPanel.add(l7);
        JSpinner spinTime = new JSpinner(new SpinnerDateModel()); spinTime.setBounds(420, 360, 320, 40);
        spinTime.setEditor(new JSpinner.DateEditor(spinTime, "HH:mm")); addFlightPanel.add(spinTime);

        JLabel l8 = new JLabel("Duration (min):"); l8.setBounds(250, 415, 160, 40); addFlightPanel.add(l8);
        JTextField tDur = new JTextField(); tDur.setBounds(420, 415, 320, 40); addFlightPanel.add(tDur);

        JLabel l9 = new JLabel("Base Price:"); l9.setBounds(250, 470, 160, 40); addFlightPanel.add(l9);
        JTextField tBase = new JTextField(); tBase.setBounds(420, 470, 320, 40); addFlightPanel.add(tBase);

        JButton btnAddReal = new JButton("Create Flight");
        btnAddReal.setBounds(420, 540, 320, 45);
        addFlightPanel.add(btnAddReal);

        btnAddReal.addActionListener(e -> {
            try {
                String flightNum = tNum.getText().trim();
                String from = tFrom.getText().trim();
                String to = tTo.getText().trim();
                String durStr = tDur.getText().trim();
                String baseStr = tBase.getText().trim();
                String distStr = tDist.getText().trim();

                if (flightNum.isEmpty() || from.isEmpty() || to.isEmpty() || durStr.isEmpty() || baseStr.isEmpty() || distStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill in all fields.");
                    return;
                }
                if (from.equalsIgnoreCase(to)) {
                    JOptionPane.showMessageDialog(this, "From and To cannot be the same.");
                    return;
                }
                if (flightManager.getFlightByNumber(flightNum) != null) {
                    JOptionPane.showMessageDialog(this, "Flight Num exists!");
                    return;
                }

                int duration;
                double basePrice;
                double distance;
                
                try {
                    duration = Integer.parseInt(durStr);
                    basePrice = Double.parseDouble(baseStr);
                    distance = Double.parseDouble(distStr);
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(this, "Duration, Base Price, Distance must be numeric.");
                    return;
                }
                if (duration <= 0 || basePrice <= 0 || distance <= 0) {
                    JOptionPane.showMessageDialog(this, "Duration/Base Price/Distance must be greater than 0.");
                    return;
                }

                java.time.LocalDate d = ((Date) spinDate.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                java.time.LocalTime t = ((Date) spinTime.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime();
                java.time.LocalDateTime flightDT = java.time.LocalDateTime.of(d, t);

                if (!flightDT.isAfter(java.time.LocalDateTime.now())) {
                    JOptionPane.showMessageDialog(this, "Cannot add a flight to a past date.");
                    return;
                }

                String prevSel = (cbPlane.getSelectedItem() instanceof PlaneItem) ? ((PlaneItem)cbPlane.getSelectedItem()).planeId : "";
                refreshPlaneComboForSlot(cbPlane, d, t, duration, null, false);

                if (prevSel != null && !prevSel.isEmpty()) {
                    boolean selectionFound = false;
                    for (int i = 0; i < cbPlane.getItemCount() && !selectionFound; i++) {
                        PlaneItem it = cbPlane.getItemAt(i);
                        if (it != null && prevSel.equalsIgnoreCase(it.planeId)) {
                            cbPlane.setSelectedIndex(i);
                            selectionFound = true;
                        }
                    }
                }

                PlaneItem sel = (PlaneItem) cbPlane.getSelectedItem();
                if (sel == null || sel.planeId == null || sel.planeId.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No available planes for this date/time. (Add a plane first using Add Plane.)");
                    return;
                }

                Plane template = planeManager.getPlaneByID(sel.planeId);
                if (template == null) {
                    JOptionPane.showMessageDialog(this, "Selected plane not found.");
                    return;
                }

                java.time.LocalDateTime slotStart = java.time.LocalDateTime.of(d, t);
                java.time.LocalDateTime slotEnd   = slotStart.plusMinutes(duration);
                if (!isPlaneAvailableForSlot(sel.planeId, slotStart, slotEnd, null)) {
                    JOptionPane.showMessageDialog(this, "This plane is on another flight during this time slot!");
                    return;
                }

                Plane pForFlight = template;
                Route r = new Route(from, to, distance);
                Flight f = new Flight(flightNum, r, d, t, duration, from, to, pForFlight);
                f.setPrice(basePrice); 

                boolean ok = flightManager.createFlight(f);
                if (ok) JOptionPane.showMessageDialog(this, "Flight Created!");
                else JOptionPane.showMessageDialog(this, "Could not create flight!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        staffContentPanel.add(addFlightPanel, "addFlightPanel");

        btnAddFlight.addActionListener(e -> {
            try {
                java.time.LocalDate d = ((Date) spinDate.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                java.time.LocalTime t = ((Date) spinTime.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime();
                int dur = Integer.parseInt(tDur.getText().trim());
                if (dur > 0) refreshPlaneComboForSlot(cbPlane, d, t, dur, null, false);
            } catch (Exception ignore) {}
            ((CardLayout) staffContentPanel.getLayout()).show(staffContentPanel, "addFlightPanel");
        });
        
        // 4. DELETE FLIGHT
        deleteFlightPanel = new JPanel(null);
        deleteFlightPanel.setBorder(BorderFactory.createTitledBorder("Delete Flight"));
        JLabel dl = new JLabel("Flight Num:"); dl.setBounds(250, 250, 160, 40); deleteFlightPanel.add(dl);
        JTextField dt = new JTextField(); dt.setBounds(420, 250, 320, 40); deleteFlightPanel.add(dt);
        JButton db = new JButton("Delete"); db.setBounds(420, 320, 320, 45); deleteFlightPanel.add(db);
        
        db.addActionListener(e -> {
            boolean done = flightManager.deleteFlight(dt.getText(), reservationManager);
            if(done) JOptionPane.showMessageDialog(this, "Deleted!");
            else JOptionPane.showMessageDialog(this, "Not Found!");
        });
        staffContentPanel.add(deleteFlightPanel, "deleteFlightPanel");
        btnDeleteFlight.addActionListener(e -> ((CardLayout) staffContentPanel.getLayout()).show(staffContentPanel, "deleteFlightPanel"));

        // 5. UPDATE FLIGHT
        updateFlightPanel = new JPanel(null);
        updateFlightPanel.setBorder(BorderFactory.createTitledBorder("Update Flight"));
        JLabel ul1 = new JLabel("Flight Num:"); ul1.setBounds(250, 150, 160, 40); updateFlightPanel.add(ul1);
        JTextField ut1 = new JTextField(); ut1.setBounds(420, 150, 320, 40); updateFlightPanel.add(ut1);

        JLabel ul2 = new JLabel("New Date:"); ul2.setBounds(250, 210, 160, 40); updateFlightPanel.add(ul2);
        JSpinner usD = new JSpinner(new SpinnerDateModel()); usD.setBounds(420, 210, 320, 40);
        usD.setEditor(new JSpinner.DateEditor(usD, "dd.MM.yyyy")); updateFlightPanel.add(usD);

        JLabel ul3 = new JLabel("New Time:"); ul3.setBounds(250, 270, 160, 40); updateFlightPanel.add(ul3);
        JSpinner usT = new JSpinner(new SpinnerDateModel()); usT.setBounds(420, 270, 320, 40);
        usT.setEditor(new JSpinner.DateEditor(usT, "HH:mm")); updateFlightPanel.add(usT);

        JLabel ul4 = new JLabel("New Plane:"); ul4.setBounds(250, 330, 160, 40); updateFlightPanel.add(ul4);
        JComboBox<PlaneItem> cbNewPlane = new JComboBox<>();
        cbNewPlane.setBounds(420, 330, 320, 40);
        cbNewPlane.addItem(new PlaneItem("", "-- Keep Current --"));
        updateFlightPanel.add(cbNewPlane);
        registerPlaneCombo(cbNewPlane);

        

        JButton ub = new JButton("Update"); ub.setBounds(420, 400, 320, 45); updateFlightPanel.add(ub);

        ub.addActionListener(e -> {
            Flight f = flightManager.getFlightByNumber(ut1.getText());
            if (f == null) {
                JOptionPane.showMessageDialog(this, "Flight Not Found");
                return;
            }

            java.time.LocalDate d = ((Date) usD.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            java.time.LocalTime t = ((Date) usT.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime();
            java.time.LocalDateTime newDT = java.time.LocalDateTime.of(d, t);
            if (!newDT.isAfter(java.time.LocalDateTime.now())) {
                JOptionPane.showMessageDialog(this, "Cannot update flight to a past date.");
                return;
            }


            int duration = f.getDurationMinutes();

            String prevSel = (cbNewPlane.getSelectedItem() instanceof PlaneItem) ? ((PlaneItem)cbNewPlane.getSelectedItem()).planeId : "";
            refreshPlaneComboForSlot(cbNewPlane, d, t, duration, f.getFlightNum(), true);

            if (prevSel != null && !prevSel.isEmpty()) {
                boolean selectionFound = false;
                for (int i = 0; i < cbNewPlane.getItemCount() && !selectionFound; i++) {
                    PlaneItem it = cbNewPlane.getItemAt(i);
                    if (it != null && prevSel.equalsIgnoreCase(it.planeId)) {
                        cbNewPlane.setSelectedIndex(i);
                        selectionFound = true;
                    }
                }
            }

            PlaneItem sel = (PlaneItem) cbNewPlane.getSelectedItem();

            String targetPlaneId;
            if (sel != null && sel.planeId != null && !sel.planeId.isEmpty()) {
                targetPlaneId = sel.planeId;
            } else {
                targetPlaneId = (f.getPlane() != null ? f.getPlane().getPlaneID() : null);
            }
            if (targetPlaneId == null || targetPlaneId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Plane could not be selected / current plane not found.");
                return;
            }
            java.time.LocalDateTime slotStart = java.time.LocalDateTime.of(d, t);
            java.time.LocalDateTime slotEnd   = slotStart.plusMinutes(duration);
            if (!isPlaneAvailableForSlot(targetPlaneId, slotStart, slotEnd, f.getFlightNum())) {
                JOptionPane.showMessageDialog(this, "This plane is on another flight during this time slot!");
                return;
            }

            if (sel != null && sel.planeId != null && !sel.planeId.isEmpty()) {
                Plane template = planeManager.getPlaneByID(sel.planeId);
                if (template == null) {
                    JOptionPane.showMessageDialog(this, "Selected plane not found.");
                    return;
                }
                
                if (template.getCapacity() < f.getPlane().getCapacity()) {
                    List<Ticket> allTickets = TicketFileManager.loadAllTickets();
                    boolean changed = false;
                    int cancelCount = 0;
                    
                    Set<String> validSeats = template.getSeatMatrix().keySet();
                    
                    for (Ticket tk : allTickets) {
                        // İlgili uçuşun aktif biletlerini bulur
                        if (tk.getReservation() != null && 
                            tk.getReservation().getFlight() != null &&
                            tk.getReservation().getFlight().getFlightNum().equals(f.getFlightNum()) && 
                            !tk.isCancelled()) {
                            
                            String seatNum = tk.getReservation().getSeat().getSeatNum();
                            
                            // Eğer koltuk yeni uçakta yoksa iptal eder
                            if (!validSeats.contains(seatNum)) {
                                tk.cancelTicket();
                                cancelCount++;
                                changed = true;
                            }
                        }
                    }
                    
                    if (changed) {
                        TicketFileManager.saveAllTickets(allTickets);
                        JOptionPane.showMessageDialog(this, 
                            "Uçak kapasitesi düşürüldü. Kapasite dışı kalan " + cancelCount + " bilet iptal edildi.", 
                            "Otomatik İptal", JOptionPane.INFORMATION_MESSAGE);
                    }
                }

                Plane pForFlight = template;
                f.setPlane(pForFlight);
            } else {
                try {
                    Plane cur = f.getPlane();
                    if (cur != null) {
                        Plane pForFlight = new Plane(cur.getPlaneID(), cur.getPlaneModel(), cur.getCapacity());
                        f.setPlane(pForFlight);
                    }
                } catch (Exception ignore) {}
            }

            f.setDate(d);
            f.setHour(t);
            flightManager.updateFlight(f);

            TicketFileManager.updateTicketsForFlight(f);
            JOptionPane.showMessageDialog(this, "Updated!");
        });

        staffContentPanel.add(updateFlightPanel, "updateFlightPanel");
        btnUpdateFlight.addActionListener(e -> ((CardLayout) staffContentPanel.getLayout()).show(staffContentPanel, "updateFlightPanel"));

        
        
     // --- Staff Rapor Paneli ---
        staffReportPanel = new JPanel(new BorderLayout());
        staffReportPanel.setBorder(BorderFactory.createTitledBorder("System Occupancy Report"));
        
        JTextArea txtStaffReport = new JTextArea();
        txtStaffReport.setEditable(false);
        txtStaffReport.setFont(new Font("Monospaced", Font.PLAIN, 12));
        staffReportPanel.add(new JScrollPane(txtStaffReport), BorderLayout.CENTER);
        
        JLabel lblStaffStatus = new JLabel("Click 'Generate' to start report.");
        lblStaffStatus.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        staffReportPanel.add(lblStaffStatus, BorderLayout.NORTH);
        
        JButton btnGenStaff = new JButton("Generate Report Now");
        staffReportPanel.add(btnGenStaff, BorderLayout.SOUTH);
        
        // Raporu Başlatan Action Listener
        btnGenStaff.addActionListener(e -> {
            // Asenkron çağrı başlar
            reportManager.generateOccupancyReportAsync(flightManager.getAllFlights(), new ReportSimulation.ReportCallback() {
                @Override
                public void onReportStart() {
                    // GUI Thread 
                    SwingUtilities.invokeLater(() -> {
                        lblStaffStatus.setText("Preparing report... Please wait.");
                        lblStaffStatus.setForeground(Color.RED);
                        txtStaffReport.setText("");
                        btnGenStaff.setEnabled(false); // Butonu kilitle
                    });
                }

                @Override
                public void onReportReady(String reportResult) {
                    // GUI Thread 
                    SwingUtilities.invokeLater(() -> {
                        lblStaffStatus.setText("Report Generated Successfully.");
                        lblStaffStatus.setForeground(new Color(46, 204, 113));
                        txtStaffReport.setText(reportResult);
                        btnGenStaff.setEnabled(true); // Butonu aç
                    });
                }
            });
        });
        
        staffContentPanel.add(staffReportPanel, "staffReportPanel");
        
        btnStaffReport.addActionListener(e -> 
            ((CardLayout) staffContentPanel.getLayout()).show(staffContentPanel, "staffReportPanel")
        );
        
        
        
        // Staff Welcome
        JPanel staffWelcome = new JPanel(new GridBagLayout());
        staffWelcome.add(new JLabel("Welcome Staff"));
        staffContentPanel.add(staffWelcome, "staffWelcome");
        
        btnStaffLogout.addActionListener(e -> {
            currentUser = null;
            ((CardLayout) mainPanel.getLayout()).show(mainPanel, "login");
        });
    }

    // Admin Panel
    private void createAdminPanel() {
        adminParentPanel = new JPanel(null);
        mainPanel.add(adminParentPanel, "admin");
        
        adminSideMenu = new JPanel();
        adminSideMenu.setBounds(0, 0, 252, 900);
        adminSideMenu.setBackground(new Color(60, 40, 80));
        adminSideMenu.setLayout(new BoxLayout(adminSideMenu, BoxLayout.Y_AXIS));
        adminSideMenu.add(Box.createVerticalStrut(20));
        
        btnAdminSeeFlights = new JButton("See Flights");
        btnAdminSeeTickets = new JButton("See Tickets");
        btnAdminAddPlane = new JButton("Add Plane");
        btnAdminAddFlight = new JButton("Add Flight");
        btnAdminDeleteFlight = new JButton("Delete Flight");
        btnAdminUpdateFlight = new JButton("Update Flight");
        btnSeeAllStaffs = new JButton("See All Staffs");
        btnAddStaff = new JButton("Add Staff");
        btnDeleteStaff = new JButton("Delete Staff");
        btnUpdateStaff = new JButton("Update Staff Info");
        btnAdminReport = new JButton("Occupancy Report");
        btnAdminLogout = new JButton("Log Out");
        
        JButton[] adminButtons = {btnAdminSeeFlights, btnAdminSeeTickets, btnAdminAddPlane, btnAdminAddFlight, 
                                 btnAdminDeleteFlight, btnAdminUpdateFlight, btnSeeAllStaffs,
                                 btnAddStaff, btnDeleteStaff, btnUpdateStaff, btnAdminReport ,btnAdminLogout};
        
        for (JButton btn : adminButtons) {
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(150, 35));
            btn.setPreferredSize(new Dimension(150, 35));
            btn.setBackground(new Color(70, 50, 90));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setFont(new Font("Arial", Font.PLAIN, 12));
            adminSideMenu.add(btn);
            adminSideMenu.add(Box.createVerticalStrut(10));
        }
        adminSideMenu.add(Box.createVerticalGlue());
        adminParentPanel.add(adminSideMenu);

        adminContentPanel = new JPanel(new CardLayout());
        adminContentPanel.setBounds(252, 0, 1008, 860);
        adminParentPanel.add(adminContentPanel);
        
        JPanel aw = new JPanel(new GridBagLayout()); 
        aw.add(new JLabel("Welcome Admin - Full Access Dashboard"));
        adminContentPanel.add(aw, "adminWelcome");

        // 1. SEE ALL STAFF
        seeAllStaffsPanel = new JPanel(new BorderLayout());
        seeAllStaffsPanel.setBorder(BorderFactory.createTitledBorder("All Staff Members"));
        String[] colsS = {"ID", "Username", "Name", "Surname", "Salary"};
        DefaultTableModel modelS = new DefaultTableModel(colsS, 0);
        JTable tableS = new JTable(modelS);
        seeAllStaffsPanel.add(new JScrollPane(tableS), BorderLayout.CENTER);
        adminContentPanel.add(seeAllStaffsPanel, "seeAllStaffs");
        
        btnSeeAllStaffs.addActionListener(e -> {
            modelS.setRowCount(0);
            for(Staff s : userManager.getAllStaff()) {
                modelS.addRow(new Object[]{s.getUserID(), s.getUsername(), s.getName(), s.getSurname(), s.getSalary()});
            }
            ((CardLayout) adminContentPanel.getLayout()).show(adminContentPanel, "seeAllStaffs");
        });

        // 2. ADD STAFF
        addStaffPanel = new JPanel(null);
        addStaffPanel.setBorder(BorderFactory.createTitledBorder("Add New Staff"));
        JLabel asl1 = new JLabel("Username:"); asl1.setBounds(250, 100, 160, 40); addStaffPanel.add(asl1);
        JTextField ast1 = new JTextField(); ast1.setBounds(420, 100, 320, 40); addStaffPanel.add(ast1);
        JLabel asl2 = new JLabel("Password:"); asl2.setBounds(250, 160, 160, 40); addStaffPanel.add(asl2);
        JTextField ast2 = new JTextField(); ast2.setBounds(420, 160, 320, 40); addStaffPanel.add(ast2);
        JLabel asl3 = new JLabel("Name:"); asl3.setBounds(250, 220, 160, 40); addStaffPanel.add(asl3);
        JTextField ast3 = new JTextField(); ast3.setBounds(420, 220, 320, 40); addStaffPanel.add(ast3);
        JLabel asl4 = new JLabel("Surname:"); asl4.setBounds(250, 280, 160, 40); addStaffPanel.add(asl4);
        JTextField ast4 = new JTextField(); ast4.setBounds(420, 280, 320, 40); addStaffPanel.add(ast4);
        JLabel asl5 = new JLabel("Salary:"); asl5.setBounds(250, 340, 160, 40); addStaffPanel.add(asl5);
        JTextField ast5 = new JTextField(); ast5.setBounds(420, 340, 320, 40); addStaffPanel.add(ast5);
        JButton asBtn = new JButton("Create Staff"); asBtn.setBounds(420, 410, 320, 45); addStaffPanel.add(asBtn);
        
        asBtn.addActionListener(e -> {
            try {
                double salary = Double.parseDouble(ast5.getText());
                if (salary < 0) {
                    JOptionPane.showMessageDialog(this, "Salary cannot be negative!");
                    return; // Eksi değer varsa işlemi durdurur
                }
                
                Staff s = new Staff(ast1.getText(), ast2.getText(), ast3.getText(), ast4.getText(), salary);
                if(userManager.addUser(s)) JOptionPane.showMessageDialog(this, "Staff Added!");
                else JOptionPane.showMessageDialog(this, "Username Taken!");
            } catch(Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + "Please fill in all fields.");
            }
        });
        adminContentPanel.add(addStaffPanel, "addStaff");
        btnAddStaff.addActionListener(e -> ((CardLayout) adminContentPanel.getLayout()).show(adminContentPanel, "addStaff"));

        // 3. DELETE STAFF
        deleteStaffPanel = new JPanel(null);
        deleteStaffPanel.setBorder(BorderFactory.createTitledBorder("Delete Staff"));
        JLabel dsl = new JLabel("Staff ID:"); dsl.setBounds(250, 250, 160, 40); deleteStaffPanel.add(dsl);
        JTextField dst = new JTextField(); dst.setBounds(420, 250, 320, 40); deleteStaffPanel.add(dst);
        JButton dsb = new JButton("Delete"); dsb.setBounds(420, 320, 320, 45); deleteStaffPanel.add(dsb);
        
        dsb.addActionListener(e -> {
            try {
                int id = Integer.parseInt(dst.getText());
                if(userManager.deleteUser(id)) JOptionPane.showMessageDialog(this, "Staff Deleted!");
                else JOptionPane.showMessageDialog(this, "ID Not Found!");
            } catch(Exception ex) {
                 JOptionPane.showMessageDialog(this, "Invalid ID");
            }
        });
        adminContentPanel.add(deleteStaffPanel, "deleteStaff");
        btnDeleteStaff.addActionListener(e -> ((CardLayout) adminContentPanel.getLayout()).show(adminContentPanel, "deleteStaff"));
        
        
        
        
     // Update Staff Panel
     updateStaffPanel = new JPanel(null);
     updateStaffPanel.setBorder(BorderFactory.createTitledBorder("Update Staff Credentials"));

     // 1. Staff ID (Arama kriteri)
     JLabel lblUSId = new JLabel("Staff ID:"); 
     lblUSId.setBounds(250, 200, 160, 40);
     updateStaffPanel.add(lblUSId);

     JTextField txtUSId = new JTextField(); 
     txtUSId.setBounds(420, 200, 320, 40);
     updateStaffPanel.add(txtUSId);

     // 2. New Password
     JLabel lblUSPass = new JLabel("New Password:"); 
     lblUSPass.setBounds(250, 260, 160, 40);
     updateStaffPanel.add(lblUSPass);

     JTextField txtUSPass = new JTextField(); 
     txtUSPass.setBounds(420, 260, 320, 40);
     updateStaffPanel.add(txtUSPass);

     // 3. New Salary
     JLabel lblUSSal = new JLabel("New Salary:"); 
     lblUSSal.setBounds(250, 320, 160, 40);
     updateStaffPanel.add(lblUSSal);

     JTextField txtUSSal = new JTextField(); 
     txtUSSal.setBounds(420, 320, 320, 40);
     updateStaffPanel.add(txtUSSal);

     // 4. Update Button
     JButton btnRealUpdateStaff = new JButton("Save Changes"); 
     btnRealUpdateStaff.setBounds(420, 390, 320, 45);
     updateStaffPanel.add(btnRealUpdateStaff);

     btnRealUpdateStaff.addActionListener(e -> {
         try {
             // Girdileri alır
             String idStr = txtUSId.getText().trim();
             String newPass = txtUSPass.getText().trim();
             String newSalStr = txtUSSal.getText().trim();

             if(idStr.isEmpty() || newPass.isEmpty() || newSalStr.isEmpty()) {
                 JOptionPane.showMessageDialog(this, "Please fill in all fields (ID, Password, Salary).");
                 return;
             }

             int staffId = Integer.parseInt(idStr);
             double newSalary = Double.parseDouble(newSalStr);
             
             if (newSalary < 0) {
                 JOptionPane.showMessageDialog(this, "Salary cannot be negative!");
                 return; // Eksi değer varsa işlemi durdurur
             }

             User targetUser = userManager.getUserById(staffId);

             if(targetUser != null && targetUser instanceof Staff) {
                 Staff targetStaff = (Staff) targetUser;
                 
                 targetStaff.setPassword(newPass);
                 targetStaff.setSalary(newSalary);
                 
                 boolean success = userManager.updateUser(targetStaff);
                 
                 if (success) {
                     JOptionPane.showMessageDialog(this, "Staff updated successfully!\nNew Salary: " + newSalary);
                     txtUSId.setText("");
                     txtUSPass.setText("");
                     txtUSSal.setText("");
                 } else {
                     JOptionPane.showMessageDialog(this, "Update failed. ", "Error", JOptionPane.ERROR_MESSAGE);
                 }
             } else {
                 JOptionPane.showMessageDialog(this, "Staff ID not found or User is not a Staff member!", "Not Found", JOptionPane.WARNING_MESSAGE);
             }

         } catch (NumberFormatException ex) {
             JOptionPane.showMessageDialog(this, "ID must be an integer and Salary must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
         } catch (Exception ex) {
             JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
         }
     });

     // Paneli CardLayout sistemine ekler
     adminContentPanel.add(updateStaffPanel, "updateStaff");

     // Menü butonuna aksiyon ekler
     btnUpdateStaff.addActionListener(ev -> 
         ((CardLayout) adminContentPanel.getLayout()).show(adminContentPanel, "updateStaff")
     );
        
        

        // 4. ADMIN SEE FLIGHTS
        adminSeeFlightsPanel = new JPanel(new BorderLayout()); 
        adminSeeFlightsPanel.setBorder(BorderFactory.createTitledBorder("All Flights (Admin View)"));
        
        String[] colsF = {"Num", "Route", "Date", "Hour", "Duration", "Plane Info", "Capacity", "Status"};
        DefaultTableModel modelF = new DefaultTableModel(colsF, 0);
        JTable tableF = new JTable(modelF);
        adminSeeFlightsPanel.add(new JScrollPane(tableF), BorderLayout.CENTER); 
        adminContentPanel.add(adminSeeFlightsPanel, "adminSeeFlights");
        
        btnAdminSeeFlights.addActionListener(e -> {
             modelF.setRowCount(0);
             
             // Şimdiki zamanı alır
             java.time.LocalDateTime now = java.time.LocalDateTime.now();

             for(Flight f : flightManager.getAllFlights()) {
                Plane p = f.getPlane();
                String planeInfo = p.getPlaneModel() + " (" + p.getPlaneID() + ")";
                
                // --- Zaman Kontrolü ---
                String status = "ACTIVE";
                java.time.LocalDateTime flightTime = java.time.LocalDateTime.of(f.getDate(), f.getHour());
                
                if (flightTime.isBefore(now)) {
                    status = "COMPLETED";
                }

                modelF.addRow(new Object[]{
                    f.getFlightNum(), 
                    f.getDeparturePlace() + "->" + f.getArrivalPlace(), 
                    f.getDate(), 
                    f.getHour(), 
                    f.getDurationMinutes() + " min",
                    planeInfo, 
                    p.getCapacity(),
                    status 
                });
            }
            ((CardLayout) adminContentPanel.getLayout()).show(adminContentPanel, "adminSeeFlights");
        });
        
        // 5. ADMIN SEE TICKETS
        adminSeeTicketsPanel = new JPanel(new BorderLayout());
        adminSeeTicketsPanel.setBorder(BorderFactory.createTitledBorder("All Tickets System-Wide (Admin)"));
        
        String[] colsT = {
            "Ticket ID", "Flight Num", "Passenger Info", "Seat", "Date", "Hour", "Price", "Baggage", "Route", "Status"
        };
        DefaultTableModel modelT = new DefaultTableModel(colsT, 0);
        JTable tableT = new JTable(modelT);
        tableT.getColumnModel().getColumn(2).setPreferredWidth(150); 
        tableT.getColumnModel().getColumn(8).setPreferredWidth(120);

        adminSeeTicketsPanel.add(new JScrollPane(tableT), BorderLayout.CENTER);
        adminContentPanel.add(adminSeeTicketsPanel, "adminSeeTickets");
        
        btnAdminSeeTickets.addActionListener(e -> {
             modelT.setRowCount(0);
             java.util.List<Ticket> allTickets = TicketFileManager.loadAllTickets();
             java.time.LocalDateTime now = java.time.LocalDateTime.now();
             
             for(Ticket t : allTickets) {
                 if (t.getReservation() != null) {
                	 Flight f = t.getReservation().getFlight();
                	 Passenger p = t.getReservation().getPassenger();
                	 String passInfo = "N/A";
                	 if(p != null) passInfo = String.format("%s %s (ID:%d) Tel:%s", p.getName(), p.getSurname(), p.getPassengerID(), p.getContactInfo());
                 
                	 String status = "ACTIVE";
                	 if(f != null) {
                		 java.time.LocalDateTime fTime = java.time.LocalDateTime.of(f.getDate(), f.getHour());
                		 if (t.isCancelled()) status = "CANCELLED";
                		 else if (fTime.isBefore(now)) status = "COMPLETED";
                	 }

                	 modelT.addRow(new Object[]{
                		 t.getTicketID(), (f != null ? f.getFlightNum() : "?"), passInfo,
                		 t.getReservation().getSeat().getSeatNum(), (f != null ? f.getDate() : "?"), (f != null ? f.getHour() : "?"),
                		 t.getPrice() + " TL", (t.getBaggage() != null ? t.getBaggage().getWeight() : 0.0) + " kg",
                		 (f != null ? f.getDeparturePlace() + "->" + f.getArrivalPlace() : "?"), status
                	 });
                 }
             }
             ((CardLayout) adminContentPanel.getLayout()).show(adminContentPanel, "adminSeeTickets");
        });

        // 5.5 ADMIN ADD PLANE
        adminAddPlanePanel = new JPanel(null);
        adminAddPlanePanel.setBorder(BorderFactory.createTitledBorder("Add New Plane (Admin)"));

        JLabel apl1 = new JLabel("Plane ID:"); apl1.setBounds(250, 200, 160, 40); adminAddPlanePanel.add(apl1);
        JTextField apId = new JTextField(); apId.setBounds(420, 200, 320, 40); adminAddPlanePanel.add(apId);

        JLabel apl2 = new JLabel("Plane Model:"); apl2.setBounds(250, 260, 160, 40); adminAddPlanePanel.add(apl2);
        JTextField apModel = new JTextField(); apModel.setBounds(420, 260, 320, 40); adminAddPlanePanel.add(apModel);

        JLabel apl3 = new JLabel("Capacity:"); apl3.setBounds(250, 320, 160, 40); adminAddPlanePanel.add(apl3);
        JTextField apCap = new JTextField(); apCap.setBounds(420, 320, 320, 40); adminAddPlanePanel.add(apCap);

        JButton btnAdminPlaneSave = new JButton("Save Plane");
        btnAdminPlaneSave.setBounds(420, 390, 320, 45);
        adminAddPlanePanel.add(btnAdminPlaneSave);

        btnAdminPlaneSave.addActionListener(ev -> {
            String id = apId.getText().trim();
            String model = apModel.getText().trim();
            String capStr = apCap.getText().trim();

            if (id.isEmpty() || model.isEmpty() || capStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.");
                return;
            }
            int cap;
            try {
                cap = Integer.parseInt(capStr);
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Capacity must be numeric.");
                return;
            }
            if (cap <= 0) {
                JOptionPane.showMessageDialog(this, "Capacity must be greater than 0.");
                return;
            }

            boolean ok = planeManager.addPlane(id, model, cap, 0.0);
            if (!ok) {
                JOptionPane.showMessageDialog(this, "This Plane ID is already in use!");
                return;
            }

            addPlaneToAllCombos(id);
            JOptionPane.showMessageDialog(this, "Plane added!");
            apId.setText(""); apModel.setText(""); apCap.setText("");
        });

        adminContentPanel.add(adminAddPlanePanel, "adminAddPlane");
        btnAdminAddPlane.addActionListener(ev -> ((CardLayout) adminContentPanel.getLayout()).show(adminContentPanel, "adminAddPlane"));
        
        // 6. ADMIN ADD FLIGHT
        adminAddFlightPanel = new JPanel(null);
        adminAddFlightPanel.setBorder(BorderFactory.createTitledBorder("Add Flight (Admin)"));


        JLabel a0 = new JLabel("Plane:"); a0.setBounds(250, 30, 160, 40); adminAddFlightPanel.add(a0);
        JComboBox<PlaneItem> cbPlaneA = new JComboBox<>();
        cbPlaneA.setBounds(420, 30, 320, 40);
        adminAddFlightPanel.add(cbPlaneA);
        registerPlaneCombo(cbPlaneA);

        JLabel l3A = new JLabel("Flight Num:"); l3A.setBounds(250, 85, 160, 40); adminAddFlightPanel.add(l3A);
        JTextField tNumA = new JTextField(); tNumA.setBounds(420, 85, 320, 40); adminAddFlightPanel.add(tNumA);

        JLabel l4A = new JLabel("From:"); l4A.setBounds(250, 140, 160, 40); adminAddFlightPanel.add(l4A);
        JTextField tFromA = new JTextField(); tFromA.setBounds(420, 140, 320, 40); adminAddFlightPanel.add(tFromA);

        JLabel l5A = new JLabel("To:"); l5A.setBounds(250, 195, 160, 40); adminAddFlightPanel.add(l5A);
        JTextField tToA = new JTextField(); tToA.setBounds(420, 195, 320, 40); adminAddFlightPanel.add(tToA);

        JLabel lDist = new JLabel("Distance (km):"); lDist.setBounds(250, 250, 160, 40); adminAddFlightPanel.add(lDist);
        JTextField tDist = new JTextField(); tDist.setBounds(420, 250, 320, 40); adminAddFlightPanel.add(tDist);

        JLabel l6A = new JLabel("Date:"); l6A.setBounds(250, 305, 160, 40); adminAddFlightPanel.add(l6A);
        JSpinner spinDateA = new JSpinner(new SpinnerDateModel()); spinDateA.setBounds(420, 305, 320, 40);
        spinDateA.setEditor(new JSpinner.DateEditor(spinDateA, "dd.MM.yyyy")); adminAddFlightPanel.add(spinDateA);

        JLabel l7A = new JLabel("Time:"); l7A.setBounds(250, 360, 160, 40); adminAddFlightPanel.add(l7A);
        JSpinner spinTimeA = new JSpinner(new SpinnerDateModel()); spinTimeA.setBounds(420, 360, 320, 40);
        spinTimeA.setEditor(new JSpinner.DateEditor(spinTimeA, "HH:mm")); adminAddFlightPanel.add(spinTimeA);

        JLabel l8A = new JLabel("Duration (min):"); l8A.setBounds(250, 415, 160, 40); adminAddFlightPanel.add(l8A);
        JTextField tDurA = new JTextField(); tDurA.setBounds(420, 415, 320, 40); adminAddFlightPanel.add(tDurA);

        JLabel l9A = new JLabel("Base Price:"); l9A.setBounds(250, 470, 160, 40); adminAddFlightPanel.add(l9A);
        JTextField tBaseA = new JTextField(); tBaseA.setBounds(420, 470, 320, 40); adminAddFlightPanel.add(tBaseA);

        JButton btnAddRealA = new JButton("Create Flight");
        btnAddRealA.setBounds(420, 540, 320, 45);
        adminAddFlightPanel.add(btnAddRealA);

        btnAddRealA.addActionListener(e -> {
            try {
                String flightNum = tNumA.getText().trim();
                String from = tFromA.getText().trim();
                String to = tToA.getText().trim();
                String durStr = tDurA.getText().trim();
                String distStr = tDist.getText().trim();
                String baseStr = tBaseA.getText().trim();

                if (flightNum.isEmpty() || from.isEmpty() || to.isEmpty() || durStr.isEmpty() || distStr.isEmpty() || baseStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill in all fields.");
                    return;
                }
                if (from.equalsIgnoreCase(to)) {
                    JOptionPane.showMessageDialog(this, "From and To cannot be the same.");
                    return;
                }
                if (flightManager.getFlightByNumber(flightNum) != null) {
                    JOptionPane.showMessageDialog(this, "Flight Num exists!");
                    return;
                }

                int duration;
                double distance;
                double basePrice;
                try {
                    duration = Integer.parseInt(durStr);
                    distance = Double.parseDouble(distStr);
                    basePrice = Double.parseDouble(baseStr);
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(this, "Duration/Distance/Base Price must be numeric.");
                    return;
                }
                if (duration <= 0 || distance <= 0 || basePrice <= 0) {
                    JOptionPane.showMessageDialog(this, "Duration/Distance/Base Price must be greater than 0.");
                    return;
                }

                java.time.LocalDate d = ((Date)spinDateA.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                java.time.LocalTime t = ((Date)spinTimeA.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime();
                java.time.LocalDateTime flightDT = java.time.LocalDateTime.of(d, t);

                if (!flightDT.isAfter(java.time.LocalDateTime.now())) {
                    JOptionPane.showMessageDialog(this, "Cannot add a flight to a past date.");
                    return;
                }

                String prevSel = (cbPlaneA.getSelectedItem() instanceof PlaneItem) ? ((PlaneItem)cbPlaneA.getSelectedItem()).planeId : "";
                refreshPlaneComboForSlot(cbPlaneA, d, t, duration, null, false);

                if (prevSel != null && !prevSel.isEmpty()) {
                    boolean selectionFound = false;
                    for (int i = 0; i < cbPlaneA.getItemCount() && !selectionFound; i++) {
                        PlaneItem it = cbPlaneA.getItemAt(i);
                        if (it != null && prevSel.equalsIgnoreCase(it.planeId)) {
                            cbPlaneA.setSelectedIndex(i);
                            selectionFound = true;
                        }
                    }
                }

                PlaneItem sel = (PlaneItem) cbPlaneA.getSelectedItem();
                if (sel == null || sel.planeId == null || sel.planeId.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No available planes for this date/time. (Add a plane first using Add Plane.)");
                    return;
                }

                Plane template = planeManager.getPlaneByID(sel.planeId);
                if (template == null) {
                    JOptionPane.showMessageDialog(this, "Selected plane not found.");
                    return;
                }

                java.time.LocalDateTime slotStart = java.time.LocalDateTime.of(d, t);
                java.time.LocalDateTime slotEnd   = slotStart.plusMinutes(duration);
                if (!isPlaneAvailableForSlot(sel.planeId, slotStart, slotEnd, null)) {
                    JOptionPane.showMessageDialog(this, "This plane is on another flight during this time slot!");
                    return;
                }

                Plane pForFlight = template;
                Route r = new Route(from, to, distance);
                Flight f = new Flight(flightNum, r, d, t, duration, from, to, pForFlight);
                f.setPrice(basePrice);

                boolean ok = flightManager.createFlight(f);
                if(ok) JOptionPane.showMessageDialog(this, "Flight Created Successfully!");
                else JOptionPane.showMessageDialog(this, "Could not create flight!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        adminContentPanel.add(adminAddFlightPanel, "adminAddFlight");
        btnAdminAddFlight.addActionListener(e -> {
            try {
                java.time.LocalDate d = ((Date)spinDateA.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                java.time.LocalTime t = ((Date)spinTimeA.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime();
                int dur = Integer.parseInt(tDurA.getText().trim());
                if (dur > 0) refreshPlaneComboForSlot(cbPlaneA, d, t, dur, null, false);
            } catch (Exception ignore) {}
            ((CardLayout) adminContentPanel.getLayout()).show(adminContentPanel, "adminAddFlight");
        });

        // 7. ADMIN DELETE FLIGHT
        adminDeleteFlightPanel = new JPanel(null);
        adminDeleteFlightPanel.setBorder(BorderFactory.createTitledBorder("Delete Flight (Admin)"));
        JLabel dlA = new JLabel("Flight Num:"); dlA.setBounds(250, 250, 160, 40); adminDeleteFlightPanel.add(dlA);
        JTextField dtA = new JTextField(); dtA.setBounds(420, 250, 320, 40); adminDeleteFlightPanel.add(dtA);
        JButton dbA = new JButton("Delete"); dbA.setBounds(420, 320, 320, 45); adminDeleteFlightPanel.add(dbA);
        
        dbA.addActionListener(e -> {
            boolean done = flightManager.deleteFlight(dtA.getText(), reservationManager);
            if(done) JOptionPane.showMessageDialog(this, "Flight Deleted!");
            else JOptionPane.showMessageDialog(this, "Not Found!");
        });
        adminContentPanel.add(adminDeleteFlightPanel, "adminDeleteFlight");
        btnAdminDeleteFlight.addActionListener(e -> ((CardLayout) adminContentPanel.getLayout()).show(adminContentPanel, "adminDeleteFlight"));

        // 8. ADMIN UPDATE FLIGHT
        adminUpdateFlightPanel = new JPanel(null);
        adminUpdateFlightPanel.setBorder(BorderFactory.createTitledBorder("Update Flight (Admin)"));
        JLabel ul1A = new JLabel("Flight Num:"); ul1A.setBounds(250, 150, 160, 40); adminUpdateFlightPanel.add(ul1A);
        JTextField ut1A = new JTextField(); ut1A.setBounds(420, 150, 320, 40); adminUpdateFlightPanel.add(ut1A);

        JLabel ul2A = new JLabel("New Date:"); ul2A.setBounds(250, 210, 160, 40); adminUpdateFlightPanel.add(ul2A);
        JSpinner usDA = new JSpinner(new SpinnerDateModel()); usDA.setBounds(420, 210, 320, 40);
        usDA.setEditor(new JSpinner.DateEditor(usDA, "dd.MM.yyyy")); adminUpdateFlightPanel.add(usDA);

        JLabel ul3A = new JLabel("New Time:"); ul3A.setBounds(250, 270, 160, 40); adminUpdateFlightPanel.add(ul3A);
        JSpinner usTA = new JSpinner(new SpinnerDateModel()); usTA.setBounds(420, 270, 320, 40);
        usTA.setEditor(new JSpinner.DateEditor(usTA, "HH:mm")); adminUpdateFlightPanel.add(usTA);

        JLabel ul4A = new JLabel("New Plane:"); ul4A.setBounds(250, 330, 160, 40); adminUpdateFlightPanel.add(ul4A);
        JComboBox<PlaneItem> cbNewPlaneA = new JComboBox<>();
        cbNewPlaneA.setBounds(420, 330, 320, 40);
        cbNewPlaneA.addItem(new PlaneItem("", "-- Keep Current --"));
        adminUpdateFlightPanel.add(cbNewPlaneA);
        registerPlaneCombo(cbNewPlaneA);

        JButton ubA = new JButton("Update"); ubA.setBounds(420, 400, 320, 45); adminUpdateFlightPanel.add(ubA);

        ubA.addActionListener(e -> {
            Flight f = flightManager.getFlightByNumber(ut1A.getText());
            if(f == null) {
                JOptionPane.showMessageDialog(this, "Flight Not Found");
                return;
            }

            java.time.LocalDate d = ((Date)usDA.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            java.time.LocalTime t = ((Date)usTA.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime();
            java.time.LocalDateTime newDT = java.time.LocalDateTime.of(d, t);

            if (!newDT.isAfter(java.time.LocalDateTime.now())) {
                JOptionPane.showMessageDialog(this, "Cannot update flight to a past date.");
                return;
            }


            int duration = f.getDurationMinutes();

            String prevSel = (cbNewPlaneA.getSelectedItem() instanceof PlaneItem) ? ((PlaneItem)cbNewPlaneA.getSelectedItem()).planeId : "";
            refreshPlaneComboForSlot(cbNewPlaneA, d, t, duration, f.getFlightNum(), true);

            if (prevSel != null && !prevSel.isEmpty()) {
                boolean selectionFound = false;
                for (int i = 0; i < cbNewPlaneA.getItemCount() && !selectionFound; i++) {
                    PlaneItem it = cbNewPlaneA.getItemAt(i);
                    if (it != null && prevSel.equalsIgnoreCase(it.planeId)) {
                        cbNewPlaneA.setSelectedIndex(i);
                        selectionFound = true;
                    }
                }
            }

            PlaneItem sel = (PlaneItem) cbNewPlaneA.getSelectedItem();

            String targetPlaneId;
            if (sel != null && sel.planeId != null && !sel.planeId.isEmpty()) {
                targetPlaneId = sel.planeId;
            } else {
                targetPlaneId = (f.getPlane() != null ? f.getPlane().getPlaneID() : null);
            }
            if (targetPlaneId == null || targetPlaneId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Plane could not be selected / current plane not found.");
                return;
            }
            java.time.LocalDateTime slotStart = java.time.LocalDateTime.of(d, t);
            java.time.LocalDateTime slotEnd   = slotStart.plusMinutes(duration);
            if (!isPlaneAvailableForSlot(targetPlaneId, slotStart, slotEnd, f.getFlightNum())) {
                JOptionPane.showMessageDialog(this, "This plane is on another flight during this time slot!");
                return;
            }

            //Kapasite Kontrolü ve Bilet İptali
            // (Uçak değişirse ve kapasitesi azalırsa kapasite dışında kalan bilet iptal edilir.)
            if (sel != null && sel.planeId != null && !sel.planeId.isEmpty()) {
                Plane template = planeManager.getPlaneByID(sel.planeId);
                if (template == null) {
                    JOptionPane.showMessageDialog(this, "Selected plane not found.");
                    return;
                }
                
                // Kapasite düşüyorsa kontrol et
                if (template.getCapacity() < f.getPlane().getCapacity()) {
                    List<Ticket> allTickets = TicketFileManager.loadAllTickets();
                    boolean changed = false;
                    int cancelCount = 0;
                    
                    Set<String> validSeats = template.getSeatMatrix().keySet();
                    
                    for (Ticket tk : allTickets) {
                        // İlgili uçuşun aktif biletlerini bul
                        if (tk.getReservation() != null && 
                            tk.getReservation().getFlight() != null &&
                            tk.getReservation().getFlight().getFlightNum().equals(f.getFlightNum()) && 
                            !tk.isCancelled()) {
                            
                            String seatNum = tk.getReservation().getSeat().getSeatNum();
                            
                            // Eğer koltuk yeni uçakta yoksa iptal et
                            if (!validSeats.contains(seatNum)) {
                                tk.cancelTicket();
                                cancelCount++;
                                changed = true;
                            }
                        }
                    }
                    
                    if (changed) {
                        TicketFileManager.saveAllTickets(allTickets);
                        JOptionPane.showMessageDialog(this, 
                        		"Aircraft capacity has been reduced. " + cancelCount + " tickets that exceeded the new capacity have been automatically cancelled.",
                        		"Automatic Cancellation", JOptionPane.INFORMATION_MESSAGE);
                    }
                }

                Plane pForFlight = template;
                f.setPlane(pForFlight);
            }

            f.setDate(d);
            f.setHour(t);
            flightManager.updateFlight(f);

            TicketFileManager.updateTicketsForFlight(f);
            JOptionPane.showMessageDialog(this, "Updated!");
        });

        adminContentPanel.add(adminUpdateFlightPanel, "adminUpdateFlight");
        btnAdminUpdateFlight.addActionListener(e -> ((CardLayout) adminContentPanel.getLayout()).show(adminContentPanel, "adminUpdateFlight"));

        
        
        
     // --- Admin Rapor Paneli ---
        adminReportPanel = new JPanel(new BorderLayout());
        adminReportPanel.setBorder(BorderFactory.createTitledBorder("System Occupancy Report (Admin View)"));
        
        JTextArea txtAdminReport = new JTextArea();
        txtAdminReport.setEditable(false);
        txtAdminReport.setFont(new Font("Monospaced", Font.PLAIN, 12));
        adminReportPanel.add(new JScrollPane(txtAdminReport), BorderLayout.CENTER);
        
        JLabel lblAdminStatus = new JLabel("Click 'Generate' to start report.");
        lblAdminStatus.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        adminReportPanel.add(lblAdminStatus, BorderLayout.NORTH);
        
        JButton btnGenAdmin = new JButton("Generate Report Now");
        adminReportPanel.add(btnGenAdmin, BorderLayout.SOUTH);
        
        // Raporu Başlatan Action Listener
        btnGenAdmin.addActionListener(e -> {
            reportManager.generateOccupancyReportAsync(flightManager.getAllFlights(), new ReportSimulation.ReportCallback() {
                @Override
                public void onReportStart() {
                    SwingUtilities.invokeLater(() -> {
                        lblAdminStatus.setText("Preparing report... Please wait."); 
                        lblAdminStatus.setForeground(Color.RED);
                        txtAdminReport.setText("");
                        btnGenAdmin.setEnabled(false);
                    });
                }

                @Override
                public void onReportReady(String reportResult) {
                    SwingUtilities.invokeLater(() -> {
                        lblAdminStatus.setText("Report Generated Successfully.");
                        lblAdminStatus.setForeground(new Color(46, 204, 113));
                        txtAdminReport.setText(reportResult);
                        btnGenAdmin.setEnabled(true);
                    });
                }
            });
        });
        
        adminContentPanel.add(adminReportPanel, "adminReportPanel");
        
        // Menü butonuna tıklayınca paneli gösterir
        btnAdminReport.addActionListener(e -> 
            ((CardLayout) adminContentPanel.getLayout()).show(adminContentPanel, "adminReportPanel")
        );
        
        
        
        
        // LOGOUT
        btnAdminLogout.addActionListener(e -> {
            currentUser = null;
            ((CardLayout) mainPanel.getLayout()).show(mainPanel, "login");
        });
    }

    private void updateScroll(JScrollPane sp) {
        mainContent.revalidate();
        SwingUtilities.invokeLater(() -> sp.getVerticalScrollBar().setValue(sp.getVerticalScrollBar().getMaximum()));
    }
}