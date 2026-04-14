package com.crime.ui;

import com.crime.dao.CaseDAO;
import com.crime.dao.EvidenceDAO;
import com.crime.dao.SuspectDAO;
import com.crime.model.Case;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class DashboardPanel extends Panel {

    private MainFrame parent;
    private CaseDAO caseDAO         = new CaseDAO();
    private SuspectDAO suspectDAO   = new SuspectDAO();
    private EvidenceDAO evidenceDAO = new EvidenceDAO();

    private Label lblOpen, lblInvestigating, lblClosed, lblSuspects, lblEvidence;
    private java.awt.List recentList;

    private static final Color BG      = new Color(15, 20, 35);
    private static final Color CARD_BG = new Color(25, 35, 60);
    private static final Color RED     = new Color(220, 60, 60);
    private static final Color YELLOW  = new Color(255, 180, 0);
    private static final Color GREEN   = new Color(60, 200, 100);
    private static final Color BLUE    = new Color(60, 130, 255);
    private static final Color PURPLE  = new Color(160, 80, 255);

    public DashboardPanel(MainFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout(15, 15));
        setBackground(BG);
        buildUI();
    }

    private void buildUI() {
        // Header
        Panel header = new Panel(new BorderLayout());
        header.setBackground(BG);
        Label title = new Label("  COMMAND DASHBOARD", Label.LEFT);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setBackground(new Color(40, 55, 90));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.addActionListener(e -> refresh());
        Panel bw = new Panel(new FlowLayout(FlowLayout.RIGHT));
        bw.setBackground(BG); bw.add(refreshBtn);
        header.add(bw, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Stat Cards
        Panel cardsRow = new Panel(new GridLayout(1, 5, 12, 0));
        cardsRow.setBackground(BG);
        cardsRow.setPreferredSize(new Dimension(0, 120));
        lblOpen          = new Label("0", Label.CENTER);
        lblInvestigating = new Label("0", Label.CENTER);
        lblClosed        = new Label("0", Label.CENTER);
        lblSuspects      = new Label("0", Label.CENTER);
        lblEvidence      = new Label("0", Label.CENTER);
        cardsRow.add(makeCard("OPEN",         lblOpen,          RED));
        cardsRow.add(makeCard("INVESTIGATING", lblInvestigating, YELLOW));
        cardsRow.add(makeCard("CLOSED",        lblClosed,        GREEN));
        cardsRow.add(makeCard("SUSPECTS",      lblSuspects,      BLUE));
        cardsRow.add(makeCard("EVIDENCE",      lblEvidence,      PURPLE));

        // Center
        Panel center = new Panel(new BorderLayout(10, 10));
        center.setBackground(BG);
        center.add(cardsRow, BorderLayout.NORTH);

        Panel recentPanel = new Panel(new BorderLayout(5, 5));
        recentPanel.setBackground(CARD_BG);
        Label rl = new Label("  Recent Cases", Label.LEFT);
        rl.setFont(new Font("SansSerif", Font.BOLD, 14));
        rl.setForeground(Color.WHITE);
        recentPanel.add(rl, BorderLayout.NORTH);
        recentList = new java.awt.List(10);
        recentList.setBackground(new Color(18, 26, 48));
        recentList.setForeground(new Color(200, 210, 255));
        recentList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        recentPanel.add(recentList, BorderLayout.CENTER);

        Panel qa = new Panel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        qa.setBackground(CARD_BG);
        Button b1 = makeBtn("Add New Case",         new Color(60, 180, 100));
        Button b2 = makeBtn("Investigation Board",  new Color(130, 60, 220));
        Button b3 = makeBtn("View Suspects",        new Color(60, 130, 220));
        b1.addActionListener(e -> parent.navigateTo("CASES"));
        b2.addActionListener(e -> parent.navigateTo("BOARD"));
        b3.addActionListener(e -> parent.navigateTo("SUSPECTS"));
        qa.add(b1); qa.add(b2); qa.add(b3);
        recentPanel.add(qa, BorderLayout.SOUTH);
        center.add(recentPanel, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
        refresh();
    }

    public void refresh() {
        int[] counts = caseDAO.getStatusCounts();
        lblOpen.setText(String.valueOf(counts[0]));
        lblInvestigating.setText(String.valueOf(counts[1]));
        lblClosed.setText(String.valueOf(counts[2]));
        lblSuspects.setText(String.valueOf(suspectDAO.getTotalCount()));
        lblEvidence.setText(String.valueOf(evidenceDAO.getTotalCount()));
        recentList.removeAll();
        for (Case c : caseDAO.getAllCases())
            recentList.add("  " + c.getCaseNumber() + "   " + c.getTitle() + "   Officer: " + c.getOfficerName());
    }

    private Panel makeCard(String label, Label val, Color color) {
        Panel card = new Panel(new BorderLayout(5, 5));
        card.setBackground(new Color(25, 35, 60));
        val.setFont(new Font("SansSerif", Font.BOLD, 36));
        val.setForeground(color);
        card.add(val, BorderLayout.CENTER);
        Label l = new Label(label, Label.CENTER);
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        l.setForeground(new Color(160, 170, 210));
        card.add(l, BorderLayout.SOUTH);
        return card;
    }

    private Button makeBtn(String t, Color bg) {
        Button b = new Button(t); b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); return b;
    }
}