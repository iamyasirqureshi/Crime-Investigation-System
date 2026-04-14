package com.crime.ui;

import java.awt.*;
import java.awt.event.*;

public class MainFrame extends Frame {

    private Panel contentPanel;
    private CardLayout cardLayout;
    private DashboardPanel dashboardPanel;
    private CasesPanel casesPanel;
    private SuspectsPanel suspectsPanel;
    private EvidencePanel evidencePanel;
    private InvestigationBoard investigationBoard;
    private Button activeBtn;

    private static final Color BG_DARK   = new Color(15, 20, 35);
    private static final Color SIDEBAR_BG = new Color(20, 28, 50);
    private static final Color ACCENT     = new Color(220, 50, 50);

    public MainFrame() {
        super("Crime Record Investigation System  |  CRIS v1.0");
        initUI();
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { System.exit(0); }
        });
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        // TOP BAR
        Panel topBar = new Panel(new BorderLayout());
        topBar.setBackground(new Color(10, 14, 28));
        topBar.setPreferredSize(new Dimension(0, 50));
        Label title = new Label("   CRIME RECORD INVESTIGATION SYSTEM", Label.LEFT);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(ACCENT);
        topBar.add(title, BorderLayout.WEST);
        Label badge = new Label("  LIVE SYSTEM   ", Label.RIGHT);
        badge.setFont(new Font("SansSerif", Font.PLAIN, 12));
        badge.setForeground(new Color(80, 200, 120));
        topBar.add(badge, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // SIDEBAR
        Panel sidebar = new Panel(new GridLayout(7, 1, 0, 2));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(200, 0));
        Label logo = new Label("  CRIS PANEL", Label.LEFT);
        logo.setFont(new Font("SansSerif", Font.BOLD, 13));
        logo.setForeground(new Color(150, 160, 200));
        sidebar.add(logo);

        Button btnDash      = makeSideBtn("  DASHBOARD");
        Button btnCases     = makeSideBtn("  CASES");
        Button btnSuspects  = makeSideBtn("  SUSPECTS");
        Button btnEvidence  = makeSideBtn("  EVIDENCE");
        Button btnBoard     = makeSideBtn("  INVESTIGATION BOARD");

        sidebar.add(btnDash);
        sidebar.add(btnCases);
        sidebar.add(btnSuspects);
        sidebar.add(btnEvidence);
        sidebar.add(btnBoard);
        Panel filler = new Panel();
        filler.setBackground(SIDEBAR_BG);
        sidebar.add(filler);
        add(sidebar, BorderLayout.WEST);

        // CONTENT
        cardLayout   = new CardLayout();
        contentPanel = new Panel(cardLayout);
        contentPanel.setBackground(BG_DARK);

        dashboardPanel     = new DashboardPanel(this);
        casesPanel         = new CasesPanel(this);
        suspectsPanel      = new SuspectsPanel(this);
        evidencePanel      = new EvidencePanel(this);
        investigationBoard = new InvestigationBoard();

        contentPanel.add(dashboardPanel,     "DASHBOARD");
        contentPanel.add(casesPanel,         "CASES");
        contentPanel.add(suspectsPanel,      "SUSPECTS");
        contentPanel.add(evidencePanel,      "EVIDENCE");
        contentPanel.add(investigationBoard, "BOARD");
        add(contentPanel, BorderLayout.CENTER);

        // Button actions
        btnDash.addActionListener(e     -> showCard("DASHBOARD", btnDash));
        btnCases.addActionListener(e    -> showCard("CASES", btnCases));
        btnSuspects.addActionListener(e -> showCard("SUSPECTS", btnSuspects));
        btnEvidence.addActionListener(e -> showCard("EVIDENCE", btnEvidence));
        btnBoard.addActionListener(e    -> showCard("BOARD", btnBoard));

        showCard("DASHBOARD", btnDash);
    }

    public void showCard(String card, Button source) {
        cardLayout.show(contentPanel, card);
        if (activeBtn != null) {
            activeBtn.setBackground(new Color(20, 28, 50));
            activeBtn.setForeground(new Color(180, 190, 220));
        }
        activeBtn = source;
        activeBtn.setBackground(ACCENT);
        activeBtn.setForeground(Color.WHITE);
        if ("BOARD".equals(card)) investigationBoard.refresh();
        if ("DASHBOARD".equals(card)) dashboardPanel.refresh();
    }

    public void navigateTo(String card) { cardLayout.show(contentPanel, card); }
    public void refreshCases()    { casesPanel.loadData(); }
    public void refreshSuspects() { suspectsPanel.loadData(); }
    public void refreshEvidence() { evidencePanel.loadData(); }

    private Button makeSideBtn(String label) {
        Button btn = new Button(label);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setBackground(new Color(20, 28, 50));
        btn.setForeground(new Color(180, 190, 220));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}