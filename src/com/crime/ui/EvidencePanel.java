package com.crime.ui;

import com.crime.dao.CaseDAO;
import com.crime.dao.EvidenceDAO;
import com.crime.model.Case;
import com.crime.model.Evidence;
import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.util.List;

public class EvidencePanel extends Panel {

    private MainFrame parent;
    private EvidenceDAO evidenceDAO = new EvidenceDAO();
    private CaseDAO caseDAO         = new CaseDAO();
    private java.awt.List evidenceList;
    private List<Evidence> evidences;

    private static final Color BG      = new Color(15, 20, 35);
    private static final Color CARD_BG = new Color(25, 35, 60);

    public EvidencePanel(MainFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout(10, 10));
        setBackground(BG);
        buildUI();
    }

    private void buildUI() {
        Panel header = new Panel(new BorderLayout());
        header.setBackground(BG);
        Label title = new Label("  EVIDENCE VAULT", Label.LEFT);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        Panel btnPanel = new Panel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        btnPanel.setBackground(BG);
        Button addBtn    = makeBtn("Log Evidence", new Color(60, 180, 100));
        Button deleteBtn = makeBtn("Delete",        new Color(200, 60, 60));
        Button refreshBtn= makeBtn("Refresh",       new Color(70, 80, 110));
        addBtn.addActionListener(e    -> showAddDialog());
        deleteBtn.addActionListener(e -> deleteSelected());
        refreshBtn.addActionListener(e-> loadData());
        btnPanel.add(addBtn); btnPanel.add(deleteBtn); btnPanel.add(refreshBtn);
        header.add(btnPanel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        evidenceList = new java.awt.List(22);
        evidenceList.setBackground(CARD_BG);
        evidenceList.setForeground(new Color(200, 210, 255));
        evidenceList.setFont(new Font("Monospaced", Font.PLAIN, 13));
        add(evidenceList, BorderLayout.CENTER);

        Panel legend = new Panel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        legend.setBackground(BG);
        for (String t : new String[]{"Physical","Digital","Witness","Document","Forensic"}) {
            Label l = new Label(t); l.setForeground(new Color(160, 200, 255));
            l.setFont(new Font("SansSerif", Font.BOLD, 11)); legend.add(l);
        }
        add(legend, BorderLayout.SOUTH);
        loadData();
    }

    public void loadData() {
        evidenceList.removeAll();
        evidences = evidenceDAO.getAllEvidence();
        for (Evidence ev : evidences)
            evidenceList.add(String.format("  %-30s  [%-10s]  Case:%-15s  Found: %s",
                    ev.getTitle(), ev.getType(),
                    ev.getCaseNumber() != null ? ev.getCaseNumber() : "N/A",
                    ev.getFoundLocation()));
    }

    private void showAddDialog() {
        Dialog dialog = new Dialog(getFrame(), "Log Evidence", true);
        dialog.setLayout(new GridLayout(8, 2, 8, 8));
        dialog.setBackground(new Color(25, 35, 60));
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        Font lf = new Font("SansSerif", Font.BOLD, 12);
        Color lc = new Color(180, 190, 230);

        TextField tfTitle = makeField();
        TextField tfLoc   = makeField();
        TextField tfDate  = makeField();
        tfDate.setText(new Date(System.currentTimeMillis()).toString());
        TextArea taDesc   = new TextArea(3, 30);
        taDesc.setBackground(new Color(18, 26, 48));
        taDesc.setForeground(Color.WHITE);

        Choice typeChoice = new Choice();
        for (String t : new String[]{"Physical","Digital","Witness","Document","Forensic"})
            typeChoice.add(t);
        typeChoice.setBackground(new Color(18, 26, 48));
        typeChoice.setForeground(Color.WHITE);

        Choice caseChoice = new Choice();
        caseChoice.setBackground(new Color(18, 26, 48));
        caseChoice.setForeground(Color.WHITE);
        List<Case> cases = caseDAO.getAllCases();
        for (Case c : cases) caseChoice.add(c.getCaseNumber() + " - " + c.getTitle());

        addRow(dialog, "Evidence Title:", tfTitle,    lf, lc);
        addRow(dialog, "Type:",           typeChoice, lf, lc);
        addRow(dialog, "Found Location:", tfLoc,      lf, lc);
        addRow(dialog, "Found Date:",     tfDate,     lf, lc);
        addRow(dialog, "Linked Case:",    caseChoice, lf, lc);
        Label dl = new Label("Description:");
        dl.setFont(lf); dl.setForeground(lc);
        dialog.add(dl); dialog.add(taDesc);

        Button save   = makeBtn("Save",   new Color(60, 180, 100));
        Button cancel = makeBtn("Cancel", new Color(100, 50, 50));

        save.addActionListener(e -> {
            try {
                Evidence ev = new Evidence();
                ev.setTitle(tfTitle.getText().trim());
                ev.setType(typeChoice.getSelectedItem());
                ev.setFoundLocation(tfLoc.getText().trim());
                ev.setFoundDate(Date.valueOf(tfDate.getText().trim()));
                ev.setDescription(taDesc.getText().trim());
                ev.setCaseId(cases.get(caseChoice.getSelectedIndex()).getCaseId());
                if (evidenceDAO.addEvidence(ev)) {
                    showMsg("Evidence logged!"); loadData(); dialog.dispose();
                } else showMsg("Failed.");
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
        int idx = evidenceList.getSelectedIndex();
        if (idx < 0) { showMsg("Select an item first."); return; }
        evidenceDAO.deleteEvidence(evidences.get(idx).getEvidenceId());
        showMsg("Deleted!"); loadData();
    }

    private Frame getFrame() {
        Container c = getParent();
        while (c != null && !(c instanceof Frame)) c = c.getParent();
        return (Frame) c;
    }
    private TextField makeField() {
        TextField tf = new TextField();
        tf.setBackground(new Color(18, 26, 48));
        tf.setForeground(Color.WHITE); return tf;
    }
    private Button makeBtn(String t, Color bg) {
        Button b = new Button(t); b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); return b;
    }
    private void addRow(Dialog d, String lt, Component f, Font lf, Color lc) {
        Label l = new Label(lt); l.setFont(lf); l.setForeground(lc); d.add(l); d.add(f);
    }
    private void showMsg(String msg) {
        Dialog d = new Dialog(getFrame(), "Info", true);
        d.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        d.setBackground(new Color(25, 35, 60));
        d.setSize(350, 130); d.setLocationRelativeTo(this);
        Label l = new Label(msg); l.setForeground(Color.WHITE);
        Button ok = makeBtn("OK", new Color(60, 130, 220));
        ok.addActionListener(e -> d.dispose()); d.add(l); d.add(ok);
        d.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { d.dispose(); }
        });
        d.setVisible(true);
    }
}