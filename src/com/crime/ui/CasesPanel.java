package com.crime.ui;

import com.crime.dao.CaseDAO;
import com.crime.model.Case;
import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.util.List;

public class CasesPanel extends Panel {

    private MainFrame parent;
    private CaseDAO caseDAO = new CaseDAO();
    private java.awt.List caseList;
    private List<Case> cases;

    private static final Color BG      = new Color(15, 20, 35);
    private static final Color CARD_BG = new Color(25, 35, 60);

    public CasesPanel(MainFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout(10, 10));
        setBackground(BG);
        buildUI();
    }

    private void buildUI() {
        Panel header = new Panel(new BorderLayout());
        header.setBackground(BG);
        Label title = new Label("  CASE MANAGEMENT", Label.LEFT);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        Panel btnPanel = new Panel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        btnPanel.setBackground(BG);
        Button addBtn    = makeBtn("Add Case",       new Color(60, 180, 100));
        Button statusBtn = makeBtn("Update Status",  new Color(60, 130, 220));
        Button deleteBtn = makeBtn("Delete",         new Color(200, 60, 60));
        Button refreshBtn= makeBtn("Refresh",        new Color(70, 80, 110));
        addBtn.addActionListener(e    -> showAddDialog());
        statusBtn.addActionListener(e -> updateStatus());
        deleteBtn.addActionListener(e -> deleteSelected());
        refreshBtn.addActionListener(e-> loadData());
        btnPanel.add(addBtn); btnPanel.add(statusBtn);
        btnPanel.add(deleteBtn); btnPanel.add(refreshBtn);
        header.add(btnPanel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        caseList = new java.awt.List(20);
        caseList.setBackground(CARD_BG);
        caseList.setForeground(new Color(200, 210, 255));
        caseList.setFont(new Font("Monospaced", Font.PLAIN, 13));
        add(caseList, BorderLayout.CENTER);

        Panel legend = new Panel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        legend.setBackground(BG);
        legend.add(makeLabel("  OPEN",         new Color(220, 80, 80)));
        legend.add(makeLabel("  INVESTIGATING", new Color(255, 200, 60)));
        legend.add(makeLabel("  CLOSED",        new Color(80, 200, 100)));
        add(legend, BorderLayout.SOUTH);
        loadData();
    }

    public void loadData() {
        caseList.removeAll();
        cases = caseDAO.getAllCases();
        for (Case c : cases) {
            String icon = switch (c.getStatus()) {
                case "OPEN"          -> "[OPEN]";
                case "INVESTIGATING" -> "[INVESTIGATING]";
                case "CLOSED"        -> "[CLOSED]";
                default              -> "[?]";
            };
            caseList.add(String.format("  %-16s  %-16s  %-35s  %s  Officer: %s",
                    icon, c.getCaseNumber(), c.getTitle(), c.getLocation(), c.getOfficerName()));
        }
    }

    private void showAddDialog() {
        Dialog dialog = new Dialog(getFrame(), "Add New Case", true);
        dialog.setLayout(new GridLayout(9, 2, 8, 8));
        dialog.setBackground(new Color(25, 35, 60));
        dialog.setSize(500, 420);
        dialog.setLocationRelativeTo(this);

        Font lf = new Font("SansSerif", Font.BOLD, 12);
        Color lc = new Color(180, 190, 230);

        TextField tfNum     = makeField();
        TextField tfTitle   = makeField();
        TextField tfLoc     = makeField();
        TextField tfOfficer = makeField();
        TextField tfDate    = makeField();
        tfDate.setText(new Date(System.currentTimeMillis()).toString());
        TextArea taDesc = new TextArea(3, 30);
        taDesc.setBackground(new Color(18, 26, 48));
        taDesc.setForeground(Color.WHITE);
        Choice statusChoice = new Choice();
        statusChoice.add("OPEN"); statusChoice.add("INVESTIGATING"); statusChoice.add("CLOSED");
        statusChoice.setBackground(new Color(18, 26, 48));
        statusChoice.setForeground(Color.WHITE);

        addRow(dialog, "Case Number:",       tfNum,        lf, lc);
        addRow(dialog, "Title:",             tfTitle,      lf, lc);
        addRow(dialog, "Location:",          tfLoc,        lf, lc);
        addRow(dialog, "Officer Name:",      tfOfficer,    lf, lc);
        addRow(dialog, "Date(YYYY-MM-DD):",  tfDate,       lf, lc);
        addRow(dialog, "Status:",            statusChoice, lf, lc);
        Label dl = new Label("Description:"); dl.setFont(lf); dl.setForeground(lc);
        dialog.add(dl); dialog.add(taDesc);

        Button save   = makeBtn("Save Case", new Color(60, 180, 100));
        Button cancel = makeBtn("Cancel",    new Color(100, 50, 50));
        save.addActionListener(e -> {
            try {
                Case c = new Case();
                c.setCaseNumber(tfNum.getText().trim());
                c.setTitle(tfTitle.getText().trim());
                c.setDescription(taDesc.getText().trim());
                c.setStatus(statusChoice.getSelectedItem());
                c.setLocation(tfLoc.getText().trim());
                c.setDateReported(Date.valueOf(tfDate.getText().trim()));
                c.setOfficerName(tfOfficer.getText().trim());
                if (caseDAO.addCase(c)) { showMsg("Case added!"); loadData(); dialog.dispose(); }
                else showMsg("Failed to add case.");
            } catch (Exception ex) { showMsg("Error: " + ex.getMessage()); }
        });
        cancel.addActionListener(e -> dialog.dispose());
        dialog.add(save); dialog.add(cancel);
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { dialog.dispose(); }
        });
        dialog.setVisible(true);
    }

    private void deleteSelected() {
        int idx = caseList.getSelectedIndex();
        if (idx < 0) { showMsg("Select a case first."); return; }
        if (caseDAO.deleteCase(cases.get(idx).getCaseId())) { showMsg("Deleted!"); loadData(); }
    }

    private void updateStatus() {
        int idx = caseList.getSelectedIndex();
        if (idx < 0) { showMsg("Select a case first."); return; }
        Case c = cases.get(idx);
        Dialog d = new Dialog(getFrame(), "Update Status", true);
        d.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
        d.setBackground(new Color(25, 35, 60));
        d.setSize(320, 160); d.setLocationRelativeTo(this);
        Label lbl = new Label("Status for: " + c.getCaseNumber());
        lbl.setForeground(Color.WHITE);
        Choice ch = new Choice();
        ch.add("OPEN"); ch.add("INVESTIGATING"); ch.add("CLOSED");
        ch.select(c.getStatus());
        ch.setBackground(new Color(18, 26, 48)); ch.setForeground(Color.WHITE);
        Button ok = makeBtn("Update", new Color(60, 130, 220));
        ok.addActionListener(e -> { caseDAO.updateStatus(c.getCaseId(), ch.getSelectedItem()); loadData(); d.dispose(); });
        d.add(lbl); d.add(ch); d.add(ok);
        d.addWindowListener(new WindowAdapter() { public void windowClosing(WindowEvent e) { d.dispose(); } });
        d.setVisible(true);
    }

    private Frame getFrame() {
        Container c = getParent();
        while (c != null && !(c instanceof Frame)) c = c.getParent();
        return (Frame) c;
    }
    private TextField makeField() {
        TextField tf = new TextField();
        tf.setBackground(new Color(18, 26, 48)); tf.setForeground(Color.WHITE); return tf;
    }
    private Button makeBtn(String t, Color bg) {
        Button b = new Button(t); b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); return b;
    }
    private Label makeLabel(String t, Color c) {
        Label l = new Label(t); l.setForeground(c);
        l.setFont(new Font("SansSerif", Font.BOLD, 12)); return l;
    }
    private void addRow(Dialog d, String lt, Component f, Font lf, Color lc) {
        Label l = new Label(lt); l.setFont(lf); l.setForeground(lc); d.add(l); d.add(f);
    }
    private void showMsg(String msg) {
        Dialog d = new Dialog(getFrame(), "Info", true);
        d.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        d.setBackground(new Color(25, 35, 60)); d.setSize(350, 130); d.setLocationRelativeTo(this);
        Label l = new Label(msg); l.setForeground(Color.WHITE);
        Button ok = makeBtn("OK", new Color(60, 130, 220));
        ok.addActionListener(e -> d.dispose()); d.add(l); d.add(ok);
        d.addWindowListener(new WindowAdapter() { public void windowClosing(WindowEvent e) { d.dispose(); } });
        d.setVisible(true);
    }
}